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
import com.eoi.jax.web.common.consts.ClusterType;
import com.eoi.jax.web.common.consts.PipelineType;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.dao.entity.TbCluster;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.model.BaseModel;

import java.util.Map;

public class PipelineResp implements BaseModel<TbPipeline> {
    private String pipelineName;
    private String pipelineType;
    private String pipelineSource;
    private PipelineConfig pipelineConfig;
    private Map<String, Object> pipelineUi;
    private String pipelineStatus;
    private String internalStatus;
    private String pipeDescription;
    private String flinkJobId;
    private String flinkSavePoint;
    private Boolean flinkSavePointIncompatible;
    private Boolean deleting;
    private Boolean processing;
    private Long processTime;
    private String flinkCheckPoint;
    private String sparkSubmissionId;
    private String sparkRestUrl;
    private String sparkAppId;
    private String yarnAppId;
    private String trackUrl;
    private String sparkHistoryServer;
    private String clusterName;
    private String optsName;
    private String startCmd;
    private Long createTime;
    private String createBy;
    private Long updateTime;
    private String updateBy;

    public String getPipelineName() {
        return pipelineName;
    }

    public PipelineResp setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
        return this;
    }

    public String getPipelineType() {
        return pipelineType;
    }

    public PipelineResp setPipelineType(String pipelineType) {
        this.pipelineType = pipelineType;
        return this;
    }

    public String getPipelineSource() {
        return pipelineSource;
    }

    public PipelineResp setPipelineSource(String pipelineSource) {
        this.pipelineSource = pipelineSource;
        return this;
    }

    public PipelineConfig getPipelineConfig() {
        return pipelineConfig;
    }

    public PipelineResp setPipelineConfig(PipelineConfig pipelineConfig) {
        this.pipelineConfig = pipelineConfig;
        return this;
    }

    public Map<String, Object> getPipelineUi() {
        return pipelineUi;
    }

    public PipelineResp setPipelineUi(Map<String, Object> pipelineUi) {
        this.pipelineUi = pipelineUi;
        return this;
    }

    public String getPipelineStatus() {
        return pipelineStatus;
    }

    public PipelineResp setPipelineStatus(String pipelineStatus) {
        this.pipelineStatus = pipelineStatus;
        return this;
    }

    public String getInternalStatus() {
        return internalStatus;
    }

    public PipelineResp setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
        return this;
    }

    public String getPipeDescription() {
        return pipeDescription;
    }

    public PipelineResp setPipeDescription(String pipeDescription) {
        this.pipeDescription = pipeDescription;
        return this;
    }

    public String getFlinkJobId() {
        return flinkJobId;
    }

    public PipelineResp setFlinkJobId(String flinkJobId) {
        this.flinkJobId = flinkJobId;
        return this;
    }

    public String getFlinkSavePoint() {
        return flinkSavePoint;
    }

    public PipelineResp setFlinkSavePoint(String flinkSavePoint) {
        this.flinkSavePoint = flinkSavePoint;
        return this;
    }

    public Boolean getFlinkSavePointIncompatible() {
        return flinkSavePointIncompatible;
    }

    public PipelineResp setFlinkSavePointIncompatible(Boolean flinkSavePointIncompatible) {
        this.flinkSavePointIncompatible = flinkSavePointIncompatible;
        return this;
    }

    public Boolean getDeleting() {
        return deleting;
    }

    public PipelineResp setDeleting(Boolean deleting) {
        this.deleting = deleting;
        return this;
    }

    public Boolean getProcessing() {
        return processing;
    }

    public PipelineResp setProcessing(Boolean processing) {
        this.processing = processing;
        return this;
    }

    public Long getProcessTime() {
        return processTime;
    }

    public PipelineResp setProcessTime(Long processTime) {
        this.processTime = processTime;
        return this;
    }

    public String getFlinkCheckPoint() {
        return flinkCheckPoint;
    }

    public PipelineResp setFlinkCheckPoint(String flinkCheckPoint) {
        this.flinkCheckPoint = flinkCheckPoint;
        return this;
    }

    public String getSparkSubmissionId() {
        return sparkSubmissionId;
    }

    public PipelineResp setSparkSubmissionId(String sparkSubmissionId) {
        this.sparkSubmissionId = sparkSubmissionId;
        return this;
    }

    public String getSparkRestUrl() {
        return sparkRestUrl;
    }

    public PipelineResp setSparkRestUrl(String sparkRestUrl) {
        this.sparkRestUrl = sparkRestUrl;
        return this;
    }

    public String getSparkAppId() {
        return sparkAppId;
    }

    public PipelineResp setSparkAppId(String sparkAppId) {
        this.sparkAppId = sparkAppId;
        return this;
    }

    public String getYarnAppId() {
        return yarnAppId;
    }

    public PipelineResp setYarnAppId(String yarnAppId) {
        this.yarnAppId = yarnAppId;
        return this;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public PipelineResp setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
        return this;
    }

    public String getSparkHistoryServer() {
        return sparkHistoryServer;
    }

    public PipelineResp setSparkHistoryServer(String sparkHistoryServer) {
        this.sparkHistoryServer = sparkHistoryServer;
        return this;
    }

    public String getClusterName() {
        return clusterName;
    }

    public PipelineResp setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public String getOptsName() {
        return optsName;
    }

    public PipelineResp setOptsName(String optsName) {
        this.optsName = optsName;
        return this;
    }

    public String getStartCmd() {
        return startCmd;
    }

    public PipelineResp setStartCmd(String startCmd) {
        this.startCmd = startCmd;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public PipelineResp setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public PipelineResp setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public PipelineResp setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public PipelineResp setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }

    public PipelineResp respFrom(TbPipeline entity) {
        PipelineResp resp = new PipelineResp().copyFrom(entity);
        resp.setPipelineUi(JsonUtil.decode2Map(entity.getPipelineUi()));
        if (StrUtil.isNotEmpty(entity.getStartCmd())) {
            resp.setPipelineType(PipelineType.FLINK_CMD.code);
        }
        PipelineConfig config = JsonUtil.decode(entity.getPipelineConfig(), PipelineConfig.class);
        resp.setPipelineConfig(config);
        return resp;
    }

    public PipelineResp respFrom(TbPipeline entity, TbCluster cluster) {
        return respFrom(entity)
                .setTrackUrl(getTrackUrlForPipeline(entity, cluster))
                .setSparkHistoryServer(getSparkHistoryUrlForPipeline(entity, cluster));
    }

    private String getTrackUrlForPipeline(TbPipeline pipeline, TbCluster cluster) {
        if (!isStarted(pipeline)) {
            return null;
        }
        if (ClusterType.YARN.isEqual(cluster.getClusterType())) {
            return pipeline.getTrackUrl();
        }
        if (PipelineType.isStreaming(pipeline.getPipelineType())) {
            return cluster.getFlinkWebUrl();
        }
        if (PipelineType.isBatch(pipeline.getPipelineType())) {
            return cluster.getSparkWebUrl();
        }
        return null;
    }

    private String getSparkHistoryUrlForPipeline(TbPipeline pipeline, TbCluster cluster) {
        if (!isStarted(pipeline)) {
            return null;
        }
        if (!PipelineType.isBatch(pipeline.getPipelineType())) {
            return null;
        }
        return cluster.getSparkHistoryServer();
    }

    private boolean isStarted(TbPipeline pipeline) {
        return StrUtil.isNotEmpty(pipeline.getYarnAppId())
                || StrUtil.isNotEmpty(pipeline.getFlinkJobId())
                || StrUtil.isNotEmpty(pipeline.getSparkAppId())
                || StrUtil.isNotEmpty(pipeline.getSparkSubmissionId())
                || StrUtil.isNotEmpty(pipeline.getTrackUrl());
    }
}
