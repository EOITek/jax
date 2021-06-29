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
import com.eoi.jax.common.dissect.DissectContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Map;

public class DissectFunction extends ProcessFunction<Map<String, Object>, Map<String, Object>> {
    private final DissectJobConfig config;
    private final OutputTag<Map<String, Object>> errorTag;
    private transient DissectContext context;

    public DissectFunction(DissectJobConfig config, OutputTag<Map<String, Object>> errorTag) {
        this.config = config;
        this.errorTag = errorTag;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        context =  DissectContext.parseDissectContext(config.getDissectPattern(), config.getAppendSeparator());
    }

    @Override
    public void processElement(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) {
        Object obj = value.get(config.getSourceField());
        String str = obj == null ? null : obj.toString();
        boolean success = true;
        if (str != null) {
            try {
                Map<String, String> result = context.doDissect(str);
                if (StrUtil.isEmpty(config.getOutputField())) {
                    value.putAll(result);
                } else {
                    value.put(config.getOutputField(), result);
                }
                if (Boolean.TRUE.equals(config.getRemoveSourceField())) {
                    value.remove(config.getSourceField());
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
