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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.exception.BizException;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.dao.entity.TbCluster;
import com.eoi.jax.web.dao.entity.TbOptsFlink;
import com.eoi.jax.web.dao.entity.TbOptsSpark;
import com.eoi.jax.web.dao.service.TbClusterService;
import com.eoi.jax.web.dao.service.TbOptsFlinkService;
import com.eoi.jax.web.dao.service.TbOptsSparkService;
import com.eoi.jax.web.model.opts.OptsFlinkReq;
import com.eoi.jax.web.model.opts.OptsFlinkResp;
import com.eoi.jax.web.model.opts.OptsResp;
import com.eoi.jax.web.model.opts.OptsSparkReq;
import com.eoi.jax.web.model.opts.OptsSparkResp;
import com.eoi.jax.web.provider.cluster.ClusterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

@Service
public class OptsService {
    private static final Logger logger = LoggerFactory.getLogger(OptsService.class);

    @Autowired
    private TbOptsFlinkService tbOptsFlinkService;

    @Autowired
    private TbOptsSparkService tbOptsSparkService;

    @Autowired
    private TbClusterService tbClusterService;

    @Autowired
    private ClusterProvider clusterProvider;

    public OptsFlinkResp flinkOptions() {
        TbOptsFlink entity = clusterProvider.defaultFlinkOpts();
        return new OptsFlinkResp().respFrom(entity);
    }

    public OptsSparkResp sparkOptions() {
        TbOptsSpark entity = clusterProvider.defaultSparkOpts();
        return new OptsSparkResp().respFrom(entity);
    }

    public List<OptsResp> listOpts() {
        List<OptsResp> list = new ArrayList<>();
        List<OptsFlinkResp> flinkList = listFlink();
        List<OptsSparkResp> sparkList = listSpark();
        list.addAll(flinkList);
        list.addAll(sparkList);
        return list.stream()
                .sorted(Comparator.comparingLong((ToLongFunction<OptsResp>) value ->
                        Optional.ofNullable(value.getUpdateTime()).orElse(0L)
                ).reversed())
                .collect(Collectors.toList());
    }

    public List<OptsFlinkResp> listFlink() {
        List<TbOptsFlink> list = tbOptsFlinkService.list(
                new LambdaQueryWrapper<TbOptsFlink>()
                        .orderByDesc(TbOptsFlink::getUpdateTime)
        );
        return list.stream()
                .map(i -> new OptsFlinkResp().respFrom(i))
                .collect(Collectors.toList());
    }

    public OptsFlinkResp createFlink(OptsFlinkReq req) {
        if (!Common.verifyName(req.getFlinkOptsName())) {
            throw new BizException(ResponseCode.OPTS_INVALID_NAME);
        }
        existFlink(req.getFlinkOptsName(), true, false);
        long now = System.currentTimeMillis();
        clusterProvider.fillFlinkOptsReq(req);
        TbOptsFlink entity = req.toEntity(new TbOptsFlink());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        tbOptsFlinkService.save(entity);
        return new OptsFlinkResp().respFrom(entity);
    }

    public OptsFlinkResp getFlink(String flinkOptsName) {
        existFlink(flinkOptsName, false, true);
        TbOptsFlink entity = tbOptsFlinkService.getById(flinkOptsName);
        return new OptsFlinkResp().respFrom(entity);
    }

    public OptsFlinkResp updateFlink(OptsFlinkReq req) {
        existFlink(req.getFlinkOptsName(), false, true);
        long now = System.currentTimeMillis();
        TbOptsFlink entity = req.toEntity(new TbOptsFlink());
        entity.setUpdateTime(now);
        tbOptsFlinkService.updateById(entity);
        return new OptsFlinkResp().respFrom(entity);
    }

    public OptsFlinkResp deleteFlink(String flinkOptsName) {
        existFlink(flinkOptsName, false, true);
        verifyFlinkUsed(flinkOptsName);
        TbOptsFlink entity = tbOptsFlinkService.getById(flinkOptsName);
        tbOptsFlinkService.removeById(flinkOptsName);
        return new OptsFlinkResp().respFrom(entity);
    }

