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
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Map;

public class TranslateFunction extends ProcessFunction<Map<String, Object>, Map<String, Object>> {
    private final TranslateJobConfig config;
    private final OutputTag<Map<String, Object>> errorTag;

    public TranslateFunction(TranslateJobConfig config, OutputTag<Map<String, Object>> errorTag) {
        this.config = config;
        this.errorTag = errorTag;
    }

    @Override
    public void processElement(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
        Object obj = value.get(config.getSourceField());
        String str = obj == null ? null : obj.toString();
        if (str == null || str.isEmpty()) {
            out.collect(value);
            return;
        }
        String result = config.getDictionary().getOrDefault(str, str);
        if (StrUtil.isNotBlank(config.getOutputField())) {
            value.put(config.getOutputField(), result);
        } else {
            value.put(config.getSourceField(), result);
        }
        out.collect(value);
    }
}
