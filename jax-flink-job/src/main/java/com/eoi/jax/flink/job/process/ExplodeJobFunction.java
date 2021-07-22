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
import java.util.Map;

public class ExplodeJobFunction extends ProcessFunction<Map<String, Object>, Map<String, Object>> {

    private ExplodeJobConfig config;
    final OutputTag<Map<String, Object>> errorTag;

    public ExplodeJobFunction(ExplodeJobConfig config, OutputTag<Map<String, Object>> errorTag) {
        this.config = config;
        this.errorTag = errorTag;
    }

    @Override
    public void processElement(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
        Map<String, Object> otherColumns = new HashMap<>();
        for (String key : value.keySet()) {
            if (!config.getSourceFields().contains(key)) {
                otherColumns.put(key, value.get(key));
            }
        }

        for (String sourceField : config.getSourceFields()) {
            if (value.containsKey(sourceField)) {
                Object valObj = value.get(sourceField);
                // 保留其他字段
                Map<String, Object> result = new HashMap<>(otherColumns);
                result.put(config.getTargetKeyField(), sourceField);
                result.put(config.getTargetValueField(), valObj);
                out.collect(result);
            } else {
                Map<String, Object> result = new HashMap<>(otherColumns);
                result.put(config.getTargetKeyField(), sourceField);
                result.put(config.getTargetValueField(), null);
                ctx.output(errorTag, result);
            }
        }
    }
}
