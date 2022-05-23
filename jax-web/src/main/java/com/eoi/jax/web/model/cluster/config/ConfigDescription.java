package com.eoi.jax.web.model.cluster.config;

import lombok.Data;

@Data
public class ConfigDescription {
    private String name;
//    private Object value;

    private String type;
    private String defaultValue;
    private Boolean required;
    private String description;
    private String group;
    private Integer displayPosition;
    private Boolean hidden;


    public static ConfigDescription fromDef(ConfigDef annotation) {
        if(null == annotation) {
            return null;
        }
        ConfigDescription desc = new ConfigDescription();
        desc.setType(annotation.type().name());
        desc.setDefaultValue(annotation.defaultValue());
        desc.setRequired(annotation.required());
        desc.setDescription(annotation.description());
        desc.setGroup(annotation.group());
        desc.setDisplayPosition(annotation.displayPosition());
        desc.setHidden(annotation.hidden());
        return desc;
    }
}
