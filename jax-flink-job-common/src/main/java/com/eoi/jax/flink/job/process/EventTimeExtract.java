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

import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.Map;

/**
 * 告警事件时间提取
 */
public class EventTimeExtract extends BoundedOutOfOrdernessTimestampExtractor<Map<String, Object>> {
    public EventTimeExtract(Time maxOutOfOrderness) {
        super(maxOutOfOrderness);
    }

    @Override
    public long extractTimestamp(Map<String, Object> element) {
        Object time = element.get("@eventWindowTimestamp");
        if (time instanceof Long) {
            return (Long) time;
        } else {
            return Long.parseLong(time.toString());
        }
    }
}
