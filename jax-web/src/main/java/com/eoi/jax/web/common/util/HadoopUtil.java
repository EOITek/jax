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

package com.eoi.jax.web.common.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.config.AppConfig;
import com.eoi.jax.web.common.exception.BizException;
import com.eoi.jax.web.dao.entity.TbCluster;
import com.eoi.jax.web.provider.cluster.ClusterVariable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class HadoopUtil {
    private static final Logger logger = LoggerFactory.getLogger(HadoopUtil.class);

    public static final String HDFS_DEFAULT_NAME_KEY = "fs.defaultFS";

    private HadoopUtil() {
        //forbid init
    }

    public static Configuration readConfig(String configHome) {
        try {
            List<String> list = FileUtil.listFileNames(configHome);
            Configuration config = new Configuration();
            for (String item : list) {
                if (item.endsWith("xml")) {
                    config.addResource(new Path(Common.pathsJoin(configHome, item)));
                }
            }
            return config;
        } catch (Exception e) {
            logger.error("read hadoop config error", e);
            throw new BizException(ResponseCode.INVALID_HADOOP_CONF);
        }
    }

    public static String readValue(String configHome, String key) {
        Configuration config = readConfig(configHome);
        return config.get(key);
    }

    public static String readHdfsServer(String configHome) {
        return readValue(configHome, HDFS_DEFAULT_NAME_KEY);
    }

    public static String readYarnWebUrl(String configHome) {
        Configuration config = readConfig(configHome);
        return getYarnWebUrl(config);
    }

    public static String getYarnWebUrl(Configuration config) {
        String schema = "http://";
        String webUrl = null;
        // HA
        if (HAUtil.isHAEnabled(config)) {
            HAUtil.verifyAndSetConfiguration(config);
            if (YarnConfiguration.useHttps(config)) {
                webUrl = HAUtil.getConfValueForRMInstance(YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS, config);
                schema = "https://";
            } else {
                webUrl = HAUtil.getConfValueForRMInstance(YarnConfiguration.RM_WEBAPP_ADDRESS, config);
            }
        } else {
            if (YarnConfiguration.useHttps(config)) {
                webUrl = config.getTrimmed(YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS);
                schema = "https://";
            } else {
                webUrl = config.getTrimmed(YarnConfiguration.RM_WEBAPP_ADDRESS);
            }
        }
        return schema + webUrl;
    }

    public static List<String> readAllYarnWebUrls(String configHome) {
        Configuration config = readConfig(configHome);
        String schema = "http://";
        String prefix = null;
        if (YarnConfiguration.useHttps(config)) {
            prefix = YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS;
            schema = "https://";
        } else {
            prefix = YarnConfiguration.RM_WEBAPP_ADDRESS;
        }
        List<String> webUrls = new ArrayList<>();
        // HA
        if (HAUtil.isHAEnabled(config)) {
            HAUtil.verifyAndSetConfiguration(config);
            Collection<String> rmIds = HAUtil.getRMHAIds(config);
            for (String rmId : rmIds) {
                webUrls.add(schema + config.getTrimmed(HAUtil.addSuffix(prefix, rmId)));
            }
        } else {
            webUrls.add(schema + config.getTrimmed(prefix));
        }
        return webUrls;
    }

    public static Configuration initConfigurationByPath(String specifiedHadoopConfPath) {
        YarnConfiguration configuration = new YarnConfiguration();
        String[] possibleHadoopConfPaths = new String[3];
        if (specifiedHadoopConfPath != null && !specifiedHadoopConfPath.isEmpty()) {
            possibleHadoopConfPaths[0] = specifiedHadoopConfPath;
        } else {
            String hadoopConfiDir = System.getenv("HADOOP_CONF_DIR");
            String hadoopHome = System.getenv("HADOOP_HOME");
            if (hadoopConfiDir != null && !hadoopConfiDir.isEmpty()) {
                possibleHadoopConfPaths[1] = hadoopConfiDir;
            } else if (hadoopHome != null && !hadoopHome.isEmpty()) {
                possibleHadoopConfPaths[1] = hadoopHome + "/conf";
                possibleHadoopConfPaths[2] = hadoopHome + "/etc/conf";
            }
        }
        for (String possibleHadoopConfPath: possibleHadoopConfPaths) {
            if (possibleHadoopConfPath != null) {
                File dir = new File(possibleHadoopConfPath);
                try {
                    File[] files = org.apache.hadoop.fs.FileUtil.listFiles(dir);
                    for (File file: files) {
                        if (file.isFile() && file.canRead() && file.getName().endsWith(".xml")) {
                            configuration.addResource(new Path(file.getAbsolutePath()));
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("Failed to list or add resource in {}", dir, ex);
                }
            }
        }
        // TODO: add more fs impl to prevent ServiceLoader file override?
        configuration.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        return configuration;
    }

    // TODO: Thread not safe, since UserGroupInformation use static context to init configuration
    public static <T> T doAction(TbCluster cluster, Function<Configuration, T> action) throws Exception {
        String hadoopConf = ClusterVariable.genHadoopConf(ClusterVariable.replaceVariable(cluster.getHadoopHome()));
        Configuration configuration = initConfigurationByPath(hadoopConf);
        if (StrUtil.isNotEmpty(cluster.getPrincipal()) && StrUtil.isNotEmpty(cluster.getKeytab())) {
            UserGroupInformation.reset();
            UserGroupInformation.setConfiguration(configuration);
            UserGroupInformation ugi = null;
            try {
                logger.info("Login from {} with keytab {}", cluster.getPrincipal(), cluster.getKeytab());
                ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(cluster.getPrincipal(), cluster.getKeytab());
            } catch (IOException ex) {
                logger.warn("Failed to login user from keytab: {}({})", cluster.getPrincipal(), cluster.getKeytab());
            }
            if (ugi != null) {
                logger.info("Do action via user {}", ugi.getUserName());
                return ugi.doAs((PrivilegedExceptionAction<T>) () -> action.apply(configuration));
            } else {
                throw new Exception(String.format("Failed to login user from keytab: %s(%s)", cluster.getPrincipal(), cluster.getKeytab()));
            }
        } else {
            return action.apply(configuration);
        }
    }

    public static Path copyFileToHdfs(File srcFile, Path destPath, TbCluster cluster) throws Exception {
        return doAction(cluster, (Configuration configuration) -> {
            try {
                FileSystem fs = FileSystem.get(configuration);
                OutputStream os = fs.create(destPath);
                IOUtils.copyBytes(new FileInputStream(srcFile), os, 4096, true);
                return fs.makeQualified(destPath);
            } catch (Exception ex) {
                logger.error(ex.toString());
                return null;
            }
        });
    }

    public static Path copyFileToMarayarnHdfs(File srcFile, String fileName, TbCluster cluster) throws Exception {
        return doAction(cluster, (Configuration configuration) -> {
            try {
                FileSystem fs = FileSystem.get(configuration);
                Path hdfsPath = getUploadPath(fs, fileName);
                OutputStream os = fs.create(hdfsPath);
                IOUtils.copyBytes(new FileInputStream(srcFile), os, 4096, true);
                return fs.makeQualified(hdfsPath);
            } catch (Exception ex) {
                logger.error(ex.toString());
                return null;
            }
        });
    }

    private static Path getUploadPath(FileSystem fs, String fileName) throws IOException {
        Path base =  new Path(fs.getHomeDirectory(), "marayarn/upload/" + LocalDate.now().toString("yyyyMMdd"));
        //检查此位置是否有同名文件
        Path finalPath = new Path(base,fileName);
        if (fs.exists(finalPath)) {
            //如果当日的目录下存在重名的，则在此目录下再根据具体时间创建一个子目录
            Path plusMs = new Path(base, LocalDateTime.now().toString("HHmmss"));
            finalPath = new Path(plusMs, fileName);
        }
        return finalPath;
    }

    public static Configuration getHadoopConf(String hadoopHome) {
        String[] possiblePaths = new String[4];
        if (hadoopHome != null) {
            possiblePaths[0] = Paths.get(hadoopHome, AppConfig.HADOOP_ETC_RELATIVE).toString(); // eoitek hadoop
            possiblePaths[1] = Paths.get(hadoopHome,AppConfig.HADOOP_ETC_HADOOP_RELATIVE).toString(); // apache hadoop
            possiblePaths[2] = Paths.get(hadoopHome,AppConfig.HADOOP_CONF).toString();
        }
        if (null != System.getenv(AppConfig.HADOOP_CONF_DIR)) {
            possiblePaths[3] = Paths.get(System.getenv(AppConfig.HADOOP_CONF_DIR)).toString(); // eoitek hadoop
        }
        Configuration hadoopConf = null;
        for (String possibleHadoopConfPath :possiblePaths) {
            hadoopConf = getHadoopConfiguration(possibleHadoopConfPath);
            if (hadoopConf != null) {
                break;
            }
        }
        return hadoopConf;
    }

    public static Configuration getHadoopConfiguration(String hadoopConfDir) {
        if (new File(hadoopConfDir).exists()) {
            Configuration hadoopConfiguration = new Configuration();
            File coreSite = new File(hadoopConfDir, "core-site.xml");
            if (coreSite.exists()) {
                hadoopConfiguration.addResource(new Path(coreSite.getAbsolutePath()));
            }
            File hdfsSite = new File(hadoopConfDir, "hdfs-site.xml");
            if (hdfsSite.exists()) {
                hadoopConfiguration.addResource(new Path(hdfsSite.getAbsolutePath()));
            }
            File yarnSite = new File(hadoopConfDir, "yarn-site.xml");
            if (yarnSite.exists()) {
                hadoopConfiguration.addResource(new Path(yarnSite.getAbsolutePath()));
            }
            // Add mapred-site.xml. We need to read configurations like compression codec.
            File mapredSite = new File(hadoopConfDir, "mapred-site.xml");
            if (mapredSite.exists()) {
                hadoopConfiguration.addResource(new Path(mapredSite.getAbsolutePath()));
            }
            return hadoopConfiguration;
        }
        return null;
    }



}
