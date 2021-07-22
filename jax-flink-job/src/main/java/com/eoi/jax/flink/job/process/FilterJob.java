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
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.api.tuple.Tuple2;
import com.eoi.jax.common.OperatorCategory;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.OutputTag;

import java.util.Map;


@Job(
        name = "FilterJob",
        display = "过滤器",
        description = "使用aviator表达式指定过滤规则，返回boolean值；"
                + "如果为true，输出到slot 0；"
                + "如果为false，输出到slot 1； ",
        icon = "FilterJob.svg",
        doc = "FilterJob.md",
        category = OperatorCategory.DATA_TRANSFORMATION
)
public class FilterJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        Tuple2<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>>,
        FilterJobConfig> {

    @Override
    public Tuple2<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>> build(
            FlinkEnvironment context,
            DataStream<Map<String,Object>> dataStream,
            FilterJobConfig config,
            JobMetaConfig metaConfig) throws Throwable {
        OutputTag<Map<String, Object>> falseTag = new OutputTag<Map<String, Object>>(metaConfig.getJobId() + "filterFalse"){};

        SingleOutputStreamOperator<Map<String, Object>> output = dataStream.process(new FilterFunction(config, falseTag))
                        .name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());
        DataStream<Map<String, Object>> falseStream = output.getSideOutput(falseTag);

        return new Tuple2<>(output, falseStream);
    }

    @Override
    public FilterJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        FilterJobConfig config = new FilterJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
