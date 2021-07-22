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
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

@Job(
        name = "PartitionJob",
        display = "对数据流做分区操作",
        description = "对于stream进行partitioning",
        icon = "PartitionJob.svg",
        doc = "PartitionJob.md",
        category = OperatorCategory.DATA_TRANSFORMATION
)
public class PartitionJob implements FlinkProcessJobBuilder<DataStream<Map<String, Object>>, DataStream<Map<String, Object>>, PartitionJobConfig> {

    @Override
    public DataStream<Map<String, Object>> build(FlinkEnvironment context, DataStream<Map<String, Object>> dataStream, PartitionJobConfig config, JobMetaConfig metaConfig) {
        if ("rebalance".equalsIgnoreCase(config.getPartitionType())) {
            dataStream = dataStream.rebalance();
        } else if ("shuffle".equalsIgnoreCase(config.getPartitionType())) {
            dataStream = dataStream.shuffle();
        } else if ("rescale".equalsIgnoreCase(config.getPartitionType())) {
            dataStream = dataStream.rescale();
        }

        return dataStream;
    }

    @Override
    public PartitionJobConfig configure(Map<String, Object> mapConfig) {
        PartitionJobConfig config = new PartitionJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
