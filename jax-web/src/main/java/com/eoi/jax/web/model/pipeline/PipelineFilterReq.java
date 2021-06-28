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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.model.BaseFilter;

import java.util.List;

public class PipelineFilterReq implements BaseFilter<TbPipeline> {
    private String search;
    private List<String> pipelineType;
    private List<String> pipelineStatus;
    private List<String> internalStatus;
    private String jobName;

    public String getSearch() {
        return search;
    }

    public PipelineFilterReq setSearch(String search) {
        this.search = search;
        return this;
    }

    public List<String> getPipelineType() {
        return pipelineType;
    }

    public void setPipelineType(List<String> pipelineType) {
        this.pipelineType = pipelineType;
    }

    public List<String> getPipelineStatus() {
        return pipelineStatus;
    }

    public PipelineFilterReq setPipelineStatus(List<String> pipelineStatus) {
        this.pipelineStatus = pipelineStatus;
        return this;
    }

    public List<String> getInternalStatus() {
        return internalStatus;
    }

    public PipelineFilterReq setInternalStatus(List<String> internalStatus) {
        this.internalStatus = internalStatus;
        return this;
    }

    public String getJobName() {
        return jobName;
    }

    public PipelineFilterReq setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    @Override
    public QueryWrapper where(QueryWrapper<TbPipeline> wrapper) {
        wrapper.lambda()
                .and(StrUtil.isNotBlank(search), q -> q
                        .like(TbPipeline::getPipelineName, search)
                        .or()
                        .like(TbPipeline::getPipeDescription, search))
                .in(CollUtil.isNotEmpty(pipelineType), TbPipeline::getPipelineType, pipelineType)
                .in(CollUtil.isNotEmpty(pipelineStatus), TbPipeline::getPipelineStatus, pipelineStatus)
                .in(CollUtil.isNotEmpty(internalStatus), TbPipeline::getInternalStatus, internalStatus)
                .like(StrUtil.isNotBlank(jobName), TbPipeline::getPipelineConfig, jobName);
        return wrapper;
    }
}
