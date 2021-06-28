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
import com.eoi.jax.web.dao.entity.TbCluster;
import com.eoi.jax.web.dao.entity.TbOptsFlink;
import com.eoi.jax.web.dao.entity.TbOptsSpark;
import org.springframework.beans.BeanUtils;

public class Cluster {
    private String clusterName;
    private String clusterType;
    private String dashboardServer;
    private String hadoopHome;
    private String hadoopConfig;
    private String yarnBin;
    private String hdfsServer;
    private String yarnWebUrl;
    private String flinkServer;
    private String flinkWebUrl;
    private String sparkServer;
    private String sparkWebUrl;
    private String pythonEnv;
    private String principal;
    private String keytab;
    private int timeoutMs;
    private ClusterFlinkOpts flinkOpts;
    private ClusterSparkOpts sparkOpts;

    public Cluster() {
    }

    public Cluster(TbCluster clusterEntity, TbOptsFlink flinkEntity, TbOptsSpark sparkEntity) {
        fromDb(clusterEntity, flinkEntity, sparkEntity);
    }

    public String getClusterName() {
        return clusterName;
    }

    public Cluster setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public String getClusterType() {
        return clusterType;
    }

    public Cluster setClusterType(String clusterType) {
        this.clusterType = clusterType;
        return this;
    }

    public String getDashboardServer() {
        return dashboardServer;
    }

    public Cluster setDashboardServer(String dashboardServer) {
        this.dashboardServer = dashboardServer;
        return this;
    }

    public String getHadoopHome() {
        return hadoopHome;
    }

    public Cluster setHadoopHome(String hadoopHome) {
        this.hadoopHome = hadoopHome;
        return this;
    }

    public String getHadoopConfig() {
        return hadoopConfig;
    }

    public Cluster setHadoopConfig(String hadoopConfig) {
        this.hadoopConfig = hadoopConfig;
        return this;
    }

    public String getYarnBin() {
        return yarnBin;
    }

    public Cluster setYarnBin(String yarnBin) {
        this.yarnBin = yarnBin;
        return this;
    }

    public String getHdfsServer() {
        return hdfsServer;
    }

    public Cluster setHdfsServer(String hdfsServer) {
        this.hdfsServer = hdfsServer;
        return this;
    }

    public String getYarnWebUrl() {
        return yarnWebUrl;
    }

    public Cluster setYarnWebUrl(String yarnWebUrl) {
        this.yarnWebUrl = yarnWebUrl;
        return this;
    }

    public String getFlinkServer() {
        return flinkServer;
    }

    public Cluster setFlinkServer(String flinkServer) {
        this.flinkServer = flinkServer;
        return this;
    }

    public String getFlinkWebUrl() {
        return flinkWebUrl;
    }

    public Cluster setFlinkWebUrl(String flinkWebUrl) {
        this.flinkWebUrl = flinkWebUrl;
        return this;
    }

    public String getSparkServer() {
        return sparkServer;
    }

    public Cluster setSparkServer(String sparkServer) {
        this.sparkServer = sparkServer;
        return this;
    }

    public String getSparkWebUrl() {
        return sparkWebUrl;
    }

    public Cluster setSparkWebUrl(String sparkWebUrl) {
        this.sparkWebUrl = sparkWebUrl;
        return this;
    }

    public String getPythonEnv() {
        return pythonEnv;
    }

    public Cluster setPythonEnv(String pythonEnv) {
        this.pythonEnv = pythonEnv;
        return this;
    }

    public String getPrincipal() {
        return principal;
    }

    public Cluster setPrincipal(String principal) {
        this.principal = principal;
        return this;
    }

    public String getKeytab() {
        return keytab;
    }

    public Cluster setKeytab(String keytab) {
        this.keytab = keytab;
        return this;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public Cluster setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    public ClusterFlinkOpts getFlinkOpts() {
        return flinkOpts;
    }

    public Cluster setFlinkOpts(ClusterFlinkOpts flinkOpts) {
        this.flinkOpts = flinkOpts;
        return this;
    }

    public ClusterSparkOpts getSparkOpts() {
        return sparkOpts;
    }

    public Cluster setSparkOpts(ClusterSparkOpts sparkOpts) {
        this.sparkOpts = sparkOpts;
        return this;
    }

    public Cluster withDefault() {
        JaxConfig config = ConfigLoader.load().jax;
        ClusterFlinkOpts flinkOpts = new ClusterFlinkOpts().withDefault();
        ClusterSparkOpts sparkOpts = new ClusterSparkOpts().withDefault();
        setFlinkOpts(flinkOpts);
        setSparkOpts(sparkOpts);
        setDashboardServer("http://localhost:3030");
        setHadoopHome(Common.pathsJoin(config.getHome(), "hadoop"));
        setHadoopConfig(Common.pathsJoin(config.getHome(), "hadoop/etc"));
        setYarnBin(Common.pathsJoin(config.getHome(), "hadoop/bin/yarn"));
        setHdfsServer("hdfs://localhost:9000");
        setYarnWebUrl("");
        setFlinkServer("localhost:8081");
        setFlinkWebUrl("http://localhost:8081");
        setSparkServer("spark://localhost:7077");
        setSparkWebUrl("http://localhost:8088");
        setPythonEnv("/home/python_venv");
        setPrincipal("");
        setKeytab("");
        setTimeoutMs(600000);
        return this;
    }

    public Cluster fromDb(TbCluster clusterEntity, TbOptsFlink flinkEntity, TbOptsSpark sparkEntity) {
        BeanUtils.copyProperties(clusterEntity, this);
        setClusterName(clusterEntity.getClusterName());
        setClusterType(clusterEntity.getClusterType());
        setHadoopHome(ClusterVariable.replaceVariable(clusterEntity.getHadoopHome()));
        setHadoopConfig(ClusterVariable.genHadoopConf(getHadoopHome()));
        setYarnBin(ClusterVariable.genYarnBin(getHadoopHome()));
        setHdfsServer(clusterEntity.getHdfsServer());
        setFlinkServer(clusterEntity.getFlinkServer());
        setFlinkWebUrl(clusterEntity.getFlinkWebUrl());
        setSparkServer(clusterEntity.getSparkServer());
        setSparkWebUrl(clusterEntity.getSparkWebUrl());
        setPythonEnv(clusterEntity.getPythonEnv());
        setPrincipal(clusterEntity.getPrincipal());
        setKeytab(clusterEntity.getKeytab());
        setTimeoutMs(clusterEntity.getTimeoutMs().intValue());
        if (flinkEntity != null) {
            setFlinkOpts(new ClusterFlinkOpts(clusterEntity, flinkEntity));
        }
        if (sparkEntity != null) {
            setSparkOpts(new ClusterSparkOpts(clusterEntity, sparkEntity));
        }
        return this;
    }
}
