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
