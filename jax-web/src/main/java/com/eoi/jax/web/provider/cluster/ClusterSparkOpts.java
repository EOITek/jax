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
import com.eoi.jax.web.dao.entity.TbOptsSpark;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ClusterSparkOpts {
    private String entryJar;
    private String entryClass;
    private String jobLib;
    private String home;
    private String bin;
    private String version;
    private String yarnQueue;
    private String driverMemory;
    private String executorMemory;
    private String driverCores;
    private String executorCores;
    private String numExecutors;
    private List<String> confList;
    private List<String> javaOptions;
    private List<String> otherStartArgs;

    public ClusterSparkOpts() {
    }

    public ClusterSparkOpts(TbCluster clusterEntity, TbOptsSpark sparkEntity) {
        fromDb(clusterEntity, sparkEntity);
    }

    public String getEntryJar() {
        return entryJar;
    }

    public ClusterSparkOpts setEntryJar(String entryJar) {
        this.entryJar = entryJar;
        return this;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public ClusterSparkOpts setEntryClass(String entryClass) {
        this.entryClass = entryClass;
        return this;
    }

    public String getJobLib() {
        return jobLib;
    }

    public ClusterSparkOpts setJobLib(String jobLib) {
        this.jobLib = jobLib;
        return this;
    }

    public String getHome() {
        return home;
    }

    public ClusterSparkOpts setHome(String home) {
        this.home = home;
        return this;
    }

    public String getBin() {
        return bin;
    }

    public ClusterSparkOpts setBin(String bin) {
        this.bin = bin;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ClusterSparkOpts setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public ClusterSparkOpts setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
        return this;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public ClusterSparkOpts setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
        return this;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public ClusterSparkOpts setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
        return this;
    }

    public String getDriverCores() {
        return driverCores;
    }

    public ClusterSparkOpts setDriverCores(String driverCores) {
        this.driverCores = driverCores;
        return this;
    }

    public String getExecutorCores() {
        return executorCores;
    }

    public ClusterSparkOpts setExecutorCores(String executorCores) {
        this.executorCores = executorCores;
        return this;
    }

    public String getNumExecutors() {
        return numExecutors;
    }

    public ClusterSparkOpts setNumExecutors(String numExecutors) {
        this.numExecutors = numExecutors;
        return this;
    }

    public List<String> getConfList() {
        return confList;
    }

    public ClusterSparkOpts setConfList(List<String> confList) {
        this.confList = confList;
        return this;
    }

    public List<String> getJavaOptions() {
        return javaOptions;
    }

    public ClusterSparkOpts setJavaOptions(List<String> javaOptions) {
        this.javaOptions = javaOptions;
        return this;
    }

    public List<String> getOtherStartArgs() {
        return otherStartArgs;
    }

    public ClusterSparkOpts setOtherStartArgs(List<String> otherStartArgs) {
        this.otherStartArgs = otherStartArgs;
        return this;
    }

    public String getLib() {
        return ClusterVariable.genSparkLib(getHome());
    }

    public ClusterSparkOpts withDefault() {
        JaxConfig config = ConfigLoader.load().jax;
        setEntryJar(Common.pathsJoin(config.getJarLib(), "jax-spark-entry.jar"));
        setEntryClass("com.eoi.jax.spark_entry.SparkMainEntry");
        setJobLib(Common.pathsJoin(config.getJarLib(), "spark"));
        setHome(Common.pathsJoin(config.getHome(), "spark"));
        setBin(Common.pathsJoin(config.getHome(), "spark/bin/spark-submit"));
        setVersion("2.4.1");
        setYarnQueue("");
        setDriverMemory("2048M");
        setExecutorMemory("2048M");
        setDriverCores("1");
        setExecutorCores("1");
        setNumExecutors("1");
        setConfList(new ArrayList<>());
        setJavaOptions(new ArrayList<>());
        setOtherStartArgs(new ArrayList<>());
        return this;
    }

    public TbOptsSpark toDb() {
        TbOptsSpark entity = new TbOptsSpark();
        entity.setEntryJar(ClusterVariable.extractVariable(getEntryJar()));
        entity.setEntryClass(getEntryClass());
        entity.setJobLib(ClusterVariable.extractVariable(getJobLib()));
        entity.setHome(ClusterVariable.extractVariable(getHome()));
        entity.setVersion(ClusterVariable.extractVariable(getVersion()));
        entity.setYarnQueue(getYarnQueue());
        entity.setDriverMemory(getDriverMemory());
        entity.setExecutorMemory(getExecutorMemory());
        entity.setDriverCores(Long.parseLong(getDriverCores()));
        entity.setExecutorCores(Long.parseLong(getExecutorCores()));
        entity.setNumExecutors(Long.parseLong(getNumExecutors()));
        entity.setConfList(JsonUtil.encode(ClusterVariable.extractVariable(getConfList())));
        entity.setOtherStartArgs(JsonUtil.encode(ClusterVariable.extractVariable(getOtherStartArgs())));
        return entity;
    }

    public ClusterSparkOpts fromDb(TbCluster clusterEntity, TbOptsSpark entity) {
        BeanUtils.copyProperties(entity, this);
        setEntryClass(entity.getEntryClass());
        setEntryJar(ClusterVariable.replaceVariable(entity.getEntryJar()));
        setJobLib(ClusterVariable.replaceVariable(entity.getJobLib()));
        setHome(ClusterVariable.replaceVariable(entity.getHome()));
        setBin(ClusterVariable.genSparkBin(getHome()));
        setVersion(entity.getVersion());
        setYarnQueue(entity.getYarnQueue());
        setDriverMemory(entity.getDriverMemory());
        setExecutorMemory(entity.getExecutorMemory());
        setDriverCores(Common.toStringNull(entity.getDriverCores()));
        setExecutorCores(Common.toStringNull(entity.getExecutorCores()));
        setNumExecutors(Common.toStringNull(entity.getNumExecutors()));
        setConfList(ClusterVariable.replaceVariable(JsonUtil.decode2ListString(entity.getConfList()), clusterEntity));
        setJavaOptions(ClusterVariable.replaceVariable(JsonUtil.decode2ListString(entity.getJavaOptions()), clusterEntity));
        setOtherStartArgs(ClusterVariable.replaceVariable(JsonUtil.decode2ListString(entity.getOtherStartArgs()), clusterEntity));
        return this;
    }
}
