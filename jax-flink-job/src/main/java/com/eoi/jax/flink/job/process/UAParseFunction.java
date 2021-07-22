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
import com.eoi.jax.common.uaparser.CachingParser;
import com.eoi.jax.common.uaparser.Client;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.HashMap;
import java.util.Map;

public class UAParseFunction extends ProcessFunction<Map<String, Object>, Map<String, Object>> {
    private final UAParseJobConfig config;
    private final OutputTag<Map<String, Object>> errorTag;
    private transient CachingParser parser;

    public UAParseFunction(UAParseJobConfig config, OutputTag<Map<String, Object>> errorTag) {
        this.config = config;
        this.errorTag = errorTag;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.parser = new CachingParser();
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
            Client client = parser.parse(str);
            Map<String, String> map = toMap(client);
            if (StrUtil.isNotBlank(config.getOutputField())) {
                value.put(config.getOutputField(), map);
            } else {
                value.putAll(map);
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

    private Map<String, String> toMap(Client ua) {
        Map<String, String> event = new HashMap<>();
        if (ua == null) {
            return event;
        }
        event.put("ua_family", ua.userAgent.family);
        event.put("ua_major", ua.userAgent.major);
        event.put("ua_minor", ua.userAgent.minor);
        event.put("ua_os_family", ua.os.family);
        event.put("ua_os_major", ua.os.major);
        event.put("ua_os_minor", ua.os.minor);
        event.put("ua_device_family", ua.device.family);
        return event;
    }
}
