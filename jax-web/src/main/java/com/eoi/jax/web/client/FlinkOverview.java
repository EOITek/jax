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

package com.eoi.jax.web.client;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class FlinkOverview {
    private Integer taskManagers;
    private Integer slotsTotal;
    private int slotsAvailable;
    private int jobsRunning;
    private int jobsFinished;
    private int jobsCancelled;
    private int jobsFailed;
    private String flinkVersion;
    private String flinkCommit;

    @JsonGetter("taskManagers")
    public Integer getTaskManagers() {
        return taskManagers;
    }

    @JsonSetter("taskmanagers")
    public void setTaskManagers(Integer taskManagers) {
        this.taskManagers = taskManagers;
    }

    @JsonGetter("slotsTotal")
    public Integer getSlotsTotal() {
        return slotsTotal;
    }

    @JsonSetter("slots-total")
    public void setSlotsTotal(Integer slotsTotal) {
        this.slotsTotal = slotsTotal;
    }

    @JsonGetter("slotsAvailable")
    public int getSlotsAvailable() {
        return slotsAvailable;
    }

    @JsonSetter("slots-available")
    public void setSlotsAvailable(int slotsAvailable) {
        this.slotsAvailable = slotsAvailable;
    }

    @JsonGetter("jobsRunning")
    public int getJobsRunning() {
        return jobsRunning;
    }

    @JsonSetter("jobs-running")
    public void setJobsRunning(int jobsRunning) {
        this.jobsRunning = jobsRunning;
    }

    @JsonGetter("jobsFinished")
    public int getJobsFinished() {
        return jobsFinished;
    }

    @JsonSetter("jobs-finished")
    public void setJobsFinished(int jobsFinished) {
        this.jobsFinished = jobsFinished;
    }

    @JsonGetter("jobsCancelled")
    public int getJobsCancelled() {
        return jobsCancelled;
    }

    @JsonSetter("jobs-cancelled")
    public void setJobsCancelled(int jobsCancelled) {
        this.jobsCancelled = jobsCancelled;
    }

    @JsonGetter("jobsFailed")
    public int getJobsFailed() {
        return jobsFailed;
    }

    @JsonSetter("jobs-failed")
    public void setJobsFailed(int jobsFailed) {
        this.jobsFailed = jobsFailed;
    }

    @JsonGetter("flinkVersion")
    public String getFlinkVersion() {
        return flinkVersion;
    }

    @JsonSetter("flink-version")
    public void setFlinkVersion(String flinkVersion) {
        this.flinkVersion = flinkVersion;
    }

    @JsonGetter("flinkCommit")
    public String getFlinkCommit() {
        return flinkCommit;
    }

    @JsonSetter("flink-commit")
    public void setFlinkCommit(String flinkCommit) {
        this.flinkCommit = flinkCommit;
    }
}
