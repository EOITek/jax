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

import com.eoi.jax.flink.job.common.TimestampConvertUtil;
import org.apache.flink.api.common.functions.MapFunction;
import org.joda.time.DateTime;

import java.util.Map;

/**
 * 提取时间字段
 */
public class TimestampMapper implements MapFunction<Map<String, Object>, Map<String, Object>> {
    private String timestampFieldName;
    private Boolean offsetBeijing;
    private TimestampConvertUtil convertUtil;
    private static final Integer WINDOW_8H = 8 * 60 * 60 * 1000;

    public TimestampMapper(String timestampFieldName, Boolean offsetBeijing, TimestampConvertUtil convertUtil) {
        this.timestampFieldName = timestampFieldName;
        this.offsetBeijing = offsetBeijing;
        this.convertUtil = convertUtil;
    }

    @Override
    public Map<String, Object> map(Map<String, Object> value) throws Exception {
        DateTime time = convertUtil.parseDateTimeFromSource(value.get(this.timestampFieldName));

        value.put("@eventWindowTimestamp", time.getMillis());

        if (Boolean.TRUE.equals(offsetBeijing)) {
            value.put("@eventWindowTimestamp", time.getMillis() + WINDOW_8H);
        }
        return value;
    }
}
