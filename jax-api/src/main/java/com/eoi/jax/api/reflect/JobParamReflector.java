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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.eoi.jax.api.annotation.DataType;
import com.eoi.jax.api.annotation.FlatStringMap;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.api.annotation.Shift;
import com.eoi.jax.api.annotation.model.JobParamMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JobParamReflector {

    public void initParams(Object config) {
        setParams(config, null, (Object object, JobParamMeta meta) ->
                StrUtil.isEmpty(meta.getDefaultValue()) ? Optional.empty() : Optional.of(meta.getDefaultValue())
        );
    }

    public void configParams(Object toConfig, Map<String, Object> fromMap) {
        setParams(toConfig, fromMap, (Object object, JobParamMeta meta) -> {
            if (object instanceof Map) {
                Optional<Object> value = Optional.ofNullable(((Map) object).get(meta.getName()));
                JobParamValidator.validate(meta, value);
                return value;
            }
            return Optional.empty();
        });
    }

    private void setParams(Object toBean, Object fromObject, ParamValueProvider valueProvider) {
        List<Field> fields = FieldScanner.listFields(toBean, true);
        for (Field field : fields) {
            Shift shift = field.getAnnotation(Shift.class);
            if (shift != null) {
                Object shiftBean = cn.hutool.core.util.ReflectUtil.getFieldValue(toBean, field);
                if (shiftBean == null) {
                    shiftBean = constructConfig(TypeUtil.getClass(field));
                }
                setParams(shiftBean, fromObject, valueProvider);
                BeanUtil.setProperty(toBean, field.getName(), shiftBean);
                continue;
            }
            Parameter annotation = field.getAnnotation(Parameter.class);
            if (annotation == null) {
                continue;
            }
            JobParamMeta meta = new JobParamMeta(annotation).setField(field);
            valueProvider.get(fromObject, meta).ifPresent(value -> {
                Object fieldValue = getParamValue(TypeUtil.getClass(field), TypeUtil.getType(field), value,
                        valueProvider);
                if (fieldValue != null) {
                    BeanUtil.setProperty(toBean, field.getName(), fieldValue);
                }
            });
        }
    }

    private Object getParamValue(Class<?> fieldClass, Type fieldType, Object fromObject,
            ParamValueProvider valueProvider) {
        DataType fieldDataType = new ParamTypeAnalyzer().analyze(fieldClass);
        if (DataType.OBJECT == fieldDataType) {
            Object fieldBean = constructConfig(fieldClass);
            if (fieldBean != null) {
                setParams(fieldBean, fromObject, valueProvider);
            }
            return fieldBean;
        } else if (DataType.LIST == fieldDataType) {
            if (!(fromObject instanceof List)) {
                return null;
            }
            List fromList = (List) fromObject;
            Type actualType = TypeUtil.getTypeArgument(fieldType);
            Class<?> actualClass = TypeUtil.getClass(actualType);
            List listBean = new ArrayList();
            for (Object item : fromList) {
                Object itemObj = getParamValue(actualClass, actualType, item, valueProvider);
                if (itemObj != null) {
                    listBean.add(itemObj);
                }
            }
            return listBean;
        } else if (DataType.MAP == fieldDataType) {
            if (!(fromObject instanceof Map)) {
                return null;
            }
            Map fromMap = (Map) fromObject;
            Map mapBean = new FlatStringMap();
            for (Object key : fromMap.keySet()) {
                Object value = fromMap.get(key);
                if (key instanceof String && value instanceof String) {
                    mapBean.put(key, value);
                }
            }
            return mapBean;
        }
        ParamValueConverter converter = new ParamValueConverter(fieldDataType);
        return converter.convert(fromObject);
    }

    private Object constructConfig(Class<?> clazz) {
        Object configObj = ReflectUtil.constructObject(clazz);
        if (configObj != null) {
            initParams(configObj);
        }
        return configObj;
    }

    /**
     * 扫描目标类中所有字段及其注解
     */
    public Map<String, JobParamMeta> scanParams(Class<?> configClass) {
        return scanParams(configClass, null);
    }

    public Map<String, JobParamMeta> scanParams(Class<?> configClass, String parentAvailableCondition) {
        final Map<String, JobParamMeta> result = new LinkedHashMap<>();
        List<Field> fields = FieldScanner.listFields(configClass, true);
        for (Field field : fields) {
            Shift shift = field.getAnnotation(Shift.class);
            if (shift != null) {
                String shiftCondition;
                if (StrUtil.isNotBlank(parentAvailableCondition) && StrUtil.isNotBlank(shift.availableCondition())) {
                    shiftCondition = String.format("%s && (%s)", parentAvailableCondition, shift.availableCondition());
                } else if (StrUtil.isNotBlank(parentAvailableCondition)) {
                    shiftCondition = parentAvailableCondition;
                } else {
                    shiftCondition = shift.availableCondition();
                }
                result.putAll(scanParams(field.getType(), shiftCondition));
                continue;
            }
            Parameter annotation = field.getAnnotation(Parameter.class);
            if (annotation == null) {
                continue;
            }
            JobParamMeta meta = new JobParamMeta(annotation).setParentAvailableCondition(parentAvailableCondition)
                    .setField(field);
            getParamMeta(meta, TypeUtil.getClass(field), TypeUtil.getType(field));
            result.put(meta.getName(), meta);
        }
        return result;
    }

    private void getParamMeta(JobParamMeta meta, Class<?> fieldClass, Type fieldType) {
        DataType fieldDataType = new ParamTypeAnalyzer().analyze(fieldClass);
        if (DataType.OBJECT == fieldDataType) {
            Map<String, JobParamMeta> map = scanParams(fieldClass);
            meta.setObjectParameters(ArrayUtil.toArray(map.values(), JobParamMeta.class));
        } else if (DataType.LIST == fieldDataType) {
            Type actualType = TypeUtil.getTypeArgument(fieldType);
            Class<?> actualClass = TypeUtil.getClass(actualType);
            DataType actualDataType = new ParamTypeAnalyzer().analyze(actualClass);
            JobParamMeta actualMeta = new JobParamMeta();
            actualMeta.setType(new DataType[]{actualDataType});
            getParamMeta(actualMeta, actualClass, actualType);
            meta.setListParameter(actualMeta);
        }
    }

    @FunctionalInterface
    public interface ParamValueProvider {

        Optional<Object> get(Object object, JobParamMeta meta);
    }
}
