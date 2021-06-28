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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.model.BaseSort;

public class PipelineSortReq implements BaseSort<TbPipeline> {
    private String pipelineName;
    private String pipelineType;
    private String pipelineStatus;
    private String internalStatus;
    private String createTime;
    private String updateTime;

    public String getPipelineName() {
        return pipelineName;
    }

    public PipelineSortReq setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
        return this;
    }

    public String getPipelineType() {
        return pipelineType;
    }

    public PipelineSortReq setPipelineType(String pipelineType) {
        this.pipelineType = pipelineType;
        return this;
    }

    public String getPipelineStatus() {
        return pipelineStatus;
    }

    public PipelineSortReq setPipelineStatus(String pipelineStatus) {
        this.pipelineStatus = pipelineStatus;
        return this;
    }

    public String getInternalStatus() {
        return internalStatus;
    }

    public PipelineSortReq setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public PipelineSortReq setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public PipelineSortReq setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public QueryWrapper<TbPipeline> order(QueryWrapper<TbPipeline> wrapper) {
        wrapper.lambda()
                .orderBy(StrUtil.isNotEmpty(pipelineName), isAsc(pipelineName), TbPipeline::getPipelineName)
                .orderBy(StrUtil.isNotEmpty(pipelineType), isAsc(pipelineType), TbPipeline::getPipelineType)
                .orderBy(StrUtil.isNotEmpty(pipelineStatus), isAsc(pipelineStatus), TbPipeline::getPipelineStatus)
                .orderBy(StrUtil.isNotEmpty(internalStatus), isAsc(internalStatus), TbPipeline::getInternalStatus)
                .orderBy(StrUtil.isNotEmpty(createTime), isAsc(createTime), TbPipeline::getCreateTime)
                .orderBy(StrUtil.isNotEmpty(updateTime), isAsc(updateTime), TbPipeline::getUpdateTime)
                .orderByDesc(StrUtil.isEmpty(updateTime), TbPipeline::getUpdateTime);
        return wrapper;
    }
}
