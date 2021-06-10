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
import com.eoi.jax.api.FlinkSourceJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.HashMap;
import java.util.Map;

public class TestSourceMapJob implements FlinkSourceJobBuilder<DataStream<Map<String, Object>>, EmptyConfig> {
    @Override
    public DataStream<Map<String, Object>> build(FlinkEnvironment context, EmptyConfig config, JobMetaConfig metaConfig) throws Throwable {
        return context.streamEnv.addSource(new SourceFunction<Map<String, Object>>() {
            @Override
            public void run(SourceContext<Map<String, Object>> ctx) throws Exception {
                ctx.collect(new HashMap<>());
            }

            @Override
            public void cancel() {

            }
        });
    }

    @Override
    public EmptyConfig configure(Map<String, Object> mapConfig) throws Throwable {
        return null;
    }
}
