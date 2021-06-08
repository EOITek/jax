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

public class SparkJobOpts {

    private String cacheLevel; // spark persist storage level
    private Boolean cache; // if force cache the results
    private Boolean enableDebug;
    private String debugEntry;
    private Map<String, Object> debugConfig;

    public String getCacheLevel() {
        return cacheLevel;
    }

    public void setCacheLevel(String cacheLevel) {
        this.cacheLevel = cacheLevel;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public Boolean getEnableDebug() {
        return enableDebug;
    }

    public void setEnableDebug(Boolean enableDebug) {
        this.enableDebug = enableDebug;
    }

    public String getDebugEntry() {
        return debugEntry;
    }

    public void setDebugEntry(String debugEntry) {
        this.debugEntry = debugEntry;
    }

    public Map<String, Object> getDebugConfig() {
        return debugConfig;
    }

    public void setDebugConfig(Map<String, Object> debugConfig) {
        this.debugConfig = debugConfig;
    }
}