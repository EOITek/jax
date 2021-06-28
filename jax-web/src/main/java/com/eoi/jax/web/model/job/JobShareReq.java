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

import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.dao.entity.TbJobShare;
import com.eoi.jax.web.model.BaseModel;

import java.util.Map;


public class JobShareReq implements BaseModel<TbJobShare> {
    private String shareName;
    private String jobName;
    private Map<String, Object> jobConfig;

    public String getShareName() {
        return shareName;
    }

    public JobShareReq setShareName(String shareName) {
        this.shareName = shareName;
        return this;
    }

    public String getJobName() {
        return jobName;
    }

    public JobShareReq setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public Map<String, Object> getJobConfig() {
        return jobConfig;
    }

    public JobShareReq setJobConfig(Map<String, Object> jobConfig) {
        this.jobConfig = jobConfig;
        return this;
    }

    public TbJobShare toEntity(TbJobShare entity) {
        this.copyTo(entity);
        entity.setJobConfig(JsonUtil.encode(jobConfig));
        return entity;
    }
}
