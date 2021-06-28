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

package com.eoi.jax.web.controller;

import com.eoi.jax.web.common.ResponseResult;
import com.eoi.jax.web.model.cluster.ClusterReq;
import com.eoi.jax.web.model.cluster.ClusterResp;
import com.eoi.jax.web.provider.resource.ClusterResourcePool;
import com.eoi.jax.web.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClusterController extends V1Controller {

    @Autowired
    private ClusterService clusterService;

    @GetMapping("cluster")
    public ResponseResult<List<ClusterResp>> list() {
        return new ResponseResult<List<ClusterResp>>().setEntity(clusterService.listCluster());
    }

    @GetMapping("cluster/{clusterName}")
    public ResponseResult<ClusterResp> get(@PathVariable("clusterName") String clusterName) {
        return new ResponseResult<ClusterResp>().setEntity(clusterService.getCluster(clusterName));
    }

    @PostMapping("cluster/{clusterName}")
    public ResponseResult<ClusterResp> create(@PathVariable("clusterName") String clusterName,
                                              @RequestBody ClusterReq req) {
        req.setClusterName(clusterName);
        return new ResponseResult<ClusterResp>().setEntity(clusterService.createCluster(req));
    }

    @PutMapping("cluster/{clusterName}")
    public ResponseResult<ClusterResp> update(@PathVariable("clusterName") String clusterName,
                                              @RequestBody ClusterReq req) {
        req.setClusterName(clusterName);
        return new ResponseResult<ClusterResp>().setEntity(clusterService.updateCluster(req));
    }

    @DeleteMapping("cluster/{clusterName}")
    public ResponseResult<ClusterResp> delete(@PathVariable("clusterName") String clusterName) {
        return new ResponseResult<ClusterResp>().setEntity(clusterService.deleteCluster(clusterName));
    }

    @GetMapping("cluster/{clusterName}/resource")
    public ResponseResult<ClusterResourcePool> getResource(@PathVariable("clusterName") String clusterName) {
        return new ResponseResult<ClusterResourcePool>().setEntity(clusterService.getResource(clusterName));
    }

    @GetMapping("cluster-resource")
    public ResponseResult<List<ClusterResp>> listResource() {
        return new ResponseResult<List<ClusterResp>>().setEntity(clusterService.listClusterResource());
    }

}
