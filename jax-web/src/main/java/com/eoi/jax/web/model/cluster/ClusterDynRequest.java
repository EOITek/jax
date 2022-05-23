package com.eoi.jax.web.model.cluster;

import lombok.Data;

import java.util.Map;

@Data
public class ClusterDynRequest {

    private String clusterName;
    private String clusterType;
    private String clusterDescription;
    private Long updateTime;
    private Map<String,Object> configs;

}
