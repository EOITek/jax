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
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.model.diagnosis.DiagnosisReq;
import com.eoi.jax.web.model.diagnosis.DiagnosisResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TaskmanagerDiagnosis extends FlinkDiagnosisBase implements IFlinkDiagnosis {

    @Override
    public List<DiagnosisResult> diagnose(DiagnosisReq req) {
        DiagnosisResult gcTime = new DiagnosisResult("gcTime");
        DiagnosisResult memoryUsed = new DiagnosisResult("memoryUsed");

        String url = getUrl(req.getTrackingUrl(), "/taskmanagers");
        Map<String, Object> resp = getMap(url, null);
        if (resp == null || resp.containsKey("errors")) {
            String noticeMsg = API_ACCESS_ERROR;
            if (resp != null && resp.get("errors") != null) {
                noticeMsg = resp.get("errors").toString();
            }
            gcTime.setStatus("info");
            gcTime.setErrorMessage(noticeMsg);
            memoryUsed.setStatus("info");
            memoryUsed.setErrorMessage(noticeMsg);
        } else {
            if (resp.get("taskmanagers") != null && ((List)resp.get("taskmanagers")).size() > 0) {
                List<Map<String,Object>> taskManagers = (List)resp.get("taskmanagers");
                for (Map<String, Object> taskManager : taskManagers) {
                    String id = taskManager.getOrDefault("id","").toString();
                    if (!StrUtil.isEmpty(id)) {
                        String tmUrl = getUrl(req.getTrackingUrl(), "/taskmanagers/" + id);
                        Map<String, Object> tmResp = getMap(tmUrl, null);
                        if (tmResp == null || tmResp.containsKey("errors")) {
                            String noticeMsg = API_ACCESS_ERROR;
                            if (tmResp != null && tmResp.get("errors") != null) {
                                noticeMsg = tmResp.get("errors").toString();
                            }
                            gcTime.setStatus("info");
                            gcTime.setErrorMessage(noticeMsg);
                            memoryUsed.setStatus("info");
                            memoryUsed.setErrorMessage(noticeMsg);
                        } else {
                            Map<String,Object> metrics = (Map<String,Object>)tmResp.get("metrics");
                            double heapUsed = NumberUtil.parseNumber(metrics.getOrDefault("heapUsed","0").toString()).doubleValue();
                            double heapCommitted = NumberUtil.parseNumber(metrics.getOrDefault("heapCommitted","0").toString()).doubleValue();
                            double nonHeapUsed = NumberUtil.parseNumber(metrics.getOrDefault("nonHeapUsed","0").toString()).doubleValue();
                            double nonHeapCommitted = NumberUtil.parseNumber(metrics.getOrDefault("nonHeapCommitted","0").toString()).doubleValue();

                            if (heapCommitted > 0 && nonHeapCommitted > 0) {
                                double memUsage = (heapUsed + nonHeapUsed) / (heapCommitted + nonHeapCommitted);
                                if (memUsage >= 0.9) {
                                    memoryUsed.setStatus("warn");
                                    memoryUsed.setErrorMessage("内存使用率超过：" + memUsage);
                                }
                            }

                            if (metrics.get("garbageCollectors") != null) {
                                List<Map<String,Object>> garbageCollectors = (List)metrics.get("garbageCollectors");

                                for (Map<String, Object> garbageCollector : garbageCollectors) {
                                    if ("PS_MarkSweep".equals(garbageCollector.getOrDefault("name","").toString())) {
                                        double count = NumberUtil.parseNumber(garbageCollector.getOrDefault("count","0").toString()).doubleValue();
                                        double time = NumberUtil.parseNumber(garbageCollector.getOrDefault("time","0").toString()).doubleValue();

                                        if (count > 0 && time / count > 1000) {
                                            gcTime.setStatus("warn");
                                            gcTime.setErrorMessage("GC耗时较长：" + time / count);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                String noTmError = "未找到任务TaskManager";
                gcTime.setStatus("info");
                gcTime.setErrorMessage(noTmError);
                memoryUsed.setStatus("info");
                memoryUsed.setErrorMessage(noTmError);
            }
        }

        return Arrays.asList(gcTime,memoryUsed);
    }
}
