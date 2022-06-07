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
import com.eoi.jax.web.dao.entity.TbJob;
import com.eoi.jax.web.dao.service.TbJobService;
import com.eoi.jax.web.model.Paged;
import com.eoi.jax.web.model.job.JobQueryReq;
import com.eoi.jax.web.model.job.JobResp;
import com.eoi.jax.web.model.pipeline.PipelineResp;
import com.eoi.jax.web.service.JobService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JobController extends V1Controller {

    @Autowired
    private JobService jobService;

    @Autowired
    private TbJobService tbJobService;

    @ApiOperation("获取job列表")
    @GetMapping("job")
    public ResponseResult<List<JobResp>> list() {
        return new ResponseResult<List<JobResp>>().setEntity(jobService.listJob());
    }

    @ApiOperation("获取job列表")
    @PostMapping("job/list/query")
    public ResponseResult<List<JobResp>> query(@RequestBody JobQueryReq req) {
        Paged<JobResp> paged = jobService.queryJob(req);
        return new ResponseResult<List<JobResp>>().setEntity(paged.getList()).setTotal(paged.getTotal());
    }

    @ApiOperation("获取job详情")
    @GetMapping("job/{jobName}")
    public ResponseResult<JobResp> get(@PathVariable("jobName") String jobName) {
        return new ResponseResult<JobResp>().setEntity(jobService.getJob(jobName));
    }

    @ApiOperation("获取job的pipeline")
    @GetMapping("job/{jobName}/pipeline")
    public ResponseResult<List<PipelineResp>> pipeline(@PathVariable("jobName") String jobName) {
        return new ResponseResult<List<PipelineResp>>().setEntity(jobService.listJobPipeline(jobName));
    }

    @ApiOperation("获取job的document")
    @GetMapping("job/{jobName}/document")
    public ResponseEntity<byte[]> document(@PathVariable("jobName") String jobName) {
        TbJob entity = tbJobService.getById(jobName);
        if (entity == null || entity.getDoc() == null || entity.getDoc().length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .header("Content-Type","text/markdown; charset=UTF-8")
                .body(entity.getDoc());
    }

    @ApiOperation("获取job的icon")
    @GetMapping("job/{jobName}/icon.svg")
    public ResponseEntity<byte[]> icon(@PathVariable("jobName") String jobName) {
        TbJob entity = tbJobService.getById(jobName);
        if (entity == null || entity.getIcon() == null || entity.getIcon().length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .header("Content-Type","image/svg+xml; charset=UTF-8")
                .body(entity.getIcon());
    }
}
