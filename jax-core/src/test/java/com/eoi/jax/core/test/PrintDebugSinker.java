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

import com.eoi.jax.api.FlinkDebugSinker;
import com.eoi.jax.api.FlinkDebugSinkerMeta;
import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.common.JsonUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.api.Table;

import java.util.Map;

public class PrintDebugSinker implements FlinkDebugSinker<PrintDebugSinkerConfig> {

    @Override
    public boolean supportDataStream() {
        return true;
    }

    @Override
    public boolean supportTable() {
        return false;
    }

    @Override
    public void sinkDataStream(FlinkEnvironment context,
                               DataStream stream,
                               PrintDebugSinkerConfig config,
                               FlinkDebugSinkerMeta meta) {
        String jobId = meta.metaConfig.getJobId();
        int slotIndex = meta.slotIndex;
        stream.addSink(new RichSinkFunction() {
            @Override
            public void invoke(Object value, Context context) throws Exception {
                System.out.println(String.format("%s[%d]: %s", jobId, slotIndex, JsonUtil.encode(value)));
            }
        });
    }

    @Override
    public void sinkTable(FlinkEnvironment context,
                          Table table,
                          PrintDebugSinkerConfig config,
                          FlinkDebugSinkerMeta meta) {
        // Not Implement
    }

    @Override
    public PrintDebugSinkerConfig configure(Map<String, Object> mapConfig) throws Throwable {
        return new PrintDebugSinkerConfig();
    }
}
