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

import com.eoi.jax.web.model.diagnosis.DiagnosisReq;
import com.eoi.jax.web.model.diagnosis.DiagnosisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClusterDiagnosis extends FlinkDiagnosisBase implements IFlinkDiagnosis {
    private static final Logger logger = LoggerFactory.getLogger(ClusterDiagnosis.class);

    @Override
    public List<DiagnosisResult> diagnose(DiagnosisReq req) {
        DiagnosisResult jobManagerStatus = new DiagnosisResult("jobManagerStatus");
        DiagnosisResult clusterResource = new DiagnosisResult("clusterResource");

        String url = getUrl(req.getTrackingUrl(), "/overview");
        Map<String, Object> resp = getMap(url, null);
        if (resp == null || resp.containsKey("errors")) {
            String connectionError = String.format("无法连接jobManager：%s", url);
            if (resp != null && resp.get("errors") != null) {
                connectionError = resp.get("errors").toString();
            }
            jobManagerStatus.setStatus("fatal");
            jobManagerStatus.setErrorMessage(connectionError);
            clusterResource.setStatus("info");
            clusterResource.setErrorMessage(API_ACCESS_ERROR);
        } else {
            int taskManagers = (int)resp.getOrDefault("taskmanagers", 0);
            int slotsTotal = (int)resp.getOrDefault("slots-total", 0);
            if (taskManagers == 0 || slotsTotal == 0) {
                clusterResource.setStatus("fatal");
                clusterResource.setErrorMessage("没有TaskManager计算资源");
            }
        }
        return Arrays.asList(jobManagerStatus, clusterResource);
    }
}
