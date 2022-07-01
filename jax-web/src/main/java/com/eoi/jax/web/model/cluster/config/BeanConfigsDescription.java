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
