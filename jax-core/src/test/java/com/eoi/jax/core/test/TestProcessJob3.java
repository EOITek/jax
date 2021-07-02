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

package com.eoi.jax.core.test;

import com.eoi.jax.api.EmptyConfig;
import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Job(
        name = "TestProcessJob3",
        display = "TestProcessJob3",
        description = "测试用 Tuple3<DataStream<Long>,DataStream<Long>,DataStream<Long>>进 DataStream<Long>出的 处理job"
)
public class TestProcessJob3
        implements FlinkProcessJobBuilder<Tuple3<DataStream<Long>,DataStream<Long>,DataStream<Long>>, DataStream<Long>, EmptyConfig> {
    @Override
    public EmptyConfig configure(Map mapConfig) {
        return new EmptyConfig();
    }

    @Override
    public DataStream<Long> build(
            FlinkEnvironment context,
            Tuple3<DataStream<Long>,DataStream<Long>,DataStream<Long>> dataStream,
            EmptyConfig config, JobMetaConfig metaConfig) throws Exception {
        List<DataStream<Long>> dataStreams = new ArrayList<>();
        if (dataStream.f0 != null) {
            dataStreams.add(dataStream.f0);
        }
        if (dataStream.f1 != null) {
            dataStreams.add(dataStream.f1);
        }
        if (dataStream.f2 != null) {
            dataStreams.add(dataStream.f2);
        }

        DataStream<Long> output = null;
        for (DataStream<Long> stream : dataStreams) {
            if (output == null) {
                output = stream;
            } else {
                output = output.union(stream);
            }
        }

        return output;
    }
}
