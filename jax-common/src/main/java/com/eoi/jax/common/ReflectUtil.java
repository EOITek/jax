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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

public class ReflectUtil {

    // Utility classes should not have public constructors
    private ReflectUtil() {
    }

    /**
     * begin from class `from`, scan the inheritance up tree, get target annotation type, extract and reduce to final result
     *
     * @param from                 from class
     * @param targetAnnotationType target annotation type
     * @param extractFunc          convert the instance of annotation type into result type T: annotation type, R: result type
     * @param mergeFunc            merge/reduce two result type into one result take result instance of current class as 1st parameter take result instance of aggregated as 2nd
     *                             parameter
     * @param <T>                  annotation type
     * @param <R>                  result type
     * @return result type instance
     */
    public static <T, R> R getAnnotationOfInheritanceTree(
            Type from,
            Class<T> targetAnnotationType,
            Function<T, R> extractFunc,
            BinaryOperator<R> mergeFunc) {
        return getAnnotationOfInheritanceTree(from, targetAnnotationType, null, extractFunc, mergeFunc);
    }

    private static <T, R> R getAnnotationOfInheritanceTree(
            Type from,
            Class<T> targetAnnotationType,
            R aggregatedAnn,
            Function<T, R> extractFunc,
            BinaryOperator<R> mergeFunc) {
        if (from == null) {
            throw new IllegalArgumentException();
        }
        Class rc = getRawType(from);
        R newAggregatedAnn;
        Annotation parameterAnn = rc.getAnnotation(targetAnnotationType);
        if (parameterAnn != null) {
            T cur = (T) parameterAnn;
            R extracted = extractFunc.apply(cur);
            newAggregatedAnn = mergeFunc.apply(extracted, aggregatedAnn);
        } else {
            newAggregatedAnn = aggregatedAnn;
        }
        Class superClass = rc.getSuperclass();
        if (superClass == null) {
            return newAggregatedAnn;
        } else {
            return getAnnotationOfInheritanceTree(superClass, targetAnnotationType, newAggregatedAnn, extractFunc, mergeFunc);
        }
    }

    /**
     * get fields from `from` type and filter by predicate inheritance tree will be scanned if enableInheritance is true only got public field if mustBePublic is true
     *
     * @param from              the current class
     * @param predicate         filter for scanned field
     * @param enableInheritance if need scan inheritance tree
     * @param mustBePublic      if only return public fields
     * @return the list of field match the condition
     */
    public static List<Field> getFieldsWithPredicate(Type from, Predicate<Field> predicate, boolean enableInheritance, boolean mustBePublic) {
        Class rc = getRawType(from);
        List<Field> candidates = new ArrayList<>();
        Field[] fields = rc.getDeclaredFields();
        for (Field field : fields) {
            if (mustBePublic && (field.getModifiers() & Modifier.PUBLIC) == 0) {
                continue;
            }
            if (predicate != null && !predicate.test(field)) {
                continue;
            }
            candidates.add(field);
        }
        // 如果不需要查找继承树，那么可以直接用getDeclaredFields获取所有的public,private,protected的fields
        if (!enableInheritance) {
            return candidates;
        }

        // 到这里既要遍历继承树，又要非public的，就需要递归查找了
        Class superClass = rc.getSuperclass();
        if (superClass != null) {
            candidates.addAll(getFieldsWithPredicate(superClass, predicate, enableInheritance, mustBePublic));
        }
        return candidates;
    }


    public static Class getRawType(Type from) {
        Class rc = null;
        if (from instanceof ParameterizedType) {
            rc = (Class) ((ParameterizedType) from).getRawType();
        } else if (from instanceof Class) {
            rc = (Class) from;
        } else {
            throw new IllegalArgumentException();
        }
        return rc;
    }

    /**
     * 从from开始递归遍历继承树，找到泛型基类to的真实参数类型。
     *
     * @param from 从这个子类开始
     * @param to   返回这个基类的参数类型
     * @return 键值对表示的参数类型，key是声明的占位符名字，value是类型
     */
    public static Map<String, Type> scanParameterizedType(Type from, Class to) {
        Map<Type, Map<String, Type>> v = visit(from, null);
        return v.get(to);
    }

    private static Map<Type, Map<String, Type>> visit(Type from, Map<String, Type> popgration) {
        Map<Type, Map<String, Type>> result = new LinkedHashMap<>();
        if (from instanceof Class) {
            Class f = (Class) from;
            Type[] interfaces = f.getGenericInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                result.putAll(visit(interfaces[i], popgration));
            }
            Type superClass = f.getGenericSuperclass();
            if (superClass != null) {
                result.putAll(visit(superClass, popgration));
            }
        } else if (from instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) from;
            Type[] actualTypes = pt.getActualTypeArguments();
            Class rawClass = (Class) pt.getRawType();
            TypeVariable[] typeVariables = rawClass.getTypeParameters();
            Map<String, Type> map = new LinkedHashMap<>();
            for (int i = 0; i < typeVariables.length; i++) {
                String name = typeVariables[i].getName();
                if (!(actualTypes[i] instanceof TypeVariable)) {
                    map.put(name, actualTypes[i]);
                } else {
                    TypeVariable at = (TypeVariable) actualTypes[i];
                    if (popgration != null && popgration.containsKey(at.getName())) {
                        map.put(name, popgration.get(at.getName()));
                    }
                }
            }
            result.put(rawClass, map);
            result.putAll(visit(rawClass, map));
        }
        return result;
    }

    /**
     * check if type childClass is inherited from superClass both of superClass and childClass will be erased if is generic type
     *
     * @param superClass super Class
     * @param childClass child class
     * @return true or false
     */
    public static boolean isAssignableBetween(Type superClass, Type childClass) {
        if (superClass == null || childClass == null) {
            return false;
        }

        Class theSuper = null;
        Class theChild = null;
        if (superClass instanceof Class) {
            theSuper = (Class) superClass;
        } else if (superClass instanceof ParameterizedType) {
            theSuper = (Class) ((ParameterizedType) superClass).getRawType();
        }

        if (childClass instanceof Class) {
            theChild = (Class) childClass;
        } else if (childClass instanceof ParameterizedType) {
            theChild = (Class) ((ParameterizedType) childClass).getRawType();
        }

        if (theSuper == null || theChild == null) {
            return false;
        }

        return theSuper.isAssignableFrom(theChild);
    }
}
