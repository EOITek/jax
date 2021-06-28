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

package com.eoi.jax.web.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.eoi.jax.web.common.consts.PipelineStatus;
import com.eoi.jax.web.common.consts.SparkStatus;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.dao.entity.TbSparkEvent;
import com.eoi.jax.web.dao.service.TbPipelineService;
import com.eoi.jax.web.dao.service.TbSparkEventService;
import com.eoi.jax.web.model.listener.SparkStateReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ListenerService {
    private static final Logger logger = LoggerFactory.getLogger(ListenerService.class);

    @Autowired
    private TbPipelineService tbPipelineService;

    @Autowired
    private TbSparkEventService tbSparkEventService;

    public TbPipeline sparkState(String pipelineName, SparkStateReq req) {
        String resStr = JsonUtil.encode(req);
        logger.info("spark state {}: {}", pipelineName, resStr);
        TbPipeline entity = tbPipelineService.getById(pipelineName);
        if (entity == null) {
            return null;
        }
        String sparkAppId = req.getApplicationId();
        String sparkRestUrl = req.getRestUrl();
        String pipelineStatus = null;
        String internalStatus = null;
        PipelineStatus sparkStatus = SparkStatus.mapStatus(req.getState());
        boolean fromClosed = PipelineStatus.STOPPED.isEqual(entity.getPipelineStatus())
                || PipelineStatus.FINISHED.isEqual(entity.getPipelineStatus())
                || PipelineStatus.FAILED.isEqual(entity.getPipelineStatus());
        boolean toClosed = SparkStatus.KILLED.isEqual(req.getState())
                || SparkStatus.FINISHED.isEqual(req.getState())
                || SparkStatus.FAILED.isEqual(req.getState());
        // 终结状态下不再变更状态（STOPPED，FINISHED，FAILED）
        if (sparkStatus != null && !(fromClosed && toClosed)) {
            internalStatus = req.getState();
            pipelineStatus = sparkStatus.code;
            // 正在停止时，发现结束则认为已停止
            if (PipelineStatus.STOPPING.isEqual(entity.getPipelineStatus()) && toClosed) {
                pipelineStatus = PipelineStatus.STOPPED.code;
            }
        }
        if (StrUtil.isEmpty(sparkAppId)
                && StrUtil.isEmpty(sparkRestUrl)
                && StrUtil.isEmpty(pipelineStatus)
                && StrUtil.isEmpty(internalStatus)) {
            return entity;
        }
        tbPipelineService.update(new LambdaUpdateWrapper<TbPipeline>()
                .set(StrUtil.isNotEmpty(sparkAppId), TbPipeline::getSparkAppId, sparkAppId)
                .set(StrUtil.isNotEmpty(sparkRestUrl), TbPipeline::getSparkRestUrl, sparkRestUrl)
                .set(StrUtil.isNotEmpty(pipelineStatus), TbPipeline::getPipelineStatus, pipelineStatus)
                .set(StrUtil.isNotEmpty(internalStatus), TbPipeline::getInternalStatus, internalStatus)
                .eq(TbPipeline::getPipelineName, pipelineName)
        );

        logger.info("update {} status to {} - {}",
                pipelineName,
                pipelineStatus,
                internalStatus
        );
        return entity;
    }

    public TbPipeline sparkEvent(String pipelineName, Map<String, Object> req) {
        String resStr = JsonUtil.encode(req);
        logger.info("spark event {}: {}", pipelineName, resStr);
        TbPipeline entity = tbPipelineService.getById(pipelineName);
        if (entity == null) {
            return null;
        }
        long now = System.currentTimeMillis();
        TbSparkEvent event = new TbSparkEvent()
                .setPipelineName(pipelineName)
                .setEventLog(JsonUtil.encode(req))
                .setCreateTime(now);
        tbSparkEventService.save(event);
        return entity;
    }
}
