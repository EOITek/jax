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

public class DiagnosisResult {

    /**
     * jobManagerStatus 是否可访问Jobmanager
     * clusterResource 是否有可用taskmanager资源
     * rootEx 根因异常
     * historyEx 历史异常
     * jobStatus 任务状态
     * allVertixStatus 任务节点状态
     * hasSourceData 输入节点是否有数据接入
     * dataLoss 是否有数据丢失
     * backpressureLevel 反压状态
     * allTaskStatus 子任务状态
     * attempt 重启次数
     * dataSkew 是否有数据倾斜
     * hasWatermark 是否有watermark
     * hasCheckpoint 是否启用checkpoint
     * checkpointFailCount 历史checkpoint失败次数
     * lastCheckpointStatus 最近一次checkpoint状态
     * lastCheckpointDuration 最近一次checkpoint时间
     * gcTime full gc时间
     * memoryUsed 内存使用比率
     * errorLog 错误日志
     */
    private String detectId;

    /**
     * normal：正常；绿色
     * fatal：异常；红色
     * error：异常；橙色
     * warn：异常；黄色
     * info：未知；灰色
     */
    private String status;

    private String errorMessage;

    public String getDetectId() {
        return detectId;
    }

    public void setDetectId(String detectId) {
        this.detectId = detectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public DiagnosisResult(String detectId) {
        this(detectId, "normal");
    }

    public DiagnosisResult(String detectId, String status) {
        this.detectId = detectId;
        this.status = status;
    }
}
