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

package com.eoi.jax.flink.job.common;

import org.apache.flink.api.java.functions.KeySelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 从map中生成keyBy
 * 根据传入的key
 * 如果出现异常，则可以设置默认的结果
 */
public class KeySelectorForMap implements KeySelector<Map<String, Object>, String> {

    private final List<String> keys;
    private final String defaultValue;
    private final String defaultEmptyValue;
    private final String splitter;

    public KeySelectorForMap(List<String> keys) {
        this(keys, "","","");
    }

    public KeySelectorForMap(List<String> keys, String defaultEmptyValue, String splitter, String valueWhenException) {
        this.keys = keys;
        this.defaultValue = valueWhenException;
        this.defaultEmptyValue = defaultEmptyValue;
        this.splitter = splitter;
    }

    @Override
    public String getKey(Map<String, Object> value) throws Exception {
        if (value == null || this.keys == null || this.keys.isEmpty()) {
            return this.defaultValue;
        }

        List<String> keyList = new ArrayList<>();
        for (String key: this.keys) {
            Object val = value.get(key);
            if (val == null || val.toString().length() == 0) {
                keyList.add(defaultEmptyValue);
            } else {
                keyList.add(val.toString());
            }
        }

        return String.join(this.splitter, keyList);
    }
}
