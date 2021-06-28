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

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.exception.BizException;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.dao.entity.TbJar;
import com.eoi.jax.web.dao.entity.TbJob;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.dao.service.TbJarService;
import com.eoi.jax.web.dao.service.TbJobService;
import com.eoi.jax.web.dao.service.TbPipelineService;
import com.eoi.jax.web.model.Paged;
import com.eoi.jax.web.model.job.JobQueryReq;
import com.eoi.jax.web.model.job.JobResp;
import com.eoi.jax.web.model.manager.JobJar;
import com.eoi.jax.web.model.manager.PipelineJar;
import com.eoi.jax.web.model.pipeline.PipelineConfig;
import com.eoi.jax.web.model.pipeline.PipelineResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private TbJobService tbJobService;

    @Autowired
    private TbJarService tbJarService;

    @Autowired
    private TbPipelineService tbPipelineService;

    @Autowired
    private JarService jarService;

    public List<JobJar> jarsFromJobs(List<String> jobNames) {
        if (CollUtil.isEmpty(jobNames)) {
            return new ArrayList<>();
        }
        final List<TbJob> allJobs = tbJobService.listWithoutIconDoc();
        final List<TbJob> jobList = jobNames.stream()
                .distinct()
                .map(jobName ->
                        allJobs.stream()
                                .filter(p -> p.getJobName().equals(jobName))
                                .findFirst()
                                .orElseThrow(() -> new BizException(
                                        ResponseCode.JOB_NOT_EXIST.code,
                                        ResponseCode.JOB_NOT_EXIST.message + ": " + jobName)
                                )
                ).collect(Collectors.toList());
        final List<PipelineJar> jarList = jarService.jarsFromNames(
                jobList.stream().map(TbJob::getJarName).distinct().collect(Collectors.toList())
        );
        return jobList.stream().map(job -> {
            PipelineJar jar = jarList.stream()
                    .filter(p -> p.getJar().getJarName().equals(job.getJarName()))
                    .findFirst()
                    .orElseThrow(() -> new BizException(
                            ResponseCode.JAR_NOT_EXIST.code,
                            ResponseCode.JAR_NOT_EXIST.message + ": " + job.getJarName())
                    );
            return new JobJar(job, jar.getJar(), jar.getCluster());
        }).collect(Collectors.toList());
    }

    public List<JobResp> listJob() {
        List<TbJob> jobs = tbJobService.listWithoutIconDoc();
        List<TbJar> jars = tbJarService.list();
        return jobs.stream().map(i -> new JobResp().respFrom(i,
                jars.stream().filter(p -> p.getJarName().equals(i.getJarName()))
                        .findFirst().orElse(null))
        ).collect(Collectors.toList());
    }

    public Paged<JobResp> queryJob(JobQueryReq req) {
        List<TbJar> jars = tbJarService.list();
        IPage<TbJob> paged = tbJobService.pageWithoutIconDoc(req.page(), req.query());
        long total = paged.getTotal();
        List<TbJob> jobs = paged.getRecords();
        return new Paged<>(total, jobs.stream().map(i -> new JobResp().respFrom(i,
                jars.stream().filter(p -> p.getJarName().equals(i.getJarName()))
                        .findFirst().orElse(null))
        ).collect(Collectors.toList()));
    }

    public JobResp getJob(String jobName) {
        TbJob job = byId(jobName);
        TbJar jar = tbJarService.getById(job.getJarName());
        return new JobResp().respFrom(job, jar);
    }

    public List<PipelineResp> listJobPipeline(String jobName) {
        List<TbPipeline> pipelines = tbPipelineService.list(
                new LambdaQueryWrapper<TbPipeline>()
                        .like(TbPipeline::getPipelineConfig, "%" + jobName + "%")
        );
        return pipelines.stream()
                .filter(i -> {
                    PipelineConfig config = JsonUtil.decode(i.getPipelineConfig(), PipelineConfig.class);
                    return config.getJobs().stream().anyMatch(j -> j.getJobName().equals(jobName));
                })
                .map(i -> new PipelineResp().respFrom(i))
                .collect(Collectors.toList());
    }

    private TbJob byId(String jobName) {
        TbJob entity = tbJobService.getById(jobName);
        if (entity == null) {
            throw new BizException(ResponseCode.JOB_NOT_EXIST);
        }
        return entity;
    }
}
