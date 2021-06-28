/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eoi.jax.web.provider.jar;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.config.ConfigLoader;
import com.eoi.jax.web.common.exception.JaxException;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.dao.entity.TbJar;
import com.eoi.jax.web.provider.cluster.Cluster;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.List;

@Service
public class HdfsFileSystem {

    @Autowired
    private JarFileCache jarFileCache;

    public String touchJarFile(TbJar jar, Cluster cluster) {
        JarCache cache = jarFileCache.getJarCache(jar);
        if (cache != null) {
            return cache.getPath();
        }

        String fileExt = Common.getFileExtension(jar.getJarPath());
        String localPath = Common.pathsJoin(
                ConfigLoader.load().jax.getJarDir(),
                Common.genJarName(jar.getJarName(), fileExt)
        );
        download(jar.getJarPath(), localPath, cluster);
        jarFileCache.createJarCache(jar, localPath);
        return localPath;
    }

    public String move(String src, String dst, Cluster cluster) {
        String res = upload(src, dst, cluster);
        try {
            FileUtil.del(src);
        } catch (Exception e) {
            throw new JaxException(e);
        }
        return res;
    }

    public String upload(String local, String remote, Cluster cluster) {
        try {
            doAction(cluster, (FileSystem hdfs) -> {
                Path localPath = new Path(local);
                Path remotePath = new Path(remote);
                if (hdfs.exists(remotePath)) {
                    hdfs.delete(remotePath, false);
                }
                hdfs.copyFromLocalFile(localPath, remotePath);
            });
        } catch (Exception e) {
            throw new JaxException(e);
        }
        String server = cluster.getHdfsServer();
        return Common.urlPathsJoin(server, remote);
    }

    public String download(String remote, String local, Cluster cluster) {
        try {
            FileUtil.del(local);
            doAction(cluster, (FileSystem hdfs) -> {
                try (
                        OutputStream out = new BufferedOutputStream(
                                new FileOutputStream(new File(local))
                        );
                        FSDataInputStream in = hdfs.open(new Path(remote));
                ) {
                    IoUtil.copy(in, out, IoUtil.DEFAULT_BUFFER_SIZE);
                }
            });
        } catch (Exception e) {
            throw new JaxException(e);
        }
        return local;
    }

    public boolean delete(String remote, Cluster cluster) {
        try {
            doAction(cluster, (FileSystem hdfs) -> {
                Path remotePath = new Path(remote);
                if (hdfs.exists(remotePath)) {
                    hdfs.delete(remotePath, true);
                }
            });
        } catch (Exception e) {
            throw new JaxException(e);
        }
        return true;
    }

    public void doAction(Cluster cluster, HdfsAction action) throws Exception {
        String server = cluster.getHdfsServer();
        URI uri = URI.create(server);
        if (needKerberosLogin(cluster)) {
            UserGroupInformation ugi = loginKerberos(cluster);
            ugi.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    try (FileSystem hdfs = FileSystem.get(uri, getConfig(cluster))) {
                        action.run(hdfs);
                    }
                    return null;
                }
            });
        } else {
            try (FileSystem hdfs = FileSystem.get(uri, getConfig(cluster))) {
                action.run(hdfs);
            }
        }
    }

    private Configuration getConfig(Cluster cluster) {
        String confPath = cluster.getHadoopConfig();
        List<String> list = FileUtil.listFileNames(confPath);
        Configuration config = new Configuration();
        for (String item : list) {
            if (item.endsWith("xml")) {
                config.addResource(new Path(Common.pathsJoin(confPath, item)));
            }
        }
        return config;
    }

    private boolean needKerberosLogin(Cluster cluster) {
        return StrUtil.isNotEmpty(cluster.getKeytab())
                && StrUtil.isNotEmpty(cluster.getPrincipal());
    }

    private UserGroupInformation loginKerberos(Cluster cluster) throws IOException {
        UserGroupInformation.setConfiguration(getConfig(cluster));
        return UserGroupInformation.loginUserFromKeytabAndReturnUGI(
                cluster.getPrincipal(),
                cluster.getKeytab()
        );
    }

    public interface HdfsAction {
        void run(FileSystem hdfs) throws Exception;
    }
}
