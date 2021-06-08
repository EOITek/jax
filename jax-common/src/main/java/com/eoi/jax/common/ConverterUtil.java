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

package com.eoi.jax.common;

import com.eoi.jax.common.converter.BooleanConverter;
import com.eoi.jax.common.converter.ConvertOption;
import com.eoi.jax.common.converter.Converter;
import com.eoi.jax.common.converter.ConverterException;
import com.eoi.jax.common.converter.DoubleConverter;
import com.eoi.jax.common.converter.FloatConverter;
import com.eoi.jax.common.converter.IntegerConverter;
import com.eoi.jax.common.converter.LongConverter;
import com.eoi.jax.common.converter.StringConverter;

public class ConverterUtil {

    public static Object convert(Object fromObject, String targetType) {
        return convert(fromObject, targetType, null);
    }

    public static Object convert(Object fromObject, String targetType, ConvertOption option) {
        Converter converter = converter(targetType);
        return converter.convert(fromObject, option);
    }

    public static Converter converter(String targetType) {
        if (BooleanConverter.support(targetType)) {
            return new BooleanConverter();
        } else if (DoubleConverter.support(targetType)) {
            return new DoubleConverter();
        } else if (FloatConverter.support(targetType)) {
            return new FloatConverter();
        } else if (LongConverter.support(targetType)) {
            return new LongConverter();
        } else if (IntegerConverter.support(targetType)) {
            return new IntegerConverter();
        } else if (StringConverter.support(targetType)) {
            return new StringConverter();
        }
        throw new ConverterException("invalid type " + targetType);
    }
}
