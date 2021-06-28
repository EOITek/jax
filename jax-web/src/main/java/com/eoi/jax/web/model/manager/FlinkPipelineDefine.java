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

import java.util.List;

public class FlinkPipelineDefine extends PipelineDefine {
    private String parallelism;
    private String timeCharacteristic;
    private String backend;
    private String rocksDbPath;
    private String checkpointURI;
    private String checkpointInterval;
    private String savepointURI;
    private String minIdleStateRetentionTime;
    private String maxIdleStateRetentionTime;
    private String yarnTaskManagerNum;
    private String yarnSlots;
    private String yarnJobManagerMemory;
    private String yarnTaskManagerMemory;
    private String yarnQueue;
    private String runtimeMode;
    private Boolean disableOperatorChaining;
    private Boolean allowNonRestoredState;
    private List<String> confList;
    private List<String> javaOptions;
    private List<String> yarnDefinitions;
    private List<String> otherStartArgs;
    private Boolean applicationMode;
    private Boolean sqlJob;
    private String startCmd;

    public String getStartCmd() {
        return startCmd;
    }

    public void setStartCmd(String startCmd) {
        this.startCmd = startCmd;
    }

    public Boolean getSqlJob() {
        return sqlJob;
    }

    public void setSqlJob(Boolean sqlJob) {
        this.sqlJob = sqlJob;
    }

    public Boolean getApplicationMode() {
        return applicationMode;
    }

    public void setApplicationMode(Boolean applicationMode) {
        this.applicationMode = applicationMode;
    }

    public String getRuntimeMode() {
        return runtimeMode;
    }

    public void setRuntimeMode(String runtimeMode) {
        this.runtimeMode = runtimeMode;
    }

    public String getParallelism() {
        return parallelism;
    }

    public FlinkPipelineDefine setParallelism(String parallelism) {
        this.parallelism = parallelism;
        return this;
    }

    public String getTimeCharacteristic() {
        return timeCharacteristic;
    }

    public FlinkPipelineDefine setTimeCharacteristic(String timeCharacteristic) {
        this.timeCharacteristic = timeCharacteristic;
        return this;
    }

    public String getBackend() {
        return backend;
    }

    public FlinkPipelineDefine setBackend(String backend) {
        this.backend = backend;
        return this;
    }

    public String getRocksDbPath() {
        return rocksDbPath;
    }

    public FlinkPipelineDefine setRocksDbPath(String rocksDbPath) {
        this.rocksDbPath = rocksDbPath;
        return this;
    }

    public String getCheckpointURI() {
        return checkpointURI;
    }

    public FlinkPipelineDefine setCheckpointURI(String checkpointURI) {
        this.checkpointURI = checkpointURI;
        return this;
    }

    public String getCheckpointInterval() {
        return checkpointInterval;
    }

    public FlinkPipelineDefine setCheckpointInterval(String checkpointInterval) {
        this.checkpointInterval = checkpointInterval;
        return this;
    }

    public String getSavepointURI() {
        return savepointURI;
    }

    public FlinkPipelineDefine setSavepointURI(String savepointURI) {
        this.savepointURI = savepointURI;
        return this;
    }

    public String getMinIdleStateRetentionTime() {
        return minIdleStateRetentionTime;
    }

    public FlinkPipelineDefine setMinIdleStateRetentionTime(String minIdleStateRetentionTime) {
        this.minIdleStateRetentionTime = minIdleStateRetentionTime;
        return this;
    }

    public String getMaxIdleStateRetentionTime() {
        return maxIdleStateRetentionTime;
    }

    public FlinkPipelineDefine setMaxIdleStateRetentionTime(String maxIdleStateRetentionTime) {
        this.maxIdleStateRetentionTime = maxIdleStateRetentionTime;
        return this;
    }

    public String getYarnTaskManagerNum() {
        return yarnTaskManagerNum;
    }

    public FlinkPipelineDefine setYarnTaskManagerNum(String yarnTaskManagerNum) {
        this.yarnTaskManagerNum = yarnTaskManagerNum;
        return this;
    }

    public String getYarnSlots() {
        return yarnSlots;
    }

    public FlinkPipelineDefine setYarnSlots(String yarnSlots) {
        this.yarnSlots = yarnSlots;
        return this;
    }

    public String getYarnJobManagerMemory() {
        return yarnJobManagerMemory;
    }

    public FlinkPipelineDefine setYarnJobManagerMemory(String yarnJobManagerMemory) {
        this.yarnJobManagerMemory = yarnJobManagerMemory;
        return this;
    }

    public String getYarnTaskManagerMemory() {
        return yarnTaskManagerMemory;
    }

    public FlinkPipelineDefine setYarnTaskManagerMemory(String yarnTaskManagerMemory) {
        this.yarnTaskManagerMemory = yarnTaskManagerMemory;
        return this;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public FlinkPipelineDefine setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
        return this;
    }

    public Boolean getDisableOperatorChaining() {
        return disableOperatorChaining;
    }

    public FlinkPipelineDefine setDisableOperatorChaining(Boolean disableOperatorChaining) {
        this.disableOperatorChaining = disableOperatorChaining;
        return this;
    }

    public Boolean getAllowNonRestoredState() {
        return allowNonRestoredState;
    }

    public FlinkPipelineDefine setAllowNonRestoredState(Boolean allowNonRestoredState) {
        this.allowNonRestoredState = allowNonRestoredState;
        return this;
    }

    public List<String> getConfList() {
        return confList;
    }

    public FlinkPipelineDefine setConfList(List<String> confList) {
        this.confList = confList;
        return this;
    }

    public List<String> getJavaOptions() {
        return javaOptions;
    }

    public FlinkPipelineDefine setJavaOptions(List<String> javaOptions) {
        this.javaOptions = javaOptions;
        return this;
    }

    public List<String> getYarnDefinitions() {
        return yarnDefinitions;
    }

    public FlinkPipelineDefine setYarnDefinitions(List<String> yarnDefinitions) {
        this.yarnDefinitions = yarnDefinitions;
        return this;
    }

    public List<String> getOtherStartArgs() {
        return otherStartArgs;
    }

    public FlinkPipelineDefine setOtherStartArgs(List<String> otherStartArgs) {
        this.otherStartArgs = otherStartArgs;
        return this;
    }
}
