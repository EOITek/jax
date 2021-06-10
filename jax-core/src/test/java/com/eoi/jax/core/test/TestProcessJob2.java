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

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.tuple.Tuple1;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

@Job(
        name = "TestProcessJob2",
        display = "TestProcessJob2",
        description = "测试用 Tuple1<DataStream<Long>>进 DataStream<Long>出的 处理job"
)
public class TestProcessJob2 implements FlinkProcessJobBuilder<Tuple1<DataStream<Long>>, DataStream<Long>, TestProcessJob2Config> {

    @Override
    public TestProcessJob2Config configure(Map mapConfig) {
        return null;
    }

    @Override
    public DataStream<Long> build(FlinkEnvironment context, Tuple1<DataStream<Long>> dataStream, TestProcessJob2Config config, JobMetaConfig metaConfig) throws Exception {
        DataStream<Long> upstream = dataStream.getField(0);
        return upstream.map(n -> n * 4);
    }
}
