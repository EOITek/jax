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

package com.eoi.jax.web.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConfigurationProperties(prefix = "jax.thread")
public class ThreadPoolConfig implements ConfigToMap {
    private String flinkScheduleCron = "0/1 * * * * ?";
    private Integer flinkThreadPoolSize = 4;

    private String sparkScheduleCron = "0/1 * * * * ?";
    private Integer sparkThreadPoolSize = 4;

    private String statusStartingScheduleCron = "0/5 * * * * ?";
    private Integer statusStartingThreadPoolSize = 4;

    private String statusStoppingScheduleCron = "0/5 * * * * ?";
    private Integer statusStoppingThreadPoolSize = 4;

    private String statusRunningScheduleCron = "0 * * * * ?";
    private Integer statusRunningThreadPoolSize = 1;

    private String statusFailedScheduleCron = "0 * * * * ?";
    private Integer statusFailedThreadPoolSize = 1;

    private String statusStoppedScheduleCron = "0 * * * * ?";
    private Integer statusStoppedThreadPoolSize = 1;

    private String statusStopFailedScheduleCron = "0 * * * * ?";
    private Integer statusStopFailedThreadPoolSize = 1;

    public String getFlinkScheduleCron() {
        return flinkScheduleCron;
    }

    public ThreadPoolConfig setFlinkScheduleCron(String flinkScheduleCron) {
        this.flinkScheduleCron = flinkScheduleCron;
        return this;
    }

    public Integer getFlinkThreadPoolSize() {
        return flinkThreadPoolSize;
    }

    public ThreadPoolConfig setFlinkThreadPoolSize(Integer flinkThreadPoolSize) {
        this.flinkThreadPoolSize = flinkThreadPoolSize;
        return this;
    }

    public String getSparkScheduleCron() {
        return sparkScheduleCron;
    }

    public ThreadPoolConfig setSparkScheduleCron(String sparkScheduleCron) {
        this.sparkScheduleCron = sparkScheduleCron;
        return this;
    }

    public Integer getSparkThreadPoolSize() {
        return sparkThreadPoolSize;
    }

    public ThreadPoolConfig setSparkThreadPoolSize(Integer sparkThreadPoolSize) {
        this.sparkThreadPoolSize = sparkThreadPoolSize;
        return this;
    }

    public String getStatusStartingScheduleCron() {
        return statusStartingScheduleCron;
    }

    public ThreadPoolConfig setStatusStartingScheduleCron(String statusStartingScheduleCron) {
        this.statusStartingScheduleCron = statusStartingScheduleCron;
        return this;
    }

    public Integer getStatusStartingThreadPoolSize() {
        return statusStartingThreadPoolSize;
    }

    public ThreadPoolConfig setStatusStartingThreadPoolSize(Integer statusStartingThreadPoolSize) {
        this.statusStartingThreadPoolSize = statusStartingThreadPoolSize;
        return this;
    }

    public String getStatusStoppingScheduleCron() {
        return statusStoppingScheduleCron;
    }

    public ThreadPoolConfig setStatusStoppingScheduleCron(String statusStoppingScheduleCron) {
        this.statusStoppingScheduleCron = statusStoppingScheduleCron;
        return this;
    }

    public Integer getStatusStoppingThreadPoolSize() {
        return statusStoppingThreadPoolSize;
    }

    public ThreadPoolConfig setStatusStoppingThreadPoolSize(Integer statusStoppingThreadPoolSize) {
        this.statusStoppingThreadPoolSize = statusStoppingThreadPoolSize;
        return this;
    }

    public String getStatusRunningScheduleCron() {
        return statusRunningScheduleCron;
    }

    public ThreadPoolConfig setStatusRunningScheduleCron(String statusRunningScheduleCron) {
        this.statusRunningScheduleCron = statusRunningScheduleCron;
        return this;
    }

    public Integer getStatusRunningThreadPoolSize() {
        return statusRunningThreadPoolSize;
    }

    public ThreadPoolConfig setStatusRunningThreadPoolSize(Integer statusRunningThreadPoolSize) {
        this.statusRunningThreadPoolSize = statusRunningThreadPoolSize;
        return this;
    }

    public String getStatusFailedScheduleCron() {
        return statusFailedScheduleCron;
    }

    public ThreadPoolConfig setStatusFailedScheduleCron(String statusFailedScheduleCron) {
        this.statusFailedScheduleCron = statusFailedScheduleCron;
        return this;
    }

    public Integer getStatusFailedThreadPoolSize() {
        return statusFailedThreadPoolSize;
    }

