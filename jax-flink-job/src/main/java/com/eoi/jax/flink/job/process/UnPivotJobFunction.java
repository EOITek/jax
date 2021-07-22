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

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnPivotJobFunction extends ProcessFunction<Map<String, Object>, Map<String, Object>> {

    private UnPivotJobConfig config;
    final OutputTag<Map<String, Object>> errorTag;

    public UnPivotJobFunction(UnPivotJobConfig config, OutputTag<Map<String, Object>> errorTag) {
        this.config = config;
        this.errorTag = errorTag;
    }

    @Override
    public void processElement(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
        Object targetValue = value.get(config.getSourceField());
        if (targetValue == null) {
            ctx.output(errorTag, value);
        } else {
            if (targetValue instanceof String) {
                if (!targetValue.toString().isEmpty()) {
                    String[] splits = targetValue.toString().split(config.getDelimiter());
                    for (String split : splits) {
                        Map<String, Object> result = new HashMap<>(value);
                        result.put(config.getTargetField(), split);
                        out.collect(result);
                    }
                } else {
                    ctx.output(errorTag, value);
                }
            } else if (targetValue instanceof List) {
                List targetValues = (List) targetValue;
                if (!targetValues.isEmpty()) {
                    for (Object split : targetValues) {
                        Map<String, Object> result = new HashMap<>(value);
                        result.put(config.getTargetField(), split);
                        out.collect(result);
                    }
                } else {
                    ctx.output(errorTag, value);
                }
            }
        }
    }
}
