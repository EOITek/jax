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

package com.eoi.jax.web.common.consts;

public enum ClusterType {
    YARN("yarn"),
    FLINK_STANDALONE("flink_standalone"),
    SPARK_STANDALONE("spark_standalone");

    public final String code;

    ClusterType(String code) {
        this.code = code;
    }

    public boolean isEqual(String code) {
        return this.code.equals(code);
    }

    public boolean supportFlink() {
        return YARN.isEqual(code) || FLINK_STANDALONE.isEqual(code);
    }

    public boolean supportSpark() {
        return YARN.isEqual(code) || SPARK_STANDALONE.isEqual(code);
    }

    public static ClusterMode toClusterMode(String clusterType) {
        return YARN.code.equalsIgnoreCase(clusterType) ? ClusterMode.YARN : ClusterMode.STANDALONE;
    }

    public static ClusterType fromString(String code) {
        for (ClusterType value : ClusterType.values()) {
            if (value.isEqual(code)) {
                return value;
            }
        }
        return null;
    }
}
