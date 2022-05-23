package com.eoi.jax.web.model.cluster.bean;

import com.eoi.jax.web.model.cluster.config.ConfigBean;
import com.eoi.jax.web.model.cluster.config.ConfigDef;
import lombok.Data;

@Data
@ConfigBean()
public class CommClusterBean implements ClusterRequest{
    @ConfigDef(description = "集群名",displayPosition = 1)
    private String clusterName;
    @ConfigDef(description = "集群类型",displayPosition = 2)
    private String clusterType;
    @ConfigDef(description = "描述",displayPosition = 3,required = false)
    private String clusterDescription;

    @ConfigDef(description = "提交超时",displayPosition = 4,required = false,type = ConfigDef.Type.LONG)
    private Long timeoutMs;

    @ConfigDef(description = "更新时间",displayPosition = 5,required = false,type = ConfigDef.Type.LONG)
    private Long updateTime;

}
