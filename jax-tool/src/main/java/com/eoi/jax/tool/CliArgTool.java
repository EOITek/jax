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

package com.eoi.jax.tool;

import cn.hutool.core.util.NumberUtil;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class CliArgTool extends LinkedHashMap<String, String> {
    public static final String NO_VALUE_KEY = "__NO_VALUE_KEY";
    public static final String DEFAULT_UNDEFINED = "<undefined>";

    private CliArgTool() {
    }

    private CliArgTool(int initialCapacity) {
        super(initialCapacity);
    }

    private CliArgTool(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    private CliArgTool(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    private CliArgTool(Map<? extends String, ? extends String> m) {
        super(m);
    }

    public static CliArgTool fromArgs(String[] args) {
        CliArgTool map = new CliArgTool(args.length / 2);
        int i = 0;

        while (i < args.length) {
            String key;
            if (args[i].startsWith("--")) {
                key = args[i].substring(2);
            } else {
                if (!args[i].startsWith("-")) {
                    throw new IllegalArgumentException(String.format("Error parsing arguments '%s' on '%s'. Please prefix keys with -- or -.", Arrays.toString(args), args[i]));
                }
                key = args[i].substring(1);
            }

            if (key.isEmpty()) {
                throw new IllegalArgumentException("The input " + Arrays.toString(args) + " contains an empty argument");
            }

            ++i;
            if (i >= args.length) {
                map.put(key, NO_VALUE_KEY);
            } else if (NumberUtil.isNumber(args[i])
                    || (!args[i].startsWith("--") && !args[i].startsWith("-"))) {
                map.put(key, args[i]);
                ++i;
            } else {
                map.put(key, NO_VALUE_KEY);
            }
        }
        return map;
    }

    public String getRequired(String key) {
        String value = get(key);
        if (value == null) {
            throw new IllegalArgumentException("Null value for required key '" + key + "'");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Empty value for required key '" + key + "'");
        }
        return value;
    }

}
