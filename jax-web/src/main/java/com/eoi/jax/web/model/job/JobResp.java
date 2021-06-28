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

package com.eoi.jax.web.model.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.consts.JobRole;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.dao.entity.TbJar;
import com.eoi.jax.web.dao.entity.TbJob;
import com.eoi.jax.web.model.BaseModel;
import com.eoi.jax.web.provider.scanner.JobMeta;

public class JobResp implements BaseModel<TbJob> {
    private String jobName;
    private String jobType;
    private String jobRole;
    private Boolean internal;
    private JobMeta jobMeta;
    private String jarName;
    private TbJar jar;

    public String getJobName() {
        return jobName;
    }

    public JobResp setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public String getJobType() {
        return jobType;
    }

    public JobResp setJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public String getJobRole() {
        if (StrUtil.isEmpty(jobRole) && getJobMeta() != null) {
            boolean hasInputs = CollUtil.isNotEmpty(getJobMeta().getInTypes());
            boolean hasOutputs =  CollUtil.isNotEmpty(getJobMeta().getOutTypes());
            if (hasInputs && hasOutputs) {
                return JobRole.PROCESS.code;
            } else if (hasInputs) {
                return JobRole.SINK.code;
            } else if (hasOutputs) {
                return JobRole.SOURCE.code;
            }
        }
        return jobRole;
    }

    public JobResp setJobRole(String jobRole) {
        this.jobRole = jobRole;
        return this;
    }

    public Boolean getInternal() {
        return internal;
    }

    public JobResp setInternal(Boolean internal) {
        this.internal = internal;
        return this;
    }

    public JobMeta getJobMeta() {
        return jobMeta;
    }

    public JobResp setJobMeta(JobMeta jobMeta) {
        this.jobMeta = jobMeta;
        return this;
    }

    public String getJarName() {
        return jarName;
    }

    public JobResp setJarName(String jarName) {
        this.jarName = jarName;
        return this;
    }

    public TbJar getJar() {
        return jar;
    }

    public JobResp setJar(TbJar jar) {
        this.jar = jar;
        return this;
    }

    private boolean getHasJobInfo() {
        return getJobMeta() != null && getJobMeta().getJobInfo() != null;
    }

    public boolean getHasDoc() {
        return getHasJobInfo() && StrUtil.isNotEmpty(getJobMeta().getJobInfo().getDoc());
    }

    public boolean getHasIcon() {
        return getHasJobInfo() && StrUtil.isNotEmpty(getJobMeta().getJobInfo().getIcon());
    }

    public String getDocUrl() {
        return getHasDoc() ? String.format("/api/v1/job/%s/document", getJobName()) : null;
    }

    public String getIconUrl() {
        return getHasIcon() ? String.format("/api/v1/job/%s/icon.svg", getJobName()) : null;
    }

    public JobResp respFrom(TbJob entity, TbJar jar) {
        JobResp resp = new JobResp();
        resp.copyFrom(entity);
        resp.setJar(jar);
        resp.setJobMeta(JsonUtil.decode(entity.getJobMeta(), JobMeta.class));
        if (resp.getJobRole() == null && resp.getHasJobInfo()) {
            resp.setJobRole(resp.getJobMeta().getJobInfo().getRole());
        }
        if (resp.getInternal() == null && resp.getHasJobInfo()) {
            resp.setInternal(resp.getJobMeta().getJobInfo().getInternal());
        }
        return resp;
    }
}
