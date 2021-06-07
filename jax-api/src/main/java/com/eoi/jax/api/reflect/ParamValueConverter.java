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

package com.eoi.jax.api.reflect;

import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.DataType;

public class ParamValueConverter {

    private ITypeConverter converter;

    public ParamValueConverter(DataType dataType) {
        switch (dataType) {
            case STRING:
                converter = new StringConverter();
                break;
            case INT:
                converter = new IntConverter();
                break;
            case LONG:
                converter = new LongConverter();
                break;
            case FLOAT:
                converter = new FloatConverter();
                break;
            case DOUBLE:
                converter = new DoubleConverter();
                break;
            case BOOL:
                converter = new BooleanConverter();
                break;
            default:
                converter = new DefaultConverter();
        }
    }

    public boolean validate(Object value) {
        return converter.validate(value);
    }

    public Object convert(Object value) {
        try {
            return converter.convert(value);
        } catch (Exception e) {
            return value;
        }
    }

    interface ITypeConverter<T> {

        default Boolean validate(Object value) {
            try {
                convert(value);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        T convert(Object value) throws JobConfigValidationException;
    }

    static class StringConverter implements ITypeConverter<String> {

        @Override
        public Boolean validate(Object value) {
            return true;
        }

        @Override
        public String convert(Object value) {
            if (value instanceof String) {
                return (String) value;
            }
            return String.valueOf(value);
        }
    }

    static class IntConverter implements ITypeConverter<Integer> {

        @Override
        public Integer convert(Object value) throws JobConfigValidationException {
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Long) {
                return ((Long) value).intValue();
            }
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (Exception ignore) {
                // do nothing
            }
            throw new JobConfigValidationException(
                    "invalid data type: expect Integer, actual " + value.getClass().getName());
        }
    }

    static class LongConverter implements ITypeConverter<Long> {

        @Override
        public Long convert(Object value) throws JobConfigValidationException {
            if (value instanceof Integer) {
                return ((Integer) value).longValue();
            } else if (value instanceof Long) {
                return (Long) value;
            }
            try {
                return Long.parseLong(String.valueOf(value));
            } catch (Exception ignore) {
                // do nothing
            }
            throw new JobConfigValidationException(
                    "invalid data type: expect Long, actual " + value.getClass().getName());
        }
    }

    static class FloatConverter implements ITypeConverter<Float> {

        @Override
        public Float convert(Object value) throws JobConfigValidationException {
            if (value instanceof Integer) {
                return ((Integer) value).floatValue();
            } else if (value instanceof Long) {
                return ((Long) value).floatValue();
            } else if (value instanceof Float) {
                return (Float) value;
            } else if (value instanceof Double) {
                return ((Double) value).floatValue();
            }
            try {
                return Float.parseFloat(String.valueOf(value));
            } catch (Exception ignore) {
                // do nothing
            }
            throw new JobConfigValidationException(
                    "invalid data type: expect Float, actual " + value.getClass().getName());
        }
    }

    static class DoubleConverter implements ITypeConverter<Double> {

        @Override
        public Double convert(Object value) throws JobConfigValidationException {
            if (value instanceof Integer) {
                return ((Integer) value).doubleValue();
            } else if (value instanceof Long) {
                return ((Long) value).doubleValue();
            } else if (value instanceof Float) {
                return ((Float) value).doubleValue();
            } else if (value instanceof Double) {
                return (Double) value;
            }
            try {
                return Double.parseDouble(String.valueOf(value));
            } catch (Exception ignore) {
                // do nothing
            }
            throw new JobConfigValidationException(
                    "invalid data type: expect Double, actual " + value.getClass().getName());
        }
    }

    static class BooleanConverter implements ITypeConverter<Boolean> {

        @Override
        public Boolean convert(Object value) throws JobConfigValidationException {
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            if ("true".equalsIgnoreCase(String.valueOf(value))) {
                return true;
            }
            if ("false".equalsIgnoreCase(String.valueOf(value))) {
                return false;
            }
            throw new JobConfigValidationException(
                    "invalid data type: expect Boolean, actual " + value.getClass().getName());
        }
    }

    static class DefaultConverter implements ITypeConverter<Object> {

        @Override
        public Object convert(Object value) throws JobConfigValidationException {
            return value;
        }
    }
}
