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

import java.math.BigDecimal;

public class FloatConverter implements Converter {

    public static final String NAME = "float";

    public static boolean support(String targetType) {
        return NAME.equalsIgnoreCase(targetType);
    }

    @Override
    public Float convert(Object from, ConvertOption option) {
        Float value = convert(from);
        if (value == null || option == null || !option.rounding()) {
            return value;
        }
        return BigDecimal.valueOf(value)
                .setScale(option.roundingScale(), option.roundingMode())
                .floatValue();
    }

    @Override
    public Float convert(Object from) {
        if (from == null) {
            return null;
        }
        if (from instanceof Double) {
            return Float.valueOf(String.valueOf(from));
        }
        if (from instanceof Float) {
            return (Float) from;
        }
        if (from instanceof Long) {
            return Float.valueOf(String.valueOf(from));
        }
        if (from instanceof Integer) {
            return Float.valueOf(String.valueOf(from));
        }
        String fromString = from.toString();
        try {
            return Float.parseFloat(fromString);
        } catch (Exception e) {
            throw new ConverterException(NAME, fromString, e);
        }
    }
}
