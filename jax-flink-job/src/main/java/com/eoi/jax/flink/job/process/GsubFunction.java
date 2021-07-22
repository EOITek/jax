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
import com.eoi.jax.common.NumberFormatUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.text.NumberFormat;
import java.util.Map;
import java.util.regex.Pattern;

public class GsubFunction extends ProcessFunction<Map<String, Object>, Map<String, Object>> {
    private final GsubJobConfig config;
    private final OutputTag<Map<String, Object>> errorTag;
    private transient boolean regexReplace;
    private transient Pattern regexPattern;
    private transient NumberFormat numberFormat;

    public GsubFunction(GsubJobConfig config, OutputTag<Map<String, Object>> errorTag) {
        this.config = config;
        this.errorTag = errorTag;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        regexReplace = Boolean.TRUE.equals(config.getRegexReplace());
        if (regexReplace) {
            regexPattern = Pattern.compile(config.getReplaceMatch());
        }
        numberFormat = NumberFormatUtil.prettyDoubleFormat();
    }

    @Override
    public void processElement(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
        Object obj = value.get(config.getSourceField());
        String str = null;
        if (obj instanceof Double) {
            str = numberFormat.format(obj);
        } else if (obj != null) {
            str = obj.toString();
        }
        if (str == null || str.isEmpty()) {
            out.collect(value);
            return;
        }
        boolean success = true;
        try {
            String result = null;
            if (regexReplace) {
                result = regexPattern.matcher(str).replaceAll(config.getReplaceValue());
            } else {
                result = str.replace(config.getReplaceMatch(), config.getReplaceValue());
            }
            if (StrUtil.isNotBlank(config.getOutputField())) {
                value.put(config.getOutputField(), result);
            } else {
                value.put(config.getSourceField(), result);
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
