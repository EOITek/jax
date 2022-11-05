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

package com.eoi.jax.web.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {
    public static final String HADOOP_CONF_DIR = "HADOOP_CONF_DIR";
    public static final Integer MAX_SQL_ROW_COUNT = 10000;
    public static final String JAX_JOB_JAR_DIR = "jaxjobjardir";
    public static final String JAX_JOB_JAR_LIB = "jaxjobjarlib";
    public static final String JAX_JOB_WORK_DIR = "jaxjobworkdir";
    public static final String JAX_JOB_PYTHON_DIR = "jaxjobpythondir";
    public static final String HADOOP_ETC_RELATIVE = "etc";
    public static final String HADOOP_ETC_HADOOP_RELATIVE = "etc/hadoop";
    public static final String HADOOP_CONF = "conf";
    public static final String HADOOP_YARN_BIN_RELATIVE = "bin/yarn";
    public static final String FLINK_BIN_RELATIVE = "bin/flink";
    public static final String SPARK_BIN_RELATIVE = "bin/spark-submit";
    public static final String PYTHON_BIN_RELATIVE = "bin/python";
    public static final String FLINK_LIB_RELATIVE = "lib";
    public static final String SPARK_LIB_RELATIVE = "jars";
    public static final String FLINK_YARN_SERVER = "yarn-cluster";
    public static final String SPARK_YARN_SERVER = "yarn";


    @Autowired
    public ServerConfig server;

    @Autowired
    public JaxConfig jax;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("server", server.toMap());
        map.put("jax", jax.toMap());
        return map;
    }
}
