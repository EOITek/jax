/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eoi.jax.web.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.consts.ClusterType;
import com.eoi.jax.web.common.exception.BizException;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.dao.entity.TbCluster;
import com.eoi.jax.web.dao.entity.TbJar;
import com.eoi.jax.web.dao.entity.TbOptsFlink;
import com.eoi.jax.web.dao.entity.TbOptsSpark;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.dao.service.TbClusterService;
import com.eoi.jax.web.dao.service.TbJarService;
import com.eoi.jax.web.dao.service.TbOptsFlinkService;
import com.eoi.jax.web.dao.service.TbOptsSparkService;
import com.eoi.jax.web.dao.service.TbPipelineService;
import com.eoi.jax.web.model.cluster.ClusterReq;
import com.eoi.jax.web.model.cluster.ClusterResp;
import com.eoi.jax.web.provider.ResourceManagerProvider;
import com.eoi.jax.web.provider.cluster.Cluster;
import com.eoi.jax.web.provider.cluster.ClusterProvider;
import com.eoi.jax.web.provider.resource.ClusterResourcePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClusterService {
    private static final Logger logger = LoggerFactory.getLogger(ClusterService.class);

    @Autowired
    private TbPipelineService tbPipelineService;

    @Autowired
    private TbJarService tbJarService;

    @Autowired
    private TbClusterService tbClusterService;

    @Autowired
    private TbOptsFlinkService tbOptsFlinkService;

    @Autowired
    private TbOptsSparkService tbOptsSparkService;

    @Autowired
    private ClusterProvider clusterProvider;

    @Autowired
    private ResourceManagerProvider resourceManagerProvider;

    public List<ClusterResp> listCluster() {
        List<TbCluster> clusterList = tbClusterService.list(
                new LambdaQueryWrapper<TbCluster>()
                        .orderByDesc(TbCluster::getUpdateTime)
        );
        Map<String, TbOptsFlink> flinkMap = tbOptsFlinkService.list()
                .stream()
                .collect(Collectors.toMap(TbOptsFlink::getFlinkOptsName, i -> i));
        Map<String, TbOptsSpark> sparkMap = tbOptsSparkService.list()
                .stream()
                .collect(Collectors.toMap(TbOptsSpark::getSparkOptsName, i -> i));
        return clusterList.stream()
                .map(i -> {
                    TbOptsFlink flink = i.getFlinkOptsName() == null ? null : flinkMap.get(i.getFlinkOptsName());
                    TbOptsSpark spark = i.getSparkOptsName() == null ? null : sparkMap.get(i.getSparkOptsName());
                    return new ClusterResp().respFrom(i, flink, spark);
                })
                .collect(Collectors.toList());
    }

    public ClusterResp getCluster(String clusterName) {
        exist(clusterName, false, true);
        TbCluster entity = tbClusterService.getById(clusterName);
        TbOptsFlink flink = tbOptsFlinkService.getById(entity.getFlinkOptsName());
        TbOptsSpark spark = tbOptsSparkService.getById(entity.getSparkOptsName());
        return new ClusterResp().respFrom(entity, flink, spark);
    }

    @Transactional(rollbackFor = Exception.class)
    public ClusterResp createCluster(ClusterReq req) {
        if (!Common.verifyName(req.getClusterName())) {
            throw new BizException(ResponseCode.CLUSTER_INVALID_NAME);
        }
        exist(req.getClusterName(), true, false);
        verifyReq(req);
        clusterProvider.fillCreateClusterReq(req);
        long now = System.currentTimeMillis();
        TbCluster entity = req.toEntity(new TbCluster());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        tbClusterService.save(entity);
        if (Boolean.TRUE.equals(req.getDefaultFlinkCluster())) {
            setDefaultFlinkCluster(req.getClusterName());
        }
        if (Boolean.TRUE.equals(req.getDefaultSparkCluster())) {
            setDefaultSparkCluster(req.getClusterName());
        }
        return new ClusterResp().respFrom(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public ClusterResp updateCluster(ClusterReq req) {
        exist(req.getClusterName(), false, true);
        verifyReq(req);
        clusterProvider.fillUpdateClusterReq(req);
        long now = System.currentTimeMillis();
        TbCluster entity = req.toEntity(new TbCluster());
        //禁止修改clusterType
        entity.setClusterType(null);
        entity.setUpdateTime(now);
        tbClusterService.updateById(entity);
        if (Boolean.TRUE.equals(req.getDefaultFlinkCluster())) {
            setDefaultFlinkCluster(req.getClusterName());
        }
        if (Boolean.TRUE.equals(req.getDefaultSparkCluster())) {
            setDefaultSparkCluster(req.getClusterName());
        }
        return new ClusterResp().respFrom(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public ClusterResp deleteCluster(String clusterName) {
        exist(clusterName, false, true);
        verifyUsed(clusterName);
        TbCluster entity = tbClusterService.getById(clusterName);
        tbClusterService.removeById(clusterName);
        return new ClusterResp().respFrom(entity);
    }

    public ClusterResourcePool getResource(String clusterName) {
        exist(clusterName, false, true);
        TbCluster entity = tbClusterService.getById(clusterName);
        Cluster cluster = clusterProvider.fromEntity(entity, null, null);
        return resourceManagerProvider.getResourcePool(cluster);
    }

    public List<ClusterResp> listClusterResource() {
        List<TbCluster> clusterList = tbClusterService.list(
                new LambdaQueryWrapper<TbCluster>()
                        .orderByDesc(TbCluster::getUpdateTime)
        );
        return clusterList.stream()
                .map(i -> {
                    ClusterResp resp = new ClusterResp().respFrom(i);
                    Cluster cluster = clusterProvider.fromEntity(i, null, null);
                    try {
                        ClusterResourcePool resourcePool = resourceManagerProvider.getResourcePool(cluster);
                        resp.setResourcePool(resourcePool);
                    } catch (Exception e) {
                        logger.warn("query resource error", e);
                    }
                    return resp;
                })
                .collect(Collectors.toList());
    }

    private void setDefaultFlinkCluster(String clusterName) {
        tbClusterService.update(
                new LambdaUpdateWrapper<TbCluster>()
                        .set(TbCluster::getDefaultFlinkCluster, false)
                        .ne(TbCluster::getClusterName, clusterName)
        );
        tbClusterService.update(
                new LambdaUpdateWrapper<TbCluster>()
                        .set(TbCluster::getDefaultFlinkCluster, true)
                        .eq(TbCluster::getClusterName, clusterName)
        );
    }

    private void setDefaultSparkCluster(String clusterName) {
        tbClusterService.update(
                new LambdaUpdateWrapper<TbCluster>()
                        .set(TbCluster::getDefaultSparkCluster, false)
                        .ne(TbCluster::getClusterName, clusterName)
        );
        tbClusterService.update(
                new LambdaUpdateWrapper<TbCluster>()
                        .set(TbCluster::getDefaultSparkCluster, true)
                        .eq(TbCluster::getClusterName, clusterName)
        );
    }

    private void verifyReq(ClusterReq req) {
        ClusterType clusterType = ClusterType.fromString(req.getClusterType());
        if (clusterType == null) {
            throw new BizException(ResponseCode.CLUSTER_INVALID_TYPE);
        }
        if (Boolean.TRUE.equals(req.getDefaultFlinkCluster()) && !clusterType.supportFlink()) {
            throw new BizException(ResponseCode.CLUSTER_INVALID_DEFAULT_FLINK);
        }
        if (Boolean.TRUE.equals(req.getDefaultSparkCluster()) && !clusterType.supportSpark()) {
            throw new BizException(ResponseCode.CLUSTER_INVALID_DEFAULT_SPARK);
        }
        boolean verifyFlinkOpts = ClusterType.YARN.isEqual(req.getClusterType())
                || ClusterType.FLINK_STANDALONE.isEqual(req.getClusterType());
        boolean verifySparkOpts = ClusterType.YARN.isEqual(req.getClusterType())
                || ClusterType.SPARK_STANDALONE.isEqual(req.getClusterType());
        if (verifyFlinkOpts) {
            verifyFlinkOpts(req.getFlinkOptsName());
        }
        if (verifySparkOpts) {
            verifySparkOpts(req.getSparkOptsName());
        }
        if (req.getTimeoutMs() <= 0) {
            req.setTimeoutMs(600000L);
        }
    }

    private void verifyFlinkOpts(String flinkOptsName) {
        if (StrUtil.isBlank(flinkOptsName)) {
            throw new BizException(ResponseCode.CLUSTER_OPTS_FLINK_REQUIRED);
        }
        int flinkCount = tbOptsFlinkService.count(
                new LambdaQueryWrapper<TbOptsFlink>()
                        .eq(TbOptsFlink::getFlinkOptsName, flinkOptsName)
        );
        if (flinkCount == 0) {
            throw new BizException(ResponseCode.CLUSTER_OPTS_FLINK_REQUIRED);
        }
    }

    private void verifySparkOpts(String sparkOptsName) {
        if (StrUtil.isBlank(sparkOptsName)) {
            throw new BizException(ResponseCode.CLUSTER_OPTS_SPARK_REQUIRED);
        }
        int sparkCount = tbOptsSparkService.count(
                new LambdaQueryWrapper<TbOptsSpark>()
                        .eq(TbOptsSpark::getSparkOptsName, sparkOptsName)
        );
        if (sparkCount == 0) {
            throw new BizException(ResponseCode.CLUSTER_OPTS_SPARK_REQUIRED);
        }
    }

    private void verifyUsed(String clusterName) {
        int pipeline = tbPipelineService.count(
                new LambdaQueryWrapper<TbPipeline>()
                        .eq(TbPipeline::getClusterName, clusterName)
        );
        if (pipeline > 0) {
            throw new BizException(ResponseCode.CLUSTER_USED_PIPELINE);
        }
        int jar = tbJarService.count(
                new LambdaQueryWrapper<TbJar>()
                        .eq(TbJar::getClusterName, clusterName)
        );
        if (jar > 0) {
            throw new BizException(ResponseCode.CLUSTER_USED_JAR);
        }
    }

    /**
     * 检查pipelineName是否存在
     */
    private boolean exist(String clusterName, boolean existThrow, boolean notExistThrow) {
        int count = tbClusterService.count(
                new LambdaQueryWrapper<TbCluster>()
                        .eq(TbCluster::getClusterName, clusterName)
        );
        if (count > 0 && existThrow) {
            throw new BizException(ResponseCode.CLUSTER_EXIST);
        }
        if (count == 0 && notExistThrow) {
            throw new BizException(ResponseCode.CLUSTER_NOT_EXIST);
        }
        return count > 0;
    }
}
