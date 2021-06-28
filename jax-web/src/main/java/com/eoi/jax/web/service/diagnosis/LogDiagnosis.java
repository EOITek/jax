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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.model.diagnosis.DiagnosisReq;
import com.eoi.jax.web.model.diagnosis.DiagnosisResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogDiagnosis extends FlinkDiagnosisBase implements IFlinkDiagnosis {

    @Override
    public List<DiagnosisResult> diagnose(DiagnosisReq req) {
        List<DiagnosisResult> diagnosisResults = new ArrayList<>();

        getTaskManagerLogs(req, diagnosisResults);

        getJobManagerLogs(req, diagnosisResults);

        return diagnosisResults;
    }

    private void getJobManagerLogs(DiagnosisReq req, List<DiagnosisResult> diagnosisResults) {
        String jmUrl = getUrl(req.getTrackingUrl(), "/jobmanager/log");
        String log = get(jmUrl, null);
        if (log == null) {
            DiagnosisResult noLog = new DiagnosisResult("jobmanager", "error");
            noLog.setErrorMessage(API_ACCESS_ERROR);
            diagnosisResults.add(noLog);
        } else {
            String[] lines = log.split("\n");
            int count = 0;
            for (String line : lines) {
                count++;
                String lineId = String.valueOf(count);
                if (line.contains(" ERROR ") || line.contains("Caused by") || line.contains("exit code")
                        || line.contains(" FATAL ") || line.contains("Exception:")) {
                    DiagnosisResult error = new DiagnosisResult(lineId, "error");
                    error.setErrorMessage(line);
                    diagnosisResults.add(error);
                }
                if (line.contains(" WARN ")) {
                    DiagnosisResult warn = new DiagnosisResult(lineId, "warn");
                    warn.setErrorMessage(line);
                    diagnosisResults.add(warn);
                }
            }
        }
    }

    private void getTaskManagerLogs(DiagnosisReq req, List<DiagnosisResult> diagnosisResults) {
        String url = getUrl(req.getTrackingUrl(), "/taskmanagers");
        Map<String, Object> resp = getMap(url, null);
        if (resp == null || resp.containsKey("errors")) {
            String noticeMsg = API_ACCESS_ERROR;
            if (resp != null && resp.get("errors") != null) {
                noticeMsg = resp.get("errors").toString();
            }
            DiagnosisResult noTaskManager = new DiagnosisResult("noTaskManager", "error");
            noTaskManager.setErrorMessage(noticeMsg);
            diagnosisResults.add(noTaskManager);
        } else {
            if (resp.get("taskmanagers") != null && ((List) resp.get("taskmanagers")).size() > 0) {
                List<Map<String,Object>> taskManagers = (List)resp.get("taskmanagers");
                for (Map<String, Object> taskManager : taskManagers) {
                    String id = taskManager.getOrDefault("id", "").toString();
                    if (!StrUtil.isEmpty(id)) {
                        String tmUrl = getUrl(req.getTrackingUrl(), "/taskmanagers/" + id + "/log");
                        String log = get(tmUrl, null);
                        if (log == null) {
                            DiagnosisResult noLog = new DiagnosisResult(id, "error");
                            noLog.setErrorMessage(id + API_ACCESS_ERROR);
                            diagnosisResults.add(noLog);
                        } else {
                            String[] lines = log.split("\n");
                            int count = 0;
                            for (String line : lines) {
                                count++;
                                String lineId = id + " " + count;
                                if (line.contains(" ERROR ") || line.contains("Caused by") || line.contains("exit code")
                                        || line.contains(" FATAL ") || line.contains("Exception:")) {
                                    DiagnosisResult error = new DiagnosisResult(lineId, "error");
                                    error.setErrorMessage(line);
                                    diagnosisResults.add(error);
                                }
                                if (line.contains(" WARN ")) {
                                    DiagnosisResult warn = new DiagnosisResult(lineId, "warn");
                                    warn.setErrorMessage(line);
                                    diagnosisResults.add(warn);
                                }
                            }
                        }
                    }
                }
            } else {
                DiagnosisResult noTaskManager = new DiagnosisResult("noTaskManager", "error");
                noTaskManager.setErrorMessage("未找到任务可用的task manager");
                diagnosisResults.add(noTaskManager);
            }
        }
    }
}
