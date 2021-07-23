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
import org.apache.flink.api.common.functions.FilterFunction;
import org.joda.time.DateTime;

import java.util.Map;

public class EventFilter implements FilterFunction<Map<String, Object>> {
    private String timestampFieldName;
    private TimestampConvertUtil convertUtil;

    public EventFilter(String timestampFieldName, TimestampConvertUtil convertUtil) {
        this.timestampFieldName = timestampFieldName;
        this.convertUtil = convertUtil;
    }

    @Override
    public boolean filter(Map<String, Object> value) {
        DateTime dateTime = convertUtil.parseDateTimeFromSource(value.get(this.timestampFieldName));
        return dateTime != null;
    }
}
