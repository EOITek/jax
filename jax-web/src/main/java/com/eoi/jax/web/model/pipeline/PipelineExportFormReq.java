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

package com.eoi.jax.web.model.pipeline;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

public class PipelineExportFormReq {

    private String pipelineNames;

    public String getPipelineNames() {
        return pipelineNames;
    }

    public void setPipelineNames(String pipelineNames) {
        this.pipelineNames = pipelineNames;
    }

    public PipelineExportReq toPipelineExportReq() {
        PipelineExportReq req = new PipelineExportReq();
        if (StrUtil.isNotEmpty(pipelineNames)) {
            req.setPipelineNames(CollUtil.newArrayList(pipelineNames.split(",")));
        }
        return req;
    }
}
