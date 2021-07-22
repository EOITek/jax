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

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.HashMap;
import java.util.Map;

public class KeyValueParseFunction extends ProcessFunction<Map<String, Object>, Map<String, Object>> {
    private final KeyValueParseJobConfig config;
    private final OutputTag<Map<String, Object>> errorTag;
    private transient boolean excludeKeysEnable;
    private transient boolean includeKeysEnable;
    private transient boolean outputFieldEnable;

    public KeyValueParseFunction(KeyValueParseJobConfig config, OutputTag<Map<String, Object>> errorTag) {
        this.config = config;
        this.errorTag = errorTag;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        excludeKeysEnable = config.getExcludeKeys() != null && !config.getExcludeKeys().isEmpty();
        includeKeysEnable = config.getIncludeKeys() != null && !config.getIncludeKeys().isEmpty();
        outputFieldEnable = config.getOutputField() != null && !config.getOutputField().isEmpty();
    }

    @Override
    public void processElement(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
        Object obj = value.get(config.getSourceField());
        String str = obj == null ? null : obj.toString();
        boolean success = true;
        if (str != null && !str.isEmpty()) {
            try {
                Map<String, String> result = new HashMap<>();
                String[] sourceArray = str.split(config.getFieldSplit());
                for (String source : sourceArray) {
                    String[] fieldArray = source.split(config.getValueSplit(), 2);
                    if (fieldArray.length != 2) {
                        continue;
                    }
                    String key = fieldArray[0];
                    String val = fieldArray[1];
                    if (Boolean.TRUE.equals(config.getTrimKey()) && key != null) {
                        key = key.trim();
                    }
                    if (Boolean.TRUE.equals(config.getTrimValue()) && val != null) {
                        val = val.trim();
                    }
                    if (key == null || key.isEmpty()
                            || (includeKeysEnable && !config.getIncludeKeys().contains(key))
                            || (excludeKeysEnable && config.getExcludeKeys().contains(key))) {
                        continue;
                    }
                    if (outputFieldEnable) {
                        result.put(key, val);
                    } else {
                        value.put(key, val);
                    }
                }
                if (outputFieldEnable) {
                    value.put(config.getOutputField(), result);
                }
                success = true;
            } catch (Exception e) {
                success = false;
            }
        }
        if (success) {
            out.collect(value);
        } else {
            ctx.output(errorTag, value);
        }
    }
}
