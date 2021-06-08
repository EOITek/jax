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

public class StringConverter implements Converter {

    public static final String NAME = "string";

    public static boolean support(String targetType) {
        return NAME.equalsIgnoreCase(targetType);
    }

    @Override
    public String convert(Object from) {
        if (from == null) {
            return null;
        }
        if (from instanceof Integer) {
            int v = (Integer) from;
            return String.valueOf(v);
        } else if (from instanceof Long) {
            long v = (Long) from;
            return String.valueOf(v);
        } else if (from instanceof Float) {
            float v = (Float) from;
            //禁止科学计数法输出
            return new BigDecimal(String.valueOf(v)).stripTrailingZeros().toPlainString();
        } else if (from instanceof Double) {
            double v = (Double) from;
            //禁止科学计数法输出
            return new BigDecimal(String.valueOf(v)).stripTrailingZeros().toPlainString();
        } else if (from instanceof String) {
            return (String) from;
        }
        return from.toString();
    }
}
