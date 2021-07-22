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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.api.tuple.Tuple2;
import com.eoi.jax.common.OperatorCategory;
import com.eoi.jax.flink.job.common.TimestampConvertJobConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.OutputTag;

import java.util.Map;

@Job(
        name = "TimestampConvertJob",
        display = "时间格式转换",
        description = "按指定输入输出时间格式进行转换",
        icon = "TimestampConvertJob.svg",
        doc = "TimestampConvertJob.md",
        category = OperatorCategory.DATA_TYPE_CONVERSION
)
public class TimestampConvertJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        Tuple2<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>>,
        TimestampConvertJobConfig> {

    @Override
    public Tuple2<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>> build(
            FlinkEnvironment context,
            DataStream<Map<String, Object>> dataStream,
            TimestampConvertJobConfig config,
            JobMetaConfig metaConfig) throws JobConfigValidationException {
        OutputTag<Map<String, Object>> errorTag = new OutputTag<Map<String, Object>>(metaConfig.getJobId() + "TimestampConvertJobError"){};

        SingleOutputStreamOperator<Map<String, Object>> output =
                dataStream.process(new TimestampConvertFunction(config, errorTag)).name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());
        DataStream<Map<String, Object>> errorStream = output.getSideOutput(errorTag);

        return new Tuple2<>(output, errorStream);
    }

    @Override
    public boolean compatibleWithLastConfig(TimestampConvertJobConfig lastConfig, TimestampConvertJobConfig currentConfig) {
        return true;
    }

    @Override
    public TimestampConvertJobConfig configure(Map<String, Object> mapConfig) throws JobConfigValidationException {
        TimestampConvertJobConfig config = new TimestampConvertJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        if (StrUtil.isEmpty(config.getOutputTimestampField())) {
            config.setOutputTimestampField(config.getTimestampField());
        }
        return config;
    }
}
