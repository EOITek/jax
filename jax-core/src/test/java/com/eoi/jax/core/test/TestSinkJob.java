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
import com.eoi.jax.api.FlinkSinkJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;

import java.util.Map;

public class TestSinkJob implements FlinkSinkJobBuilder<DataStream<Long>, Object> {

    @Override
    public void build(FlinkEnvironment context, DataStream<Long> dataStream, Object config, JobMetaConfig metaConfig) throws Exception {
        dataStream.addSink(new PrintSinkFunction());
    }

    @Override
    public Object configure(Map<String, Object> mapConfig) {
        return null;
    }
}
