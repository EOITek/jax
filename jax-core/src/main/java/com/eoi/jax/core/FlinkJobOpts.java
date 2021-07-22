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

public class FlinkJobOpts {
    private Boolean enableDebug;
    private String debugEntry;
    private Map<String, Object> debugConfig;

    /**
     * Flink 算子高级配置
     * 参考： https://doc.eoitek.net/pages/viewpage.action?pageId=19908592
     */
    private String slotSharingGroup;
    private String chainingStrategy;
    private Long bufferTimeout;
    private Integer parallelism;
    private Integer maxParallelism;

    public FlinkJobOpts() {
        enableDebug = false;
        debugEntry = null;
        debugConfig = null;
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

    public String getSlotSharingGroup() {
        return slotSharingGroup;
    }

    public void setSlotSharingGroup(String slotSharingGroup) {
        this.slotSharingGroup = slotSharingGroup;
    }

    public String getChainingStrategy() {
        return chainingStrategy;
    }

    public void setChainingStrategy(String chainingStrategy) {
        this.chainingStrategy = chainingStrategy;
    }

    public Long getBufferTimeout() {
        return bufferTimeout;
    }

    public void setBufferTimeout(Long bufferTimeout) {
        this.bufferTimeout = bufferTimeout;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public Integer getMaxParallelism() {
        return maxParallelism;
    }

    public void setMaxParallelism(Integer maxParallelism) {
        this.maxParallelism = maxParallelism;
    }
}