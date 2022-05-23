package com.eoi.jax.web.model.cluster.bean;

import com.eoi.jax.web.model.cluster.config.ConfigBean;
import com.eoi.jax.web.model.cluster.config.ConfigDef;

@ConfigBean("spark")
public class SparkStandaloneClusterBean extends CommClusterBean {


    @ConfigDef(description = "SPARK_WEB_ADDR",displayPosition = 21)
    private String sparkWebUrl;
    @ConfigDef(description = "SPARK_MASTER",displayPosition = 22)
    private String sparkServer;

    @ConfigDef(description = "Spark框架集",displayPosition = 23)
    private String sparkOptsName;

    @ConfigDef(description = "默认Spark运行集群",displayPosition = 24)
    private Boolean defaultSparkCluster;


}
