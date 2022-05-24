package com.eoi.jax.web.model.cluster.bean;

import com.eoi.jax.web.model.cluster.config.ConfigBean;
import com.eoi.jax.web.model.cluster.config.ConfigDef;
import lombok.Data;

@Data
@ConfigBean("flink_standalone")
public class FlinkStandaloneClusterBean extends CommClusterBean {

    @ConfigDef(label = "FLINK_WEB_ADDR",displayPosition = 31)
    private String flinkWebUrl;

    @ConfigDef(label = "FLINK_JOB_MASTER",displayPosition = 32)
    private String flinkServer;

//    @ConfigDef(label = "Flink框架集",displayPosition = 33)
//    private String flinkOptsName;
//
//    @ConfigDef(label = "默认Flink运行集群",displayPosition = 34,type = ConfigDef.Type.BOOL)
//    private Boolean defaultFlinkCluster;



}
