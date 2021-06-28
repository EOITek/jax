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

@TableName("tb_opts_flink")
public class TbOptsFlink {

    @TableId("flink_opts_name")
    private String flinkOptsName;

    @TableField("opts_description")
    private String optsDescription;

    @TableField("entry_jar")
    private String entryJar;

    @TableField("entry_class")
    private String entryClass;

    @TableField("job_lib")
    private String jobLib;

    @TableField("home")
    private String home;

    @TableField("version")
    private String version;

    @TableField("yarn_queue")
    private String yarnQueue;

    @TableField("parallelism")
    private Long parallelism;

    @TableField("time_characteristic")
    private String timeCharacteristic;

    @TableField("allow_non_restore_state")
    private Boolean allowNonRestoredState;

    @TableField("backend")
    private String backend;

    @TableField("rocks_db_path")
    private String rocksDbPath;

    @TableField("savepoint_uri")
    private String savepointURI;

    @TableField("checkpoint_uri")
    private String checkpointURI;

    @TableField("checkpoint_interval")
    private Long checkpointInterval;

    @TableField("min_idle_state_retention_time")
    private Long minIdleStateRetentionTime;

    @TableField("max_idle_state_retention_time")
    private Long maxIdleStateRetentionTime;

    @TableField("yarn_task_manager_num")
    private Long yarnTaskManagerNum;

    @TableField("yarn_slots")
    private Long yarnSlots;

    @TableField("yarn_job_manager_memory")
    private String yarnJobManagerMemory;

    @TableField("yarn_task_manager_memory")
    private String yarnTaskManagerMemory;

    @TableField("disable_operator_chaining")
    private Boolean disableOperatorChaining;

    @TableField("conf_list")
    private String confList;

    @TableField("java_options")
    private String javaOptions;

    @TableField("other_start_args")
    private String otherStartArgs;

    @TableField("runtime_mode")
    private String runtimeMode;

    @TableField("application_mode")
    private Boolean applicationMode;

    @TableField("create_time")
    private Long createTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_time")
    private Long updateTime;

    @TableField("update_by")
    private String updateBy;

    public String getFlinkOptsName() {
        return flinkOptsName;
    }

    public void setFlinkOptsName(String flinkOptsName) {
        this.flinkOptsName = flinkOptsName;
    }

    public String getOptsDescription() {
        return optsDescription;
    }

    public void setOptsDescription(String optsDescription) {
        this.optsDescription = optsDescription;
    }

    public String getEntryJar() {
        return entryJar;
    }

    public void setEntryJar(String entryJar) {
        this.entryJar = entryJar;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public void setEntryClass(String entryClass) {
        this.entryClass = entryClass;
    }

    public String getJobLib() {
        return jobLib;
    }

    public void setJobLib(String jobLib) {
        this.jobLib = jobLib;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public void setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
    }

    public Long getParallelism() {
        return parallelism;
    }

    public void setParallelism(Long parallelism) {
        this.parallelism = parallelism;
    }

    public String getTimeCharacteristic() {
        return timeCharacteristic;
    }

    public void setTimeCharacteristic(String timeCharacteristic) {
        this.timeCharacteristic = timeCharacteristic;
    }

    public Boolean getAllowNonRestoredState() {
        return allowNonRestoredState;
    }

    public void setAllowNonRestoredState(Boolean allowNonRestoredState) {
        this.allowNonRestoredState = allowNonRestoredState;
    }

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public String getRocksDbPath() {
        return rocksDbPath;
    }

    public void setRocksDbPath(String rocksDbPath) {
        this.rocksDbPath = rocksDbPath;
    }

    public String getSavepointURI() {
        return savepointURI;
    }

    public void setSavepointURI(String savepointURI) {
        this.savepointURI = savepointURI;
    }

    public String getCheckpointURI() {
        return checkpointURI;
    }

    public void setCheckpointURI(String checkpointURI) {
        this.checkpointURI = checkpointURI;
    }

    public Long getCheckpointInterval() {
        return checkpointInterval;
    }

    public void setCheckpointInterval(Long checkpointInterval) {
        this.checkpointInterval = checkpointInterval;
    }

    public Long getMinIdleStateRetentionTime() {
        return minIdleStateRetentionTime;
    }

    public void setMinIdleStateRetentionTime(Long minIdleStateRetentionTime) {
        this.minIdleStateRetentionTime = minIdleStateRetentionTime;
    }

    public Long getMaxIdleStateRetentionTime() {
        return maxIdleStateRetentionTime;
    }

    public void setMaxIdleStateRetentionTime(Long maxIdleStateRetentionTime) {
        this.maxIdleStateRetentionTime = maxIdleStateRetentionTime;
    }

    public Long getYarnTaskManagerNum() {
        return yarnTaskManagerNum;
    }

    public void setYarnTaskManagerNum(Long yarnTaskManagerNum) {
        this.yarnTaskManagerNum = yarnTaskManagerNum;
    }

    public Long getYarnSlots() {
        return yarnSlots;
    }

    public void setYarnSlots(Long yarnSlots) {
        this.yarnSlots = yarnSlots;
    }

    public String getYarnJobManagerMemory() {
        return yarnJobManagerMemory;
    }

    public void setYarnJobManagerMemory(String yarnJobManagerMemory) {
        this.yarnJobManagerMemory = yarnJobManagerMemory;
    }

    public String getYarnTaskManagerMemory() {
        return yarnTaskManagerMemory;
    }

    public void setYarnTaskManagerMemory(String yarnTaskManagerMemory) {
        this.yarnTaskManagerMemory = yarnTaskManagerMemory;
    }

    public Boolean getDisableOperatorChaining() {
        return disableOperatorChaining;
    }

    public void setDisableOperatorChaining(Boolean disableOperatorChaining) {
        this.disableOperatorChaining = disableOperatorChaining;
    }

    public String getConfList() {
        return confList;
    }

    public void setConfList(String confList) {
        this.confList = confList;
    }

    public String getJavaOptions() {
        return javaOptions;
    }

    public void setJavaOptions(String javaOptions) {
        this.javaOptions = javaOptions;
    }

    public String getOtherStartArgs() {
        return otherStartArgs;
    }

    public void setOtherStartArgs(String otherStartArgs) {
        this.otherStartArgs = otherStartArgs;
    }

    public String getRuntimeMode() {
        return runtimeMode;
    }

    public void setRuntimeMode(String runtimeMode) {
        this.runtimeMode = runtimeMode;
    }

    public Boolean getApplicationMode() {
        return applicationMode;
    }

    public void setApplicationMode(Boolean applicationMode) {
        this.applicationMode = applicationMode;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
}