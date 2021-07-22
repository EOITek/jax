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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.common.JsonUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParseJobFunction extends ProcessFunction<Map<String, Object>, Map<String, Object>> {
    private final JsonParseJobConfig config;
    private final OutputTag<Map<String, Object>> errorTag;
    private transient boolean flatMap;

    public JsonParseJobFunction(JsonParseJobConfig config, OutputTag<Map<String, Object>> errorTag) {
        this.config = config;
        this.errorTag = errorTag;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        flatMap = config.getFlatDelimiter() != null && !config.getFlatDelimiter().isEmpty();
    }

    @Override
    public void processElement(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
        Object obj = value.get(config.getSourceField());
        String str = obj == null ? null : obj.toString();
        if (str == null || str.isEmpty()) {
            out.collect(value);
            return;
        }
        boolean success = true;
        try {
            Map<String, Object> result;
            if (StrUtil.isNotBlank(config.getOutputField())) {
                result = new HashMap<>();
                value.put(config.getOutputField(), result);
            } else {
                result = value;
            }
            if (flatMap) {
                flattenMap(result, JsonUtil.decode2Map(str), null, config.getFlatDelimiter());
            } else {
                result.putAll(JsonUtil.decode2Map(str));
            }
            success = true;
        } catch (Exception e) {
            success = false;
        }
        if (success) {
            out.collect(value);
        } else {
            ctx.output(errorTag, value);
        }
    }

    public void flattenMap(Map<String, Object> result, Object from, String prefix, String delimiter) {
        if (from instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) from;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String name = prefix == null ? key : prefix + delimiter + key;
                flattenMap(result, value, name, delimiter);
            }
        } else if (from instanceof List) {
            List list = (List) from;
            int size = list.size();
            for (int i = 0; i < size; i++) {
                String name = prefix == null ? String.format("[%d]", i) :  String.format("%s[%d]", prefix, i);
                flattenMap(result, list.get(i), name, delimiter);
            }
        } else {
            result.put(prefix, from);
        }
    }
}
