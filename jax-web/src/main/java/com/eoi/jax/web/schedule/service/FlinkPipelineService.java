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

package com.eoi.jax.web.schedule.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.eoi.jax.manager.api.JobResult;
import com.eoi.jax.manager.flink.FlinkJobListResult;
import com.eoi.jax.manager.flink.FlinkJobStartResult;
import com.eoi.jax.manager.flink.FlinkJobState;
import com.eoi.jax.manager.flink.FlinkJobStopResult;
import com.eoi.jax.manager.yarn.YarnJobGetResult;
import com.eoi.jax.web.common.consts.FlinkStatus;
import com.eoi.jax.web.common.consts.PipelineStatus;
import com.eoi.jax.web.common.consts.PipelineType;
import com.eoi.jax.web.common.consts.YarnState;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.dao.service.TbPipelineService;
import com.eoi.jax.web.model.manager.Pipeline;
import com.eoi.jax.web.provider.FlinkPipelineManager;
import com.eoi.jax.web.provider.SparkPipelineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlinkPipelineService extends BasePipelineService {
    private static final Logger logger = LoggerFactory.getLogger(FlinkPipelineService.class);

    @Autowired
    private TbPipelineService tbPipelineService;

    @Autowired
    private FlinkPipelineManager flinkPipelineManager;

    @Autowired
    private SparkPipelineManager sparkPipelineManager;

    @Override
    public List<String> getPipelineType() {
        return Arrays.asList(PipelineType.STREAMING.code, PipelineType.FLINK_SQL.code, PipelineType.FLINK_CMD.code);
    }

    @Override
    public boolean isRunning(TbPipeline entity) {
        return FlinkStatus.isRunning(entity.getInternalStatus());
    }

    public boolean status(List<TbPipeline> batch) {
        Map<String, String> pipelineStatusMap = batch.stream()
                .collect(
                        HashMap::new,
                        (m, v) -> m.put(v.getPipelineName(), v.getPipelineStatus()),
                        HashMap::putAll
                );
        Map<String, String> internalStatusMap = batch.stream()
                .collect(
                        HashMap::new,
                        (m, v) -> m.put(v.getPipelineName(), v.getInternalStatus()),
                        HashMap::putAll
                );
        try {
            List<Pipeline> params = batch.stream()
                    .map(this::pipeline)
                    .collect(Collectors.toList());
            JobResult result = flinkPipelineManager.list(params);
            FlinkJobListResult flink = (FlinkJobListResult) result;
            for (TbPipeline entity : batch) {
                final String flinkJobId = entity.getFlinkJobId();
                final String originPipelineStatus = pipelineStatusMap.get(entity.getPipelineName());
                final String originInternalStatus = internalStatusMap.get(entity.getPipelineName());
                Optional<FlinkJobState> state = flink.getJobList().stream()
                        .filter(i -> flinkJobId.equalsIgnoreCase(i.getJobId()))
                        .findFirst();
                Pipeline param = pipeline(entity);
                YarnJobGetResult yarnResult = null;
                if (StrUtil.isNotEmpty(entity.getYarnAppId())) {
                    yarnResult = (YarnJobGetResult) sparkPipelineManager.getYarnApp(param);
                }
                TbPipeline item = setFlinkJobStatus(entity, state, yarnResult);
                updatePipelineStatus(item, originPipelineStatus, originInternalStatus);
            }
            return true;
        } catch (Exception e) {
            logger.error("status pipeline error", e);
        }
        return false;
    }

    private TbPipeline setFlinkJobStatus(TbPipeline entity, Optional<FlinkJobState> state, YarnJobGetResult yarnResult) {
        if (state.isPresent()) {
            FlinkJobState flinkJobState = state.get();
            entity.setInternalStatus(flinkJobState.getJobState());
            PipelineStatus pipelineStatus = FlinkStatus.mapStatus(flinkJobState.getJobState());
            if (pipelineStatus != null) {
                entity.setPipelineStatus(pipelineStatus.code);
            }
        } else {
            entity.setInternalStatus(FlinkStatus.NOT_FOUND.code);
            if (PipelineStatus.RUNNING.isEqual(entity.getPipelineStatus())) {
                if (yarnResult != null && yarnResult.isSuccess() && yarnResult.getReport() != null) {
                    if (YarnState.FINISHED.isEqual(yarnResult.getReport().getState())
                            && (YarnState.SUCCEEDED.isEqual(yarnResult.getReport().getFinalStatus())
                                    || YarnState.KILLED.isEqual(yarnResult.getReport().getFinalStatus()))) {
                        entity.setPipelineStatus(PipelineStatus.STOPPED.code);
                    } else {
                        entity.setPipelineStatus(PipelineStatus.FAILED.code);
                    }
                } else {
                    entity.setPipelineStatus(PipelineStatus.FAILED.code);
                }
            } else if (PipelineStatus.STOPPING.isEqual(entity.getPipelineStatus())
                    || PipelineStatus.STOPPED.isEqual(entity.getPipelineStatus())
                    || PipelineStatus.STOP_FAILED.isEqual(entity.getPipelineStatus())) {
                // 已经停止的flink可能无法找到其状态，所以当找不到状态时认为已其停止
                entity.setPipelineStatus(PipelineStatus.STOPPED.code);
            }  else if (PipelineStatus.STARTING.isEqual(entity.getPipelineStatus())
                    && (System.currentTimeMillis() - entity.getUpdateTime()) > 60 * 1000L) {
                // 容忍时间1分钟，1分钟内还未发现flink任务，则认为其已经失败
                entity.setPipelineStatus(PipelineStatus.FAILED.code);
            }
        }
        return entity;
    }


    @Override
    public JobResult getYarnPipeline(TbPipeline pipeline) {
        Pipeline param = pipeline(pipeline);
        return flinkPipelineManager.getYarnApp(param);
    }

    @Override
    public JobResult startPipeline(TbPipeline pipeline) {
        boolean incompatible = Boolean.TRUE.equals(pipeline.getFlinkSavePointIncompatible());
        if (incompatible) {
            // 不兼容时，清空savepoint
            pipeline.setFlinkSavePoint("");
            logger.warn("find incompatible config and clear savepoint: {}", pipeline.getPipelineName());
        }
        Pipeline param = pipeline(pipeline);
        //先清空内部状态，再启动
        tbPipelineService.update(new LambdaUpdateWrapper<TbPipeline>()
                .set(TbPipeline::getInternalStatus, null)//清空内部状态
                .eq(TbPipeline::getPipelineName, pipeline.getPipelineName())
        );
        JobResult result = flinkPipelineManager.start(param);
        FlinkJobStartResult flink = (FlinkJobStartResult) result;
        pipeline.setFlinkJobId(flink.getJobId());
        pipeline.setYarnAppId(flink.getYarnId());
        tbPipelineService.update(
                new LambdaUpdateWrapper<TbPipeline>()
                        .set(!flink.isSuccess(), TbPipeline::getPipelineStatus, PipelineStatus.START_FAILED.code)
                        // 不兼容时，清空savepoint
                        .set(incompatible, TbPipeline::getFlinkSavePoint, "")
                        .set(TbPipeline::getFlinkJobId, flink.getJobId())
                        .set(TbPipeline::getYarnAppId, flink.getYarnId())
                        .eq(TbPipeline::getPipelineName, pipeline.getPipelineName())
        );
        return result;
    }

    @Override
    public JobResult stopPipeline(TbPipeline pipeline) {
        Pipeline param = pipeline(pipeline);
        boolean disable = Boolean.TRUE.equals(pipeline.getFlinkSavePointDisable());
        if (disable) {
            param.getCluster().getFlinkOpts().setSavepointURI("");
            logger.warn("find disabled savepoint: {}", pipeline.getPipelineName());
        }
        JobResult result = flinkPipelineManager.stop(param);
        FlinkJobStopResult flink = (FlinkJobStopResult) result;
        tbPipelineService.update(
                new LambdaUpdateWrapper<TbPipeline>()
                        .set(!flink.isSuccess(), TbPipeline::getPipelineStatus, PipelineStatus.STOP_FAILED.code)
                        .set(TbPipeline::getFlinkSavePoint, flink.getSavePoint())
                        .eq(TbPipeline::getPipelineName, pipeline.getPipelineName())
        );
        return result;
    }

    @Override
    public JobResult deletePipeline(TbPipeline pipeline) {
        Pipeline param = pipeline(pipeline);
        if (StrUtil.isNotEmpty(pipeline.getYarnAppId())) {
            return flinkPipelineManager.killYarnApp(param);
        }
        return flinkPipelineManager.delete(param);
    }
}
