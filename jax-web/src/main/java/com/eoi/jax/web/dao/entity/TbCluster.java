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

@TableName("tb_cluster")
public class TbCluster {

    @TableId("cluster_name")
    private String clusterName;

    @TableField("cluster_type")
    private String clusterType;

    @TableField("cluster_description")
    private String clusterDescription;

    @TableField("hadoop_home")
    private String hadoopHome;

    @TableField("hdfs_server")
    private String hdfsServer;

    @TableField("yarn_web_url")
    private String yarnWebUrl;

    @TableField("flink_server")
    private String flinkServer;

    @TableField("flink_web_url")
    private String flinkWebUrl;

    @TableField("spark_server")
    private String sparkServer;

    @TableField("spark_web_url")
    private String sparkWebUrl;

    @TableField("spark_history_server")
    private String sparkHistoryServer;

    @TableField("python_env")
    private String pythonEnv;

    @TableField("principal")
    private String principal;

    @TableField("keytab")
    private String keytab;

    @TableField("timeout_ms")
    private Long timeoutMs;

    @TableField("default_flink_cluster")
    private Boolean defaultFlinkCluster;

    @TableField("default_spark_cluster")
    private Boolean defaultSparkCluster;

    @TableField("flink_opts_name")
    private String flinkOptsName;

    @TableField("spark_opts_name")
    private String sparkOptsName;

    @TableField("create_time")
    private Long createTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_time")
    private Long updateTime;

    @TableField("update_by")
    private String updateBy;

    public String getClusterName() {
        return clusterName;
    }

    public TbCluster setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public String getClusterType() {
        return clusterType;
    }

    public TbCluster setClusterType(String clusterType) {
        this.clusterType = clusterType;
        return this;
    }

    public String getClusterDescription() {
        return clusterDescription;
    }

    public TbCluster setClusterDescription(String clusterDescription) {
        this.clusterDescription = clusterDescription;
        return this;
    }

    public String getHadoopHome() {
        return hadoopHome;
    }

    public TbCluster setHadoopHome(String hadoopHome) {
        this.hadoopHome = hadoopHome;
        return this;
    }

    public String getHdfsServer() {
        return hdfsServer;
    }

    public TbCluster setHdfsServer(String hdfsServer) {
        this.hdfsServer = hdfsServer;
        return this;
    }

    public String getYarnWebUrl() {
        return yarnWebUrl;
    }

    public TbCluster setYarnWebUrl(String yarnWebUrl) {
        this.yarnWebUrl = yarnWebUrl;
        return this;
    }

    public String getFlinkServer() {
        return flinkServer;
    }

    public TbCluster setFlinkServer(String flinkServer) {
        this.flinkServer = flinkServer;
        return this;
    }

    public String getFlinkWebUrl() {
        return flinkWebUrl;
    }

    public TbCluster setFlinkWebUrl(String flinkWebUrl) {
        this.flinkWebUrl = flinkWebUrl;
        return this;
    }

    public String getSparkServer() {
        return sparkServer;
    }

    public TbCluster setSparkServer(String sparkServer) {
        this.sparkServer = sparkServer;
        return this;
    }

    public String getSparkWebUrl() {
        return sparkWebUrl;
    }

    public TbCluster setSparkWebUrl(String sparkWebUrl) {
        this.sparkWebUrl = sparkWebUrl;
        return this;
    }

    public String getSparkHistoryServer() {
        return sparkHistoryServer;
    }

    public TbCluster setSparkHistoryServer(String sparkHistoryServer) {
        this.sparkHistoryServer = sparkHistoryServer;
        return this;
    }

    public String getPythonEnv() {
        return pythonEnv;
    }

    public TbCluster setPythonEnv(String pythonEnv) {
        this.pythonEnv = pythonEnv;
        return this;
    }

    public String getPrincipal() {
        return principal;
    }

    public TbCluster setPrincipal(String principal) {
        this.principal = principal;
        return this;
    }

    public String getKeytab() {
        return keytab;
    }

    public TbCluster setKeytab(String keytab) {
        this.keytab = keytab;
        return this;
    }

    public Long getTimeoutMs() {
        return timeoutMs;
    }

    public TbCluster setTimeoutMs(Long timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    public Boolean getDefaultFlinkCluster() {
        return defaultFlinkCluster;
    }

    public TbCluster setDefaultFlinkCluster(Boolean defaultFlinkCluster) {
        this.defaultFlinkCluster = defaultFlinkCluster;
        return this;
    }

    public Boolean getDefaultSparkCluster() {
        return defaultSparkCluster;
    }

    public TbCluster setDefaultSparkCluster(Boolean defaultSparkCluster) {
        this.defaultSparkCluster = defaultSparkCluster;
        return this;
    }

    public String getFlinkOptsName() {
        return flinkOptsName;
    }

    public TbCluster setFlinkOptsName(String flinkOptsName) {
        this.flinkOptsName = flinkOptsName;
        return this;
    }

    public String getSparkOptsName() {
        return sparkOptsName;
    }

    public TbCluster setSparkOptsName(String sparkOptsName) {
        this.sparkOptsName = sparkOptsName;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public TbCluster setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public TbCluster setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public TbCluster setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public TbCluster setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }
}