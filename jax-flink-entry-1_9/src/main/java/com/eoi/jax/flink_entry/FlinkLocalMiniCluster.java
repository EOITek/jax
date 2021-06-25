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

package com.eoi.jax.flink_entry;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.api.FlinkEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FlinkLocalMiniCluster {
    public static void main(String[] args) {
        try {
            ParameterTool params = ParameterTool.fromArgs(args);
            String host = params.get("host");
            String port = params.get("port");
            String path = params.get("job_file");
            String jobJson = new String(Files.readAllBytes(new File(path).toPath()), StandardCharsets.UTF_8);
            FlinkPipelineDescription description = FlinkContextMainBuilder.description(jobJson);
            Configuration configuration = new Configuration();
            if (StrUtil.isNotBlank(host)) {
                configuration.setString(RestOptions.ADDRESS, host);
            }
            if (StrUtil.isNotBlank(port)) {
                configuration.setInteger(RestOptions.PORT, Integer.parseInt(port));
            }
            configuration.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER, true);
            LocalStreamEnvironment streamEnv = StreamExecutionEnvironment.createLocalEnvironment(1, configuration);
            FlinkContextMainBuilder.buildTimeCharacteristic(streamEnv, description);
            FlinkContextMainBuilder.buildGlobalJobParameters(streamEnv, description);
            StreamTableEnvironment tableEnv = FlinkContextMainBuilder.buildTableEnvironment(streamEnv, description);
            FlinkEnvironment context = FlinkContextMainBuilder.buildDAG(streamEnv, tableEnv, description);

            if (!FlinkContextMainBuilder.isSqlJob(description)) {
                System.out.println(context.streamEnv.getExecutionPlan());
                context.streamEnv.execute(description.getPipelineName());
            }
        } catch (Throwable e) {
            e.printStackTrace(System.err);
        }
    }
}
