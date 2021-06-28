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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eoi.jax.web.dao.entity.TbJob;
import com.eoi.jax.web.model.BaseSort;

public class JobSortReq implements BaseSort<TbJob> {
    private String jobName;
    private String jobType;
    private String jobRole;

    public String getJobName() {
        return jobName;
    }

    public JobSortReq setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public String getJobType() {
        return jobType;
    }

    public JobSortReq setJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public String getJobRole() {
        return jobRole;
    }

    public JobSortReq setJobRole(String jobRole) {
        this.jobRole = jobRole;
        return this;
    }

    @Override
    public QueryWrapper<TbJob> order(QueryWrapper<TbJob> wrapper) {
        wrapper.lambda()
                .orderBy(StrUtil.isNotEmpty(jobName), isAsc(jobName), TbJob::getJobName)
                .orderBy(StrUtil.isNotEmpty(jobType), isAsc(jobType), TbJob::getJobType)
                .orderBy(StrUtil.isNotEmpty(jobRole), isAsc(jobRole), TbJob::getJobRole)
                .orderByAsc(StrUtil.isEmpty(jobName), TbJob::getJobName);
        return wrapper;
    }
}
