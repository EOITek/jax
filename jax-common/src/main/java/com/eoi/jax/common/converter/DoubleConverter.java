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

public class DoubleConverter implements Converter {

    public static final String NAME = "double";

    public static boolean support(String targetType) {
        return NAME.equalsIgnoreCase(targetType);
    }

    @Override
    public Double convert(Object from, ConvertOption option) {
        Double value = convert(from);
        if (value == null || option == null || !option.rounding()) {
            return value;
        }
        return BigDecimal.valueOf(value)
                .setScale(option.roundingScale(), option.roundingMode())
                .doubleValue();
    }

    @Override
    public Double convert(Object from) {
        if (from == null) {
            return null;
        }
        if (from instanceof Double) {
            return (Double) from;
        }
        if (from instanceof Float) {
            return Double.valueOf(String.valueOf(from));
        }
        if (from instanceof Long) {
            return Double.valueOf(String.valueOf(from));
        }
        if (from instanceof Integer) {
            return Double.valueOf(String.valueOf(from));
        }
        String fromString = from.toString();
        try {
            return Double.parseDouble(fromString);
        } catch (Exception e) {
            throw new ConverterException(NAME, fromString, e);
        }
    }
}