    public List<OptsSparkResp> listSpark() {
        List<TbOptsSpark> list = tbOptsSparkService.list(
                new LambdaQueryWrapper<TbOptsSpark>()
                        .orderByDesc(TbOptsSpark::getUpdateTime)
        );
        return list.stream()
                .map(i -> new OptsSparkResp().respFrom(i))
                .collect(Collectors.toList());
    }

    public OptsSparkResp createSpark(OptsSparkReq req) {
        if (!Common.verifyName(req.getSparkOptsName())) {
            throw new BizException(ResponseCode.OPTS_INVALID_NAME);
        }
        existSpark(req.getSparkOptsName(), true, false);
        long now = System.currentTimeMillis();
        clusterProvider.fillSparkOptsReq(req);
        TbOptsSpark entity = req.toEntity(new TbOptsSpark());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        tbOptsSparkService.save(entity);
        return new OptsSparkResp().respFrom(entity);
    }

    public OptsSparkResp getSpark(String sparkOptsName) {
        existSpark(sparkOptsName, false, true);
        TbOptsSpark entity = tbOptsSparkService.getById(sparkOptsName);
        return new OptsSparkResp().respFrom(entity);
    }

    public OptsSparkResp updateSpark(OptsSparkReq req) {
        existSpark(req.getSparkOptsName(), false, true);
        long now = System.currentTimeMillis();
        TbOptsSpark entity = req.toEntity(new TbOptsSpark());
        entity.setUpdateTime(now);
        tbOptsSparkService.updateById(entity);
        return new OptsSparkResp().respFrom(entity);
    }

    public OptsSparkResp deleteSpark(String sparkOptsName) {
        existSpark(sparkOptsName, false, true);
        verifySparkUsed(sparkOptsName);
        TbOptsSpark entity = tbOptsSparkService.getById(sparkOptsName);
        tbOptsSparkService.removeById(sparkOptsName);
        return new OptsSparkResp().respFrom(entity);
    }

    private void verifyFlinkUsed(String flinkOptsName) {
        int count = tbClusterService.count(
                new LambdaQueryWrapper<TbCluster>()
                        .eq(TbCluster::getFlinkOptsName, flinkOptsName)
        );
        if (count > 0) {
            throw new BizException(ResponseCode.OPTS_USED);
        }
    }

    private void verifySparkUsed(String sparkOptsName) {
        int count = tbClusterService.count(
                new LambdaQueryWrapper<TbCluster>()
                        .eq(TbCluster::getSparkOptsName, sparkOptsName)
        );
        if (count > 0) {
            throw new BizException(ResponseCode.OPTS_USED);
        }
    }

    private boolean existFlink(String optsName, boolean existThrow, boolean notExistThrow) {
        int count = tbOptsFlinkService.count(
                new LambdaQueryWrapper<TbOptsFlink>()
                        .eq(TbOptsFlink::getFlinkOptsName, optsName)
        );
        if (count > 0 && existThrow) {
            throw new BizException(ResponseCode.OPTS_EXIST);
        }
        if (count == 0 && notExistThrow) {
            throw new BizException(ResponseCode.OPTS_NOT_EXIST);
        }
        return count > 0;
    }

    private boolean existSpark(String optsName, boolean existThrow, boolean notExistThrow) {
        int count = tbOptsSparkService.count(
                new LambdaQueryWrapper<TbOptsSpark>()
                        .eq(TbOptsSpark::getSparkOptsName, optsName)
        );
        if (count > 0 && existThrow) {
            throw new BizException(ResponseCode.OPTS_EXIST);
        }
        if (count == 0 && notExistThrow) {
            throw new BizException(ResponseCode.OPTS_NOT_EXIST);
        }
        return count > 0;
    }
}
