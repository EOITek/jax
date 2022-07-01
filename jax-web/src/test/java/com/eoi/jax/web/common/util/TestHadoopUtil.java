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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Test;

public class TestHadoopUtil {

    @Test
    public void testGetConfDir() {
        String hadoopHome = "D:\\app-bigdata\\hadoop\\hadoop-2.7.7";
        Configuration conf = HadoopUtil.getConfFromHadoopHome(hadoopHome);
        String hdfs = conf.get(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY);
        String yarnUrl = conf.get(YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS);
        System.out.println(conf);

        String confDir = "D:\\app-bigdata\\hadoop\\hadoop-2.7.2\\etc\\hadoop";
        String hdfsServer = HadoopUtil.readHdfsServer(confDir);
        String yarnWebUrl = HadoopUtil.readYarnWebUrl(confDir);
        System.out.println(hdfsServer);

    }

}
