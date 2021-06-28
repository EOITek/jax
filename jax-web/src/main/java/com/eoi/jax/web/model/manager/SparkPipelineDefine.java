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

public class SparkPipelineDefine extends PipelineDefine {
    private String stateListenerUrl;
    private String eventListenerUrl;
    private String driverMemory;
    private String executorMemory;
    private String driverCores;
    private String executorCores;
    private String numExecutors;
    private String yarnQueue;
    private List<String> confList;
    private List<String> javaOptions;
    private List<String> otherStartArgs;
    private Boolean enableHive;
    private Boolean optimizePipeline;
    private List<String> jars;
    private List<String> pyFiles;

    public Boolean getEnableHive() {
        return enableHive;
    }

    public SparkPipelineDefine setEnableHive(Boolean enableHive) {
        this.enableHive = enableHive;
        return this;
    }

    public Boolean getOptimizePipeline() {
        return optimizePipeline;
    }

    public void setOptimizePipeline(Boolean optimizePipeline) {
        this.optimizePipeline = optimizePipeline;
    }

    public String getStateListenerUrl() {
        return stateListenerUrl;
    }

    public SparkPipelineDefine setStateListenerUrl(String stateListenerUrl) {
        this.stateListenerUrl = stateListenerUrl;
        return this;
    }

    public String getEventListenerUrl() {
        return eventListenerUrl;
    }

    public SparkPipelineDefine setEventListenerUrl(String eventListenerUrl) {
        this.eventListenerUrl = eventListenerUrl;
        return this;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public SparkPipelineDefine setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
        return this;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public SparkPipelineDefine setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
        return this;
    }

    public String getDriverCores() {
        return driverCores;
    }

    public SparkPipelineDefine setDriverCores(String driverCores) {
        this.driverCores = driverCores;
        return this;
    }

    public String getExecutorCores() {
        return executorCores;
    }

    public SparkPipelineDefine setExecutorCores(String executorCores) {
        this.executorCores = executorCores;
        return this;
    }

    public String getNumExecutors() {
        return numExecutors;
    }

    public SparkPipelineDefine setNumExecutors(String numExecutors) {
        this.numExecutors = numExecutors;
        return this;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public SparkPipelineDefine setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
        return this;
    }

    public List<String> getConfList() {
        return confList;
    }

    public SparkPipelineDefine setConfList(List<String> confList) {
        this.confList = confList;
        return this;
    }

    public List<String> getJavaOptions() {
        return javaOptions;
    }

    public SparkPipelineDefine setJavaOptions(List<String> javaOptions) {
        this.javaOptions = javaOptions;
        return this;
    }

    public List<String> getOtherStartArgs() {
        return otherStartArgs;
    }

    public SparkPipelineDefine setOtherStartArgs(List<String> otherStartArgs) {
        this.otherStartArgs = otherStartArgs;
        return this;
    }

    public List<String> getJars() {
        return jars;
    }

    public SparkPipelineDefine setJars(List<String> jars) {
        this.jars = jars;
        return this;
    }

    public List<String> getPyFiles() {
        return pyFiles;
    }

    public SparkPipelineDefine setPyFiles(List<String> pyFiles) {
        this.pyFiles = pyFiles;
        return this;
    }
}
