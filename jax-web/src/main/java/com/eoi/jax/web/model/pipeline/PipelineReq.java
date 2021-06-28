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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.consts.PipelineType;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.model.BaseModel;

import java.util.Map;

public class PipelineReq implements BaseModel<TbPipeline> {
    private String pipelineName;
    private String pipelineType;
    private PipelineConfig pipelineConfig;
    private Map<String, Object> pipelineUi;
    private String pipeDescription;
    private String clusterName;
    private String optsName;
    private String startCmd;

    public String getStartCmd() {
        return startCmd;
    }

    public void setStartCmd(String startCmd) {
        this.startCmd = startCmd;
    }

    public boolean isCustomCmd() {
        return StrUtil.isNotBlank(startCmd);
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public PipelineReq setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
        return this;
    }

    public String getPipelineType() {
        return pipelineType;
    }

    public PipelineReq setPipelineType(String pipelineType) {
        this.pipelineType = pipelineType;
        return this;
    }

    public PipelineConfig getPipelineConfig() {
        return pipelineConfig;
    }

    public PipelineReq setPipelineConfig(PipelineConfig pipelineConfig) {
        this.pipelineConfig = pipelineConfig;
        return this;
    }

    public Map<String, Object> getPipelineUi() {
        return pipelineUi;
    }

    public PipelineReq setPipelineUi(Map<String, Object> pipelineUi) {
        this.pipelineUi = pipelineUi;
        return this;
    }

    public String getPipeDescription() {
        return pipeDescription;
    }

    public PipelineReq setPipeDescription(String pipeDescription) {
        this.pipeDescription = pipeDescription;
        return this;
    }

    public String getClusterName() {
        return clusterName;
    }

    public PipelineReq setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public String getOptsName() {
        return optsName;
    }

    public PipelineReq setOptsName(String optsName) {
        this.optsName = optsName;
        return this;
    }

    public TbPipeline toEntity(TbPipeline entity) {
        this.copyTo(entity);
        entity.setPipelineConfig(JsonUtil.encode(this.getPipelineConfig()));
        entity.setPipelineUi(JsonUtil.encode(this.getPipelineUi()));
        if (StrUtil.isNotEmpty(entity.getStartCmd())) {
            entity.setPipelineType(PipelineType.FLINK_CMD.code);
        }
        return entity;
    }

    public static PipelineReq fromEntity(TbPipeline pipeline) {
        PipelineReq pipelineReq = new PipelineReq();
        pipelineReq.setPipelineName(pipeline.getPipelineName());
        pipelineReq.setPipelineType(pipeline.getPipelineType());
        pipelineReq.setPipelineConfig(JsonUtil.decode(pipeline.getPipelineConfig(), PipelineConfig.class));
        if (StrUtil.isNotEmpty(pipeline.getPipelineUi())) {
            pipelineReq.setPipelineUi(JsonUtil.decode2Map(pipeline.getPipelineUi()));
        }
        pipelineReq.setPipeDescription(pipeline.getPipeDescription());
        pipelineReq.setClusterName(pipeline.getClusterName());
        return pipelineReq;
    }
}
