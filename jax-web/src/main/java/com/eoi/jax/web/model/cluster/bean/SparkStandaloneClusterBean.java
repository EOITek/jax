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

@ConfigBean("spark_standalone")
public class SparkStandaloneClusterBean extends CommClusterBean {

    @ConfigDef(label = "SPARK_WEB_ADDR",
            description = "http://localhost:8088",
            displayPosition = 21)
    private String sparkWebUrl;
    @ConfigDef(label = "SPARK_MASTER",
            description = "http://localhost:7077",
            displayPosition = 22)
    private String sparkServer;

    public String getSparkWebUrl() {
        return sparkWebUrl;
    }

    public void setSparkWebUrl(String sparkWebUrl) {
        this.sparkWebUrl = sparkWebUrl;
    }

    public String getSparkServer() {
        return sparkServer;
    }

    public void setSparkServer(String sparkServer) {
        this.sparkServer = sparkServer;
    }
}
