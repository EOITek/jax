package com.eoi.jax.web.model.cluster.config;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class BeanConfigsDescription {

    private String name;
    private String beanClass;
    private Map<String, ConfigDescription> configs;

}
