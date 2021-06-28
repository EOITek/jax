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

package com.eoi.jax.web.model.opts;

import com.eoi.jax.web.common.consts.OptsType;
import com.eoi.jax.web.dao.entity.TbOptsFlink;
import com.eoi.jax.web.model.BaseModel;

import java.util.ArrayList;
import java.util.List;

public class OptsFlinkResp implements BaseModel<TbOptsFlink>, OptsResp {
    private String flinkOptsName;
    private String optsDescription;
    private String entryClass;
    private String version;

    @OptsValue(
            type = OptsValueType.BOOL,
            description = "是否为application模式启动"
    )
    private Boolean applicationMode;

    @OptsValue(
            description = "flink主目录，一般为${JAX_HOME}/flink"
    )
    private String home;

    @OptsValue(
            description = "引导jar包，一般为${JAX_HOME}/jax/jar_lib/jax-flink-entry.jar"
    )
    private String entryJar;

    @OptsValue(
            description = "lib目录，一般为${JAX_HOME}/jax/jar_lib/flink"
    )
    private String jobLib;

    @OptsValue(
            required = false,
            description = "提交到Yarn的Queue名字"
    )
    private String yarnQueue;

    @OptsValue(
            type = OptsValueType.LONG,
            description = "并行度"
    )
    private Long parallelism;

    @OptsValue(
            description = "时间类型"
    )
    private String timeCharacteristic;

    @OptsValue(
            description = "状态存储类型"
    )
    private String backend;

    @OptsValue(
            description = "rocksDb存储路径"
    )
    private String rocksDbPath;

    @OptsValue(
            description = "savePoint地址"
    )
    private String savepointURI;

    @OptsValue(
            description = "checkPoint地址"
    )
    private String checkpointURI;

    @OptsValue(
            type = OptsValueType.LONG,
            description = "checkPoint间隔时间"
    )
    private Long checkpointInterval;

    @OptsValue(
            type = OptsValueType.LONG,
            description = "状态最小保持时间(小时)"
    )
    private Long minIdleStateRetentionTime;

    @OptsValue(
            type = OptsValueType.LONG,
            description = "状态最大保持时间(小时)"
    )
    private Long maxIdleStateRetentionTime;

    @OptsValue(
            type = OptsValueType.LONG,
            description = "yarn内TaskManager容器的数量"
    )
    private Long yarnTaskManagerNum;

    @OptsValue(
            type = OptsValueType.LONG,
            description = "yarn内每个TaskManager的slot数量"
    )
    private Long yarnSlots;

    @OptsValue(
            description = "JobManager的容器的内存大小(默认MB); flink1.10开始建议最少配置1024MB，否则可能启动失败"
    )
    private String yarnJobManagerMemory;

    @OptsValue(
            description = "TaskManager的容器的内存大小(默认MB)"
    )
    private String yarnTaskManagerMemory;


    @OptsValue(
            required = false,
            type = OptsValueType.BOOL,
            description = "允许跳过那些不能使用的savepoint"
    )
    private Boolean allowNonRestoredState;

    @OptsValue(
            type = OptsValueType.BOOL,
            description = "关闭对operator进行chain优化，默认不关闭"
    )
    private Boolean disableOperatorChaining;

    @OptsValue(
            description = "Flink1.12运行模式，可选【STREAMING，BATCH，AUTOMATIC】",
            required = false
    )
    private String runtimeMode;

    @OptsValue(
            required = false,
            type = OptsValueType.LIST,
            description = "Java Options"
    )
    private List<String> javaOptions;

    @OptsValue(
            required = false,
            type = OptsValueType.LIST,
            description = "额外启动参数"
    )
    private List<String> otherStartArgs;

    private Long createTime;
    private String createBy;
    private Long updateTime;
    private String updateBy;

    public String getFlinkOptsName() {
        return flinkOptsName;
    }

    public OptsFlinkResp setFlinkOptsName(String flinkOptsName) {
        this.flinkOptsName = flinkOptsName;
        return this;
    }

    public String getOptsDescription() {
        return optsDescription;
    }

    public OptsFlinkResp setOptsDescription(String optsDescription) {
        this.optsDescription = optsDescription;
        return this;
    }

    public String getEntryJar() {
        return entryJar;
    }

