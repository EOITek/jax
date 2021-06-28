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

package com.eoi.jax.web.provider.cluster;

import com.eoi.jax.web.common.config.ConfigLoader;
import com.eoi.jax.web.common.config.JaxConfig;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.dao.entity.TbCluster;
import com.eoi.jax.web.dao.entity.TbOptsFlink;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ClusterFlinkOpts {
    private String entryJar;
    private String entryClass;
    private String jobLib;
    private String home;
    private String bin;
    private String version;
    private String yarnQueue;
    private String parallelism;
    private String timeCharacteristic;
    private String backend;
    private String rocksDbPath;
    private String savepointURI;
    private String checkpointURI;
    private String checkpointInterval;
    private String minIdleStateRetentionTime;
    private String maxIdleStateRetentionTime;
    private String yarnTaskManagerNum;
    private String yarnSlots;
    private String yarnJobManagerMemory;
    private String yarnTaskManagerMemory;
    private Boolean disableOperatorChaining;
    private Boolean allowNonRestoredState;
    private List<String> confList;
    private List<String> javaOptions;
    private List<String> otherStartArgs;
    private Boolean applicationMode;
    private String runtimeMode;

    public ClusterFlinkOpts() {
    }

    public ClusterFlinkOpts(TbCluster clusterEntity, TbOptsFlink flinkEntity) {
        fromDb(clusterEntity, flinkEntity);
    }

    public String getEntryJar() {
        return entryJar;
    }

    public ClusterFlinkOpts setEntryJar(String entryJar) {
        this.entryJar = entryJar;
        return this;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public ClusterFlinkOpts setEntryClass(String entryClass) {
        this.entryClass = entryClass;
        return this;
    }

    public String getJobLib() {
        return jobLib;
    }

    public ClusterFlinkOpts setJobLib(String jobLib) {
        this.jobLib = jobLib;
        return this;
    }

    public String getHome() {
        return home;
    }

    public ClusterFlinkOpts setHome(String home) {
        this.home = home;
        return this;
    }

    public String getBin() {
        return bin;
    }

    public ClusterFlinkOpts setBin(String bin) {
        this.bin = bin;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ClusterFlinkOpts setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public ClusterFlinkOpts setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
        return this;
    }

    public String getParallelism() {
        return parallelism;
    }

    public ClusterFlinkOpts setParallelism(String parallelism) {
        this.parallelism = parallelism;
        return this;
    }

    public String getTimeCharacteristic() {
        return timeCharacteristic;
    }

    public ClusterFlinkOpts setTimeCharacteristic(String timeCharacteristic) {
        this.timeCharacteristic = timeCharacteristic;
        return this;
    }

    public String getBackend() {
        return backend;
    }

    public ClusterFlinkOpts setBackend(String backend) {
        this.backend = backend;
        return this;
    }

    public String getRocksDbPath() {
        return rocksDbPath;
    }

    public ClusterFlinkOpts setRocksDbPath(String rocksDbPath) {
        this.rocksDbPath = rocksDbPath;
        return this;
    }

    public String getSavepointURI() {
        return savepointURI;
    }

    public ClusterFlinkOpts setSavepointURI(String savepointURI) {
        this.savepointURI = savepointURI;
        return this;
    }

    public String getCheckpointURI() {
        return checkpointURI;
    }

    public ClusterFlinkOpts setCheckpointURI(String checkpointURI) {
        this.checkpointURI = checkpointURI;
        return this;
    }

    public String getCheckpointInterval() {
        return checkpointInterval;
    }

    public ClusterFlinkOpts setCheckpointInterval(String checkpointInterval) {
        this.checkpointInterval = checkpointInterval;
        return this;
    }

    public String getMinIdleStateRetentionTime() {
        return minIdleStateRetentionTime;
    }

    public ClusterFlinkOpts setMinIdleStateRetentionTime(String minIdleStateRetentionTime) {
        this.minIdleStateRetentionTime = minIdleStateRetentionTime;
        return this;
    }

    public String getMaxIdleStateRetentionTime() {
        return maxIdleStateRetentionTime;
    }

    public ClusterFlinkOpts setMaxIdleStateRetentionTime(String maxIdleStateRetentionTime) {
        this.maxIdleStateRetentionTime = maxIdleStateRetentionTime;
        return this;
    }

    public String getYarnTaskManagerNum() {
        return yarnTaskManagerNum;
    }

    public ClusterFlinkOpts setYarnTaskManagerNum(String yarnTaskManagerNum) {
        this.yarnTaskManagerNum = yarnTaskManagerNum;
        return this;
    }

    public String getYarnSlots() {
        return yarnSlots;
    }

    public ClusterFlinkOpts setYarnSlots(String yarnSlots) {
        this.yarnSlots = yarnSlots;
        return this;
    }

    public String getYarnJobManagerMemory() {
        return yarnJobManagerMemory;
    }

    public ClusterFlinkOpts setYarnJobManagerMemory(String yarnJobManagerMemory) {
        this.yarnJobManagerMemory = yarnJobManagerMemory;
        return this;
    }

    public String getYarnTaskManagerMemory() {
        return yarnTaskManagerMemory;
    }

    public ClusterFlinkOpts setYarnTaskManagerMemory(String yarnTaskManagerMemory) {
        this.yarnTaskManagerMemory = yarnTaskManagerMemory;
        return this;
    }

    public Boolean getDisableOperatorChaining() {
        return disableOperatorChaining;
    }

    public ClusterFlinkOpts setDisableOperatorChaining(Boolean disableOperatorChaining) {
        this.disableOperatorChaining = disableOperatorChaining;
        return this;
    }

    public Boolean getAllowNonRestoredState() {
        return allowNonRestoredState;
    }

    public ClusterFlinkOpts setAllowNonRestoredState(Boolean allowNonRestoredState) {
        this.allowNonRestoredState = allowNonRestoredState;
        return this;
    }

    public List<String> getConfList() {
        return confList;
    }

    public ClusterFlinkOpts setConfList(List<String> confList) {
        this.confList = confList;
        return this;
    }

    public List<String> getJavaOptions() {
        return javaOptions;
    }

    public ClusterFlinkOpts setJavaOptions(List<String> javaOptions) {
        this.javaOptions = javaOptions;
        return this;
    }

    public List<String> getOtherStartArgs() {
        return otherStartArgs;
    }

    public ClusterFlinkOpts setOtherStartArgs(List<String> otherStartArgs) {
        this.otherStartArgs = otherStartArgs;
        return this;
    }

    public Boolean isApplicationMode() {
        return applicationMode;
    }

    public ClusterFlinkOpts setApplicationMode(Boolean applicationMode) {
        this.applicationMode = applicationMode;
        return this;
    }

    public String getRuntimeMode() {
        return runtimeMode;
    }

    public ClusterFlinkOpts setRuntimeMode(String runtimeMode) {
        this.runtimeMode = runtimeMode;
        return this;
    }

    public String getLib() {
        return ClusterVariable.genFlinkLib(getHome());
    }

    public ClusterFlinkOpts withDefault() {
        JaxConfig config = ConfigLoader.load().jax;
        setEntryJar(Common.pathsJoin(config.getJarLib(), "jax-flink-entry.jar"));
        setEntryClass("com.eoi.jax.flink_entry.FlinkMainEntry");
        setJobLib(Common.pathsJoin(config.getJarLib(), "flink"));
        setHome(Common.pathsJoin(config.getHome(), "flink"));
        setBin(Common.pathsJoin(config.getHome(), "flink/bin/flink"));
        setVersion("1.9.1");
        setYarnQueue("");
        setParallelism("1");
        setTimeCharacteristic("processing");
        setBackend("rocksdb");
        setRocksDbPath(Common.pathsJoin(config.getJarLib(), "flink/rocksdb"));
        setSavepointURI(Common.pathsJoin(config.getJarLib(), "flink/checkpoint"));
        setCheckpointURI(Common.pathsJoin(config.getJarLib(), "flink/savepoint"));
        setCheckpointInterval("300000");
        setMinIdleStateRetentionTime("12");
        setMaxIdleStateRetentionTime("24");
        setYarnTaskManagerNum("1");
        setYarnSlots("1");
        setYarnJobManagerMemory("4096");
        setYarnTaskManagerMemory("2048");
        setDisableOperatorChaining(false);
        setAllowNonRestoredState(false);
        setConfList(new ArrayList<>());
        setJavaOptions(new ArrayList<>());
        setOtherStartArgs(new ArrayList<>());
        setApplicationMode(false);
        setRuntimeMode("STREAMING");
        return this;
    }

    public TbOptsFlink toDb() {
        TbOptsFlink entity = new TbOptsFlink();
        entity.setEntryJar(ClusterVariable.extractVariable(getEntryJar()));
        entity.setEntryClass(getEntryClass());
        entity.setJobLib(ClusterVariable.extractVariable(getJobLib()));
        entity.setHome(ClusterVariable.extractVariable(getHome()));
        entity.setVersion(getVersion());
        entity.setYarnQueue(getYarnQueue());
        entity.setParallelism(Long.parseLong(getParallelism()));
        entity.setTimeCharacteristic(getTimeCharacteristic());
        entity.setBackend(getBackend());
        entity.setRocksDbPath(ClusterVariable.extractVariable(getRocksDbPath()));
        entity.setSavepointURI(ClusterVariable.extractVariable(getSavepointURI()));
        entity.setCheckpointURI(ClusterVariable.extractVariable(getCheckpointURI()));
        entity.setCheckpointInterval(Long.parseLong(getCheckpointInterval()));
        entity.setMinIdleStateRetentionTime(Long.parseLong(getMinIdleStateRetentionTime()));
        entity.setMaxIdleStateRetentionTime(Long.parseLong(getMaxIdleStateRetentionTime()));
        entity.setYarnTaskManagerNum(Long.parseLong(getYarnTaskManagerNum()));
        entity.setYarnSlots(Long.parseLong(getYarnSlots()));
        entity.setYarnJobManagerMemory(getYarnJobManagerMemory());
        entity.setYarnTaskManagerMemory(getYarnTaskManagerMemory());
        entity.setDisableOperatorChaining(getDisableOperatorChaining());
        entity.setRuntimeMode(getRuntimeMode());
        entity.setApplicationMode(isApplicationMode());
        entity.setOtherStartArgs(JsonUtil.encode(ClusterVariable.extractVariable(getOtherStartArgs())));
        return entity;
    }

    public ClusterFlinkOpts fromDb(TbCluster clusterEntity, TbOptsFlink entity) {
        BeanUtils.copyProperties(entity, this);
        setEntryClass(entity.getEntryClass());
        setEntryJar(ClusterVariable.replaceVariable(entity.getEntryJar()));
        setJobLib(ClusterVariable.replaceVariable(entity.getJobLib()));
        setHome(ClusterVariable.replaceVariable(entity.getHome()));
        setBin(ClusterVariable.genFlinkBin(getHome()));
        setVersion(entity.getVersion());
        setYarnQueue(entity.getYarnQueue());
        setParallelism(Common.toStringNull(entity.getParallelism()));
        setTimeCharacteristic(entity.getTimeCharacteristic());
        setBackend(entity.getBackend());
        setRocksDbPath(ClusterVariable.replaceVariable(entity.getRocksDbPath(), clusterEntity));
        setSavepointURI(ClusterVariable.replaceVariable(entity.getSavepointURI(), clusterEntity));
        setCheckpointURI(ClusterVariable.replaceVariable(entity.getCheckpointURI(), clusterEntity));
        setCheckpointInterval(Common.toStringNull(entity.getCheckpointInterval()));
        setMinIdleStateRetentionTime(Common.toStringNull(entity.getMinIdleStateRetentionTime()));
        setMaxIdleStateRetentionTime(Common.toStringNull(entity.getMaxIdleStateRetentionTime()));
        setYarnTaskManagerNum(Common.toStringNull(entity.getYarnTaskManagerNum()));
        setYarnSlots(Common.toStringNull(entity.getYarnSlots()));
        setYarnJobManagerMemory(entity.getYarnJobManagerMemory());
        setYarnTaskManagerMemory(entity.getYarnTaskManagerMemory());
        setDisableOperatorChaining(entity.getDisableOperatorChaining());
        setRuntimeMode(entity.getRuntimeMode());
        setApplicationMode(entity.getApplicationMode());
        setAllowNonRestoredState(entity.getAllowNonRestoredState());
        setConfList(ClusterVariable.replaceVariable(JsonUtil.decode2ListString(entity.getConfList()), clusterEntity));
        setJavaOptions(ClusterVariable.replaceVariable(JsonUtil.decode2ListString(entity.getJavaOptions()), clusterEntity));
        setOtherStartArgs(ClusterVariable.replaceVariable(JsonUtil.decode2ListString(entity.getOtherStartArgs()), clusterEntity));
        return this;
    }
}
