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

package com.eoi.jax.web.model.diagnosis;

public class DiagnosisReq {

    private String flinkJobId;

    private String trackingUrl;

    /**
     * 诊断类型：
     * cluster - 集群诊断
     * exception - 异常诊断
     * task - 任务诊断
     * checkpoint - checkpoint诊断
     * tm - taskmanager诊断
     * log - 日志诊断
     */
    private String type;

    // 为了适配不同flink版本的api差异，
    // 只区分到第2级，例如：1.9，1.10，1.11，1.12
    private String flinkVersion = "1.9";

    public String getFlinkJobId() {
        return flinkJobId;
    }

    public void setFlinkJobId(String flinkJobId) {
        this.flinkJobId = flinkJobId;
    }

    public String getFlinkVersion() {
        return flinkVersion;
    }

    public void setFlinkVersion(String flinkVersion) {
        this.flinkVersion = flinkVersion;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
