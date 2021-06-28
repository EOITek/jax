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
import com.eoi.jax.web.dao.entity.TbOptsFlink;
import com.eoi.jax.web.dao.entity.TbOptsSpark;
import com.eoi.jax.web.model.BaseModel;
import com.eoi.jax.web.model.opts.OptsFlinkResp;
import com.eoi.jax.web.model.opts.OptsSparkResp;
import com.eoi.jax.web.provider.resource.ClusterResourcePool;

public class ClusterResp implements BaseModel<TbCluster> {
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
    private Boolean defaultFlinkCluster;
    private Boolean defaultSparkCluster;
    private Long timeoutMs;
    private String flinkOptsName;
    private String sparkOptsName;
    private Long createTime;
    private String createBy;
    private Long updateTime;
    private String updateBy;
    private OptsFlinkResp flinkOpts;
    private OptsSparkResp sparkOpts;
    private ClusterResourcePool resourcePool;

    public String getClusterName() {
        return clusterName;
    }

    public ClusterResp setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public String getClusterType() {
        return clusterType;
    }

    public ClusterResp setClusterType(String clusterType) {
        this.clusterType = clusterType;
        return this;
    }

    public String getClusterDescription() {
        return clusterDescription;
    }

    public ClusterResp setClusterDescription(String clusterDescription) {
        this.clusterDescription = clusterDescription;
        return this;
    }

    public String getHadoopHome() {
        return hadoopHome;
    }

    public ClusterResp setHadoopHome(String hadoopHome) {
        this.hadoopHome = hadoopHome;
        return this;
    }

    public String getHdfsServer() {
        return hdfsServer;
    }

    public ClusterResp setHdfsServer(String hdfsServer) {
        this.hdfsServer = hdfsServer;
        return this;
    }

    public String getYarnWebUrl() {
        return yarnWebUrl;
    }

    public ClusterResp setYarnWebUrl(String yarnWebUrl) {
        this.yarnWebUrl = yarnWebUrl;
        return this;
    }

    public String getFlinkServer() {
        return flinkServer;
    }

    public ClusterResp setFlinkServer(String flinkServer) {
        this.flinkServer = flinkServer;
        return this;
    }

    public String getFlinkWebUrl() {
        return flinkWebUrl;
    }

    public ClusterResp setFlinkWebUrl(String flinkWebUrl) {
        this.flinkWebUrl = flinkWebUrl;
        return this;
    }

    public String getSparkServer() {
        return sparkServer;
    }

    public ClusterResp setSparkServer(String sparkServer) {
        this.sparkServer = sparkServer;
        return this;
    }

    public String getSparkWebUrl() {
        return sparkWebUrl;
    }

    public ClusterResp setSparkWebUrl(String sparkWebUrl) {
        this.sparkWebUrl = sparkWebUrl;
        return this;
    }

    public String getSparkHistoryServer() {
        return sparkHistoryServer;
    }

    public ClusterResp setSparkHistoryServer(String sparkHistoryServer) {
        this.sparkHistoryServer = sparkHistoryServer;
        return this;
    }

    public String getPythonEnv() {
        return pythonEnv;
    }

    public ClusterResp setPythonEnv(String pythonEnv) {
        this.pythonEnv = pythonEnv;
        return this;
    }

    public String getPrincipal() {
        return principal;
    }

    public ClusterResp setPrincipal(String principal) {
        this.principal = principal;
        return this;
    }

    public String getKeytab() {
        return keytab;
    }

    public ClusterResp setKeytab(String keytab) {
        this.keytab = keytab;
        return this;
    }

    public Boolean getDefaultFlinkCluster() {
        return defaultFlinkCluster;
    }

    public ClusterResp setDefaultFlinkCluster(Boolean defaultFlinkCluster) {
        this.defaultFlinkCluster = defaultFlinkCluster;
        return this;
    }

    public Boolean getDefaultSparkCluster() {
        return defaultSparkCluster;
    }

    public ClusterResp setDefaultSparkCluster(Boolean defaultSparkCluster) {
        this.defaultSparkCluster = defaultSparkCluster;
        return this;
    }

    public Long getTimeoutMs() {
        return timeoutMs;
    }

    public ClusterResp setTimeoutMs(Long timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    public String getFlinkOptsName() {
        return flinkOptsName;
    }

    public ClusterResp setFlinkOptsName(String flinkOptsName) {
        this.flinkOptsName = flinkOptsName;
        return this;
    }

    public String getSparkOptsName() {
        return sparkOptsName;
    }

    public ClusterResp setSparkOptsName(String sparkOptsName) {
        this.sparkOptsName = sparkOptsName;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public ClusterResp setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public ClusterResp setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public ClusterResp setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public ClusterResp setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }

    public OptsFlinkResp getFlinkOpts() {
        return flinkOpts;
    }

    public ClusterResp setFlinkOpts(OptsFlinkResp flinkOpts) {
        this.flinkOpts = flinkOpts;
        return this;
    }

    public OptsSparkResp getSparkOpts() {
        return sparkOpts;
    }

    public ClusterResp setSparkOpts(OptsSparkResp sparkOpts) {
        this.sparkOpts = sparkOpts;
        return this;
    }

    public ClusterResourcePool getResourcePool() {
        return resourcePool;
    }

    public ClusterResp setResourcePool(ClusterResourcePool resourcePool) {
        this.resourcePool = resourcePool;
        return this;
    }

    public ClusterResp respFrom(TbCluster cluster) {
        return copyFrom(cluster);
    }

    public ClusterResp respFrom(TbCluster cluster, TbOptsFlink flink, TbOptsSpark spark) {
        if (flink != null) {
            setFlinkOpts(new OptsFlinkResp().respFrom(flink));
        }
        if (spark != null) {
            setSparkOpts(new OptsSparkResp().respFrom(spark));
        }
        return copyFrom(cluster);
    }
}
