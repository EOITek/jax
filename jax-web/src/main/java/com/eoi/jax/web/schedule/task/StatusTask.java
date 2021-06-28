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

package com.eoi.jax.web.schedule.task;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.consts.PipelineStatus;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.schedule.async.StatusPipelineAsync;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DisallowConcurrentExecution
public class StatusTask extends QuartzJobBean {
    @Autowired
    private StatusPipelineAsync statusPipelineAsync;

    private String flinkStatus;
    private String sparkStatus;

    public StatusTask setFlinkStatus(String flinkStatus) {
        this.flinkStatus = flinkStatus;
        return this;
    }

    public StatusTask setSparkStatus(String sparkStatus) {
        this.sparkStatus = sparkStatus;
        return this;
    }

    private List<String> getFlinkStatusList() {
        if (flinkStatus == null || flinkStatus.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(StrUtil.split(flinkStatus, ","));
    }

    private List<String> getSparkStatusList() {
        if (sparkStatus == null || sparkStatus.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(StrUtil.split(sparkStatus, ","));
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<List<TbPipeline>> list = statusPipelineAsync.getTodoStatus(
                getFlinkStatusList(),
                getSparkStatusList()
        );
        for (List<TbPipeline> pipelines : list) {
            if (hasStatus(pipelines, PipelineStatus.STARTING.code)) {
                statusPipelineAsync.executeStarting(pipelines);
            } else if (hasStatus(pipelines, PipelineStatus.STOPPING.code)) {
                statusPipelineAsync.executeStopping(pipelines);
            } else if (hasStatus(pipelines, PipelineStatus.RUNNING.code)) {
                statusPipelineAsync.executeRunning(pipelines);
            } else if (hasStatus(pipelines, PipelineStatus.STOP_FAILED.code)) {
                statusPipelineAsync.executeStopFailed(pipelines);
            } else if (hasStatus(pipelines, PipelineStatus.FAILED.code)) {
                statusPipelineAsync.executeFailed(pipelines);
            } else if (hasStatus(pipelines, PipelineStatus.STOPPED.code)) {
                statusPipelineAsync.executeStopped(pipelines);
            }
        }
    }

    private boolean hasStatus(List<TbPipeline> pipelines, String status) {
        for (TbPipeline pipeline : pipelines) {
            if (status.equals(pipeline.getPipelineStatus())) {
                return true;
            }
        }
        return false;
    }

    public static JobDataMap genStarting() {
        JobDataMap map = new JobDataMap();
        map.put("flinkStatus", PipelineStatus.STARTING.code);
        map.put("sparkStatus", PipelineStatus.STARTING.code);
        return map;
    }

    public static JobDataMap genStopping() {
        JobDataMap map = new JobDataMap();
        map.put("flinkStatus", PipelineStatus.STOPPING.code);
        map.put("sparkStatus", PipelineStatus.STOPPING.code);
        return map;
    }

    public static JobDataMap genRunning() {
        JobDataMap map = new JobDataMap();
        map.put("flinkStatus", PipelineStatus.RUNNING.code);
        map.put("sparkStatus", PipelineStatus.RUNNING.code);
        return map;
    }

    public static JobDataMap genStopFailed() {
        JobDataMap map = new JobDataMap();
        map.put("flinkStatus", PipelineStatus.STOP_FAILED.code);
        return map;
    }

    public static JobDataMap genFailed() {
        JobDataMap map = new JobDataMap();
        map.put("flinkStatus", PipelineStatus.FAILED.code);
        return map;
    }

    public static JobDataMap genStopped() {
        JobDataMap map = new JobDataMap();
        map.put("flinkStatus", PipelineStatus.STOPPED.code);
        return map;
    }
}
