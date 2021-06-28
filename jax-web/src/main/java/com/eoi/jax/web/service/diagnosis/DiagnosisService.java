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
import com.eoi.jax.web.model.diagnosis.DiagnosisResp;
import com.eoi.jax.web.model.diagnosis.SourceResp;
import com.eoi.jax.web.model.diagnosis.SourceStat;
import com.eoi.jax.web.model.pipeline.PipelineResp;
import com.eoi.jax.web.service.PipelineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiagnosisService extends FlinkDiagnosisBase {
    private static final Logger logger = LoggerFactory.getLogger(DiagnosisService.class);

    @Autowired
    private PipelineService pipelineService;

    public DiagnosisResp flinkDiagnose(DiagnosisReq req) {
        DiagnosisResp resp = new DiagnosisResp();

        IFlinkDiagnosis flinkDiagnosis = FlinkDiagnosisFactory.getFlinkDiagnosis(req);
        if (flinkDiagnosis != null) {
            try {
                resp.setDetects(flinkDiagnosis.diagnose(req));
            } catch (Exception ex) {
                logger.error("flinkDiagnose error", ex);
            }
        } else {
            String errorMsg = "not support Diagnosis type:" + req.getType() + " or flink version:" + req.getFlinkVersion();
            logger.error(errorMsg);
        }
        return resp;
    }

    public SourceResp sourceStat(String pipelineName) {
        SourceResp sourceResp = new SourceResp();
        PipelineResp pipelineResp = pipelineService.getPipeline(pipelineName);

        if (pipelineResp == null) {
            return sourceResp;
        }

        String trackingUrl = pipelineResp.getTrackUrl();
        String flinkJobId = pipelineResp.getFlinkJobId();

        String url = getUrl(trackingUrl, "/jobs/" + flinkJobId);

        Map<String, Object> resp = getMap(url, null);
        if (resp == null || resp.containsKey("errors")) {
            return sourceResp;
        } else {
            Object plan = resp.get("plan");
            Object vertices = resp.get("vertices");
            if (vertices != null && plan != null && vertices instanceof List && ((List)vertices).size() > 0) {
                List<Map<String,Object>> verticesList  = (List) vertices;

                Map<String, Map<String,Object>> verticesMap = new HashMap<>();
                for (Map<String, Object> v : verticesList) {
                    // 收集所有节点信息
                    String id = v.getOrDefault("id", "").toString();
                    Map<String,Object> info = new HashMap<>();
                    String name = v.getOrDefault("name", "").toString();
                    info.put("name", name);
                    Map<String, Object> metrics = (Map<String, Object>)v.get("metrics");
                    long writeRecords = NumberUtil.parseNumber(metrics.getOrDefault("write-records", "0").toString()).longValue();
                    info.put("writeRecords", writeRecords);
                    long writeBytes = NumberUtil.parseNumber(metrics.getOrDefault("write-bytes", "0").toString()).longValue();
                    info.put("writeBytes", writeBytes);
                    verticesMap.put(id, info);
                }

                List<Map<String,Object>> nodes  = (List)((Map<String,Object>) plan).get("nodes");
                List<SourceStat> sources = new ArrayList<>();
                for (Map<String, Object> node : nodes) {
                    String id = node.getOrDefault("id", "").toString();
                    Map<String, Object> info = verticesMap.get(id);

                    if (info != null) {
                        // 没有inputs的节点为source
                        boolean hasInputs = node.get("inputs") != null && ((List) node.get("inputs")).size() > 0;
                        if (!hasInputs) {
                            SourceStat sourceStat = new SourceStat();
                            sourceStat.setId(id);
                            sourceStat.setCount((long)info.get("writeRecords"));
                            sourceStat.setBytes((long)info.get("writeBytes"));
                            sourceStat.setSourceName(info.get("name").toString());
                            sources.add(sourceStat);
                        }
                    }
                }
                sourceResp.setSourceStats(sources);
            }
        }

        return sourceResp;
    }
}
