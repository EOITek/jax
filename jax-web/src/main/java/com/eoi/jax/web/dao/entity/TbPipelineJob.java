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
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("tb_pipeline_job")
public class TbPipelineJob {

    @TableField("pipeline_name")
    private String pipelineName;

    @TableField("job_id")
    private String jobId;

    @TableField("job_name")
    private String jobName;

    @TableField("job_config")
    private String jobConfig;

    @TableField("opts")
    private String opts;

    public String getPipelineName() {
        return pipelineName;
    }

    public TbPipelineJob setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
        return this;
    }

    public String getJobId() {
        return jobId;
    }

    public TbPipelineJob setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getJobName() {
        return jobName;
    }

    public TbPipelineJob setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public String getJobConfig() {
        return jobConfig;
    }

    public TbPipelineJob setJobConfig(String jobConfig) {
        this.jobConfig = jobConfig;
        return this;
    }

    public String getOpts() {
        return opts;
    }

    public TbPipelineJob setOpts(String opts) {
        this.opts = opts;
        return this;
    }
}
