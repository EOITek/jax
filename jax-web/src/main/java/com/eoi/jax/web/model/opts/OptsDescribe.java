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

package com.eoi.jax.web.model.opts;

public class OptsDescribe {
    private String name;
    private String type;
    private Object value;
    private String description;
    private Boolean required;

    public String getName() {
        return name;
    }

    public OptsDescribe setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public OptsDescribe setType(String type) {
        this.type = type;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public OptsDescribe setValue(Object value) {
        this.value = value;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public OptsDescribe setDescription(String description) {
        this.description = description;
        return this;
    }

    public Boolean getRequired() {
        return required;
    }

    public OptsDescribe setRequired(Boolean required) {
        this.required = required;
        return this;
    }
}
