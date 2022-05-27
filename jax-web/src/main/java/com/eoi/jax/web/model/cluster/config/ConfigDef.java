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

package com.eoi.jax.web.model.cluster.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigDef {

    public enum Type {
        BOOL("BOOL"),
        LONG("LONG"),
        DOUBLE("DOUBLE"),
        STRING("STRING"),
        TEXT("TEXT"),
        LIST("LIST"),
        MAP("MAP"),
        ENUM_CHOOSER("ENUM_CHOOSER"),

        ;

        public final transient String code;

        Type(String code) {
            this.code = code;
        }

        public boolean isEqual(String code) {
            return this.code.equals(code);
        }

    }

    Type type() default Type.STRING;

    String label() default ""; // 字段显示标签，表示字段在表单中显示的label

    String defaultValue() default "";

    boolean required() default true;

    String description() default "";

    String placeholder() default "";

    String group() default "";

    int displayPosition() default 0;

    boolean hidden() default false;

    

}
