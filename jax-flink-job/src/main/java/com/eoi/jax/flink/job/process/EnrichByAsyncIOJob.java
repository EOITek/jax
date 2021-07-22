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
import com.eoi.jax.common.OperatorCategory;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;
import java.util.concurrent.TimeUnit;


@Job(
        name = "EnrichByAsyncIOJob",
        display = "字段丰富（异步查询）",
        description = "通过jdbc异步查询从指定表获取数据来丰富字段;\n"
                + "使用flink async io的方式提高查询效率，但不保证输出数据的原始顺序。",
        icon = "EnrichByAsyncIOJob.svg",
        doc = "EnrichByAsyncIOJob.md",
        category = OperatorCategory.FIELD_PROCESSING
)
public class EnrichByAsyncIOJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        DataStream<Map<String, Object>>,
        EnrichByAsyncIOJobConfig> {

    @Override
    public DataStream<Map<String, Object>> build(
            FlinkEnvironment context,
            DataStream<Map<String,Object>> dataStream,
            EnrichByAsyncIOJobConfig config,
            JobMetaConfig metaConfig) throws Throwable {

        return AsyncDataStream.unorderedWait(dataStream,
                new EnrichByAsyncIOFunction(config), config.getTimeout(), TimeUnit.MILLISECONDS, config.getCapacity())
                        .name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());
    }

    @Override
    public EnrichByAsyncIOJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        EnrichByAsyncIOJobConfig config = new EnrichByAsyncIOJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
