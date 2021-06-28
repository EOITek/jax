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

package com.eoi.jax.web.model.pipeline;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.util.JsonUtil;

import java.util.Map;

public class JobDebugOpts {
    private Boolean enableDebug;
    private String debugEntry;
    private Map<String, Object> debugConfig;

    public Boolean getEnableDebug() {
        return enableDebug;
    }

    public JobDebugOpts setEnableDebug(Boolean enableDebug) {
        this.enableDebug = enableDebug;
        return this;
    }

    public String getDebugEntry() {
        return debugEntry;
    }

    public JobDebugOpts setDebugEntry(String debugEntry) {
        this.debugEntry = debugEntry;
        return this;
    }

    public Map<String, Object> getDebugConfig() {
        return debugConfig;
    }

    public JobDebugOpts setDebugConfig(Map<String, Object> debugConfig) {
        this.debugConfig = debugConfig;
        return this;
    }

    public JobDebugOpts fromMap(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        enableDebug = (Boolean) map.get("enableDebug");
        debugEntry = (String) map.get("debugEntry");
        debugConfig = (Map<String, Object>) map.get("debugConfig");
        return this;
    }

    public JobDebugOpts fromString(String str) {
        if (StrUtil.isEmpty(str)) {
            return null;
        }
        try {
            return JsonUtil.decode(str, JobDebugOpts.class);
        } catch (Exception e) {
            return null;
        }
    }
}
