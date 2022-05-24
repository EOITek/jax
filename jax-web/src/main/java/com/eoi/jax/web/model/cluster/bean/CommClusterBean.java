package com.eoi.jax.web.model.cluster.bean;

import com.eoi.jax.web.model.cluster.config.ConfigDef;

public class CommClusterBean {

    @ConfigDef(label = "提交超时",
            displayPosition = 4,required = false,
            type = ConfigDef.Type.LONG,
            description = "单位毫秒")
    private Long timeoutMs;

    public Long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }



}
