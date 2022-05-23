package com.eoi.jax.web.model.cluster.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigDef {

    public enum Type {
        BOOLEAN("BOOL"),
        LONG("LONG"),
        DOUBLE("DOUBLE"),
        STRING("STRING"),
        LIST("LIST"),
        MAP("MAP"),
        ;

//        private final transient Object defaultValue;
//
//        Type(Object defaultValue) {
//            this.defaultValue = defaultValue;
//        }
//
//        public Object getDefault(Class variableClass) {
//            Object value;
//            if (variableClass.isEnum()) {
//                value = variableClass.getEnumConstants()[0];
//            } else if (Map.class.isAssignableFrom(variableClass)) {
//                value = Collections.emptyMap();
//            } else if (List.class.isAssignableFrom(variableClass)) {
//                value = Collections.emptyList();
//            } else {
//                value = defaultValue;
//            }
//            return value;
//        }

        public final transient String code;

        Type(String code) {
            this.code = code;
        }

        public boolean isEqual(String code) {
            return this.code.equals(code);
        }

    }

    Type type() default Type.STRING;

    String defaultValue() default "";

    boolean required() default true;

    String description() default "";

    String group() default "";

    int displayPosition() default Integer.MAX_VALUE;

    boolean hidden() default false;

}
