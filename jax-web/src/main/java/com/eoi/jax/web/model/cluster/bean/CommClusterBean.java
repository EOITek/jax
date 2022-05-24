package com.eoi.jax.web.model.cluster.bean;

import com.eoi.jax.web.model.cluster.config.ConfigDef;
import lombok.Data;

@Data
public class CommClusterBean {

//    @ConfigDef(label = "描述",displayPosition = 3,required = false)
//    private String clusterDescription;

    @ConfigDef(label = "提交超时",displayPosition = 4,required = false,type = ConfigDef.Type.LONG,description = "单位秒",defaultValue = "60")
    private Long timeoutMs;


}
