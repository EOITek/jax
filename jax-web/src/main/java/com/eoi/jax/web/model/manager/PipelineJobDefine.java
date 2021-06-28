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

package com.eoi.jax.web.model.manager;

import java.util.Map;

public class PipelineJobDefine {
    private String entry;
    private String id;
    private String name;
    private Map<String,Object> config;
    //新增debug参数
    private Map<String,Object> opts;

    public String getEntry() {
        return entry;
    }

    public PipelineJobDefine setEntry(String entry) {
        this.entry = entry;
        return this;
    }

    public String getId() {
        return id;
    }

    public PipelineJobDefine setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PipelineJobDefine setName(String name) {
        this.name = name;
        return this;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public PipelineJobDefine setConfig(Map<String, Object> config) {
        this.config = config;
        return this;
    }

    public Map<String, Object> getOpts() {
        return opts;
    }

    public PipelineJobDefine setOpts(Map<String, Object> opts) {
        this.opts = opts;
        return this;
    }
}
