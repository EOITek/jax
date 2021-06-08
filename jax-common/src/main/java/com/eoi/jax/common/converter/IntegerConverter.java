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

public class IntegerConverter implements Converter {

    public static final String NAME = "integer";
    public static final String NAME2 = "int";

    public static boolean support(String targetType) {
        return NAME.equalsIgnoreCase(targetType) || NAME2.equalsIgnoreCase(targetType);
    }

    @Override
    public Integer convert(Object from) {
        if (from == null) {
            return null;
        }
        if (from instanceof Double) {
            return ((Double) from).intValue();
        }
        if (from instanceof Float) {
            return ((Float) from).intValue();
        }
        if (from instanceof Long) {
            return ((Long) from).intValue();
        }
        if (from instanceof Integer) {
            return (Integer) from;
        }
        String fromString = from.toString();
        try {
            return Integer.parseInt(fromString);
        } catch (Exception e) {
            throw new ConverterException(NAME, fromString, e);
        }
    }
}
