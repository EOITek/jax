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

package com.eoi.jax.web.model.cluster.bean;

import com.eoi.jax.web.model.cluster.config.ConfigBean;
import com.eoi.jax.web.model.cluster.config.ConfigDef;

@ConfigBean("flink_standalone")
public class FlinkStandaloneClusterBean extends CommClusterBean {

    @ConfigDef(label = "FLINK_WEB_ADDR",
            description = "http://localhost:8081",
            displayPosition = 31)
    private String flinkWebUrl;

    @ConfigDef(label = "FLINK_JOB_MASTER",
            description = "localhost:8081",
            displayPosition = 32)
    private String flinkServer;

    public String getFlinkWebUrl() {
        return flinkWebUrl;
    }

    public void setFlinkWebUrl(String flinkWebUrl) {
        this.flinkWebUrl = flinkWebUrl;
    }

    public String getFlinkServer() {
        return flinkServer;
    }

    public void setFlinkServer(String flinkServer) {
        this.flinkServer = flinkServer;
    }
}
