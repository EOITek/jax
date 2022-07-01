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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eoi.jax.web.common.ResponseResult;
import com.eoi.jax.web.common.Tuple;
import com.eoi.jax.web.dao.entity.TbPipelineConsole;
import com.eoi.jax.web.dao.entity.TbPipelineLog;
import com.eoi.jax.web.model.Paged;
import com.eoi.jax.web.model.pipeline.PipelineBuildIn;
import com.eoi.jax.web.model.pipeline.PipelineExportFormReq;
import com.eoi.jax.web.model.pipeline.PipelineExportReq;
import com.eoi.jax.web.model.pipeline.PipelineQueryReq;
import com.eoi.jax.web.model.pipeline.PipelineReq;
import com.eoi.jax.web.model.pipeline.PipelineResp;
import com.eoi.jax.web.service.PipelineService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@RestController
public class PipelineController extends V1Controller {
    @Autowired
    private PipelineService pipelineService;

    /**
     * 分页获取Pipeline列表
     * @param search 按照名称和描述筛选列表
     * @param type 类别：流处理（streaming）、批处理（batch）
     * @param status 状态：运行中（RUNNING）、已完成（FINISHED）、运行失败（FAILED）
     * @param inStatus 内部状态：运行中（RUNNING）、已完成（FINISHED）、运行失败（FAILED）
     */
    @ApiOperation("获取pipeline列表")
    @GetMapping("pipeline")
    public ResponseResult<List<PipelineResp>> list(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "type", required = false) String[] type,
            @RequestParam(value = "status", required = false) String[] status,
            @RequestParam(value = "inStatus", required = false) String[] inStatus,
            @RequestParam(value = "pageIndex", required = false) Integer pageIndex,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = "update_time") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "desc") String order) {
        Paged<PipelineResp> page = pipelineService.listPipelineByPage(search, type, status, inStatus, pageIndex, pageSize, sortBy, order);
        return new ResponseResult<List<PipelineResp>>().setEntity(page.getList()).setTotal(page.getTotal());
    }

    @ApiOperation("分页查询pipeline")
    @PostMapping("pipeline/list/query")
    public ResponseResult<List<PipelineResp>> queryPipeline(@RequestBody PipelineQueryReq req) {
        Paged<PipelineResp> page = pipelineService.queryPipeline(req);
        return new ResponseResult<List<PipelineResp>>().setEntity(page.getList()).setTotal(page.getTotal());
    }

    @ApiOperation("获取pipeline详情")
    @GetMapping("pipeline/{pipelineName}")
    public ResponseResult<PipelineResp> get(@PathVariable("pipelineName") String pipelineName) {
        return new ResponseResult<PipelineResp>().setEntity(pipelineService.getPipeline(pipelineName));
    }

    @ApiOperation("新建pipeline草稿")
    @PostMapping("pipeline/{pipelineName}/draft")
    public ResponseResult<PipelineResp> createDraft(
            @PathVariable("pipelineName") String pipelineName,
            @RequestBody PipelineReq req) {
        req.setPipelineName(pipelineName);
        return new ResponseResult<PipelineResp>().setEntity(pipelineService.createDraft(req));
    }

    @ApiOperation("修改pipeline草稿")
    @PutMapping("pipeline/{pipelineName}/draft")
    public ResponseResult<PipelineResp> updateDraft(
            @PathVariable("pipelineName") String pipelineName,
            @RequestBody PipelineReq req) {
        req.setPipelineName(pipelineName);
        return new ResponseResult<PipelineResp>().setEntity(pipelineService.updateDraft(req));
    }

    @ApiOperation("暂存pipeline")
    @PostMapping("pipeline/{pipelineName}/stage")
    public ResponseResult<PipelineResp> createStage(
            @PathVariable("pipelineName") String pipelineName,
            @RequestBody PipelineReq req) {
        req.setPipelineName(pipelineName);
        return new ResponseResult<PipelineResp>().setEntity(pipelineService.createStage(req));
    }

    @ApiOperation("暂存pipeline")
    @PutMapping("pipeline/{pipelineName}/stage")
    public ResponseResult<PipelineResp> updateStage(
            @PathVariable("pipelineName") String pipelineName,
            @RequestParam(value = "autoCreate", required = false) Boolean autoCreate,
            @RequestBody PipelineReq req) {
        req.setPipelineName(pipelineName);
        return new ResponseResult<PipelineResp>().setEntity(pipelineService.updateStage(req,  Boolean.TRUE.equals(autoCreate)));
    }

    @ApiOperation("新建pipeline并启动")
    @PostMapping("pipeline/{pipelineName}")
    public ResponseResult<PipelineResp> create(
            @PathVariable("pipelineName") String pipelineName,
            @RequestBody PipelineReq req) {
        req.setPipelineName(pipelineName);
        return new ResponseResult<PipelineResp>().setEntity(pipelineService.createPipeline(req));
    }

    @ApiOperation("修改pipeline并启动")
    @PutMapping("pipeline/{pipelineName}")
    public ResponseResult<PipelineResp> update(
            @PathVariable("pipelineName") String pipelineName,
            @RequestParam(value = "autoCreate", required = false) Boolean autoCreate,
            @RequestBody PipelineReq req) {
        req.setPipelineName(pipelineName);
        return new ResponseResult<PipelineResp>().setEntity(
                pipelineService.updatePipeline(req,
                        Boolean.TRUE.equals(autoCreate))
        );
    }

    @ApiOperation("启动pipeline")
    @PutMapping("pipeline/{pipelineName}/start")
    public ResponseResult<PipelineResp> start(
            @PathVariable("pipelineName") String pipelineName,
            @RequestParam(value = "forceStart", required = false) Boolean forceStart) {
        return new ResponseResult<PipelineResp>().setEntity(
                pipelineService.startPipeline(pipelineName,
                        Boolean.TRUE.equals(forceStart))
        );
    }

    @ApiOperation("停止pipeline")
    @PutMapping("pipeline/{pipelineName}/stop")
    public ResponseResult<PipelineResp> stop(
            @PathVariable("pipelineName") String pipelineName,
            @RequestParam(value = "forceStop", required = false) Boolean forceStop,
            @RequestParam(value = "disableSavePoint", required = false) Boolean disableSavePoint) {
        forceStop = Boolean.TRUE.equals(forceStop);
        disableSavePoint = disableSavePoint == null ? forceStop : disableSavePoint;
        return new ResponseResult<PipelineResp>().setEntity(
                pipelineService.stopPipeline(pipelineName, forceStop, disableSavePoint)
        );
    }

    @ApiOperation("删除pipeline")
    @DeleteMapping("pipeline/{pipelineName}")
    public ResponseResult<PipelineResp> delete(
            @PathVariable("pipelineName") String pipelineName,
            @RequestParam(value = "forceDelete", required = false) Boolean forceDelete) {
        return new ResponseResult<PipelineResp>().setEntity(
                pipelineService.deletePipeline(pipelineName,
                        Boolean.TRUE.equals(forceDelete))
        );
    }

    @ApiOperation("pipeline日志")
    @GetMapping("pipeline/{pipelineName}/log")
    public ResponseResult<List<TbPipelineLog>> log(@PathVariable("pipelineName") String pipelineName) {
        return new ResponseResult<List<TbPipelineLog>>().setEntity(pipelineService.logPipeline(pipelineName));
    }

    @ApiOperation("pipeline控制台")
    @GetMapping("pipeline/{pipelineName}/console")
    public ResponseResult<List<TbPipelineConsole>> listConsole(@PathVariable("pipelineName") String pipelineName,
                                                               @RequestParam(value = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
                                                               @RequestParam(value = "pageSize", required = false, defaultValue = "1000") Integer pageSize
    ) {
        IPage<TbPipelineConsole> page = pipelineService.listPipelineConsole(pipelineName, pageIndex, pageSize);
        return new ResponseResult<List<TbPipelineConsole>>().setEntity(page.getRecords()).setTotal(page.getTotal());
    }

    @ApiOperation("pipeline控制台删除")
    @DeleteMapping("pipeline/{pipelineName}/console")
    public ResponseResult<Boolean> deleteConsole(@PathVariable("pipelineName") String pipelineName) {
        return new ResponseResult<Boolean>().setEntity(pipelineService.deletePipelineConsole(pipelineName));
    }

    @ApiOperation("批量导出pipeline")
    @PostMapping("pipeline-export")
    public void export(@ModelAttribute PipelineExportFormReq req, final HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.addHeader("Content-Disposition", String.format("attachment;filename=export-pipeline-%s.zip", System.currentTimeMillis()));
        PipelineExportReq request = req.toPipelineExportReq();
        pipelineService.exportPipeline(request, response.getOutputStream());
    }

    @ApiOperation("批量导入pipeline")
    @PostMapping("pipeline-import")
    public ResponseResult<List<Tuple<String, String>>> importPipeline(@RequestParam("file") MultipartFile file) {
        return new ResponseResult<List<Tuple<String, String>>>().setEntity(pipelineService.importPipeline(file));
    }

    @ApiOperation("批量导入pipeline-内置")
    @PostMapping("pipeline-import/build-in")
    public ResponseResult<List<Tuple<String, String>>> importBuildInPipeline(@RequestBody PipelineBuildIn req) {
        return new ResponseResult<List<Tuple<String, String>>>().setEntity(pipelineService.importPipeline(req));
    }

    @ApiOperation("build-in jar")
    @GetMapping("pipeline-import/build-in")
    public ResponseResult<PipelineBuildIn> buildIns() {
        return new ResponseResult<PipelineBuildIn>().setEntity(pipelineService.listBuildIns());
    }
}
