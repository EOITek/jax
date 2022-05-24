package com.eoi.jax.web.model.cluster;

import com.eoi.jax.web.dao.entity.TbCluster;
import lombok.Data;

import java.util.Map;

@Data
public class ClusterDynRequest {

    private String clusterName;
    private String clusterType;
    private String clusterDescription;
    private Long updateTime;
    private Map<String,Object> configs;


    public TbCluster toEntity(TbCluster tbCluster) {

        return null;
    }
}
