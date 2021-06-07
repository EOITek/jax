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

package com.eoi.jax.api.annotation.model;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.api.annotation.DataType;
import com.eoi.jax.api.reflect.ParamTypeAnalyzer;

import java.lang.reflect.Field;

public abstract class ParamMeta {

    private String name;
    private String label;
    private DataType[] type;
    private String description;
    private boolean optional;
    private String defaultValue;

    public ParamMeta setField(Field field) {
        if (StrUtil.isEmpty(this.name)) {
            this.name = field.getName();
        }
        if (StrUtil.isEmpty(this.label)) {
            this.label = field.getName();
        }
        if (StrUtil.isEmpty(this.description)) {
            this.description = field.getName();
        }
        if (ArrayUtil.isEmpty(type)) {
            ParamTypeAnalyzer analyzer = new ParamTypeAnalyzer();
            this.type = ArrayUtil.toArray(analyzer.analyze(field), DataType.class);
        }
        if (StrUtil.isNotEmpty(this.defaultValue)) {
            //有默认值则认为非必填，反之不成立
            this.optional = true;
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public ParamMeta setName(String name) {
        this.name = name;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public ParamMeta setLabel(String label) {
        this.label = label;
        return this;
    }

    public DataType[] getType() {
        return type;
    }

    public ParamMeta setType(DataType[] type) {
        this.type = type;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ParamMeta setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean getOptional() {
        return optional;
    }

    public ParamMeta setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public ParamMeta setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
}
