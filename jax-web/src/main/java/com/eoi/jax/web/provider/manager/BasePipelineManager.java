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

package com.eoi.jax.web.provider.manager;

import cn.hutool.core.io.FileUtil;
import com.eoi.jax.manager.JaxManager;
import com.eoi.jax.manager.api.JobGetParam;
import com.eoi.jax.manager.api.JobGetResult;
import com.eoi.jax.manager.api.JobListParam;
import com.eoi.jax.manager.api.JobListResult;
import com.eoi.jax.manager.api.JobStartParam;
import com.eoi.jax.manager.api.JobStartResult;
import com.eoi.jax.manager.api.JobStopParam;
import com.eoi.jax.manager.api.JobStopResult;
import com.eoi.jax.manager.yarn.YarnJobGetParam;
import com.eoi.jax.manager.yarn.YarnJobStopParam;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.config.ConfigLoader;
import com.eoi.jax.web.common.exception.JaxException;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.model.manager.FileUrl;
import com.eoi.jax.web.model.manager.JobJar;
import com.eoi.jax.web.model.manager.OpType;
import com.eoi.jax.web.model.manager.OpUUID;
import com.eoi.jax.web.model.manager.Pipeline;
import com.eoi.jax.web.model.manager.PipelineDefine;
import com.eoi.jax.web.model.manager.PipelineEdgeDefine;
import com.eoi.jax.web.model.manager.PipelineJobDefine;
import com.eoi.jax.web.provider.cluster.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BasePipelineManager {
    private static final Logger logger = LoggerFactory.getLogger(BasePipelineManager.class);

    @Autowired
    private JarUrlProvider jarUrlProvider;

    @Autowired
    private JaxManagerProvider jaxManagerProvider;

    protected PipelineDefine setDefineJobEde(PipelineDefine define, Pipeline pipeline) {
        List<PipelineJobDefine> jobList = pipeline.getPipelineConfig().getJobs().stream().map(i -> {
            JobJar job = pipeline.getJobJars().stream().filter(p -> p.getJob().getJobName().equals(i.getJobName())).findFirst().orElseThrow(
                    () -> new JaxException(ResponseCode.JOB_NOT_EXIST.message)
            );
            return new PipelineJobDefine()
                    .setName(i.getJobName())
                    .setId(i.getJobId())
                    .setConfig(i.getJobConfig())
                    .setEntry(job.getJob().getJobName())
                    .setOpts(i.getJobOpts());
        }).collect(Collectors.toList());
        List<PipelineEdgeDefine> edgeList = pipeline.getPipelineConfig().getEdges().stream()
                .map(i -> new PipelineEdgeDefine()
                        .setFrom(i.getFrom())
                        .setTo(i.getTo())
                        .setFromSlot(i.getFromSlot())
                        .setToSlot(i.getToSlot()))
                .collect(Collectors.toList());
        define.setJobs(jobList);
        define.setEdges(edgeList);
        return define;
    }

    protected FileUrl genDefineFile(PipelineDefine define) {
        long now = System.currentTimeMillis();
        String fileName = define.getPipelineName() + "-" + now + ".pipeline";
        String filePath = Common.pathsJoin(ConfigLoader.load().jax.getWork(), fileName);
        try {
            FileUtil.writeString(JsonUtil.encode(define), filePath, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new JaxException(e);
        }
        return jarUrlProvider.genWorkUrl(filePath);
    }

    public abstract JobStartParam genStartParam(Pipeline pipeline);

    public abstract JobStopParam genStopParam(Pipeline pipeline);

    public abstract JobGetParam genGetParam(Pipeline pipeline);

    public abstract JobListParam genListParam(List<Pipeline> pipelines);

    public abstract JobStopParam genDeleteParam(Pipeline pipeline);

    public abstract JaxManager genJaxManager(Pipeline pipeline);

    private JaxManager getYarnManager(Pipeline pipeline) {
        return jaxManagerProvider.yarnJaxManager(pipeline.getCluster());
    }

    public JobStartResult start(Pipeline pipeline) {
        logger.info("start pipeline {} on {}", pipeline.getPipelineName(), pipeline.getClusterName());
        JobStartParam param = genStartParam(pipeline);
        param.setUuid(new OpUUID()
                .setPipelineName(pipeline.getPipelineName())
                .setOpTime(System.currentTimeMillis())
                .setOpType(OpType.START));
        String paramJson = JsonUtil.encode(param);
        logger.info("start pipeline {} param {}", pipeline.getPipelineName(), paramJson);
        JobStartResult result = genJaxManager(pipeline).start(param);
        String resultJson = JsonUtil.encode(result);
        logger.info("start pipeline {} result {}", pipeline.getPipelineName(), resultJson);
        return result;
    }

    public JobStopResult stop(Pipeline pipeline) {
        logger.info("stop pipeline {} on {}", pipeline.getPipelineName(), pipeline.getClusterName());
        JobStopParam param = genStopParam(pipeline);
        param.setUuid(new OpUUID()
                .setPipelineName(pipeline.getPipelineName())
                .setOpTime(System.currentTimeMillis())
                .setOpType(OpType.STOP));
        String paramJson = JsonUtil.encode(param);
        logger.info("stop pipeline {} param {}", pipeline.getPipelineName(), paramJson);
        JobStopResult result = genJaxManager(pipeline).stop(param);
        String resultJson = JsonUtil.encode(result);
        logger.info("stop pipeline {} result {}", pipeline.getPipelineName(), resultJson);
        return result;
    }

    public JobGetResult get(Pipeline pipeline) {
        logger.debug("get pipeline {} on {}", pipeline.getPipelineName(), pipeline.getClusterName());
        JobGetParam param = genGetParam(pipeline);
        param.setUuid(new OpUUID()
                .setPipelineName(pipeline.getPipelineName())
                .setOpTime(System.currentTimeMillis())
                .setOpType(OpType.GET));
        String paramJson = JsonUtil.encode(param);
        logger.debug("get pipeline {} param {}", pipeline.getPipelineName(), paramJson);
        JobGetResult result = genJaxManager(pipeline).get(param);
        String resultJson = JsonUtil.encode(result);
        logger.debug("get pipeline {} result {}", pipeline.getPipelineName(), resultJson);
        return result;
    }

    public JobListResult list(List<Pipeline> pipelines) {
        logger.debug("list pipeline");
        JobListParam param = genListParam(pipelines);
        param.setUuid(
                new OpUUID()
                        .setPipelineName(pipelines.stream()
                                .map(Pipeline::getPipelineName)
                                .collect(Collectors.joining(",")))
                        .setOpTime(System.currentTimeMillis())
                        .setOpType(OpType.LIST)

        );
        String paramJson = JsonUtil.encode(param);
        logger.debug("list pipeline param {}", paramJson);
        JobListResult result = genJaxManager(pipelines.get(0)).list(param);
        String resultJson = JsonUtil.encode(result);
        logger.debug("list pipeline result {}", resultJson);
        return result;
    }

    public JobStopResult delete(Pipeline pipeline) {
        logger.info("delete pipeline {} on {}", pipeline.getPipelineName(), pipeline.getClusterName());
        JobStopParam param = genDeleteParam(pipeline);
        param.setUuid(new OpUUID()
                .setPipelineName(pipeline.getPipelineName())
                .setOpTime(System.currentTimeMillis())
                .setOpType(OpType.DELETE));
        String paramJson = JsonUtil.encode(param);
        logger.info("delete pipeline {} param {}", pipeline.getPipelineName(), paramJson);
        JobStopResult result = genJaxManager(pipeline).stop(param);
        String resultJson = JsonUtil.encode(result);
        logger.info("delete pipeline {} result {}", pipeline.getPipelineName(), resultJson);
        return result;
    }

    public JobStopResult killYarnApp(Pipeline pipeline) {
        logger.info("kill pipeline {} on {}", pipeline.getPipelineName(), pipeline.getClusterName());
        Cluster cluster = pipeline.getCluster();
        YarnJobStopParam param = new YarnJobStopParam()
                .setApplicationId(pipeline.getYarnAppId())
                .setTimeOutMs(cluster.getTimeoutMs())
                .setPrincipal(cluster.getPrincipal())
                .setKeytab(cluster.getKeytab());
        param.setHadoopConfDir(cluster.getHadoopConfig());
        param.setUuid(new OpUUID()
                .setPipelineName(pipeline.getPipelineName())
                .setOpTime(System.currentTimeMillis())
                .setOpType(OpType.DELETE));
        String paramJson = JsonUtil.encode(param);
        logger.info("kill pipeline {} param {}", pipeline.getPipelineName(), paramJson);
        JaxManager jaxManager = getYarnManager(pipeline);
        JobStopResult result = jaxManager.stop(param);
        String resultJson = JsonUtil.encode(result);
        logger.info("kill pipeline {} result {}", pipeline.getPipelineName(), resultJson);
        return result;
    }

    public JobGetResult getYarnApp(Pipeline pipeline) {
        logger.debug("get pipeline {} on {}", pipeline.getPipelineName(), pipeline.getClusterName());
        Cluster cluster = pipeline.getCluster();
        YarnJobGetParam param = new YarnJobGetParam()
                .setApplicationId(pipeline.getYarnAppId())
                .setTimeOutMs(5000)
                .setPrincipal(cluster.getPrincipal())
                .setKeytab(cluster.getKeytab());
        param.setHadoopConfDir(cluster.getHadoopConfig());
        param.setUuid(new OpUUID()
                .setPipelineName(pipeline.getPipelineName())
                .setOpTime(System.currentTimeMillis())
                .setOpType(OpType.GET));
        String paramJson = JsonUtil.encode(param);
        logger.debug("get pipeline {} param {}", pipeline.getPipelineName(), paramJson);
        JaxManager jaxManager = getYarnManager(pipeline);
        JobGetResult result = jaxManager.get(param);
        String resultJson = JsonUtil.encode(result);
        logger.debug("get pipeline {} result {}", pipeline.getPipelineName(), resultJson);
        return result;
    }
}
