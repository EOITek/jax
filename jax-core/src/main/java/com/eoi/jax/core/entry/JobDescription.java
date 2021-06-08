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

package com.eoi.jax.core.entry;

import com.eoi.jax.common.JsonUtil;

import java.util.Map;

public class JobDescription {

    private String entry;
    private String id;
    private Map<String, Object> config;
    private Map<String, Object> opts;

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public Map<String, Object> getOpts() {
        return opts;
    }

    public void setOpts(Map<String, Object> opts) {
        this.opts = opts;
    }

    @Override
    public String toString() {
        try {
            return String.format("id: %s, entry: %s, config: %s", id, entry, JsonUtil.encode(config));
        } catch (Exception ex) {
            return String.format("id: %s, entry: %s, config: %s", id, entry, ex.getMessage());
        }
    }
}
