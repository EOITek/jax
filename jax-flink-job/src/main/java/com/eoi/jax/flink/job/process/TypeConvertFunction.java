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

import com.eoi.jax.common.ConverterUtil;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Map;

public class TypeConvertFunction extends ProcessFunction<Map<String, Object>, Map<String, Object>> {
    private final TypeConvertJobConfig config;
    private final OutputTag<Map<String, Object>> errorTag;

    public TypeConvertFunction(TypeConvertJobConfig config, OutputTag<Map<String, Object>> errorTag) {
        this.config = config;
        this.errorTag = errorTag;
    }

    @Override
    public void processElement(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
        if (config.getFields() == null || config.getFields().isEmpty()) {
            out.collect(value);
            return;
        }
        boolean success = true;
        try {
            for (TypeConvertJobParam field : config.getFields()) {
                String key = field.getField();
                String type = field.getType();
                String output = field.getOutput();
                if (!value.containsKey(key)) {
                    continue;
                }
                Object from = value.get(key);
                Object converted = ConverterUtil.convert(from, type, field.convertOption());
                output = output == null || output.isEmpty() ? key : output;
                value.put(output, converted);
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
}
