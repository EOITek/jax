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

package com.eoi.jax.web.controller;

import com.eoi.jax.web.common.ResponseResult;
import com.eoi.jax.web.model.diagnosis.DiagnosisReq;
import com.eoi.jax.web.model.diagnosis.DiagnosisResp;
import com.eoi.jax.web.model.diagnosis.SourceResp;
import com.eoi.jax.web.service.diagnosis.DiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DiagosisController extends V1Controller {

    @Autowired
    private DiagnosisService diagnosisService;

    @GetMapping("diagnosis/{type}")
    public ResponseResult<DiagnosisResp> diagnosis(
            @PathVariable("type") String type,
            @RequestParam(value = "flinkJobId") String flinkJobId,
            @RequestParam(value = "trackingUrl") String trackingUrl) {
        DiagnosisReq req = new DiagnosisReq();
        req.setType(type);
        req.setFlinkJobId(flinkJobId);
        req.setTrackingUrl(trackingUrl);
        return new ResponseResult<DiagnosisResp>().setEntity(diagnosisService.flinkDiagnose(req));
    }

    @GetMapping("diagnosis/source/{pipelineName}")
    public ResponseResult<SourceResp> sourceStat(@PathVariable("pipelineName") String pipelineName) {
        return new ResponseResult<SourceResp>().setEntity(diagnosisService.sourceStat(pipelineName));
    }
}
