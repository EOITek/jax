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

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import com.eoi.jax.common.JsonUtil;
import com.eoi.jax.web.model.diagnosis.DiagnosisReq;
import com.eoi.jax.web.model.diagnosis.DiagnosisResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TaskDiagnosis extends FlinkDiagnosisBase implements IFlinkDiagnosis {

    static CompletionService<Map<String,Object>> completionService = new ExecutorCompletionService<Map<String,Object>>(Executors.newFixedThreadPool(8));

    private static final Logger logger = LoggerFactory.getLogger(TaskDiagnosis.class);

    @Override
    public List<DiagnosisResult> diagnose(DiagnosisReq req) {
        DiagnosisResult jobStatus = new DiagnosisResult("jobStatus");
        DiagnosisResult allVertixStatus = new DiagnosisResult("allVertixStatus");
        DiagnosisResult hasSourceData = new DiagnosisResult("hasSourceData");
        DiagnosisResult dataLoss = new DiagnosisResult("dataLoss");
        DiagnosisResult backpressureLevel = new DiagnosisResult("backpressureLevel");
        DiagnosisResult hasWatermarkRst = new DiagnosisResult("hasWatermark", "info");
        hasWatermarkRst.setErrorMessage("未发现任何Watermark");
        DiagnosisResult allTaskStatus = new DiagnosisResult("allTaskStatus");
        DiagnosisResult attempt = new DiagnosisResult("attempt");
        DiagnosisResult dataSkew = new DiagnosisResult("dataSkew");

        String url = getUrl(req.getTrackingUrl(), "/jobs/" + req.getFlinkJobId());
        Map<String, Object> resp = getMap(url, null);
        if (resp == null || resp.containsKey("errors")) {
            String noticeMsg = API_ACCESS_ERROR;
            if (resp != null && resp.get("errors") != null) {
                noticeMsg = resp.get("errors").toString();
            }
            jobStatus.setStatus("info");
            jobStatus.setErrorMessage(noticeMsg);
            allVertixStatus.setStatus("info");
            allVertixStatus.setErrorMessage(noticeMsg);
            hasSourceData.setStatus("info");
            hasSourceData.setErrorMessage(noticeMsg);
            dataLoss.setStatus("info");
            dataLoss.setErrorMessage(noticeMsg);
            backpressureLevel.setStatus("info");
            backpressureLevel.setErrorMessage(noticeMsg);
            hasWatermarkRst.setStatus("info");
            hasWatermarkRst.setErrorMessage(noticeMsg);
            allTaskStatus.setStatus("info");
            allTaskStatus.setErrorMessage(noticeMsg);
            attempt.setStatus("info");
            attempt.setErrorMessage(noticeMsg);
            dataSkew.setStatus("info");
            dataSkew.setErrorMessage(noticeMsg);
        } else {
            Object jobStateObj = resp.get("state");
            if (jobStateObj != null && !"RUNNING".equals(jobStateObj.toString())) {
                jobStatus.setStatus("error");
                jobStatus.setErrorMessage("任务运行状态：" + jobStateObj.toString());
            }

            Object plan = resp.get("plan");
            Object vertices = resp.get("vertices");
            if (vertices != null && plan != null && vertices instanceof List && ((List)vertices).size() > 0) {
                List<Map<String,Object>> verticesList  = (List) vertices;

                List<Map<String,Object>> verticesNotRunning =  new ArrayList<>();
                boolean hasWatermark = false;
                Map<String, Map<String,Object>> verticesMap = new HashMap<>();
                for (Map<String, Object> v : verticesList) {
                    // 检查所有节点的状态是否为RUNNING
                    if (!"RUNNING".equals(v.get("status"))) {
                        verticesNotRunning.add(v);
                    }

                    // 收集所有节点信息
                    String id = v.getOrDefault("id", "").toString();
                    Map<String,Object> info = new HashMap<>();
                    String name = v.getOrDefault("name", "").toString();
                    info.put("name", name);
                    if (name.contains("Timestamps/Watermarks")) {
                        hasWatermark = true;
                    }
                    Map<String, Object> metrics = (Map<String, Object>)v.get("metrics");
                    long readRecords = NumberUtil.parseNumber(metrics.getOrDefault("read-records", "0").toString()).longValue();
                    info.put("readRecords", readRecords);
                    long writeRecords = NumberUtil.parseNumber(metrics.getOrDefault("write-records", "0").toString()).longValue();
                    info.put("writeRecords", writeRecords);
                    verticesMap.put(id, info);
                }

                List<Map<String,Object>> nodes  = (List)((Map<String,Object>) plan).get("nodes");
                List<Map<String,Object>> sourceNoInput =  new ArrayList<>();
                Map<String,List<String>> dataLossMap =  new HashMap<>();
                for (Map<String, Object> node : nodes) {
                    String id = node.getOrDefault("id", "").toString();
                    Map<String, Object> info = verticesMap.get(id);

                    if (info != null) {
                        boolean hasInputs = node.get("inputs") != null && ((List) node.get("inputs")).size() > 0;
                        if (hasInputs) {
                            // 检查上下游输入输出数量是否一致
                            List<Map<String, Object>> inputList = (List) node.get("inputs");
                            List<String> inputIds = inputList.stream().map(x -> x.getOrDefault("id", "").toString()).collect(Collectors.toList());
                            long inputCount = (long)info.get("readRecords");
                            int outputCount = 0;
                            for (String inputId : inputIds) {
                                Map<String, Object> tmpVertix = verticesMap.get(inputId);
                                if (tmpVertix != null) {
                                    outputCount += (long)tmpVertix.get("writeRecords");
                                }

                                if (tmpVertix.get("downStream") == null) {
                                    tmpVertix.put("downStream",new ArrayList<String>());
                                }
                                ((List)tmpVertix.get("downStream")).add(id);
                            }
                            if (inputCount != outputCount) {
                                dataLossMap.put(id, inputIds);
                            }
                        } else {
                            // 检查输入source节点是否有数据
                            if ((long)info.get("writeRecords") == 0) {
                                sourceNoInput.add(info);
                            }
                        }
                    }
                }

                if (sourceNoInput.size() > 0) {
                    hasSourceData.setStatus("error");
                    StringBuilder errMsgBuilder = new StringBuilder();
                    for (Map<String, Object> v : sourceNoInput) {
                        errMsgBuilder.append("以下输入节点没有数据\n");
                        errMsgBuilder.append(v.get("name")).append("\n");
                    }
                    hasSourceData.setErrorMessage(errMsgBuilder.toString());
                }

                if (dataLossMap.size() > 0) {
                    dataLoss.setStatus("error");
                    StringBuilder errMsgBuilder = new StringBuilder();
                    for (String id : dataLossMap.keySet()) {
                        List<String> inputNames = dataLossMap.get(id).stream()
                                .map(x -> verticesMap.get(x).getOrDefault("name","").toString())
                                .collect(Collectors.toList());
                        errMsgBuilder.append("上游节点：").append(String.join(",",inputNames)).append("的输出条数与下游节点：")
                                .append(verticesMap.get(id).getOrDefault("name","").toString()).append("的输入条数不一致\n");
                    }
                    dataLoss.setErrorMessage(errMsgBuilder.toString());
                }


                // 并行调用每个节点的反压api
                detectBackpressure(req, backpressureLevel, verticesMap);

                // 每个节点检测：子任务状态，重启次数，是否有数据倾斜, watermark
                detectVertices(req, hasWatermarkRst, allTaskStatus, attempt, dataSkew, hasWatermark, verticesMap);
            } else {
                String noticeMsg = "没有找到任何节点";
                allVertixStatus.setStatus("info");
                allVertixStatus.setErrorMessage(noticeMsg);
                hasSourceData.setStatus("info");
                hasSourceData.setErrorMessage(noticeMsg);
                dataLoss.setStatus("info");
                dataLoss.setErrorMessage(noticeMsg);
            }
        }

        return Arrays.asList(jobStatus, allVertixStatus, hasSourceData, dataLoss, backpressureLevel, allTaskStatus, attempt, dataSkew, hasWatermarkRst);
    }

    private void detectVertices(DiagnosisReq req,
                                DiagnosisResult hasWatermarkRst,
                                DiagnosisResult allTaskStatus,
                                DiagnosisResult attempt,
                                DiagnosisResult dataSkew,
                                boolean hasWatermark,
                                Map<String, Map<String, Object>> verticesMap) {
        int requestCount = 0;
        for (String vid : verticesMap.keySet()) {
            completionService.submit(() -> {
                String bpUrl = getUrl(req.getTrackingUrl(), "/jobs/" + req.getFlinkJobId() + "/vertices/" + vid);
                return getMap(bpUrl, null);
            });
            requestCount++;
            if (hasWatermark) {
                completionService.submit(() -> {
                    String bpUrl = getUrl(req.getTrackingUrl(), "/jobs/" + req.getFlinkJobId() + "/vertices/" + vid + "/metrics?get=0.currentInputWatermark");
                    List<Map<String, Object>> listMap = getList(bpUrl, null);
                    MapBuilder<String, Object> ret = MapUtil.builder();
                    return ret.put("watermark", listMap).build();
                });
                requestCount++;
            } else {
                hasWatermarkRst.setStatus("normal");
                hasWatermarkRst.setErrorMessage(null);
            }
        }

        List<Map<String, Object>> errorTaskStatus = new ArrayList<>();
        List<Map<String, Object>> errorAttempts = new ArrayList<>();
        List<Map<String, Object>> errorDataSkew = new ArrayList<>();
        boolean hasRequestFailed = false;
        String errorMsg = API_ACCESS_ERROR;
        for (int i = 0; i < requestCount; i++) {
            try {
                Map<String, Object> result = completionService.take().get();
                if (result == null || result.containsKey("errors")) {
                    hasRequestFailed = true;
                    if (result != null && result.get("errors") != null) {
                        errorMsg = result.get("errors").toString();
                    }
                }
                if (result.containsKey("watermark")) {
                    List<Map<String, Object>> watermarkList = (List<Map<String, Object>>)result.get("watermark");
                    for (Map<String, Object> w : watermarkList) {
                        long watermarkValue = NumberUtil.parseNumber(w.getOrDefault("value","0").toString()).longValue();
                        if (watermarkValue > 0) {
                            hasWatermarkRst.setStatus("normal");
                            hasWatermarkRst.setErrorMessage("当前watermark：" + new DateTime(watermarkValue).toString());
                        }
                    }
                    if (hasRequestFailed) {
                        hasWatermarkRst.setStatus("info");
                        hasWatermarkRst.setErrorMessage(errorMsg);
                    }
                }
                if (result.get("subtasks") != null) {
                    String id = result.getOrDefault("id", "").toString();
                    Map<String,Object> tmpVertex = verticesMap.get(id);
                    tmpVertex.put("id", id);
                    List<Map<String, Object>> subTasks = (List<Map<String, Object>>)result.get("subtasks");
                    List<Double> readCounts = new ArrayList<>();
                    for (Map<String, Object> subTask : subTasks) {
                        int attemptCount = NumberUtil.parseNumber(subTask.getOrDefault("attempt","0").toString()).intValue();
                        if (attemptCount > 0) {
                            errorAttempts.add(tmpVertex);
                        }

                        String status = subTask.getOrDefault("status","").toString();
                        if (!"RUNNING".equals(status)) {
                            errorTaskStatus.add(tmpVertex);
                        }

                        Map<String,Object> metrics = (Map<String,Object>)subTask.get("metrics");
                        double readCount = NumberUtil.parseNumber(metrics.getOrDefault("read-records","0").toString()).doubleValue();
                        readCounts.add(readCount);
                    }

                    double[] readArray = new double[readCounts.size()];
                    for (int i1 = 0; i1 < readCounts.size(); i1++) {
                        readArray[i1] = readCounts.get(i1);
                    }
                    DescriptiveStatistics da = new DescriptiveStatistics(readArray);
                    double q3 = da.getPercentile(75);
                    double q1 = da.getPercentile(25);
                    double iqr = q3 - q1;
                    double min = q1 - 1.5 * iqr;
                    double max = q3 + 1.5 * iqr;
                    double tmax = readCounts.stream().mapToDouble(x -> x).max().getAsDouble();
                    double tmin = readCounts.stream().mapToDouble(x -> x).min().getAsDouble();
                    if (readCounts.stream().anyMatch(x -> x < min || x > max) || (tmax > tmin * 2)) {
                        errorDataSkew.add(tmpVertex);
                    }
                }
            } catch (Exception e) {
                logger.error("vertices diagnosis error:", e);
            }
        }

        if (errorAttempts.size() > 0) {
            attempt.setStatus("error");
            StringBuilder msg = new StringBuilder();
            for (Map<String, Object> errorAttempt : errorAttempts) {
                msg.append(errorAttempt.get("name").toString())
                        .append(" 有线程重启过\n")
                        .append(" 详情：").append(getUrl(req.getTrackingUrl(),
                        "/#/job/" + req.getFlinkJobId() + "/overview/" + errorAttempt.get("id").toString()) + "/subtasks  \n\n");
            }
            attempt.setErrorMessage(msg.toString());
        } else if (hasRequestFailed) {
            attempt.setStatus("info");
            attempt.setErrorMessage(errorMsg);
        }
        if (errorTaskStatus.size() > 0) {
            allTaskStatus.setStatus("error");
            StringBuilder msg = new StringBuilder();
            for (Map<String, Object> errorTaskStat : errorTaskStatus) {
                msg.append(errorTaskStat.get("name").toString())
                        .append(" 有线程运行状态不是running\n")
                        .append(" 详情：").append(getUrl(req.getTrackingUrl(),
                        "/#/job/" + req.getFlinkJobId() + "/overview/" + errorTaskStat.get("id").toString()) + "/subtasks  \n\n");
            }
            allTaskStatus.setErrorMessage(msg.toString());
        } else if (hasRequestFailed) {
            allTaskStatus.setStatus("info");
            allTaskStatus.setErrorMessage(errorMsg);
        }

        if (errorDataSkew.size() > 0) {
            dataSkew.setStatus("error");
            StringBuilder msg = new StringBuilder();
            for (Map<String, Object> ds : errorDataSkew) {
                msg.append(ds.get("name").toString())
                        .append(" 发现数据倾斜\n")
                        .append(" 详情：").append(getUrl(req.getTrackingUrl(), "/#/job/" + req.getFlinkJobId() + "/overview/" + ds.get("id").toString()) + "/subtasks  \n\n");
            }
            dataSkew.setErrorMessage(msg.toString());
        } else if (hasRequestFailed) {
            dataSkew.setStatus("info");
            dataSkew.setErrorMessage(errorMsg);
        }
    }

    private void detectBackpressure(DiagnosisReq req, DiagnosisResult backpressureLevel, Map<String, Map<String, Object>> verticesMap) {
        StringBuilder bpMsg = new StringBuilder();
        int maxBpStatus = 0;
        for (String vid : verticesMap.keySet()) {
            String bpUrl = getUrl(req.getTrackingUrl(), "/jobs/" + req.getFlinkJobId() + "/vertices/" + vid + "/backpressure");
            Map<String, Object> bpResp =  getMap(bpUrl, null);
            String name = verticesMap.get(vid).get("name").toString();

            if (bpResp == null || bpResp.containsKey("errors")) {
                String noticeMsg = API_ACCESS_ERROR;
                if (bpResp != null && bpResp.get("errors") != null) {
                    noticeMsg = bpResp.get("errors").toString();
                }
                if (maxBpStatus < 1) {
                    maxBpStatus = 1;
                    bpMsg.append(name).append(noticeMsg).append("\n");
                }
            } else {
                String status = bpResp.getOrDefault("status", "deprecated").toString();
                if ("ok".equals(status)) {
                    String bpLvl = bpResp.getOrDefault("backpressure-level", "ok").toString();
                    if (!"ok".equals(bpLvl)) {
                        if ("low".equalsIgnoreCase(bpLvl) && maxBpStatus < 2) {
                            maxBpStatus = 2;
                        }
                        if ("high".equalsIgnoreCase(bpLvl) && maxBpStatus < 3) {
                            maxBpStatus = 3;
                        }

                        try {
                            List<String> downStreamNames = new ArrayList<>();
                            if (verticesMap.get(vid).containsKey("downStream")) {
                                List<String> downStreamIds = (List)verticesMap.get(vid).get("downStream");

                                downStreamNames = downStreamIds.stream()
                                        .map(x -> verticesMap.get(x).getOrDefault("name","").toString())
                                        .collect(Collectors.toList());
                            }
                            bpMsg.append("反压节点：").append(name).append(" 反压状态:").append(bpLvl)
                                    .append("\n下游节点名：").append(String.join(",", downStreamNames))
                                    .append("\n").append(JsonUtil.encodePretty(bpResp)).append("\n")
                                    .append(" 详情：").append(getUrl(req.getTrackingUrl(), "/#/job/" + req.getFlinkJobId() + "/overview/" + vid + "/backpressure  \n\n"));

                        } catch (JsonProcessingException ignore) {
                        }
                    }
                } else {
                    if (maxBpStatus < 1) {
                        maxBpStatus = 1;
                    }
                }
            }
        }

        if (maxBpStatus > 0) {
            switch (maxBpStatus) {
                case 1:
                    backpressureLevel.setStatus("info");
                    backpressureLevel.setErrorMessage("反压指标正在测量中...\n");
                    break;
                case 2:
                    backpressureLevel.setStatus("warn");
                    backpressureLevel.setErrorMessage(bpMsg.toString());
                    break;
                case 3:
                    backpressureLevel.setStatus("error");
                    backpressureLevel.setErrorMessage(bpMsg.toString());
                    break;
                default: //do nothing
            }
        }
    }
}
