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

package com.eoi.jax.api.annotation;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * FlatStringMap代表只有一层的Map&lt;String, String&gt;, 用于特殊承载一些不定参数数量的配置项
 */
public class FlatStringMap extends LinkedHashMap<String, String> {

    public static FlatStringMap fromMap(Map<String, ?> config) {
        if (config == null) {
            return null;
        }
        FlatStringMap flatStringMap = new FlatStringMap();
        for (Map.Entry<String, ?> entry : config.entrySet()) {
            if (entry.getValue() instanceof String || entry.getValue() instanceof CharSequence) {
                flatStringMap.put(entry.getKey(), ((CharSequence) entry.getValue()).toString());
            }
        }
        return flatStringMap;
    }
}
