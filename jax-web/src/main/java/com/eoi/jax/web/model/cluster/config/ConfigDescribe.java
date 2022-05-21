package com.eoi.jax.web.model.cluster.config;

import lombok.Data;

@Data
public class ConfigDescribe {
    private String name;
    private Object value;

    private String type;
    private String defaultValue;
    private Boolean required;
    private String description;
    private String group;
    private Integer displayPosition;
    private Boolean hidden;


}
