package com.eoi.jax.web.model.cluster.bean;

import com.eoi.jax.web.model.cluster.config.ConfigDef;
import lombok.Data;

@Data
//@ConfigBean("abc")
public class FlinkStandaloneClusterBean extends CommClusterBean {

    @ConfigDef(description = "FLINK_WEB_ADDR",displayPosition = 31)
    private String flinkWebUrl;

    @ConfigDef(description = "FLINK_JOB_MASTER",displayPosition = 32)
    private String flinkServer;

    @ConfigDef(description = "Flink框架集",displayPosition = 33)
    private String flinkOptsName;

    @ConfigDef(description = "默认Flink运行集群",displayPosition = 34,type = ConfigDef.Type.BOOLEAN)
    private Boolean defaultFlinkCluster;



}
