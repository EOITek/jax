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

package com.eoi.jax.common.converter;

public class LongConverter implements Converter {

    public static final String NAME = "long";

    public static boolean support(String targetType) {
        return NAME.equalsIgnoreCase(targetType);
    }

    @Override
    public Long convert(Object from) {
        if (from == null) {
            return null;
        }
        if (from instanceof Double) {
            return ((Double) from).longValue();
        }
        if (from instanceof Float) {
            return ((Float) from).longValue();
        }
        if (from instanceof Long) {
            return (Long) from;
        }
        if (from instanceof Integer) {
            return ((Integer) from).longValue();
        }
        String fromString = from.toString();
        try {
            return Long.parseLong(fromString);
        } catch (Exception e) {
            throw new ConverterException(NAME, fromString, e);
        }
    }
}
