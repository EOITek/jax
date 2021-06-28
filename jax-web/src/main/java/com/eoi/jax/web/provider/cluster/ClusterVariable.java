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

package com.eoi.jax.web.provider.cluster;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.config.AppConfig;
import com.eoi.jax.web.common.config.ConfigLoader;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.dao.entity.TbCluster;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClusterVariable {
    public static final String VAR_JAX_HOME = "${JAX_HOME}";
    public static final String VAR_HDFS_SERVER = "${HDFS_SERVER}";
    public static final String VAR_PYTHON_ENV = "${PYTHON_ENV}";

    private ClusterVariable() {
        // forbid init instance
    }

    public static String genPythonBin(String pythonEnv) {
        return Common.pathsJoin(pythonEnv, AppConfig.PYTHON_BIN_RELATIVE);
    }

    public static String genHadoopConf(String hadoopHome) {
        return Common.pathsJoin(hadoopHome, AppConfig.HADOOP_ETC_RELATIVE);
    }

    public static String genYarnBin(String hadoopHome) {
        return Common.pathsJoin(hadoopHome, AppConfig.HADOOP_YARN_BIN_RELATIVE);
    }

    public static String genFlinkBin(String flinkHome) {
        return Common.pathsJoin(flinkHome, AppConfig.FLINK_BIN_RELATIVE);
    }

    public static String genSparkBin(String sparkHome) {
        return Common.pathsJoin(sparkHome, AppConfig.SPARK_BIN_RELATIVE);
    }

    public static String genFlinkLib(String flinkHome) {
        return Common.pathsJoin(flinkHome, AppConfig.FLINK_LIB_RELATIVE);
    }

    public static String genSparkLib(String sparkHome) {
        return Common.pathsJoin(sparkHome, AppConfig.SPARK_LIB_RELATIVE);
    }

    public static String replaceVariable(String var) {
        var = StrUtil.replace(var,
                VAR_JAX_HOME,
                ConfigLoader.load().jax.getHome());
        return var;
    }

    public static String replaceVariable(String var, TbCluster clusterEntity) {
        var = replaceVariable(var);
        var = StrUtil.replace(var,
                VAR_HDFS_SERVER,
                clusterEntity.getHdfsServer());
        var = StrUtil.replace(var,
                VAR_PYTHON_ENV,
                clusterEntity.getPythonEnv());
        return var;
    }

    public static List<String> replaceVariable(List<String> list, TbCluster clusterEntity) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().map(var ->
                replaceVariable(var, clusterEntity)
        ).collect(Collectors.toList());
    }

    public static String extractVariable(String var) {
        var = StrUtil.replace(var,
                ConfigLoader.load().jax.getHome(),
                VAR_JAX_HOME);
        return var;
    }

    public static List<String> extractVariable(List<String> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().map(var ->
                extractVariable(var)
        ).collect(Collectors.toList());
    }

    public static String extractVariable(String var, TbCluster clusterEntity) {
        var = extractVariable(var);
        var = StrUtil.replace(var,
                clusterEntity.getHdfsServer(),
                VAR_HDFS_SERVER);
        var = StrUtil.replace(var,
                clusterEntity.getPythonEnv(),
                VAR_PYTHON_ENV);
        return var;
    }

    public static List<String> extractVariable(List<String> list, TbCluster clusterEntity) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().map(var ->
                extractVariable(var, clusterEntity)
        ).collect(Collectors.toList());
    }
}

