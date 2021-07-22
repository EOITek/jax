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

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

public class EnrichByBroadcastFunction extends BroadcastProcessFunction<Map<String, Object>, Map<String, Object>,Map<String, Object>> {

    public static final MapStateDescriptor<String, Map<String,Object>> enrichBroadcastDesc =
            new MapStateDescriptor<>(
                    "EnrichBroadcastState",
                    BasicTypeInfo.STRING_TYPE_INFO,
                    TypeInformation.of(new TypeHint<Map<String,Object>>() {}));

    private String key;

    public EnrichByBroadcastFunction(String key) {
        this.key = key;
    }

    @Override
    public void processElement(Map<String, Object> value,
                               ReadOnlyContext ctx,
                               Collector<Map<String, Object>> out) throws Exception {
        Object keyObj = value.getOrDefault(key, null);
        if (keyObj != null) {
            Map<String, Object> enrichMap = ctx.getBroadcastState(enrichBroadcastDesc).get(keyObj.toString());
            Map<String, Object> result = new HashMap<>(value);
            if (enrichMap != null) {
                result.putAll(enrichMap);
            }
            out.collect(result);
        } else {
            out.collect(value);
        }
    }

    @Override
    public void processBroadcastElement(Map<String, Object> value,
                                        Context ctx,
                                        Collector<Map<String, Object>> out) throws Exception {
        Object keyObj = value.getOrDefault(key, null);
        if (keyObj != null) {
            ctx.getBroadcastState(enrichBroadcastDesc).put(keyObj.toString(), value);
        }
    }
}
