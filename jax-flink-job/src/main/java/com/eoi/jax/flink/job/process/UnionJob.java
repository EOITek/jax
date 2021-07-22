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

import com.eoi.jax.api.EmptyConfig;
import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.tuple.Tuple4;
import com.eoi.jax.common.OperatorCategory;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

@Job(
        name = "UnionJob",
        display = "数据流合并Job",
        description = "数据流合并Job, 最多支持4个数据流同时合并",
        icon = "UnionJob.svg",
        doc = "UnionJob.md",
        category = OperatorCategory.DATA_TRANSFORMATION
)
public class UnionJob implements
        FlinkProcessJobBuilder<
                Tuple4<DataStream<Map<String, Object>>,
                        DataStream<Map<String, Object>>,
                        DataStream<Map<String, Object>>,
                        DataStream<Map<String, Object>>>,
                DataStream<Map<String, Object>>, EmptyConfig> {
    @Override
    public DataStream<Map<String, Object>> build(
            FlinkEnvironment context,
            Tuple4<DataStream<Map<String, Object>>,
                    DataStream<Map<String, Object>>,
                    DataStream<Map<String, Object>>,
                    DataStream<Map<String, Object>>> dataStreamDataStreamDataStreamDataStreamTuple4,
            EmptyConfig config,
            JobMetaConfig metaConfig) {
        DataStream<Map<String, Object>> result = dataStreamDataStreamDataStreamDataStreamTuple4.f0;
        if (dataStreamDataStreamDataStreamDataStreamTuple4.f1 != null) {
            result = result.union(dataStreamDataStreamDataStreamDataStreamTuple4.f1);
        }
        if (dataStreamDataStreamDataStreamDataStreamTuple4.f2 != null) {
            result = result.union(dataStreamDataStreamDataStreamDataStreamTuple4.f2);
        }
        if (dataStreamDataStreamDataStreamDataStreamTuple4.f3 != null) {
            result = result.union(dataStreamDataStreamDataStreamDataStreamTuple4.f3);
        }
        return result;
    }

    @Override
    public EmptyConfig configure(Map<String, Object> mapConfig) {
        return new EmptyConfig();
    }

    @Override
    public boolean compatibleWithLastConfig(EmptyConfig lastConfig, EmptyConfig currentConfig) {
        return true;
    }
}
