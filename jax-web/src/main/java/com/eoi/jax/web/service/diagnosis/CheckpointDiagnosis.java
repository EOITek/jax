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

package com.eoi.jax.web.service.diagnosis;

import cn.hutool.core.util.NumberUtil;
import com.eoi.jax.web.model.diagnosis.DiagnosisReq;
import com.eoi.jax.web.model.diagnosis.DiagnosisResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CheckpointDiagnosis extends FlinkDiagnosisBase implements IFlinkDiagnosis {

    @Override
    public List<DiagnosisResult> diagnose(DiagnosisReq req) {
        DiagnosisResult hasCheckpoint = new DiagnosisResult("hasCheckpoint");
        DiagnosisResult checkpointFailCount = new DiagnosisResult("checkpointFailCount");
        DiagnosisResult lastCheckpointStatus = new DiagnosisResult("lastCheckpointStatus");
        DiagnosisResult lastCheckpointDuration = new DiagnosisResult("lastCheckpointDuration");

        String url = getUrl(req.getTrackingUrl(), "/jobs/" + req.getFlinkJobId() + "/checkpoints/config");
        Map<String, Object> resp = getMap(url, null);
        if (resp == null || resp.containsKey("errors")) {
            String noticeMsg = API_ACCESS_ERROR;
            if (resp != null && resp.get("errors") != null) {
                noticeMsg = resp.get("errors").toString();
            }
            hasCheckpoint.setStatus("info");
            hasCheckpoint.setErrorMessage(noticeMsg);
            checkpointFailCount.setStatus("info");
            checkpointFailCount.setErrorMessage(noticeMsg);
            lastCheckpointStatus.setStatus("info");
            lastCheckpointStatus.setErrorMessage(noticeMsg);
            lastCheckpointDuration.setStatus("info");
            lastCheckpointDuration.setErrorMessage(noticeMsg);
        } else {
            long interval = NumberUtil.parseNumber(resp.getOrDefault("interval","9223372036854775807").toString()).longValue();
            long timeout = NumberUtil.parseNumber(resp.getOrDefault("timeout","9223372036854775807").toString()).longValue();
            if (interval == 9223372036854775807L) {
                String notReadyMsg = "Checkpoint未启用";
                hasCheckpoint.setStatus("warn");
                hasCheckpoint.setErrorMessage(notReadyMsg);
                checkpointFailCount.setStatus("info");
                checkpointFailCount.setErrorMessage(notReadyMsg);
                lastCheckpointStatus.setStatus("info");
                lastCheckpointStatus.setErrorMessage(notReadyMsg);
                lastCheckpointDuration.setStatus("info");
                lastCheckpointDuration.setErrorMessage(notReadyMsg);
            } else {
                String ckUrl = getUrl(req.getTrackingUrl(), "/jobs/" + req.getFlinkJobId() + "/checkpoints");
                Map<String, Object> ckResp = getMap(ckUrl, null);
                if (ckResp == null || ckResp.containsKey("errors")) {
                    String noticeMsg = API_ACCESS_ERROR;
                    if (ckResp != null && ckResp.get("errors") != null) {
                        noticeMsg = ckResp.get("errors").toString();
                    }
                    checkpointFailCount.setStatus("info");
                    checkpointFailCount.setErrorMessage(noticeMsg);
                    lastCheckpointStatus.setStatus("info");
                    lastCheckpointStatus.setErrorMessage(noticeMsg);
                    lastCheckpointDuration.setStatus("info");
                    lastCheckpointDuration.setErrorMessage(noticeMsg);
                } else {
                    if (ckResp.get("counts") != null) {
                        Map<String, Object> counts = (Map<String, Object>) ckResp.get("counts");
                        int failedCount = NumberUtil.parseNumber(counts.getOrDefault("failed", "0").toString()).intValue();
                        if (failedCount > 0) {
                            checkpointFailCount.setStatus("warn");
                            checkpointFailCount.setErrorMessage("历史checkpoint失败次数：" + failedCount);
                        }
                    } else {
                        checkpointFailCount.setStatus("info");
                        checkpointFailCount.setErrorMessage("无法获取历史checkpoint失败次数");
                    }


                    if (ckResp.get("latest") != null) {
                        Map<String, Object> latest = (Map<String, Object>) ckResp.get("latest");

                        if (latest.get("failed") != null) {
                            lastCheckpointStatus.setStatus("error");
                            lastCheckpointStatus.setErrorMessage("最近一次checkpoint失败");
                        }

                        if (latest.get("completed") != null) {
                            Map<String, Object> completed = (Map<String, Object>)latest.get("completed");
                            long duration = NumberUtil.parseNumber(completed.getOrDefault("end_to_end_duration","0").toString()).longValue();
                            if (duration > interval || duration > timeout) {
                                lastCheckpointDuration.setStatus("error");
                                lastCheckpointDuration.setErrorMessage("最近一次checkpoint时间超长：" + duration);
                            }
                        }
                    } else {
                        lastCheckpointStatus.setStatus("info");
                        lastCheckpointStatus.setErrorMessage("无法获取");
                        lastCheckpointDuration.setStatus("info");
                        lastCheckpointDuration.setErrorMessage("无法获取");
                    }
                }
            }
        }

        return Arrays.asList(hasCheckpoint,checkpointFailCount,lastCheckpointStatus,lastCheckpointDuration);
    }
}
