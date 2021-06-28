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

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.eoi.jax.manager.api.JobResult;
import com.eoi.jax.manager.yarn.YarnJobGetResult;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.consts.PipelineStatus;
import com.eoi.jax.web.common.consts.PipelineType;
import com.eoi.jax.web.common.exception.JaxException;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.dao.entity.TbPipelineJob;
import com.eoi.jax.web.dao.entity.TbPipelineLog;
import com.eoi.jax.web.dao.service.TbPipelineJobService;
import com.eoi.jax.web.dao.service.TbPipelineLogService;
import com.eoi.jax.web.dao.service.TbPipelineService;
import com.eoi.jax.web.model.manager.OpType;
import com.eoi.jax.web.model.manager.Pipeline;
import com.eoi.jax.web.service.PipelineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public abstract class BasePipelineService {
    private static final Logger logger = LoggerFactory.getLogger(BasePipelineService.class);

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private TbPipelineService tbPipelineService;

    @Autowired
    private TbPipelineJobService tbPipelineJobService;

    @Autowired
    private TbPipelineLogService tbPipelineLogService;

    public abstract List<String> getPipelineType();

    public abstract boolean isRunning(TbPipeline entity);

    public abstract JobResult startPipeline(TbPipeline pipeline);

    public abstract JobResult stopPipeline(TbPipeline pipeline);

    public abstract JobResult deletePipeline(TbPipeline pipeline);

    public abstract JobResult getYarnPipeline(TbPipeline pipeline);

    public boolean restart(TbPipeline entity) {
        boolean success = true;
        if (isRunning(entity)) {
            success = stop(entity);
            entity = pipeline(entity.getPipelineName());
        }
        if (success) {
            success = start(entity);
        }
        return success;
    }

    public boolean start(TbPipeline entity) {
        boolean success = false;
        if (!updatePipelineStatus(entity.getPipelineName(), PipelineStatus.STARTING.code, true)) {
            return false;
        }
        String message;
        try {
            JobResult result = startPipeline(entity);
            success = result.isSuccess();
            message = JsonUtil.encode(result);
        } catch (Exception e) {
            logger.error("start pipeline " + entity.getPipelineName() + " error", e);
            updatePipelineStatus(entity.getPipelineName(), PipelineStatus.START_FAILED.code, null);
            message = ExceptionUtil.stacktraceToString(e);
        } finally {
            updatePipelineAction(entity.getPipelineName(), null);
        }
        createPipelineLog(entity.getPipelineName(), OpType.START.code, message);
        try {
            trackUrl(entity);
            trackJobId(entity);
        } catch (Exception e) {
            logger.error("track pipeline url " + entity.getPipelineName() + " error", e);
        }
        return success;
    }

    public boolean stop(TbPipeline entity) {
        boolean success = false;
        if (!updatePipelineStatus(entity.getPipelineName(), PipelineStatus.STOPPING.code, true)) {
            return false;
        }
        String message;
        try {
            JobResult result = stopPipeline(entity);
            success = result.isSuccess();
            message = JsonUtil.encode(result);
        } catch (Exception e) {
            logger.error("stop pipeline " + entity.getPipelineName() + " error", e);
            updatePipelineStatus(entity.getPipelineName(), PipelineStatus.STOP_FAILED.code, null);
            message = ExceptionUtil.stacktraceToString(e);
        } finally {
            updatePipelineAction(entity.getPipelineName(), null);
        }
        createPipelineLog(entity.getPipelineName(), OpType.STOP.code, message);
        return success;
    }

    public boolean delete(TbPipeline entity) {
        boolean success = false;
        if (!updatePipelineStatus(entity.getPipelineName(), PipelineStatus.DELETING.code, true)) {
            return false;
        }
        if (StrUtil.isNotEmpty(entity.getFlinkJobId())
                || StrUtil.isNotEmpty(entity.getSparkSubmissionId())
                || StrUtil.isNotEmpty(entity.getYarnAppId())) {
            try {
                JobResult result = deletePipeline(entity);
                success = result.isSuccess();
            } catch (Exception e) {
                logger.error("delete pipeline " + entity.getPipelineName() + " error", e);
            }
        } else {
            success = true;
        }
        pipelineService.clearPipeline(entity);
        return success;
    }

    public String trackUrl(TbPipeline entity) {
        if (StrUtil.isEmpty(entity.getYarnAppId())) {
            return null;
        }
        JobResult result = getYarnPipeline(entity);
        if (result == null) {
            return null;
        }
        YarnJobGetResult yarn = (YarnJobGetResult) result;
        if (yarn.getReport() == null) {
            logger.warn("track url {}", yarn.getMessage());
            return null;
        }
        String trackUrl = yarn.getReport().getTrackingUrl();
        entity.setTrackUrl(trackUrl);
        tbPipelineService.update(
                new LambdaUpdateWrapper<TbPipeline>()
                        .set(TbPipeline::getTrackUrl, trackUrl)
                        .eq(TbPipeline::getPipelineName, entity.getPipelineName())
        );
        return trackUrl;
    }

    /**
     * flink application mode 通过 /jobs api来获取job id
     */
    public void trackJobId(TbPipeline entity) throws InterruptedException {
        if (PipelineType.isStreaming(entity.getPipelineType())
                && StrUtil.isEmpty(entity.getFlinkJobId())
                && StrUtil.isNotEmpty(entity.getTrackUrl())
        ) {
            int maxRetry = 10;
            while (maxRetry > 0) {
                try {
                    String resp = HttpUtil.get(entity.getTrackUrl() + "jobs");
                    if (StrUtil.isNotEmpty(resp)) {
                        Map<String, Object> respMap = JsonUtil.decode2Map(resp);
                        if (respMap.containsKey("jobs")) {
                            List jobList = (List) respMap.get("jobs");
                            if (jobList.size() > 0) {
                                Map<String, Object> job = (Map<String, Object>) jobList.get(0);
                                String jobId = job.getOrDefault("id", "").toString();

                                tbPipelineService.update(
                                        new LambdaUpdateWrapper<TbPipeline>()
                                                .set(TbPipeline::getFlinkJobId, jobId)
                                                .eq(TbPipeline::getPipelineName, entity.getPipelineName())
                                );
                                logger.info("trackJobId found {} for {}", jobId, entity.getTrackUrl());
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    logger.error("trackJobId error", ex);
                }
                logger.info("trackJobId not found {}", entity.getTrackUrl());
                maxRetry--;
                // waiting for job manager start the job
                Thread.sleep(5000);
            }
        }
    }

    public List<TbPipeline> query(LambdaQueryWrapper<TbPipeline> wrapper) {
        return tbPipelineService.list(wrapper);
    }

    public void createPipelineLog(String pipelineName, String logType, String pipelineLog) {
        tbPipelineLogService.save(new TbPipelineLog()
                .setPipelineName(pipelineName)
                .setLogType(logType)
                .setPipelineLog(pipelineLog)
                .setCreateTime(System.currentTimeMillis())
        );
    }

    public boolean updatePipelineStatus(String pipelineName, String pipelineStatus, Boolean processing) {
        Long now = System.currentTimeMillis();
        return tbPipelineService.update(
                new LambdaUpdateWrapper<TbPipeline>()
                        .set(TbPipeline::getPipelineStatus, pipelineStatus)
                        .set(TbPipeline::getProcessing, processing)
                        .set(TbPipeline::getProcessTime, now)
                        .isNull(Boolean.TRUE.equals(processing), TbPipeline::getProcessing)
                        .eq(TbPipeline::getPipelineName, pipelineName)
        );
    }

    public boolean updatePipelineStatus(TbPipeline item, String originPipelineStatus, String originInternalStatus) {
        if (StrUtil.equals(originPipelineStatus, item.getPipelineStatus())
                && StrUtil.equals(originInternalStatus, item.getInternalStatus())) {
            //状态未发生变化，不需要更新
            return false;
        }
        boolean updated = tbPipelineService.update(
                new LambdaUpdateWrapper<TbPipeline>()
                        .set(TbPipeline::getPipelineStatus, item.getPipelineStatus())
                        .set(TbPipeline::getInternalStatus, item.getInternalStatus())
                        .isNull(TbPipeline::getProcessing)
                        .eq(TbPipeline::getPipelineName, item.getPipelineName())
                        .eq(TbPipeline::getPipelineStatus, originPipelineStatus)
        );
        if (updated) {
            logger.info("update {} status from {} - {} to {} - {}",
                    item.getPipelineName(),
                    originPipelineStatus,
                    originInternalStatus,
                    item.getPipelineStatus(),
                    item.getInternalStatus()
            );
        }
        return updated;
    }

    public boolean updatePipelineAction(String pipelineName, Boolean processing) {
        return tbPipelineService.update(
                new LambdaUpdateWrapper<TbPipeline>()
                        .set(TbPipeline::getProcessing, processing)
                        .eq(TbPipeline::getPipelineName, pipelineName)
        );
    }

    public TbPipeline pipeline(String pipelineName) {
        return tbPipelineService.getById(pipelineName);
    }

    public Pipeline pipeline(TbPipeline entity) {
        List<TbPipelineJob> jobs = tbPipelineJobService.list(
                new LambdaQueryWrapper<TbPipelineJob>().eq(TbPipelineJob::getPipelineName, entity.getPipelineName())
        );
        if (StrUtil.isEmpty(entity.getStartCmd()) && jobs.isEmpty()) {
            throw new JaxException(ResponseCode.JOB_NOT_EXIST.message);
        }
        return pipelineService.genPipeline(entity, jobs);
    }

}
