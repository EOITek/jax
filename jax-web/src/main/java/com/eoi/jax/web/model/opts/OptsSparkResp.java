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
import com.eoi.jax.web.dao.entity.TbOptsSpark;
import com.eoi.jax.web.model.BaseModel;

import java.util.ArrayList;
import java.util.List;

public class OptsSparkResp implements BaseModel<TbOptsSpark>, OptsResp {
    private String sparkOptsName;
    private String optsDescription;
    private String entryClass;
    private String version;

    @OptsValue(
            description = "spark主目录，一般为${JAX_HOME}/spark"
    )
    private String home;

    @OptsValue(
            description = "引导jar包，一般为${JAX_HOME}/jax/jar_lib/jax-spark-entry.jar"
    )
    private String entryJar;

    @OptsValue(
            description = "lib目录，一般为${JAX_HOME}/jax/jar_lib/spark"
    )
    private String jobLib;

    @OptsValue(
            required = false,
            description = "提交到Yarn的Queue名字"
    )
    private String yarnQueue;

    @OptsValue(
            description = "driver内存大小"
    )
    private String driverMemory;

    @OptsValue(
            description = "每个executor的内存大小"
    )
    private String executorMemory;

    @OptsValue(
            type = OptsValueType.LONG,
            description = "driver的cpu核数"
    )
    private Long driverCores;

    @OptsValue(
            type = OptsValueType.LONG,
            description = "每个executor的cpu核数"
    )
    private Long executorCores;

    @OptsValue(
            type = OptsValueType.LONG,
            description = "yarn内executor的数量"
    )
    private Long numExecutors;

    @OptsValue(
            required = false,
            type = OptsValueType.LIST,
            description = "Spark的配置属性"
    )
    private List<String> confList;

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

    public String getSparkOptsName() {
        return sparkOptsName;
    }

    public OptsSparkResp setSparkOptsName(String sparkOptsName) {
        this.sparkOptsName = sparkOptsName;
        return this;
    }

    public String getOptsDescription() {
        return optsDescription;
    }

    public OptsSparkResp setOptsDescription(String optsDescription) {
        this.optsDescription = optsDescription;
        return this;
    }

    public String getEntryJar() {
        return entryJar;
    }

    public OptsSparkResp setEntryJar(String entryJar) {
        this.entryJar = entryJar;
        return this;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public OptsSparkResp setEntryClass(String entryClass) {
        this.entryClass = entryClass;
        return this;
    }

    public String getJobLib() {
        return jobLib;
    }

    public OptsSparkResp setJobLib(String jobLib) {
        this.jobLib = jobLib;
        return this;
    }

    public String getHome() {
        return home;
    }

    public OptsSparkResp setHome(String home) {
        this.home = home;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public OptsSparkResp setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public OptsSparkResp setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
        return this;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public OptsSparkResp setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
        return this;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public OptsSparkResp setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
        return this;
    }

    public Long getDriverCores() {
        return driverCores;
    }

    public OptsSparkResp setDriverCores(Long driverCores) {
        this.driverCores = driverCores;
        return this;
    }

    public Long getExecutorCores() {
        return executorCores;
    }

    public OptsSparkResp setExecutorCores(Long executorCores) {
        this.executorCores = executorCores;
        return this;
    }

    public Long getNumExecutors() {
        return numExecutors;
    }

    public OptsSparkResp setNumExecutors(Long numExecutors) {
        this.numExecutors = numExecutors;
        return this;
    }

    public List<String> getConfList() {
        if (confList == null) {
            return new ArrayList<>();
        }
        return confList;
    }

    public OptsSparkResp setConfList(List<String> confList) {
        this.confList = confList;
        if (this.confList == null) {
            this.confList = new ArrayList<>();
        }
        return this;
    }

    public List<String> getJavaOptions() {
        return javaOptions;
    }

    public OptsSparkResp setJavaOptions(List<String> javaOptions) {
        this.javaOptions = javaOptions;
        return this;
    }

    public List<String> getOtherStartArgs() {
        if (otherStartArgs == null) {
            return new ArrayList<>();
        }
        return otherStartArgs;
    }

    public OptsSparkResp setOtherStartArgs(List<String> otherStartArgs) {
        this.otherStartArgs = otherStartArgs;
        if (this.otherStartArgs == null) {
            this.otherStartArgs = new ArrayList<>();
        }
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public OptsSparkResp setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public OptsSparkResp setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    @Override
    public Long getUpdateTime() {
        return updateTime;
    }

    public OptsSparkResp setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public OptsSparkResp setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }

    @Override
    public String getOptsType() {
        return OptsType.SPARK.code;
    }

    @Override
    public String getOptsName() {
        return sparkOptsName;
    }

    public OptsSparkResp respFrom(TbOptsSpark entity) {
        copyFrom(entity);
        setConfList(decode2ListString(entity.getConfList()));
        setJavaOptions(decode2ListString(entity.getJavaOptions()));
        setOtherStartArgs(decode2ListString(entity.getOtherStartArgs()));
        return this;
    }
}
