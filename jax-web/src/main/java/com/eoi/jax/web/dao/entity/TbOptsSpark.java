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

package com.eoi.jax.web.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("tb_opts_spark")
public class TbOptsSpark {

    @TableId("spark_opts_name")
    private String sparkOptsName;

    @TableField("opts_description")
    private String optsDescription;

    @TableField("entry_jar")
    private String entryJar;

    @TableField("entry_class")
    private String entryClass;

    @TableField("job_lib")
    private String jobLib;

    @TableField("home")
    private String home;

    @TableField("version")
    private String version;

    @TableField("yarn_queue")
    private String yarnQueue;

    @TableField("driver_memory")
    private String driverMemory;

    @TableField("executor_memory")
    private String executorMemory;

    @TableField("driver_cores")
    private Long driverCores;

    @TableField("executor_cores")
    private Long executorCores;

    @TableField("num_executors")
    private Long numExecutors;

    @TableField("conf_list")
    private String confList;

    @TableField("java_options")
    private String javaOptions;

    @TableField("other_start_args")
    private String otherStartArgs;

    @TableField("create_time")
    private Long createTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_time")
    private Long updateTime;

    @TableField("update_by")
    private String updateBy;

    public String getSparkOptsName() {
        return sparkOptsName;
    }

    public TbOptsSpark setSparkOptsName(String sparkOptsName) {
        this.sparkOptsName = sparkOptsName;
        return this;
    }

    public String getOptsDescription() {
        return optsDescription;
    }

    public TbOptsSpark setOptsDescription(String optsDescription) {
        this.optsDescription = optsDescription;
        return this;
    }

    public String getEntryJar() {
        return entryJar;
    }

    public TbOptsSpark setEntryJar(String entryJar) {
        this.entryJar = entryJar;
        return this;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public TbOptsSpark setEntryClass(String entryClass) {
        this.entryClass = entryClass;
        return this;
    }

    public String getJobLib() {
        return jobLib;
    }

    public TbOptsSpark setJobLib(String jobLib) {
        this.jobLib = jobLib;
        return this;
    }

    public String getHome() {
        return home;
    }

    public TbOptsSpark setHome(String home) {
        this.home = home;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public TbOptsSpark setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public TbOptsSpark setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
        return this;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public TbOptsSpark setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
        return this;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public TbOptsSpark setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
        return this;
    }

    public Long getDriverCores() {
        return driverCores;
    }

    public TbOptsSpark setDriverCores(Long driverCores) {
        this.driverCores = driverCores;
        return this;
    }

    public Long getExecutorCores() {
        return executorCores;
    }

    public TbOptsSpark setExecutorCores(Long executorCores) {
        this.executorCores = executorCores;
        return this;
    }

    public Long getNumExecutors() {
        return numExecutors;
    }

    public TbOptsSpark setNumExecutors(Long numExecutors) {
        this.numExecutors = numExecutors;
        return this;
    }

    public String getConfList() {
        return confList;
    }

    public TbOptsSpark setConfList(String confList) {
        this.confList = confList;
        return this;
    }

    public String getJavaOptions() {
        return javaOptions;
    }

    public TbOptsSpark setJavaOptions(String javaOptions) {
        this.javaOptions = javaOptions;
        return this;
    }

    public String getOtherStartArgs() {
        return otherStartArgs;
    }

    public TbOptsSpark setOtherStartArgs(String otherStartArgs) {
        this.otherStartArgs = otherStartArgs;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public TbOptsSpark setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public TbOptsSpark setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public TbOptsSpark setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public TbOptsSpark setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }
}