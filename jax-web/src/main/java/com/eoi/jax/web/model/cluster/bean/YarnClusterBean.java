package com.eoi.jax.web.model.cluster.bean;

import com.eoi.jax.web.model.cluster.config.ConfigBean;
import com.eoi.jax.web.model.cluster.config.ConfigDef;

@ConfigBean("yarn")
public class YarnClusterBean extends CommClusterBean {

    @ConfigDef(label = "HADOOP_HOME",displayPosition = 11)
    private String hadoopHome;
    @ConfigDef(label = "PYTHON_HOME",displayPosition = 12)
    private String pythonEnv;
    @ConfigDef(label = "Kerberos Principal",displayPosition = 13)
    private String principal;
    @ConfigDef(label = "Kerberos Keytab",displayPosition = 14,type = ConfigDef.Type.TEXT)
    private String keytab;


//    @ConfigDef(label = "Flink框架集",displayPosition = 15)
//    private String flinkOptsName;
//
//    @ConfigDef(label = "Spark框架集",displayPosition = 16)
//    private String sparkOptsName;
//
//    @ConfigDef(label = "默认Spark运行集群",displayPosition = 17,type = ConfigDef.Type.BOOL)
//    private Boolean defaultSparkCluster;
//
//    @ConfigDef(label = "默认Flink运行集群",displayPosition = 18,type = ConfigDef.Type.BOOL)
//    private Boolean defaultFlinkCluster;



}
