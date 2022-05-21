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
import com.eoi.jax.web.model.cluster.config.ConfigDescribe;
import com.eoi.jax.web.model.opts.MigrationResp;
import com.eoi.jax.web.model.opts.OptsDescribe;
import com.eoi.jax.web.model.opts.OptsFlinkResp;
import com.eoi.jax.web.model.opts.OptsSparkResp;
import com.eoi.jax.web.provider.resource.ClusterResourcePool;
import com.eoi.jax.web.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("cluster-options")
    public ResponseResult<Map<String,List<OptsDescribe>>> clusterOptionsTemplate() {
        MigrationResp resp = new MigrationResp();
        OptsFlinkResp flinkResp = optsService.flinkOptions();
        OptsSparkResp sparkResp = optsService.sparkOptions();
        resp.setFlinkOptsList(flinkResp.getOptsList());
        resp.setSparkOptsList(sparkResp.getOptsList());


        class RMClusterOptionDesc{
            private String name;
            private String desc;
            private String description;
            private String entryClass;
            private String version;
            private List<ConfigDescribe> options;
        }


        RMClusterOptionDesc yarnOptDesc = null;
        RMClusterOptionDesc k8sOptDesc = null;
        RMClusterOptionDesc flinkStandaloneOptDesc = null;
        RMClusterOptionDesc sparkStandaloneOptDesc = null;

        Map<String,List<OptsDescribe>> typeOptionDescMap = new HashMap<>();
        List<RMClusterOptionDesc> supportedRMClusterTypes = Arrays.asList(
                yarnOptDesc,
                flinkStandaloneOptDesc,
                sparkStandaloneOptDesc
        );


        supportedRMClusterTypes.forEach(typeDesc->{
//            typeOptionDescMap.put(typeDesc.name,typeDesc);
            typeOptionDescMap.put(typeDesc.name,typeDesc.options);
        });


        return new ResponseResult<Map<String,List<OptsDescribe>>>().setEntity(typeOptionDescMap);
    }

}