    public OptsFlinkResp setEntryJar(String entryJar) {
        this.entryJar = entryJar;
        return this;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public OptsFlinkResp setEntryClass(String entryClass) {
        this.entryClass = entryClass;
        return this;
    }

    public String getJobLib() {
        return jobLib;
    }

    public OptsFlinkResp setJobLib(String jobLib) {
        this.jobLib = jobLib;
        return this;
    }

    public String getHome() {
        return home;
    }

    public OptsFlinkResp setHome(String home) {
        this.home = home;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public OptsFlinkResp setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public OptsFlinkResp setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
        return this;
    }

    public Long getParallelism() {
        return parallelism;
    }

    public OptsFlinkResp setParallelism(Long parallelism) {
        this.parallelism = parallelism;
        return this;
    }

    public String getTimeCharacteristic() {
        return timeCharacteristic;
    }

    public OptsFlinkResp setTimeCharacteristic(String timeCharacteristic) {
        this.timeCharacteristic = timeCharacteristic;
        return this;
    }

    public String getBackend() {
        return backend;
    }

    public OptsFlinkResp setBackend(String backend) {
        this.backend = backend;
        return this;
    }

    public String getRocksDbPath() {
        return rocksDbPath;
    }

    public OptsFlinkResp setRocksDbPath(String rocksDbPath) {
        this.rocksDbPath = rocksDbPath;
        return this;
    }

    public String getCheckpointURI() {
        return checkpointURI;
    }

    public OptsFlinkResp setCheckpointURI(String checkpointURI) {
        this.checkpointURI = checkpointURI;
        return this;
    }

    public String getSavepointURI() {
        return savepointURI;
    }

    public OptsFlinkResp setSavepointURI(String savepointURI) {
        this.savepointURI = savepointURI;
        return this;
    }

    public Long getCheckpointInterval() {
        return checkpointInterval;
    }

    public OptsFlinkResp setCheckpointInterval(Long checkpointInterval) {
        this.checkpointInterval = checkpointInterval;
        return this;
    }

    public Long getMinIdleStateRetentionTime() {
        return minIdleStateRetentionTime;
    }

    public OptsFlinkResp setMinIdleStateRetentionTime(Long minIdleStateRetentionTime) {
        this.minIdleStateRetentionTime = minIdleStateRetentionTime;
        return this;
    }

    public Long getMaxIdleStateRetentionTime() {
        return maxIdleStateRetentionTime;
    }

    public OptsFlinkResp setMaxIdleStateRetentionTime(Long maxIdleStateRetentionTime) {
        this.maxIdleStateRetentionTime = maxIdleStateRetentionTime;
        return this;
    }

    public Long getYarnTaskManagerNum() {
        return yarnTaskManagerNum;
    }

    public OptsFlinkResp setYarnTaskManagerNum(Long yarnTaskManagerNum) {
        this.yarnTaskManagerNum = yarnTaskManagerNum;
        return this;
    }

    public Long getYarnSlots() {
        return yarnSlots;
    }

    public OptsFlinkResp setYarnSlots(Long yarnSlots) {
        this.yarnSlots = yarnSlots;
        return this;
    }

    public String getYarnJobManagerMemory() {
        return yarnJobManagerMemory;
    }

    public OptsFlinkResp setYarnJobManagerMemory(String yarnJobManagerMemory) {
        this.yarnJobManagerMemory = yarnJobManagerMemory;
        return this;
    }

    public String getYarnTaskManagerMemory() {
        return yarnTaskManagerMemory;
    }

    public OptsFlinkResp setYarnTaskManagerMemory(String yarnTaskManagerMemory) {
        this.yarnTaskManagerMemory = yarnTaskManagerMemory;
        return this;
    }

    public Boolean getAllowNonRestoredState() {
        return allowNonRestoredState;
    }

    public OptsFlinkResp setAllowNonRestoredState(Boolean allowNonRestoredState) {
        this.allowNonRestoredState = allowNonRestoredState;
        return this;
    }

    public Boolean getDisableOperatorChaining() {
        return disableOperatorChaining;
    }

    public OptsFlinkResp setDisableOperatorChaining(Boolean disableOperatorChaining) {
        this.disableOperatorChaining = disableOperatorChaining;
        return this;
    }

    public List<String> getJavaOptions() {
        return javaOptions;
    }

    public OptsFlinkResp setJavaOptions(List<String> javaOptions) {
        this.javaOptions = javaOptions;
        return this;
    }

    public List<String> getOtherStartArgs() {
        if (otherStartArgs == null) {
            return new ArrayList<>();
        }
        return otherStartArgs;
    }

    public OptsFlinkResp setOtherStartArgs(List<String> otherStartArgs) {
        this.otherStartArgs = otherStartArgs;
        if (this.otherStartArgs == null) {
            this.otherStartArgs = new ArrayList<>();
        }
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public OptsFlinkResp setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public Boolean getApplicationMode() {
        return applicationMode;
    }

    public void setApplicationMode(Boolean applicationMode) {
        this.applicationMode = applicationMode;
    }

    public String getCreateBy() {
        return createBy;
    }

    public OptsFlinkResp setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    @Override
    public Long getUpdateTime() {
        return updateTime;
    }

    public OptsFlinkResp setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public OptsFlinkResp setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }

    public String getRuntimeMode() {
        return runtimeMode;
    }

    public OptsFlinkResp setRuntimeMode(String runtimeMode) {
        this.runtimeMode = runtimeMode;
        return this;
    }

    @Override
    public String getOptsType() {
        return OptsType.FLINK.code;
    }

    @Override
    public String getOptsName() {
        return flinkOptsName;
    }

    public OptsFlinkResp respFrom(TbOptsFlink entity) {
        copyFrom(entity);
        setJavaOptions(decode2ListString(entity.getJavaOptions()));
        setOtherStartArgs(decode2ListString(entity.getOtherStartArgs()));
        return this;
    }
}
