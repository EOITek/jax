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
import com.eoi.jax.manager.api.JobGetResult;
import com.eoi.jax.manager.api.JobResult;
import com.eoi.jax.manager.spark.SparkJobStartResult;
import com.eoi.jax.manager.yarn.YarnAppReport;
import com.eoi.jax.manager.yarn.YarnJobGetResult;
import com.eoi.jax.web.common.consts.PipelineStatus;
import com.eoi.jax.web.common.consts.PipelineType;
import com.eoi.jax.web.common.consts.SparkStatus;
import com.eoi.jax.web.common.consts.YarnState;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.dao.service.TbPipelineService;
import com.eoi.jax.web.model.manager.Pipeline;
import com.eoi.jax.web.provider.SparkPipelineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SparkPipelineService extends BasePipelineService {
    private static final Logger logger = LoggerFactory.getLogger(SparkPipelineService.class);

    @Autowired
    private TbPipelineService tbPipelineService;

    @Autowired
    private SparkPipelineManager sparkPipelineManager;

    @Override
    public List<String> getPipelineType() {
        return Arrays.asList(PipelineType.BATCH.code);
    }

    @Override
    public boolean isRunning(TbPipeline entity) {
        return SparkStatus.isRunning(entity.getInternalStatus());
    }

    public boolean status(List<TbPipeline> batch) {
        boolean success = false;
        for (TbPipeline entity : batch) {
            try {
                String originPipelineStatus = entity.getPipelineStatus();
                String originInternalStatus = entity.getInternalStatus();
                Pipeline param = pipeline(entity);
                JobGetResult result = sparkPipelineManager.getYarnApp(param);
                YarnJobGetResult yarn = (YarnJobGetResult) result;
                if (!yarn.isSuccess() || yarn.getReport() == null) {
                    break;
                }
                TbPipeline item = setSparkJobStatus(entity, yarn.getReport());
                updatePipelineStatus(item, originPipelineStatus, originInternalStatus);
            } catch (Exception e) {
                logger.error("status pipeline error", e);
            }
        }
        return success;
    }

    private TbPipeline setSparkJobStatus(TbPipeline entity, YarnAppReport report) {
        if (PipelineStatus.STARTING.isEqual(entity.getPipelineStatus())) {
            if (YarnState.RUNNING.isEqual(report.getState())) {
                entity.setPipelineStatus(PipelineStatus.RUNNING.code);
                entity.setInternalStatus(SparkStatus.STARTED.code);
            } else if (YarnState.FINISHED.isEqual(report.getState())) {
                entity.setPipelineStatus(PipelineStatus.FINISHED.code);
                entity.setInternalStatus(SparkStatus.FINISHED.code);
            } else if (YarnState.FAILED.isEqual(report.getState())) {
                entity.setPipelineStatus(PipelineStatus.FAILED.code);
                entity.setInternalStatus(SparkStatus.FAILED.code);
            } else if (YarnState.KILLED.isEqual(report.getState())) {
                entity.setPipelineStatus(PipelineStatus.STOPPED.code);
                entity.setInternalStatus(SparkStatus.FAILED.code);
            }
        } else if (PipelineStatus.STOPPING.isEqual(entity.getPipelineStatus())) {
            if (YarnState.FINISHED.isEqual(report.getState())) {
                entity.setPipelineStatus(PipelineStatus.STOPPED.code);
                entity.setInternalStatus(SparkStatus.FINISHED.code);
            } else if (YarnState.FAILED.isEqual(report.getState())) {
                entity.setPipelineStatus(PipelineStatus.STOPPED.code);
                entity.setInternalStatus(SparkStatus.FAILED.code);
            } else if (YarnState.KILLED.isEqual(report.getState())) {
                entity.setPipelineStatus(PipelineStatus.STOPPED.code);
                entity.setInternalStatus(SparkStatus.FAILED.code);
            }
        } else if (PipelineStatus.RUNNING.isEqual(entity.getPipelineStatus())) {
            if (YarnState.FINISHED.isEqual(report.getState())) {
                entity.setPipelineStatus(PipelineStatus.FINISHED.code);
                entity.setInternalStatus(SparkStatus.FINISHED.code);
            } else if (YarnState.FAILED.isEqual(report.getState())) {
                entity.setPipelineStatus(PipelineStatus.FAILED.code);
                entity.setInternalStatus(SparkStatus.FAILED.code);
            } else if (YarnState.KILLED.isEqual(report.getState())) {
                entity.setPipelineStatus(PipelineStatus.STOPPED.code);
                entity.setInternalStatus(SparkStatus.FAILED.code);
            }
        }
        return entity;
    }

    @Override
    public JobResult getYarnPipeline(TbPipeline pipeline) {
        Pipeline param = pipeline(pipeline);
        return sparkPipelineManager.getYarnApp(param);
    }

    @Override
    public JobResult startPipeline(TbPipeline pipeline) {
        Pipeline param = pipeline(pipeline);
        //先清空内部状态，再启动，spark可能会在启动命令执行完之前回调写入状态
        tbPipelineService.update(new LambdaUpdateWrapper<TbPipeline>()
                .set(TbPipeline::getInternalStatus, null)//清空内部状态
                .eq(TbPipeline::getPipelineName, pipeline.getPipelineName())
        );
        JobResult result = sparkPipelineManager.start(param);
        SparkJobStartResult spark = (SparkJobStartResult) result;
        pipeline.setSparkSubmissionId(spark.getSubmissionId());
        pipeline.setYarnAppId(spark.getYarnId());
        tbPipelineService.update(
                new LambdaUpdateWrapper<TbPipeline>()
                        .set(!spark.isSuccess(), TbPipeline::getPipelineStatus, PipelineStatus.START_FAILED.code)
                        .set(TbPipeline::getSparkSubmissionId, spark.getSubmissionId())
                        .set(TbPipeline::getYarnAppId, spark.getYarnId())
                        .eq(TbPipeline::getPipelineName, pipeline.getPipelineName())
        );
        return result;
    }

    @Override
    public JobResult stopPipeline(TbPipeline pipeline) {
        Pipeline param = pipeline(pipeline);
        JobResult result;
        if (StrUtil.isNotEmpty(pipeline.getYarnAppId())) {
            result = sparkPipelineManager.killYarnApp(param);
        } else {
            result = sparkPipelineManager.stop(param);
        }

        if (!result.isSuccess()) {
            tbPipelineService.update(
                    new LambdaUpdateWrapper<TbPipeline>()
                            .set(TbPipeline::getPipelineStatus, PipelineStatus.STOP_FAILED.code)
                            .eq(TbPipeline::getPipelineName, pipeline.getPipelineName())
            );
        }
        return result;
    }

    @Override
    public JobResult deletePipeline(TbPipeline pipeline) {
        Pipeline param = pipeline(pipeline);
        if (StrUtil.isNotEmpty(pipeline.getYarnAppId())) {
            return sparkPipelineManager.killYarnApp(param);
        }
        return sparkPipelineManager.delete(param);
    }
}
