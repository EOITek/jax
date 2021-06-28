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

import com.eoi.jax.web.common.consts.ClusterType;
import com.eoi.jax.web.dao.entity.TbCluster;
import com.eoi.jax.web.dao.entity.TbJar;
import com.eoi.jax.web.provider.JarFileProvider;
import com.eoi.jax.web.provider.cluster.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class JarFileProviderImpl implements JarFileProvider {

    @Autowired
    private HdfsFileSystem hdfsFileSystem;

    @Autowired
    private LocalFileSystem localFileSystem;

    @Override
    public String saveTempFile(MultipartFile file, String tmpPath) {
        return localFileSystem.saveTmpFile(file, tmpPath);
    }

    @Override
    public String moveJavaFile(String tmpPath, String jarPath, Cluster cluster) {
        if (cluster != null && ClusterType.YARN.isEqual(cluster.getClusterType())) {
            return hdfsFileSystem.move(tmpPath, jarPath, cluster);
        }
        return localFileSystem.move(tmpPath, jarPath);
    }

    @Override
    public Boolean deleteJarFile(String jarPath, Cluster cluster) {
        if (cluster != null && ClusterType.YARN.isEqual(cluster.getClusterType())) {
            return hdfsFileSystem.delete(jarPath, cluster);
        }
        return localFileSystem.delete(jarPath);
    }

    @Override
    public String touchJarFile(TbJar jar, TbCluster cluster) {
        if (cluster != null && ClusterType.YARN.isEqual(cluster.getClusterType())) {
            return hdfsFileSystem.touchJarFile(jar, new Cluster(cluster, null, null));
        }
        return jar.getJarPath();
    }
}
