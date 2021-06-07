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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReflectUtil {

    private ReflectUtil() {
        // forbid init instance
    }

    /**
     * 获得对象中所有字段
     */
    public static List<Field> listFields(Object bean, boolean withSuperClassFields) {
        if (bean == null) {
            return new ArrayList<>();
        }
        return listFields(bean.getClass(), withSuperClassFields);
    }

    /**
     * 获得类中所有字段
     */
    public static List<Field> listFields(Class<?> beanClass, boolean withSuperClassFields) {
        if (beanClass == null) {
            return new ArrayList<>();
        }

        List<Field> list = new ArrayList<>();

        Class<?> superClass = beanClass.getSuperclass();
        if (withSuperClassFields && superClass != null) {
            list.addAll(listFields(superClass, true));
        }

        Field[] declaredFields = beanClass.getDeclaredFields();
        Collections.addAll(list, declaredFields);
        return list;
    }

    public static Object constructObject(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception ignore) {
            return null;
        }
    }
}
