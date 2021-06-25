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

package com.eoi.jax.manager.spark;

import com.eoi.jax.manager.api.UUID;

public abstract class BaseSparkJob {
    private UUID uuid;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    abstract BaseSparkJob setVersion(String version);

    abstract String getVersion();

    abstract BaseSparkJob setMasterUrl(String masterUrl);

    abstract String getMasterUrl();

    public boolean isYarnCluster() {
        return "yarn".equalsIgnoreCase(getMasterUrl());
    }
}
