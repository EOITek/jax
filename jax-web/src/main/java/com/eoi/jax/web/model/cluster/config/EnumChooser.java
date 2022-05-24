package com.eoi.jax.web.model.cluster.config;

public @interface EnumChooser {

    String[] candidates() default {};

    String defaultValue() default "";

    boolean enableCandidatesByURL() default false;

    String url() default "";

}
