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

package com.eoi.jax.flink.job.process;

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.tuple.Tuple2;
import com.eoi.jax.common.OperatorCategory;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.OutputTag;

import java.util.Map;

@Job(
        name = "JsonStringToMapJob",
        display = "字符串转Json",
        description = "将json string转为map",
        icon = "JsonStringToMapJob.svg",
        doc = "JsonStringToMapJob.md",
        category = OperatorCategory.TEXT_ANALYSIS
)
public class JsonStringToMapJob implements FlinkProcessJobBuilder<
        DataStream<String>,
        Tuple2<DataStream<Map<String, Object>>, DataStream<String>>,
        JsonStringToMapJobConfig> {

    @Override
    public Tuple2<DataStream<Map<String, Object>>, DataStream<String>> build(
            FlinkEnvironment context,
            DataStream<String> stringDataStream,
            JsonStringToMapJobConfig config,
            JobMetaConfig metaConfig) throws Throwable {
        OutputTag<String> errorTag = new OutputTag<String>(metaConfig.getJobId() + "_Error"){};
        SingleOutputStreamOperator<Map<String, Object>> sideOutput = stringDataStream
                .process(new JsonStringToMapFunction(errorTag))
                .name(metaConfig.getJobEntry())
                .uid(metaConfig.getJobId());
        DataStream<String> errorStream = sideOutput.getSideOutput(errorTag);
        return new Tuple2<>(sideOutput, errorStream);
    }

    @Override
    public JsonStringToMapJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        return new JsonStringToMapJobConfig();
    }
}
