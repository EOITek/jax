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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;
import java.util.Map;

public class SparkOverview {
    private String url;
    private Integer aliveWorkers;
    private Integer cores;
    private Integer coresUsed;
    private Integer memory;
    private Integer memoryUsed;
    private String status;
    private List<Map<String, Object>> activeApps;
    private List<Map<String, Object>> activeDrivers;
    private Integer appsRunning;
    private Integer driversDunning;

    @JsonGetter("url")
    public String getUrl() {
        return url;
    }

    @JsonGetter("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonGetter("aliveWorkers")
    public Integer getAliveWorkers() {
        return aliveWorkers;
    }

    @JsonSetter("aliveworkers")
    public void setAliveWorkers(Integer aliveWorkers) {
        this.aliveWorkers = aliveWorkers;
    }

    @JsonGetter("cores")
    public Integer getCores() {
        return cores;
    }

    @JsonSetter("cores")
    public void setCores(Integer cores) {
        this.cores = cores;
    }

    @JsonGetter("coresUsed")
    public Integer getCoresUsed() {
        return coresUsed;
    }

    @JsonSetter("coresused")
    public void setCoresUsed(Integer coresUsed) {
        this.coresUsed = coresUsed;
    }

    @JsonGetter("memory")
    public Integer getMemory() {
        return memory;
    }

    @JsonSetter("memory")
    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    @JsonGetter("memoryUsed")
    public Integer getMemoryUsed() {
        return memoryUsed;
    }

    @JsonSetter("memoryused")
    public void setMemoryUsed(Integer memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    @JsonGetter("status")
    public String getStatus() {
        return status;
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonIgnore
    public List<Map<String, Object>> getActiveApps() {
        return activeApps;
    }

    @JsonSetter("activeapps")
    public void setActiveApps(List<Map<String, Object>> activeApps) {
        this.activeApps = activeApps;
    }

    @JsonIgnore
    public List<Map<String, Object>> getActiveDrivers() {
        return activeDrivers;
    }

    @JsonSetter("activedrivers")
    public void setActiveDrivers(List<Map<String, Object>> activeDrivers) {
        this.activeDrivers = activeDrivers;
    }

    public Integer getAppsRunning() {
        return appsRunning;
    }

    public void setAppsRunning(Integer appsRunning) {
        this.appsRunning = appsRunning;
    }

    public Integer getDriversDunning() {
        return driversDunning;
    }

    public void setDriversDunning(Integer driversDunning) {
        this.driversDunning = driversDunning;
    }
}
