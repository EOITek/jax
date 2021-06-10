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

package com.eoi.jax.common.test.reflect;

import com.eoi.jax.common.ReflectUtil;
import com.eoi.jax.common.test.reflect.nested.ClassFlag;
import com.eoi.jax.common.test.reflect.nested.Flag;
import com.eoi.jax.common.test.reflect.nested.FourthClass;
import com.eoi.jax.common.test.reflect.nested.FourthClass2;
import com.eoi.jax.common.test.reflect.nested.FourthClass3;
import com.eoi.jax.common.test.reflect.nested.FourthClass4;
import com.eoi.jax.common.test.reflect.nested.SecondInterface;
import com.eoi.jax.common.test.reflect.nested.T0;
import com.eoi.jax.common.test.reflect.nested.T1;
import com.eoi.jax.common.test.reflect.nested.T2;
import com.eoi.jax.common.test.reflect.nested.TopInterface;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReflectUtilTest {

    @Test
    public void test_getAnnByClassTree() {
        List<ClassFlag> parameters = ReflectUtil.getAnnotationOfInheritanceTree(
                FourthClass3.class,
                ClassFlag.class,
                Arrays::asList,
                (cur, agg) -> {
                    if (agg == null || cur == null) {
                        return cur;
                    }
                    List<ClassFlag> arrayList = new ArrayList<>();
                    arrayList.addAll(agg);
                    arrayList.addAll(cur);
                    return arrayList;
                });
        Assert.assertEquals(3, parameters.size());
    }

    @Test
    public void test_scanParameterizedType_1() {
        Map<String, Type> m = ReflectUtil.scanParameterizedType(TestSubClass2.class, ParameterizedInterface.class);
        Assert.assertEquals(3, m.size());
        Assert.assertEquals(String.class, m.get("T1"));
        Assert.assertEquals(Object.class, m.get("T2"));
        Assert.assertEquals(Long.class, m.get("T3"));
    }

    @Test
    public void test_scanParameterizedType_2() {
        Map<String, Type> m = ReflectUtil.scanParameterizedType(TestSubClass1.class, CommonInterface.class);
        Assert.assertNull(m);
    }

    @Test
    public void test_Nested_1() {
        Map<String, Type> m = ReflectUtil.scanParameterizedType(FourthClass.class, TopInterface.class);
        Assert.assertEquals(3, m.size());
        Assert.assertEquals(T0.class, m.get("T1"));
        Assert.assertTrue(m.get("T2") instanceof ParameterizedType);
        Assert.assertEquals(T1.class, ((ParameterizedType) m.get("T2")).getRawType());
        Assert.assertEquals(T2.class, m.get("T3"));
        m = ReflectUtil.scanParameterizedType(FourthClass2.class, TopInterface.class);
        Assert.assertEquals(3, m.size());
        Assert.assertEquals(T0.class, m.get("T1"));
        Assert.assertEquals(T1.class, m.get("T2"));
        Assert.assertEquals(T2.class, m.get("T3"));
        m = ReflectUtil.scanParameterizedType(FourthClass3.class, TopInterface.class);
        Assert.assertEquals(3, m.size());
        Assert.assertEquals(T0.class, m.get("T1"));
        Assert.assertEquals(T1.class, m.get("T2"));
        Assert.assertEquals(T2.class, m.get("T3"));
        m = ReflectUtil.scanParameterizedType(FourthClass4.class, TopInterface.class);
        Assert.assertEquals(3, m.size());
        Assert.assertEquals(T0.class, m.get("T1"));
        Assert.assertTrue(m.get("T2") instanceof ParameterizedType);
        Assert.assertEquals(T1.class, ((ParameterizedType) m.get("T2")).getRawType());
        Assert.assertEquals(T2.class, m.get("T3"));
        m = ReflectUtil.scanParameterizedType(FourthClass.class, SecondInterface.class);
        Assert.assertEquals(2, m.size());
        Assert.assertTrue(m.get("E") instanceof ParameterizedType);
        Assert.assertEquals(T1.class, ((ParameterizedType) m.get("E")).getRawType());
        Assert.assertEquals(T2.class, m.get("F"));
    }

    @Test
    public void test_getFieldsWithPredicate_1() {
        List<Field> fields = ReflectUtil.getFieldsWithPredicate(FourthClass3.class, null, false, true);
        Assert.assertEquals(1, fields.size());
        Assert.assertEquals("public_fourthClass2", fields.get(0).getName());

        fields = ReflectUtil.getFieldsWithPredicate(FourthClass3.class, null, false, false);
        List<String> f1s = fields.stream().map(Field::getName).collect(Collectors.toList());
        Assert.assertTrue(f1s.contains("public_fourthClass2"));
        Assert.assertTrue(f1s.contains("private_fourthClass"));

        fields = ReflectUtil.getFieldsWithPredicate(FourthClass3.class, f -> f.getAnnotation(Flag.class) != null, false, true);
        f1s = fields.stream().map(Field::getName).collect(Collectors.toList());
        Assert.assertEquals(1, fields.size());
        Assert.assertEquals("public_fourthClass2", fields.get(0).getName());

        fields = ReflectUtil.getFieldsWithPredicate(FourthClass3.class, f -> f.getAnnotation(Flag.class) == null, false, true);
        Assert.assertEquals(0, fields.size());
    }

    @Test
    public void test_getFieldsWithPredicate_2() {
        List<Field> fields = ReflectUtil.getFieldsWithPredicate(FourthClass3.class, null, true, true);
        List<String> f1s = fields.stream().map(Field::getName).collect(Collectors.toList());
        Assert.assertTrue(f1s.contains("public_fourthClass2"));
        Assert.assertFalse(f1s.contains("private_fourthClass"));
        Assert.assertTrue(f1s.contains("f3"));

        fields = ReflectUtil.getFieldsWithPredicate(FourthClass3.class, null, true, false);
        f1s = fields.stream().map(Field::getName).collect(Collectors.toList());
        Assert.assertTrue(f1s.contains("public_fourthClass2"));
        Assert.assertTrue(f1s.contains("private_fourthClass"));
        Assert.assertTrue(f1s.contains("f1"));
        Assert.assertTrue(f1s.contains("f2"));
        Assert.assertTrue(f1s.contains("f3"));

        fields = ReflectUtil.getFieldsWithPredicate(FourthClass3.class, f -> f.getAnnotation(Flag.class) != null, true, true);
        Assert.assertEquals(1, fields.size());
        Assert.assertEquals("public_fourthClass2", fields.get(0).getName());

        fields = ReflectUtil.getFieldsWithPredicate(FourthClass3.class, f -> f.getAnnotation(Flag.class) == null, true, true);
        Assert.assertEquals(1, fields.size());
    }
}
