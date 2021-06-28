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

package com.eoi.jax.web.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("tb_pipeline")
public class TbPipeline {

    @TableId("pipeline_name")
    private String pipelineName;

    @TableField("pipeline_type")
    private String pipelineType;

    @TableField("pipeline_source")
    private String pipelineSource;

    @TableField("pipeline_config")
    private String pipelineConfig;

    @TableField("pipeline_ui")
    private String pipelineUi;

    @TableField("pipeline_status")
    private String pipelineStatus;

    @TableField("internal_status")
    private String internalStatus;

    @TableField("pipe_description")
    private String pipeDescription;

    @TableField("flink_save_point")
    private String flinkSavePoint;

    @TableField("flink_save_point_incompatible")
    private Boolean flinkSavePointIncompatible;

    @TableField("flink_save_point_disable")
    private Boolean flinkSavePointDisable;

    @TableField("flink_check_point")
    private String flinkCheckPoint;

    @TableField("flink_job_id")
    private String flinkJobId;

    @TableField("spark_submission_id")
    private String sparkSubmissionId;

    @TableField("spark_rest_url")
    private String sparkRestUrl;

    @TableField("spark_app_id")
    private String sparkAppId;

    @TableField("yarn_app_id")
    private String yarnAppId;

    @TableField("track_url")
    private String trackUrl;

    @TableField("deleting")
    private Boolean deleting;

    @TableField("processing")
    private Boolean processing;

    @TableField("process_time")
    private Long processTime;

    @TableField("cluster_name")
    private String clusterName;

    @TableField("opts_name")
    private String optsName;

    @TableField("start_cmd")
    private String startCmd;

    @TableField("create_time")
    private Long createTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_time")
    private Long updateTime;

    @TableField("update_by")
    private String updateBy;

    public String getPipelineName() {
        return pipelineName;
    }

    public TbPipeline setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
        return this;
    }

    public String getPipelineType() {
        return pipelineType;
    }

    public TbPipeline setPipelineType(String pipelineType) {
        this.pipelineType = pipelineType;
        return this;
    }

    public String getPipelineSource() {
        return pipelineSource;
    }

    public TbPipeline setPipelineSource(String pipelineSource) {
        this.pipelineSource = pipelineSource;
        return this;
    }

    public String getPipelineConfig() {
        return pipelineConfig;
    }

    public TbPipeline setPipelineConfig(String pipelineConfig) {
        this.pipelineConfig = pipelineConfig;
        return this;
    }

    public String getPipelineUi() {
        return pipelineUi;
    }

    public TbPipeline setPipelineUi(String pipelineUi) {
        this.pipelineUi = pipelineUi;
        return this;
    }

    public String getPipelineStatus() {
        return pipelineStatus;
    }

    public TbPipeline setPipelineStatus(String pipelineStatus) {
        this.pipelineStatus = pipelineStatus;
        return this;
    }

    public String getInternalStatus() {
        return internalStatus;
    }

    public TbPipeline setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
        return this;
    }

    public String getPipeDescription() {
        return pipeDescription;
    }

    public TbPipeline setPipeDescription(String pipeDescription) {
        this.pipeDescription = pipeDescription;
        return this;
    }

    public String getFlinkSavePoint() {
        return flinkSavePoint;
    }

    public TbPipeline setFlinkSavePoint(String flinkSavePoint) {
        this.flinkSavePoint = flinkSavePoint;
        return this;
    }

    public Boolean getFlinkSavePointIncompatible() {
        return flinkSavePointIncompatible;
    }

    public TbPipeline setFlinkSavePointIncompatible(Boolean flinkSavePointIncompatible) {
        this.flinkSavePointIncompatible = flinkSavePointIncompatible;
        return this;
    }

    public Boolean getFlinkSavePointDisable() {
        return flinkSavePointDisable;
    }

    public TbPipeline setFlinkSavePointDisable(Boolean flinkSavePointDisable) {
        this.flinkSavePointDisable = flinkSavePointDisable;
        return this;
    }

    public String getFlinkCheckPoint() {
        return flinkCheckPoint;
    }

    public TbPipeline setFlinkCheckPoint(String flinkCheckPoint) {
        this.flinkCheckPoint = flinkCheckPoint;
        return this;
    }

    public String getFlinkJobId() {
        return flinkJobId;
    }

    public TbPipeline setFlinkJobId(String flinkJobId) {
        this.flinkJobId = flinkJobId;
        return this;
    }

    public String getSparkSubmissionId() {
        return sparkSubmissionId;
    }

    public TbPipeline setSparkSubmissionId(String sparkSubmissionId) {
        this.sparkSubmissionId = sparkSubmissionId;
        return this;
    }

    public String getSparkRestUrl() {
        return sparkRestUrl;
    }

    public TbPipeline setSparkRestUrl(String sparkRestUrl) {
        this.sparkRestUrl = sparkRestUrl;
        return this;
    }

    public String getSparkAppId() {
        return sparkAppId;
    }

    public TbPipeline setSparkAppId(String sparkAppId) {
        this.sparkAppId = sparkAppId;
        return this;
    }

    public String getYarnAppId() {
        return yarnAppId;
    }

    public TbPipeline setYarnAppId(String yarnAppId) {
        this.yarnAppId = yarnAppId;
        return this;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public TbPipeline setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
        return this;
    }

    public Boolean getDeleting() {
        return deleting;
    }

    public TbPipeline setDeleting(Boolean deleting) {
        this.deleting = deleting;
        return this;
    }

    public Boolean getProcessing() {
        return processing;
    }

    public TbPipeline setProcessing(Boolean processing) {
        this.processing = processing;
        return this;
    }

    public Long getProcessTime() {
        return processTime;
    }

    public TbPipeline setProcessTime(Long processTime) {
        this.processTime = processTime;
        return this;
    }

    public String getClusterName() {
        return clusterName;
    }

    public TbPipeline setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public String getOptsName() {
        return optsName;
    }

    public TbPipeline setOptsName(String optsName) {
        this.optsName = optsName;
        return this;
    }

    public String getStartCmd() {
        return startCmd;
    }

    public TbPipeline setStartCmd(String startCmd) {
        this.startCmd = startCmd;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public TbPipeline setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public TbPipeline setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public TbPipeline setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public TbPipeline setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }
}
