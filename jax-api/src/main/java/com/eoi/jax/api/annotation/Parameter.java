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

package com.eoi.jax.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于在配置类字段上标注字段的元数据信息 有些元数据信息关注数据本身，有些关注前端展示
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Parameter {

    String name() default ""; // 字段名，表示字段在最终json中的key

    String label() default ""; // 字段显示标签，表示字段在表单中显示的label

    String description() default ""; // 字段显示的描述

    String placeholder() default ""; // 字段作为文本域显示时的placeholder

    String[] candidates() default {}; // 字段的可选值，如果是数字类型，前端负责转化类型

    boolean optional() default false; // 字段是否选填

    String defaultValue() default ""; // 字段默认值

    InputType inputType() default InputType.TEXT; // 字段在前端显示的input type，只针对文本域区分text和password

    String range() default ""; // 数字类型的字段设置可填的有效范围

    String regex() default ""; // 文本类型的字段设置验证正则，javascript正则表达式

    int order() default 0; //排序顺序，由小到大正序排列，数字越大排序越后

    String requireCondition() default ""; //是否必填的条件表达式

    String availableCondition() default ""; //是否可见的条件表达式
}
