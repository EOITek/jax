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
        name = "DissectJob",
        display = "文本解构",
        description = "用于对指定的字段进行模式匹配和内容提取，能快速对文本进行结构化提取，比正则解析易于理解，且高效",
        icon = "DissectJob.svg",
        doc = "DissectJob.md",
        category = OperatorCategory.TEXT_ANALYSIS
)
public class DissectJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        Tuple2<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>>,
        DissectJobConfig> {

    @Override
    public Tuple2<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>> build(
            FlinkEnvironment context,
            DataStream<Map<String, Object>> dataStream,
            DissectJobConfig config,
            JobMetaConfig metaConfig) throws Throwable {
        OutputTag<Map<String, Object>> errorTag = new OutputTag<Map<String, Object>>(metaConfig.getJobId() + "_Error"){};

        SingleOutputStreamOperator<Map<String, Object>> output = dataStream
                .process(new DissectFunction(config, errorTag))
                .name(metaConfig.getJobEntry())
                .uid(metaConfig.getJobId());
        DataStream<Map<String, Object>> errorStream = output.getSideOutput(errorTag);

        return new Tuple2<>(output, errorStream);
    }

    @Override
    public DissectJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        return DissectJobConfig.fromMap(mapConfig);
    }
}
