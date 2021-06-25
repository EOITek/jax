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

package com.eoi.jax.flink_entry;

import com.eoi.jax.api.ConfigUtil;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.common.JsonUtil;
import com.eoi.jax.core.entry.EdgeDescription;
import com.eoi.jax.core.entry.JobDescription;
import org.apache.flink.api.java.utils.ParameterTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlinkPipelineDescription {

    private String pipelineName;
    private String timeCharacteristic;
    private Long checkpointInterval;
    private String backend;
    private String checkpointURI;
    private String rocksDbPath;
    private Integer minIdleStateRetentionTime;
    private Integer maxIdleStateRetentionTime;
    private List<JobDescription> jobs;
    private List<EdgeDescription> edges;
    private Boolean disableOperatorChaining = false;
    private Boolean sqlJob;

    public Boolean getSqlJob() {
        return sqlJob;
    }

    public void setSqlJob(Boolean sqlJob) {
        this.sqlJob = sqlJob;
    }

    public Boolean getDisableOperatorChaining() {
        return disableOperatorChaining;
    }

    public void setDisableOperatorChaining(Boolean disableOperatorChaining) {
        this.disableOperatorChaining = disableOperatorChaining;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public String getTimeCharacteristic() {
        return timeCharacteristic;
    }

    public void setTimeCharacteristic(String timeCharacteristic) {
        this.timeCharacteristic = timeCharacteristic;
    }

    public Long getCheckpointInterval() {
        return checkpointInterval;
    }

    public void setCheckpointInterval(Long checkpointInterval) {
        this.checkpointInterval = checkpointInterval;
    }

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public String getCheckpointURI() {
        return checkpointURI;
    }

    public void setCheckpointURI(String checkpointURI) {
        this.checkpointURI = checkpointURI;
    }

    public String getRocksDbPath() {
        return rocksDbPath;
    }

    public void setRocksDbPath(String rocksDbPath) {
        this.rocksDbPath = rocksDbPath;
    }

    public Integer getMinIdleStateRetentionTime() {
        return minIdleStateRetentionTime;
    }

    public void setMinIdleStateRetentionTime(Integer minIdleStateRetentionTime) {
        this.minIdleStateRetentionTime = minIdleStateRetentionTime;
    }

    public Integer getMaxIdleStateRetentionTime() {
        return maxIdleStateRetentionTime;
    }

    public void setMaxIdleStateRetentionTime(Integer maxIdleStateRetentionTime) {
        this.maxIdleStateRetentionTime = maxIdleStateRetentionTime;
    }

    public List<JobDescription> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobDescription> jobs) {
        this.jobs = jobs;
    }

    public List<EdgeDescription> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeDescription> edges) {
        this.edges = edges;
    }

    public void ensureDefaults() {
        if (this.minIdleStateRetentionTime == null) {
            this.minIdleStateRetentionTime = 12;
        }
        if (this.maxIdleStateRetentionTime == null) {
            this.maxIdleStateRetentionTime = 24;
        }
    }

    // TODO: [M] more check should be apply, like jobs edges...
    public void validate(boolean isSqlJob) throws JobConfigValidationException {
        ConfigUtil.checkIfNullOrEmpty(this.pipelineName, "pipelineName");
        ConfigUtil.checkIfNullOrEmpty(this.jobs, "jobs");
        if (!isSqlJob) {
            ConfigUtil.checkIfNullOrEmpty(this.timeCharacteristic, "timeCharacteristic");
            ConfigUtil.checkIfNullOrEmpty(this.checkpointURI, "checkpointURI");
            ConfigUtil.checkIfNullOrEmpty(this.backend, "backend");
            ConfigUtil.checkIfNull(this.checkpointInterval, "checkpointInterval");
            ConfigUtil.checkIfNullOrEmpty(this.edges, "edges");
        }
    }

    /**
     * 把FlinkPipelineDescription转化为ParameterTool，此后可以在TaskManager中获取到该参数
     */
    public ParameterTool toFlinkParameter() throws Exception {
        Map<String, Object> pipelineMap = JsonUtil.decode(JsonUtil.encode(this), Map.class);
        Map<String, String> pipelineArgs = new HashMap<>();
        for (Map.Entry<String, Object> entry : pipelineMap.entrySet()) {
            if (entry.getValue() == null) {
                pipelineArgs.put(entry.getKey(), "null");
            } else {
                pipelineArgs.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return ParameterTool.fromMap(pipelineArgs);
    }
}
