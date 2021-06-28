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

package com.eoi.jax.web.schedule.async;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eoi.jax.web.common.consts.PipelineStatus;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.schedule.service.BasePipelineService;

import java.util.Arrays;
import java.util.List;

public abstract class BasePipelineAsync {

    public void execute(TbPipeline pipeline, BasePipelineService service) {
        if (PipelineStatus.WAITING_START.isEqual(pipeline.getPipelineStatus())) {
            service.start(pipeline);
        } else if (PipelineStatus.WAITING_STOP.isEqual(pipeline.getPipelineStatus())) {
            service.stop(pipeline);
        } else if (PipelineStatus.RESTARTING.isEqual(pipeline.getPipelineStatus())) {
            service.restart(pipeline);
        } else if (PipelineStatus.DELETING.isEqual(pipeline.getPipelineStatus())
                || Boolean.TRUE.equals(pipeline.getDeleting())) {
            service.delete(pipeline);
        }
    }

    public TbPipeline getTodo(BasePipelineService service) {
        List<TbPipeline> pipelines = service.query(
                new LambdaQueryWrapper<TbPipeline>()
                        .in(TbPipeline::getPipelineType, service.getPipelineType())
                        .isNull(TbPipeline::getProcessing)
                        .eq(TbPipeline::getDeleting, true)
                        .orderByAsc(TbPipeline::getUpdateTime)
        );
        if (CollUtil.isEmpty(pipelines)) {
            pipelines = service.query(
                    new LambdaQueryWrapper<TbPipeline>()
                            .in(TbPipeline::getPipelineType, service.getPipelineType())
                            .isNull(TbPipeline::getProcessing)
                            .in(TbPipeline::getPipelineStatus, Arrays.asList(
                                    PipelineStatus.DELETING.code,
                                    PipelineStatus.WAITING_START.code,
                                    PipelineStatus.WAITING_STOP.code,
                                    PipelineStatus.RESTARTING.code))
                            .orderByAsc(TbPipeline::getUpdateTime)
            );
        }
        return pipelines.stream().findFirst().orElse(null);
    }
}
