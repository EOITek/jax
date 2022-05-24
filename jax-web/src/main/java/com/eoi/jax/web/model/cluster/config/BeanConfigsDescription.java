package com.eoi.jax.web.model.cluster.config;

import java.util.Map;


public class BeanConfigsDescription {

    private String name;
    private String beanClass;
    private Map<String, ConfigDescription> configs;

    public BeanConfigsDescription(String name, String beanClass, Map<String, ConfigDescription> configs) {
        this.name = name;
        this.beanClass = beanClass;
        this.configs = configs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(String beanClass) {
        this.beanClass = beanClass;
    }

    public Map<String, ConfigDescription> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, ConfigDescription> configs) {
        this.configs = configs;
    }
}
