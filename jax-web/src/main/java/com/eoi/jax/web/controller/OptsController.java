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
import com.eoi.jax.web.model.opts.*;
import com.eoi.jax.web.service.OptsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OptsController extends V1Controller {

    @Autowired
    private OptsService optsService;

    @ApiOperation("获取opts列表")
    @GetMapping("opts")
    public ResponseResult<List<OptsResp>> list() {
        return new ResponseResult<List<OptsResp>>().setEntity(optsService.listOpts());
    }

    @ApiOperation("获取opts-flink列表")
    @GetMapping("opts/flink")
    public ResponseResult<List<OptsFlinkResp>> listFlink() {
        return new ResponseResult<List<OptsFlinkResp>>().setEntity(optsService.listFlink());
    }

    @ApiOperation("获取opts-flink详情")
    @GetMapping("opts/flink/{flinkOptsName}")
    public ResponseResult<OptsFlinkResp> getFlink(@PathVariable("flinkOptsName") String flinkOptsName) {
        return new ResponseResult<OptsFlinkResp>().setEntity(optsService.getFlink(flinkOptsName));
    }

    @ApiOperation("新建opts-flink")
    @PostMapping("opts/flink/{flinkOptsName}")
    public ResponseResult<OptsFlinkResp> createFlink(@PathVariable("flinkOptsName") String flinkOptsName,
                                                     @RequestBody OptsFlinkReq req) {
        req.setFlinkOptsName(flinkOptsName);
        return new ResponseResult<OptsFlinkResp>().setEntity(optsService.createFlink(req));
    }

    @ApiOperation("更新opts-flink")
    @PutMapping("opts/flink/{flinkOptsName}")
    public ResponseResult<OptsFlinkResp> updateFlink(@PathVariable("flinkOptsName") String flinkOptsName,
                                                     @RequestBody OptsFlinkReq req) {
        req.setFlinkOptsName(flinkOptsName);
        return new ResponseResult<OptsFlinkResp>().setEntity(optsService.updateFlink(req));
    }

    @ApiOperation("删除opts-flink详情")
    @DeleteMapping("opts/flink/{flinkOptsName}")
    public ResponseResult<OptsFlinkResp> deleteFlink(@PathVariable("flinkOptsName") String flinkOptsName) {
        return new ResponseResult<OptsFlinkResp>().setEntity(optsService.deleteFlink(flinkOptsName));
    }

    @ApiOperation("获取opts-spark列表")
    @GetMapping("opts/spark")
    public ResponseResult<List<OptsSparkResp>> listSpark() {
        return new ResponseResult<List<OptsSparkResp>>().setEntity(optsService.listSpark());
    }

    @ApiOperation("获取opts-spark详情")
    @GetMapping("opts/spark/{sparkOptsName}")
    public ResponseResult<OptsSparkResp> getSpark(@PathVariable("sparkOptsName") String sparkOptsName) {
        return new ResponseResult<OptsSparkResp>().setEntity(optsService.getSpark(sparkOptsName));
    }

    @ApiOperation("新建opts-spark")
    @PostMapping("opts/spark/{sparkOptsName}")
    public ResponseResult<OptsSparkResp> createSpark(@PathVariable("sparkOptsName") String sparkOptsName,
                                                     @RequestBody OptsSparkReq req) {
        req.setSparkOptsName(sparkOptsName);
        return new ResponseResult<OptsSparkResp>().setEntity(optsService.createSpark(req));
    }

    @ApiOperation("更新opts-spark")
    @PutMapping("opts/spark/{sparkOptsName}")
    public ResponseResult<OptsSparkResp> updateSpark(@PathVariable("sparkOptsName") String sparkOptsName,
                                                     @RequestBody OptsSparkReq req) {
        req.setSparkOptsName(sparkOptsName);
        return new ResponseResult<OptsSparkResp>().setEntity(optsService.updateSpark(req));
    }

    @ApiOperation("删除opts-spark详情")
    @DeleteMapping("opts/spark/{sparkOptsName}")
    public ResponseResult<OptsSparkResp> deleteSpark(@PathVariable("sparkOptsName") String sparkOptsName) {
        return new ResponseResult<OptsSparkResp>().setEntity(optsService.deleteSpark(sparkOptsName));
    }

    @ApiOperation("获取opts列表")
    @GetMapping("opts-options")
    public ResponseResult<MigrationResp> options() {
        MigrationResp resp = new MigrationResp();
        OptsFlinkResp flinkResp = optsService.flinkOptions();
        OptsSparkResp sparkResp = optsService.sparkOptions();
        resp.setFlinkOptsList(flinkResp.getOptsList());
        resp.setSparkOptsList(sparkResp.getOptsList());
        return new ResponseResult<MigrationResp>().setEntity(resp);
    }
}
