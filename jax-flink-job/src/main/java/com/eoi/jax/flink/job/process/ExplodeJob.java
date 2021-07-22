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
        name = "ExplodeJob",
        display = "列转行（宽表转高表）",
        description = "多列转到同一列，即宽表转高表；\n"
                + "原始字段名放到高表的目标字段名列，原始字段值放到高表的目标字段值列",
        doc = "ExplodeJob.md",
        icon = "ExplodeJob.svg",
        category = OperatorCategory.DATA_TRANSFORMATION
)
public class ExplodeJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        Tuple2<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>>,
        ExplodeJobConfig> {
    @Override
    public Tuple2<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>> build(FlinkEnvironment context,
                                                 DataStream<Map<String, Object>> dataStream,
                                                 ExplodeJobConfig config, JobMetaConfig metaConfig) throws Throwable {
        OutputTag<Map<String, Object>> errorTag = new OutputTag<Map<String, Object>>(metaConfig.getJobId() + "ExplodeError"){};

        SingleOutputStreamOperator<Map<String, Object>> output =
                dataStream.process(new ExplodeJobFunction(config, errorTag))
                        .name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());
        DataStream<Map<String, Object>> errorStream = output.getSideOutput(errorTag);

        return new Tuple2<>(output, errorStream);
    }

    @Override
    public ExplodeJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        ExplodeJobConfig config = new ExplodeJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