    public ThreadPoolConfig setStatusFailedThreadPoolSize(Integer statusFailedThreadPoolSize) {
        this.statusFailedThreadPoolSize = statusFailedThreadPoolSize;
        return this;
    }

    public String getStatusStoppedScheduleCron() {
        return statusStoppedScheduleCron;
    }

    public ThreadPoolConfig setStatusStoppedScheduleCron(String statusStoppedScheduleCron) {
        this.statusStoppedScheduleCron = statusStoppedScheduleCron;
        return this;
    }

    public Integer getStatusStoppedThreadPoolSize() {
        return statusStoppedThreadPoolSize;
    }

    public ThreadPoolConfig setStatusStoppedThreadPoolSize(Integer statusStoppedThreadPoolSize) {
        this.statusStoppedThreadPoolSize = statusStoppedThreadPoolSize;
        return this;
    }

    public String getStatusStopFailedScheduleCron() {
        return statusStopFailedScheduleCron;
    }

    public ThreadPoolConfig setStatusStopFailedScheduleCron(String statusStopFailedScheduleCron) {
        this.statusStopFailedScheduleCron = statusStopFailedScheduleCron;
        return this;
    }

    public Integer getStatusStopFailedThreadPoolSize() {
        return statusStopFailedThreadPoolSize;
    }

    public ThreadPoolConfig setStatusStopFailedThreadPoolSize(Integer statusStopFailedThreadPoolSize) {
        this.statusStopFailedThreadPoolSize = statusStopFailedThreadPoolSize;
        return this;
    }

    @Bean("FlinkThreadPoolTaskExecutor")
    public AsyncTaskExecutor flinkThreadPoolTaskExecutor() {
        return createThreadPoolTaskExecutor(
                "FlinkThreadPoolTaskExecutor",
                getFlinkThreadPoolSize());
    }

    @Bean("SparkThreadPoolTaskExecutor")
    public AsyncTaskExecutor sparkThreadPoolTaskExecutor() {
        return createThreadPoolTaskExecutor(
                "SparkThreadPoolTaskExecutor",
                getSparkThreadPoolSize());
    }

    @Bean("StatusStartingThreadPoolTaskExecutor")
    public AsyncTaskExecutor statusStartingThreadPoolTaskExecutor() {
        return createThreadPoolTaskExecutor(
                "StatusStartingThreadPoolTaskExecutor",
                getStatusStartingThreadPoolSize());
    }

    @Bean("StatusStoppingThreadPoolTaskExecutor")
    public AsyncTaskExecutor statusStoppingThreadPoolTaskExecutor() {
        return createThreadPoolTaskExecutor(
                "StatusStoppingThreadPoolTaskExecutor",
                getStatusStoppingThreadPoolSize());
    }

    @Bean("StatusRunningThreadPoolTaskExecutor")
    public AsyncTaskExecutor statusRunningThreadPoolTaskExecutor() {
        return createThreadPoolTaskExecutor(
                "StatusRunningThreadPoolTaskExecutor",
                getStatusRunningThreadPoolSize());
    }

    @Bean("StatusFailedThreadPoolTaskExecutor")
    public AsyncTaskExecutor statusFailedThreadPoolTaskExecutor() {
        return createThreadPoolTaskExecutor(
                "StatusFailedThreadPoolTaskExecutor",
                getStatusFailedThreadPoolSize());
    }

    @Bean("StatusStoppedThreadPoolTaskExecutor")
    public AsyncTaskExecutor statusStoppedThreadPoolTaskExecutor() {
        return createThreadPoolTaskExecutor(
                "StatusStoppedThreadPoolTaskExecutor",
                getStatusStoppedThreadPoolSize());
    }

    @Bean("StatusStopFailedThreadPoolTaskExecutor")
    public AsyncTaskExecutor statusStopFailedThreadPoolTaskExecutor() {
        return createThreadPoolTaskExecutor(
                "StatusStopFailedThreadPoolTaskExecutor",
                getStatusStopFailedThreadPoolSize());
    }

    private ThreadPoolTaskExecutor createThreadPoolTaskExecutor(
            String threadNamePrefix,
            int poolSize) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix(threadNamePrefix);
        taskExecutor.setCorePoolSize(poolSize);
        taskExecutor.setMaxPoolSize(poolSize);
        taskExecutor.setQueueCapacity(0);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.setRejectedExecutionHandler((runnable, executor) -> {
            if (!executor.isShutdown()) {
                try {
                    executor.getQueue().put(runnable);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        return taskExecutor;
    }
}
