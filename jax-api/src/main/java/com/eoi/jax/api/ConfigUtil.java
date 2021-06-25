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

package com.eoi.jax.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ConfigUtil {
    private ConfigUtil() {

    }

    public static void checkIfNull(Object object, String fieldName) throws JobConfigValidationException {
        if (object == null) {
            throw new JobConfigValidationException(String.format("%s must not be null", fieldName));
        }
    }

    public static void checkIfNullOrEmpty(String literal, String fieldName) throws JobConfigValidationException {
        if (literal == null || literal.length() == 0) {
            throw new JobConfigValidationException(String.format("%s must not be null or empty string", fieldName));
        }
    }

    public static void checkIfNullOrEmpty(List list, String fieldName) throws JobConfigValidationException {
        if (list == null || list.isEmpty()) {
            throw new JobConfigValidationException(String.format("%s must not be null or 0 length", fieldName));
        }
    }

    public static void putIfNotNullOrEmpty(Map<String, String> map, String key, String value) {
        if (value != null && value.length() > 0) {
            map.put(key, value);
        }
    }

    public static List<String> toStringListIfNeedSplit(Object source, String delimiters) {
        List<String> result = new ArrayList<>();
        if (source == null) {
            return result;
        }
        if (source instanceof List) {
            for (Object item: (List) source) {
                result.add(item.toString());
            }
        } else if (source instanceof String) {
            StringTokenizer st = new StringTokenizer(((String) source), delimiters);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                token = token.trim();
                if (token.length() > 0) {
                    result.add(token);
                }
            }
        }
        return result;
    }

    public static <T> List<T> duplicateAsList(T elem, int count) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(elem);
        }
        return list;
    }
}
