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

package com.eoi.jax.core;

import java.util.Map;

public class BaseJobNode extends AbstractDAGNode {

    protected ClassLoader loader;
    protected String id;
    protected String entry;
    protected Map<String, Object> config;

    public ClassLoader getLoader() {
        return loader;
    }

    public BaseJobNode setLoader(ClassLoader loader) {
        this.loader = loader;
        return this;
    }

    public BaseJobNode setId(String id) {
        this.id = id;
        return this;
    }

    public String getEntry() {
        return entry;
    }

    public BaseJobNode setEntry(String entry) {
        this.entry = entry;
        return this;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public BaseJobNode setConfig(Map<String, Object> config) {
        this.config = config;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }
}
