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
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.model.diagnosis.DiagnosisReq;
import com.eoi.jax.web.model.diagnosis.DiagnosisResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExceptionDiagnosis extends FlinkDiagnosisBase implements IFlinkDiagnosis {

    @Override
    public List<DiagnosisResult> diagnose(DiagnosisReq req) {
        DiagnosisResult rootEx = new DiagnosisResult("rootEx");
        DiagnosisResult historyEx = new DiagnosisResult("historyEx");

        String url = getUrl(req.getTrackingUrl(), "/jobs/" + req.getFlinkJobId() + "/exceptions");
        Map<String, Object> resp = getMap(url, null);
        if (resp == null || resp.containsKey("errors")) {
            String noticeMsg = API_ACCESS_ERROR;
            if (resp != null && resp.get("errors") != null) {
                noticeMsg = resp.get("errors").toString();
            }
            rootEx.setStatus("info");
            rootEx.setErrorMessage(noticeMsg);
            historyEx.setStatus("info");
            historyEx.setErrorMessage(noticeMsg);
        } else {
            Object rootExMsg = resp.get("root-exception");
            if (rootExMsg != null && !StrUtil.isEmpty(rootExMsg.toString())) {
                rootEx.setStatus("error");
                rootEx.setErrorMessage(rootExMsg.toString());
            }

            Object allExMsg = resp.get("all-exceptions");
            if (allExMsg != null && allExMsg instanceof List && ((List)allExMsg).size() > 0) {
                List errList  = (List)allExMsg;
                historyEx.setStatus("error");
                try {
                    historyEx.setErrorMessage(JsonUtil.encodePretty(errList));
                } catch (Exception ignore) {
                }
            }
        }

        return Arrays.asList(rootEx, historyEx);
    }
}
