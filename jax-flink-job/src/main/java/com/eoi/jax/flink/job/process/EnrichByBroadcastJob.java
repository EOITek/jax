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
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;


@Job(
        name = "EnrichByBroadcastJob",
        display = "字段丰富（数据流广播）",
        description = "通过key反查字典数据流，如果找到则把字典里其他字段拼到原始数据上；如果字典里没有找到则不添加字段。",
        icon = "EnrichByBroadcastJob.svg",
        doc = "EnrichByBroadcastJob.md",
        category = OperatorCategory.FIELD_PROCESSING
)
public class EnrichByBroadcastJob implements FlinkProcessJobBuilder<
        Tuple2<DataStream<Map<String,Object>>,DataStream<Map<String,Object>>>,
        DataStream<Map<String, Object>>,
        EnrichByBroadcastJobConfig> {

    @Override
    public DataStream<Map<String, Object>> build(
            FlinkEnvironment context,
            Tuple2<DataStream<Map<String,Object>>,DataStream<Map<String,Object>>> dataStreams,
            EnrichByBroadcastJobConfig config,
            JobMetaConfig metaConfig) throws Throwable {
        DataStream<Map<String,Object>> dataStream = dataStreams.f0;
        DataStream<Map<String,Object>> ruleStream = dataStreams.f1;

        BroadcastStream<Map<String,Object>> ruleBroadcastStream = ruleStream.broadcast(EnrichByBroadcastFunction.enrichBroadcastDesc);

        return dataStream.connect(ruleBroadcastStream)
                .process(new EnrichByBroadcastFunction(config.getKey()))
                .name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());
    }

    @Override
    public EnrichByBroadcastJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        EnrichByBroadcastJobConfig config = new EnrichByBroadcastJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
