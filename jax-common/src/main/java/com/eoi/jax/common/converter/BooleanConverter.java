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

import cn.hutool.core.util.StrUtil;

public class BooleanConverter implements Converter {

    public static final String NAME = "boolean";
    public static final String NAME2 = "bool";

    public static boolean support(String targetType) {
        return NAME.equalsIgnoreCase(targetType) || NAME2.equalsIgnoreCase(targetType);
    }

    @Override
    public Boolean convert(Object from) {
        if (from == null) {
            return null;
        }
        if (from instanceof Boolean) {
            return (Boolean) from;
        }

        String fromString = from.toString().toLowerCase();
        if (StrUtil.isEmpty(fromString)) {
            return null;
        } else if ("true".equals(fromString) || "yes".equals(fromString) || "1".equals(fromString)) {
            return true;
        } else if ("false".equals(fromString) || "no".equals(fromString) || "0".equals(fromString)) {
            return false;
        } else {
            throw new ConverterException(NAME, fromString);
        }
    }
}
