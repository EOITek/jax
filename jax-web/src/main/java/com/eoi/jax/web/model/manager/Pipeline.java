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

package com.eoi.jax.web.model.manager;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.dao.entity.TbPipelineJob;
import com.eoi.jax.web.model.pipeline.PipelineConfig;
import com.eoi.jax.web.model.pipeline.PipelineJob;
import com.eoi.jax.web.provider.cluster.Cluster;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Pipeline {
    private String pipelineName;
    private String pipelineType;
    private PipelineConfig pipelineConfig;
    private String pipelineUi;
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
    private String dashboardUrl;
    private String trackUrl;
    private String clusterName;
    private Long createTime;
    private String createBy;
    private Long updateTime;
    private String updateBy;
    private List<JobJar> jobJars = new ArrayList<>();
    private List<PipelineJar> extJars = new ArrayList<>();
    private Cluster cluster;
    private Boolean sqlJob;
    private String startCmd;

    public Pipeline(TbPipeline entity, List<TbPipelineJob> jobs, Cluster cluster) {
        BeanUtils.copyProperties(entity, this);
        boolean isCustomCmd = StrUtil.isNotBlank(entity.getStartCmd());
        if (!isCustomCmd) {
            this.pipelineConfig = JsonUtil.decode(entity.getPipelineConfig(), PipelineConfig.class);
            this.pipelineConfig.setJobs(jobs.stream().map(i -> new PipelineJob().respFrom(i)).collect(Collectors.toList()));
        }
        this.cluster = cluster;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public Pipeline setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
        return this;
    }

    public boolean isCustomCmd() {
        return StrUtil.isNotBlank(startCmd);
    }

    public String getStartCmd() {
        return startCmd;
    }

    public void setStartCmd(String startCmd) {
        this.startCmd = startCmd;
    }

    public String getPipelineType() {
        return pipelineType;
    }

    public Pipeline setPipelineType(String pipelineType) {
        this.pipelineType = pipelineType;
        return this;
    }

    public PipelineConfig getPipelineConfig() {
        return pipelineConfig;
    }

    public Pipeline setPipelineConfig(PipelineConfig pipelineConfig) {
        this.pipelineConfig = pipelineConfig;
        return this;
    }

    public String getPipelineUi() {
        return pipelineUi;
    }

    public Pipeline setPipelineUi(String pipelineUi) {
        this.pipelineUi = pipelineUi;
        return this;
    }

    public String getPipelineStatus() {
        return pipelineStatus;
    }

    public Pipeline setPipelineStatus(String pipelineStatus) {
        this.pipelineStatus = pipelineStatus;
        return this;
    }

    public Boolean getSqlJob() {
        return sqlJob;
    }

    public void setSqlJob(Boolean sqlJob) {
        this.sqlJob = sqlJob;
    }

    public String getInternalStatus() {
        return internalStatus;
    }

    public Pipeline setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
        return this;
    }

    public String getPipeDescription() {
        return pipeDescription;
    }

    public Pipeline setPipeDescription(String pipeDescription) {
        this.pipeDescription = pipeDescription;
        return this;
    }

    public String getFlinkJobId() {
        return flinkJobId;
    }

    public Pipeline setFlinkJobId(String flinkJobId) {
        this.flinkJobId = flinkJobId;
        return this;
    }

    public String getFlinkSavePoint() {
        return flinkSavePoint;
    }

    public Pipeline setFlinkSavePoint(String flinkSavePoint) {
        this.flinkSavePoint = flinkSavePoint;
        return this;
    }

    public Boolean getFlinkSavePointIncompatible() {
        return flinkSavePointIncompatible;
    }

    public Pipeline setFlinkSavePointIncompatible(Boolean flinkSavePointIncompatible) {
        this.flinkSavePointIncompatible = flinkSavePointIncompatible;
        return this;
    }

    public Boolean getDeleting() {
        return deleting;
    }

    public Pipeline setDeleting(Boolean deleting) {
        this.deleting = deleting;
        return this;
    }

    public Boolean getProcessing() {
        return processing;
    }

    public Pipeline setProcessing(Boolean processing) {
        this.processing = processing;
        return this;
    }

    public Long getProcessTime() {
        return processTime;
    }

    public Pipeline setProcessTime(Long processTime) {
        this.processTime = processTime;
        return this;
    }

    public String getFlinkCheckPoint() {
        return flinkCheckPoint;
    }

    public Pipeline setFlinkCheckPoint(String flinkCheckPoint) {
        this.flinkCheckPoint = flinkCheckPoint;
        return this;
    }

    public String getSparkSubmissionId() {
        return sparkSubmissionId;
    }

    public Pipeline setSparkSubmissionId(String sparkSubmissionId) {
        this.sparkSubmissionId = sparkSubmissionId;
        return this;
    }

    public String getSparkRestUrl() {
        return sparkRestUrl;
    }

    public Pipeline setSparkRestUrl(String sparkRestUrl) {
        this.sparkRestUrl = sparkRestUrl;
        return this;
    }

    public String getSparkAppId() {
        return sparkAppId;
    }

    public Pipeline setSparkAppId(String sparkAppId) {
        this.sparkAppId = sparkAppId;
        return this;
    }

    public String getYarnAppId() {
        return yarnAppId;
    }

    public Pipeline setYarnAppId(String yarnAppId) {
        this.yarnAppId = yarnAppId;
        return this;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public Pipeline setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
        return this;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public Pipeline setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
        return this;
    }

    public String getClusterName() {
        return clusterName;
    }

    public Pipeline setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public Pipeline setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public Pipeline setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public Pipeline setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public Pipeline setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }

    public List<JobJar> getJobJars() {
        return jobJars;
    }

    public Pipeline setJobJars(List<JobJar> jobJars) {
        this.jobJars = jobJars;
        return this;
    }

    public List<PipelineJar> getExtJars() {
        return extJars;
    }

    public Pipeline setExtJars(List<PipelineJar> extJars) {
        this.extJars = extJars;
        return this;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public Pipeline setCluster(Cluster cluster) {
        this.cluster = cluster;
        return this;
    }
}
