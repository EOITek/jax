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

package com.eoi.jax.web.model.cluster;

import com.eoi.jax.web.dao.entity.TbCluster;
import com.eoi.jax.web.model.BaseModel;

public class ClusterReq implements BaseModel<TbCluster> {
    private String clusterName;
    private String clusterType;
    private String clusterDescription;
    private String hadoopHome;
    private String hdfsServer;
    private String yarnWebUrl;
    private String flinkServer;
    private String flinkWebUrl;
    private String sparkServer;
    private String sparkWebUrl;
    private String sparkHistoryServer;
    private String pythonEnv;
    private String principal;
    private String keytab;
    private Long timeoutMs;
    private Boolean defaultFlinkCluster;
    private Boolean defaultSparkCluster;
    private String flinkOptsName;
    private String sparkOptsName;

    public String getClusterName() {
        return clusterName;
    }

    public ClusterReq setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public String getClusterType() {
        return clusterType;
    }

    public ClusterReq setClusterType(String clusterType) {
        this.clusterType = clusterType;
        return this;
    }

    public String getClusterDescription() {
        return clusterDescription;
    }

    public ClusterReq setClusterDescription(String clusterDescription) {
        this.clusterDescription = clusterDescription;
        return this;
    }

    public String getHadoopHome() {
        return hadoopHome;
    }

    public ClusterReq setHadoopHome(String hadoopHome) {
        this.hadoopHome = hadoopHome;
        return this;
    }

    public String getHdfsServer() {
        return hdfsServer;
    }

    public ClusterReq setHdfsServer(String hdfsServer) {
        this.hdfsServer = hdfsServer;
        return this;
    }

    public String getYarnWebUrl() {
        return yarnWebUrl;
    }

    public ClusterReq setYarnWebUrl(String yarnWebUrl) {
        this.yarnWebUrl = yarnWebUrl;
        return this;
    }

    public String getFlinkServer() {
        return flinkServer;
    }

    public ClusterReq setFlinkServer(String flinkServer) {
        this.flinkServer = flinkServer;
        return this;
    }

    public String getFlinkWebUrl() {
        return flinkWebUrl;
    }

    public ClusterReq setFlinkWebUrl(String flinkWebUrl) {
        this.flinkWebUrl = flinkWebUrl;
        return this;
    }

    public String getSparkServer() {
        return sparkServer;
    }

    public ClusterReq setSparkServer(String sparkServer) {
        this.sparkServer = sparkServer;
        return this;
    }

    public String getSparkWebUrl() {
        return sparkWebUrl;
    }

    public ClusterReq setSparkWebUrl(String sparkWebUrl) {
        this.sparkWebUrl = sparkWebUrl;
        return this;
    }

    public String getSparkHistoryServer() {
        return sparkHistoryServer;
    }

    public ClusterReq setSparkHistoryServer(String sparkHistoryServer) {
        this.sparkHistoryServer = sparkHistoryServer;
        return this;
    }

    public String getPythonEnv() {
        return pythonEnv;
    }

    public ClusterReq setPythonEnv(String pythonEnv) {
        this.pythonEnv = pythonEnv;
        return this;
    }

    public String getPrincipal() {
        return principal;
    }

    public ClusterReq setPrincipal(String principal) {
        this.principal = principal;
        return this;
    }

    public String getKeytab() {
        return keytab;
    }

    public ClusterReq setKeytab(String keytab) {
        this.keytab = keytab;
        return this;
    }

    public Long getTimeoutMs() {
        return timeoutMs;
    }

    public ClusterReq setTimeoutMs(Long timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    public Boolean getDefaultFlinkCluster() {
        return defaultFlinkCluster;
    }

    public ClusterReq setDefaultFlinkCluster(Boolean defaultFlinkCluster) {
        this.defaultFlinkCluster = defaultFlinkCluster;
        return this;
    }

    public Boolean getDefaultSparkCluster() {
        return defaultSparkCluster;
    }

    public ClusterReq setDefaultSparkCluster(Boolean defaultSparkCluster) {
        this.defaultSparkCluster = defaultSparkCluster;
        return this;
    }

    public String getFlinkOptsName() {
        return flinkOptsName;
    }

    public ClusterReq setFlinkOptsName(String flinkOptsName) {
        this.flinkOptsName = flinkOptsName;
        return this;
    }

    public String getSparkOptsName() {
        return sparkOptsName;
    }

    public ClusterReq setSparkOptsName(String sparkOptsName) {
        this.sparkOptsName = sparkOptsName;
        return this;
    }

    public TbCluster toEntity(TbCluster entity) {
        this.copyTo(entity);
        return entity;
    }
}
