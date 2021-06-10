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
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.HashMap;
import java.util.Map;

public class TestProcessJob0 implements FlinkProcessJobBuilder<DataStream<Long>, DataStream<Map<String, Object>>, EmptyConfig> {
    @Override
    public DataStream<Map<String, Object>> build(
            FlinkEnvironment context, DataStream<Long> longDataStream, EmptyConfig config, JobMetaConfig metaConfig)
            throws Exception {
        return longDataStream.map(f -> {
            Map<String, Object> b = new HashMap<>();
            b.put("index", f);
            return b;
        }).returns(new TypeHint<Map<String, Object>>() { });
    }

    @Override
    public EmptyConfig configure(Map<String, Object> mapConfig) throws Exception {
        return null;
    }
}
