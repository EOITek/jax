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

package com.eoi.jax.manager.flink;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.manager.api.UUID;
import com.eoi.jax.manager.process.LineHandler;

public abstract class BaseFlinkJob {
    private LineHandler processLineHandler;
    private UUID uuid;

    public LineHandler getProcessLineHandler() {
        return processLineHandler;
    }

    public BaseFlinkJob setProcessLineHandler(LineHandler processLineHandler) {
        this.processLineHandler = processLineHandler;
        return this;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    abstract BaseFlinkJob setVersion(String version);

    abstract String getVersion();

    abstract BaseFlinkJob setJobManager(String jobManager);

    abstract String getJobManager();

    public boolean isBeforeV190() {
        return compareVersion("1.9.0") < 0;
    }

    public boolean isYarnCluster() {
        return "yarn-cluster".equalsIgnoreCase(getJobManager());
    }

    /**
     * 比较版本对象
     * @param version version对象
     * @return 排序值。负数：小于，正数：大于，0：等于
     */
    protected int compareVersion(String version) {
        return StrUtil.compareVersion(getVersion(), version);
    }
}
