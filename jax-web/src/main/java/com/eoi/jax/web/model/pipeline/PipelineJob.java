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

import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.dao.entity.TbPipelineJob;
import com.eoi.jax.web.model.BaseModel;

import java.util.Map;

public class PipelineJob implements BaseModel<TbPipelineJob> {
    private String jobId;
    private String jobName;
    private Map<String, Object> jobConfig;
    private Map<String, Object> jobOpts;

    public String getJobId() {
        return jobId;
    }

    public PipelineJob setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getJobName() {
        return jobName;
    }

    public PipelineJob setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public Map<String, Object> getJobConfig() {
        return jobConfig;
    }

    public PipelineJob setJobConfig(Map<String, Object> jobConfig) {
        this.jobConfig = jobConfig;
        return this;
    }

    public Map<String, Object> getJobOpts() {
        return jobOpts;
    }

    public PipelineJob setJobOpts(Map<String, Object> jobOpts) {
        this.jobOpts = jobOpts;
        return this;
    }

    public TbPipelineJob toEntity(TbPipelineJob entity) {
        this.copyTo(entity);
        entity.setJobConfig(JsonUtil.encode(this.getJobConfig()));
        entity.setOpts(JsonUtil.encode(this.getJobOpts()));
        return entity;
    }

    public PipelineJob respFrom(TbPipelineJob entity) {
        PipelineJob resp = new PipelineJob().copyFrom(entity);
        resp.setJobConfig(JsonUtil.decode2Map(entity.getJobConfig()));
        resp.setJobOpts(JsonUtil.decode2Map(entity.getOpts()));
        return resp;
    }
}
