package com.eoi.jax.web.model.cluster.config;

import lombok.Data;

@Data
public class ConfigDescription {
    private String name;
//    private Object value;

    private String type;
    private String label;
    private String defaultValue;
    private Boolean required;
    private String description;
    private String placeholder;
    private String group;
    private Integer displayPosition;
    private Boolean hidden;


    public static ConfigDescription fromDef(String fieldName, ConfigDef annotation) {
        if(null == annotation) {
            return null;
        }
        ConfigDescription desc = new ConfigDescription();
        desc.setName(fieldName);
        desc.setType(annotation.type().name());
        String label = annotation.label();
        if(null == label && label.isEmpty()){
            label = fieldName;
        }
        desc.setLabel(label);
        desc.setDefaultValue(annotation.defaultValue());
        desc.setRequired(annotation.required());
        desc.setDescription(annotation.description());
        desc.setPlaceholder(annotation.placeholder());
        desc.setGroup(annotation.group());
        desc.setDisplayPosition(annotation.displayPosition());
        desc.setHidden(annotation.hidden());
        return desc;
    }


}
