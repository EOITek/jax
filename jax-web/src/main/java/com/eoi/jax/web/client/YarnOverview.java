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

public class YarnOverview {
    private Integer appsSubmitted;
    private Integer appsCompleted;
    private Integer appsPending;
    private Integer appsRunning;
    private Integer appsKilled;
    private Long reservedMB;
    private Long allocatedMB;
    private Long availableMB;
    private Long totalMB;
    private Long reservedVirtualCores;
    private Long availableVirtualCores;
    private Long allocatedVirtualCores;
    private Long totalVirtualCores;
    private Integer containersAllocated;
    private Integer containersReserved;
    private Integer containersPending;
    private Integer totalNodes;
    private Integer activeNodes;
    private Integer lostNodes;
    private Integer unhealthyNodes;
    private Integer decommissioningNodes;
    private Integer decommissionedNodes;
    private Integer rebootedNodes;
    private Integer shutdownNodes;

    public Integer getAppsSubmitted() {
        return appsSubmitted;
    }

    public YarnOverview setAppsSubmitted(Integer appsSubmitted) {
        this.appsSubmitted = appsSubmitted;
        return this;
    }

    public Integer getAppsCompleted() {
        return appsCompleted;
    }

    public YarnOverview setAppsCompleted(Integer appsCompleted) {
        this.appsCompleted = appsCompleted;
        return this;
    }

    public Integer getAppsPending() {
        return appsPending;
    }

    public YarnOverview setAppsPending(Integer appsPending) {
        this.appsPending = appsPending;
        return this;
    }

    public Integer getAppsRunning() {
        return appsRunning;
    }

    public YarnOverview setAppsRunning(Integer appsRunning) {
        this.appsRunning = appsRunning;
        return this;
    }

    public Integer getAppsKilled() {
        return appsKilled;
    }

    public YarnOverview setAppsKilled(Integer appsKilled) {
        this.appsKilled = appsKilled;
        return this;
    }

    public Long getReservedMB() {
        return reservedMB;
    }

    public YarnOverview setReservedMB(Long reservedMB) {
        this.reservedMB = reservedMB;
        return this;
    }

    public Long getAllocatedMB() {
        return allocatedMB;
    }

    public YarnOverview setAllocatedMB(Long allocatedMB) {
        this.allocatedMB = allocatedMB;
        return this;
    }

    public Long getAvailableMB() {
        return availableMB;
    }

    public YarnOverview setAvailableMB(Long availableMB) {
        this.availableMB = availableMB;
        return this;
    }

    public Long getTotalMB() {
        return totalMB;
    }

    public YarnOverview setTotalMB(Long totalMB) {
        this.totalMB = totalMB;
        return this;
    }

    public Long getReservedVirtualCores() {
        return reservedVirtualCores;
    }

    public YarnOverview setReservedVirtualCores(Long reservedVirtualCores) {
        this.reservedVirtualCores = reservedVirtualCores;
        return this;
    }

    public Long getAvailableVirtualCores() {
        return availableVirtualCores;
    }

    public YarnOverview setAvailableVirtualCores(Long availableVirtualCores) {
        this.availableVirtualCores = availableVirtualCores;
        return this;
    }

    public Long getAllocatedVirtualCores() {
        return allocatedVirtualCores;
    }

    public YarnOverview setAllocatedVirtualCores(Long allocatedVirtualCores) {
        this.allocatedVirtualCores = allocatedVirtualCores;
        return this;
    }

    public Long getTotalVirtualCores() {
        return totalVirtualCores;
    }

    public YarnOverview setTotalVirtualCores(Long totalVirtualCores) {
        this.totalVirtualCores = totalVirtualCores;
        return this;
    }

    public Integer getContainersAllocated() {
        return containersAllocated;
    }

    public YarnOverview setContainersAllocated(Integer containersAllocated) {
        this.containersAllocated = containersAllocated;
        return this;
    }

    public Integer getContainersReserved() {
        return containersReserved;
    }

    public YarnOverview setContainersReserved(Integer containersReserved) {
        this.containersReserved = containersReserved;
        return this;
    }

    public Integer getContainersPending() {
        return containersPending;
    }

    public YarnOverview setContainersPending(Integer containersPending) {
        this.containersPending = containersPending;
        return this;
    }

    public Integer getTotalNodes() {
        return totalNodes;
    }

    public YarnOverview setTotalNodes(Integer totalNodes) {
        this.totalNodes = totalNodes;
        return this;
    }

    public Integer getActiveNodes() {
        return activeNodes;
    }

    public YarnOverview setActiveNodes(Integer activeNodes) {
        this.activeNodes = activeNodes;
        return this;
    }

    public Integer getLostNodes() {
        return lostNodes;
    }

    public YarnOverview setLostNodes(Integer lostNodes) {
        this.lostNodes = lostNodes;
        return this;
    }

    public Integer getUnhealthyNodes() {
        return unhealthyNodes;
    }

    public YarnOverview setUnhealthyNodes(Integer unhealthyNodes) {
        this.unhealthyNodes = unhealthyNodes;
        return this;
    }

    public Integer getDecommissioningNodes() {
        return decommissioningNodes;
    }

    public YarnOverview setDecommissioningNodes(Integer decommissioningNodes) {
        this.decommissioningNodes = decommissioningNodes;
        return this;
    }

    public Integer getDecommissionedNodes() {
        return decommissionedNodes;
    }

    public YarnOverview setDecommissionedNodes(Integer decommissionedNodes) {
        this.decommissionedNodes = decommissionedNodes;
        return this;
    }

    public Integer getRebootedNodes() {
        return rebootedNodes;
    }

    public YarnOverview setRebootedNodes(Integer rebootedNodes) {
        this.rebootedNodes = rebootedNodes;
        return this;
    }

    public Integer getShutdownNodes() {
        return shutdownNodes;
    }

    public YarnOverview setShutdownNodes(Integer shutdownNodes) {
        this.shutdownNodes = shutdownNodes;
        return this;
    }
}
