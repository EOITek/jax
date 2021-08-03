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
import com.eoi.jax.common.OperatorCategory;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

@Job(
        name = "FilterFieldJob",
        display = "自定义输出字段",
        description = "用于指定是否输出某些字段",
        icon = "FilterFieldJob.svg",
        doc = "FilterFieldJob.md",
        category = OperatorCategory.DATA_TRANSFORMATION
)
public class FilterFieldJob implements FlinkProcessJobBuilder<DataStream<Map<String, Object>>,
        DataStream<Map<String, Object>>, FilterFieldJobConfig> {

    @Override
    public DataStream<Map<String, Object>> build(
            FlinkEnvironment context,
            DataStream<Map<String, Object>> dataStream,
            FilterFieldJobConfig config, JobMetaConfig metaConfig) throws Throwable {
        return dataStream.map(new FilterFieldJobFunction(config.getWhiteListFields(),config.getBlackListFields()))
                .name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());
    }

    @Override
    public FilterFieldJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        return FilterFieldJobConfig.fromMap(mapConfig);
    }
}
