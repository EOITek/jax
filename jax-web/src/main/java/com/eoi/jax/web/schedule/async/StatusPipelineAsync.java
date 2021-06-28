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
import com.eoi.jax.web.common.consts.PipelineType;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.dao.service.TbPipelineService;
import com.eoi.jax.web.schedule.service.FlinkPipelineService;
import com.eoi.jax.web.schedule.service.SparkPipelineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StatusPipelineAsync {
    private static final Logger logger = LoggerFactory.getLogger(StatusPipelineAsync.class);

    @Autowired
    private TbPipelineService tbPipelineService;

    @Autowired
    private FlinkPipelineService flinkPipelineService;

    @Autowired
    private SparkPipelineService sparkPipelineService;

    @Async("StatusStartingThreadPoolTaskExecutor")
    public void executeStarting(List<TbPipeline> pipelines) {
        logger.debug("executeStarting");
        executeStatus(pipelines);
    }

    @Async("StatusStoppingThreadPoolTaskExecutor")
    public void executeStopping(List<TbPipeline> pipelines) {
        logger.debug("executeStopping");
        executeStatus(pipelines);
    }

    @Async("StatusRunningThreadPoolTaskExecutor")
    public void executeRunning(List<TbPipeline> pipelines) {
        logger.debug("executeRunning");
        executeStatus(pipelines);
    }

    @Async("StatusFailedThreadPoolTaskExecutor")
    public void executeFailed(List<TbPipeline> pipelines) {
        logger.debug("executeFailed");
        executeStatus(pipelines);
    }

    @Async("StatusStoppedThreadPoolTaskExecutor")
    public void executeStopped(List<TbPipeline> pipelines) {
        logger.debug("executeStopped");
        executeStatus(pipelines);
    }

    @Async("StatusStopFailedThreadPoolTaskExecutor")
    public void executeStopFailed(List<TbPipeline> pipelines) {
        logger.debug("executeStopFailed");
        executeStatus(pipelines);
    }

    private void executeStatus(List<TbPipeline> pipelines) {
        TbPipeline first = pipelines.get(0);
        if (PipelineType.isStreaming(first.getPipelineType())) {
            flinkPipelineService.status(pipelines);
        } else if (PipelineType.isBatch(first.getPipelineType())) {
            sparkPipelineService.status(pipelines);
        }
    }

    public List<List<TbPipeline>> getTodoStatus(List<String> flinkStatus, List<String> sparkStatus) {
        List<TbPipeline> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(flinkStatus)) {
            list.addAll(getFlinkTodoList(flinkStatus));
        }
        if (CollUtil.isNotEmpty(sparkStatus)) {
            list.addAll(getSparkTodoList(sparkStatus));
        }
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        list = list.stream()
                .sorted(Comparator.comparingLong(TbPipeline::getUpdateTime).reversed())
                .collect(Collectors.toList());
        return groupList(list);
    }

    private List<TbPipeline> getFlinkTodoList(List<String> pipelineStatus) {
        return tbPipelineService.list(
                new LambdaQueryWrapper<TbPipeline>()
                        .in(TbPipeline::getPipelineStatus, pipelineStatus)
                        .in(TbPipeline::getPipelineType, Arrays.asList(PipelineType.STREAMING.code, PipelineType.FLINK_SQL.code, PipelineType.FLINK_CMD.code))
                        .isNotNull(TbPipeline::getFlinkJobId)
                        .isNull(TbPipeline::getProcessing) // 正在处理的不能同步其状态
        );
    }

    private List<TbPipeline> getSparkTodoList(List<String> pipelineStatus) {
        return tbPipelineService.list(
                new LambdaQueryWrapper<TbPipeline>()
                        .in(TbPipeline::getPipelineStatus, pipelineStatus)
                        .eq(TbPipeline::getPipelineType, PipelineType.BATCH.code)
                        .isNotNull(TbPipeline::getYarnAppId)
                        .isNull(TbPipeline::getProcessing) // 正在处理的不能同步其状态
        );
    }

    private List<List<TbPipeline>> groupList(List<TbPipeline> pipelines) {
        Map<String, List<TbPipeline>> batchMap = new LinkedHashMap<>();
        for (TbPipeline pipeline : pipelines) {
            String key = pipeline.getPipelineType() + "-" + pipeline.getClusterName() + "-" + pipeline.getYarnAppId();
            List<TbPipeline> list = batchMap.computeIfAbsent(key, k -> new ArrayList<>());
            list.add(pipeline);
        }
        return new ArrayList<>(batchMap.values());
    }
}
