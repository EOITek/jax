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

package com.eoi.jax.web.provider.resource;

import com.eoi.jax.web.client.FlinkOverview;
import com.eoi.jax.web.client.SparkOverview;
import com.eoi.jax.web.client.YarnOverview;

public class ClusterResourcePool {
    private String clusterName;
    private String clusterType;
    private FlinkOverview flink = new FlinkOverview();
    private Boolean flinkGot = false;
    private SparkOverview spark = new SparkOverview();
    private Boolean sparkGot = false;
    private YarnOverview yarn = new YarnOverview();
    private Boolean yarnGot = false;

    public String getClusterName() {
        return clusterName;
    }

    public ClusterResourcePool setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public String getClusterType() {
        return clusterType;
    }

    public ClusterResourcePool setClusterType(String clusterType) {
        this.clusterType = clusterType;
        return this;
    }

    public FlinkOverview getFlink() {
        return flink;
    }

    public ClusterResourcePool setFlink(FlinkOverview flink) {
        this.flink = flink;
        return this;
    }

    public SparkOverview getSpark() {
        return spark;
    }

    public ClusterResourcePool setSpark(SparkOverview spark) {
        this.spark = spark;
        return this;
    }

    public YarnOverview getYarn() {
        return yarn;
    }

    public ClusterResourcePool setYarn(YarnOverview yarn) {
        this.yarn = yarn;
        return this;
    }

    public Boolean getFlinkGot() {
        return flinkGot;
    }

    public ClusterResourcePool setFlinkGot(Boolean flinkGot) {
        this.flinkGot = flinkGot;
        return this;
    }

    public Boolean getSparkGot() {
        return sparkGot;
    }

    public ClusterResourcePool setSparkGot(Boolean sparkGot) {
        this.sparkGot = sparkGot;
        return this;
    }

    public Boolean getYarnGot() {
        return yarnGot;
    }

    public ClusterResourcePool setYarnGot(Boolean yarnGot) {
        this.yarnGot = yarnGot;
        return this;
    }
}
