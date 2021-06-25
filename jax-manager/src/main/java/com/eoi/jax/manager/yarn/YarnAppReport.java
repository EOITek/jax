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

package com.eoi.jax.manager.yarn;

import org.apache.hadoop.yarn.api.records.ApplicationReport;

public class YarnAppReport {
    private String applicationId;
    private String name;
    private String state;
    private String trackingUrl;
    private String queue;
    private Long startTime;
    private Long finishTime;
    private String finalStatus;

    public static YarnAppReport fromReport(ApplicationReport report) {
        YarnAppReport app = new YarnAppReport();
        app.setApplicationId(report.getApplicationId().toString());
        app.setName(report.getName());
        app.setState(report.getYarnApplicationState().name());
        app.setTrackingUrl(report.getTrackingUrl());
        app.setQueue(report.getQueue());
        app.setStartTime(report.getStartTime());
        app.setFinishTime(report.getFinishTime());
        app.setFinalStatus(report.getFinalApplicationStatus().name());
        return app;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public YarnAppReport setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getName() {
        return name;
    }

    public YarnAppReport setName(String name) {
        this.name = name;
        return this;
    }

    public String getState() {
        return state;
    }

    public YarnAppReport setState(String state) {
        this.state = state;
        return this;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public YarnAppReport setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
        return this;
    }

    public String getQueue() {
        return queue;
    }

    public YarnAppReport setQueue(String queue) {
        this.queue = queue;
        return this;
    }

    public Long getStartTime() {
        return startTime;
    }

    public YarnAppReport setStartTime(Long startTime) {
        this.startTime = startTime;
        return this;
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public YarnAppReport setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
        return this;
    }

    public String getFinalStatus() {
        return finalStatus;
    }

    public YarnAppReport setFinalStatus(String finalStatus) {
        this.finalStatus = finalStatus;
        return this;
    }
}
