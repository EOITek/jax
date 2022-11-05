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


public class ConfigDescription {
    private String name;

    private String type;
    private String label;
    private String defaultValue;
    private Boolean required;
    private String description;
    private String placeholder;
    private String group;
    private Integer displayPosition;
    private Boolean hidden;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getDisplayPosition() {
        return displayPosition;
    }

    public void setDisplayPosition(Integer displayPosition) {
        this.displayPosition = displayPosition;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public static ConfigDescription fromDef(String fieldName, ConfigDef annotation) {
        if (null == annotation) {
            return null;
        }
        ConfigDescription desc = new ConfigDescription();
        desc.setName(fieldName);
        desc.setType(annotation.type().name());
        String label = annotation.label();
        if (null == label && label.isEmpty()) {
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
