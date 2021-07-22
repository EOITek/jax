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
        name = "StringDecoderJob",
        display = "解码器(String)",
        description = "把byte数组按指定字符集转换为String,并输出到指定字段，如果不指定输出字段则默认输出到@message字段，"
                + "\n输出slot 0 为decode成功数据，输出slot 1 为decode失败数据（错误原因添加在error_msg字段） ",
        icon = "StringDecoderJob.svg",
        doc = "StringDecoderJob.md",
        category = OperatorCategory.TEXT_ANALYSIS
)
public class StringDecoderJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        Tuple2<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>>,
        StringDecoderJobConfig> {

    @Override
    public Tuple2<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>> build(
            FlinkEnvironment context,
            DataStream<Map<String,Object>> dataStream,
            StringDecoderJobConfig config,
            JobMetaConfig metaConfig) throws Throwable {
        OutputTag<Map<String, Object>> errorTag = new OutputTag<Map<String, Object>>(metaConfig.getJobId() + "StringDecoderJobError"){};

        SingleOutputStreamOperator<Map<String, Object>> output =
                dataStream.process(new StringDecoderFunction(config, errorTag)).name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());
        DataStream<Map<String, Object>> errorStream = output.getSideOutput(errorTag);

        return new Tuple2<>(output, errorStream);
    }

    @Override
    public StringDecoderJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        StringDecoderJobConfig config = new StringDecoderJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
