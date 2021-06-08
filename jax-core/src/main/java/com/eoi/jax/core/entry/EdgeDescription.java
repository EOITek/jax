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

package com.eoi.jax.core.entry;

import com.eoi.jax.api.tuple.Tuple2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EdgeDescription {

    private String from;
    private String to;
    private Integer toSlot;
    private Integer fromSlot;

    private static final Pattern indexPattern = Pattern.compile("(.+)\\[(\\d+)\\]$");

    public Integer getToSlot() {
        return toSlot;
    }

    public void setToSlot(Integer toSlot) {
        this.toSlot = toSlot;
    }

    public Integer getFromSlot() {
        return fromSlot;
    }

    public void setFromSlot(Integer fromSlot) {
        this.fromSlot = fromSlot;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        if (from != null && !from.isEmpty()) {
            Tuple2<String, Integer> tuple2 = split(from);
            if (tuple2 != null) {
                this.from = tuple2.f0;
                this.fromSlot = tuple2.f1;
            } else {
                this.from = from;
            }
        } else {
            this.from = from;
        }
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        if (to != null && !to.isEmpty()) {
            Tuple2<String, Integer> tuple2 = split(to);
            if (tuple2 != null) {
                this.to = tuple2.f0;
                this.toSlot = tuple2.f1;
            } else {
                this.to = to;
            }
        } else {
            this.to = to;
        }
    }

    public static Tuple2<String, Integer> split(String source) {
        Matcher matcher = indexPattern.matcher(source);
        if (matcher.find()) {
            String number = matcher.group(2);
            try {
                return Tuple2.of(matcher.group(1), Integer.valueOf(number));
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }
}
