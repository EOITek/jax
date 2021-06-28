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

package com.eoi.jax.web.schedule;

import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.config.AppConfig;
import com.eoi.jax.web.common.config.ThreadPoolConfig;
import com.eoi.jax.web.common.exception.BizException;
import com.eoi.jax.web.provider.manager.ConsoleLogLineQueue;
import com.eoi.jax.web.schedule.task.FlinkTask;
import com.eoi.jax.web.schedule.task.SparkTask;
import com.eoi.jax.web.schedule.task.StatusTask;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JaxScheduler {
    private static final Logger logger = LoggerFactory.getLogger(JaxScheduler.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ConsoleLogLineQueue consoleLogLineQueue;

    private static final String FLINK_TRIGGER_GROUP = "flink_trigger_group";
    private static final String FLINK_JOB_GROUP = "flink_job_group";
    private static final String FLINK_TRIGGER_NAME = "flink_trigger";
    private static final String FLINK_JOB_NAME = "flink_job";
    private static final TriggerKey FLINK_TRIGGER_KEY = TriggerKey.triggerKey(FLINK_TRIGGER_NAME, FLINK_TRIGGER_GROUP);
    private static final JobKey FLINK_JOB_KEY = JobKey.jobKey(FLINK_JOB_NAME, FLINK_JOB_GROUP);

    private static final String SPARK_TRIGGER_GROUP = "spark_trigger_group";
    private static final String SPARK_JOB_GROUP = "spark_job_group";
    private static final String SPARK_TRIGGER_NAME = "spark_trigger";
    private static final String SPARK_JOB_NAME = "spark_job";
    private static final TriggerKey SPARK_TRIGGER_KEY = TriggerKey.triggerKey(SPARK_TRIGGER_NAME, SPARK_TRIGGER_GROUP);
    private static final JobKey SPARK_JOB_KEY = JobKey.jobKey(SPARK_JOB_NAME, SPARK_JOB_GROUP);

    private static final String STATUS_TRIGGER_GROUP = "status_trigger_group";
    private static final String STATUS_JOB_GROUP = "status_job_group";
    private static final String STATUS_TRIGGER_STARTING = "status_trigger_starting";
    private static final String STATUS_TRIGGER_STOPPING = "status_trigger_stopping";
    private static final String STATUS_TRIGGER_RUNNING = "status_trigger_running";
    private static final String STATUS_TRIGGER_FAILED = "status_trigger_failed";
    private static final String STATUS_TRIGGER_STOP_FAILED = "status_trigger_stop_failed";
    private static final String STATUS_TRIGGER_STOPPED = "status_trigger_stopped";
    private static final String STATUS_JOB_STARTING = "status_job_starting";
    private static final String STATUS_JOB_STOPPING = "status_job_stopping";
    private static final String STATUS_JOB_RUNNING = "status_job_running";
    private static final String STATUS_JOB_FAILED = "status_job_failed";
    private static final String STATUS_JOB_STOP_FAILED = "status_job_stop_failed";
    private static final String STATUS_JOB_STOPPED = "status_job_stopped";
    private static final TriggerKey STATUS_TRIGGER_STARTING_KEY = TriggerKey.triggerKey(STATUS_TRIGGER_STARTING, STATUS_TRIGGER_GROUP);
    private static final TriggerKey STATUS_TRIGGER_STOPPING_KEY = TriggerKey.triggerKey(STATUS_TRIGGER_STOPPING, STATUS_TRIGGER_GROUP);
    private static final TriggerKey STATUS_TRIGGER_RUNNING_KEY = TriggerKey.triggerKey(STATUS_TRIGGER_RUNNING, STATUS_TRIGGER_GROUP);
    private static final TriggerKey STATUS_TRIGGER_FAILED_KEY = TriggerKey.triggerKey(STATUS_TRIGGER_FAILED, STATUS_TRIGGER_GROUP);
    private static final TriggerKey STATUS_TRIGGER_STOP_FAILED_KEY = TriggerKey.triggerKey(STATUS_TRIGGER_STOP_FAILED, STATUS_TRIGGER_GROUP);
    private static final TriggerKey STATUS_TRIGGER_STOPPED_KEY = TriggerKey.triggerKey(STATUS_TRIGGER_STOPPED, STATUS_TRIGGER_GROUP);
    private static final JobKey STATUS_JOB_STARTING_KEY = JobKey.jobKey(STATUS_JOB_STARTING, STATUS_JOB_GROUP);
    private static final JobKey STATUS_JOB_STOPPING_KEY = JobKey.jobKey(STATUS_JOB_STOPPING, STATUS_JOB_GROUP);
    private static final JobKey STATUS_JOB_RUNNING_KEY = JobKey.jobKey(STATUS_JOB_RUNNING, STATUS_JOB_GROUP);
    private static final JobKey STATUS_JOB_FAILED_KEY = JobKey.jobKey(STATUS_JOB_FAILED, STATUS_JOB_GROUP);
    private static final JobKey STATUS_JOB_STOP_FAILED_KEY = JobKey.jobKey(STATUS_JOB_STOP_FAILED, STATUS_JOB_GROUP);
    private static final JobKey STATUS_JOB_STOPPED_KEY = JobKey.jobKey(STATUS_JOB_STOPPED, STATUS_JOB_GROUP);

    public void initialize() {
        try {
            scheduler.clear();
            schedule(true);
            scheduler.start();
        } catch (Exception e) {
            logger.info("Quartz Error", e);
        }
    }

    public void destroy() {
        try {
            scheduler.shutdown(false);
        } catch (SchedulerException e) {
            logger.info("Quartz Error", e);
        }
    }

    public void restart() {
        try {
            schedule(true);
        } catch (Exception e) {
            logger.info("Quartz Error", e);
            throw new BizException(ResponseCode.FAILED, e);
        }
    }

    public void schedule(boolean reschedule) throws SchedulerException {
        ThreadPoolConfig config = appConfig.jax.getThread();
        schedule(FLINK_TRIGGER_KEY, FLINK_JOB_KEY, config.getFlinkScheduleCron(), FlinkTask.class, new JobDataMap(), reschedule);
        schedule(SPARK_TRIGGER_KEY, SPARK_JOB_KEY, config.getSparkScheduleCron(), SparkTask.class, new JobDataMap(), reschedule);
        schedule(STATUS_TRIGGER_STARTING_KEY, STATUS_JOB_STARTING_KEY, config.getStatusStartingScheduleCron(), StatusTask.class, StatusTask.genStarting(), reschedule);
        schedule(STATUS_TRIGGER_STOPPING_KEY, STATUS_JOB_STOPPING_KEY, config.getStatusStoppingScheduleCron(), StatusTask.class, StatusTask.genStopping(), reschedule);
        schedule(STATUS_TRIGGER_RUNNING_KEY, STATUS_JOB_RUNNING_KEY, config.getStatusRunningScheduleCron(), StatusTask.class, StatusTask.genRunning(), reschedule);
        schedule(STATUS_TRIGGER_FAILED_KEY, STATUS_JOB_FAILED_KEY, config.getStatusFailedScheduleCron(), StatusTask.class, StatusTask.genFailed(), reschedule);
        schedule(STATUS_TRIGGER_STOP_FAILED_KEY, STATUS_JOB_STOP_FAILED_KEY, config.getStatusStopFailedScheduleCron(), StatusTask.class, StatusTask.genStopFailed(), reschedule);
        schedule(STATUS_TRIGGER_STOPPED_KEY, STATUS_JOB_STOPPED_KEY, config.getStatusStoppedScheduleCron(), StatusTask.class, StatusTask.genStopped(), reschedule);
    }

    private void schedule(TriggerKey triggerKey, JobKey jobKey, String cron, Class<? extends Job> jobClz, JobDataMap jobData, boolean reschedule) throws SchedulerException {
        if (scheduler.getTrigger(triggerKey) != null) {
            if (reschedule) {
                logger.info("Quartz Reschedule: {}", jobKey);
                scheduler.unscheduleJob(triggerKey);
            } else {
                logger.info("Quartz Exist: {}", jobKey);
                return;
            }
        }
        JobDetail jobDetail = JobBuilder
                .newJob(jobClz)
                .setJobData(jobData)
                .withIdentity(jobKey)
                .build();
        CronScheduleBuilder cronBuilder = CronScheduleBuilder
                .cronSchedule(cron)
                .withMisfireHandlingInstructionDoNothing();
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(cronBuilder)
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
        logger.info("Quartz Init: {}", jobKey);
    }

    @Scheduled(cron = "0/2 * * * * ?")
    public void consoleLogPersist() {
        consoleLogLineQueue.persist();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void consoleLogRetention() {
        consoleLogLineQueue.retention();
    }
}
