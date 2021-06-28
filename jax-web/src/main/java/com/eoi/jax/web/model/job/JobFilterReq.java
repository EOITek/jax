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
import com.eoi.jax.web.model.BaseFilter;

public class JobFilterReq implements BaseFilter<TbJob> {
    private String search;
    private String jobType;
    private String jobRole;
    private String jarName;

    public String getSearch() {
        return search;
    }

    public JobFilterReq setSearch(String search) {
        this.search = search;
        return this;
    }

    public String getJobType() {
        return jobType;
    }

    public JobFilterReq setJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public String getJobRole() {
        return jobRole;
    }

    public JobFilterReq setJobRole(String jobRole) {
        this.jobRole = jobRole;
        return this;
    }

    public String getJarName() {
        return jarName;
    }

    public JobFilterReq setJarName(String jarName) {
        this.jarName = jarName;
        return this;
    }

    @Override
    public QueryWrapper<TbJob> where(QueryWrapper<TbJob> wrapper) {
        wrapper.lambda()
                .and(StrUtil.isNotBlank(search), q -> q
                        .like(TbJob::getJobName, search)
                        .or()
                        .like(TbJob::getJobMeta, search))
                .eq(StrUtil.isNotBlank(jobType), TbJob::getJobType, jobType)
                .eq(StrUtil.isNotBlank(jobRole), TbJob::getJobRole, jobRole)
                .eq(StrUtil.isNotBlank(jarName), TbJob::getJarName, jarName);
        return wrapper;
    }
}
