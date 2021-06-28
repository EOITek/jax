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

package com.eoi.jax.web.provider.validator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.manager.process.ProcessOutput;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.config.ConfigLoader;
import com.eoi.jax.web.common.consts.PipelineType;
import com.eoi.jax.web.common.exception.BizException;
import com.eoi.jax.web.common.exception.JaxException;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.model.manager.JobJar;
import com.eoi.jax.web.model.manager.Pipeline;
import com.eoi.jax.web.model.pipeline.PipelineEdge;
import com.eoi.jax.web.model.pipeline.PipelineJob;
import com.eoi.jax.web.model.pipeline.PipelineResp;
import com.eoi.jax.web.provider.JarFileProvider;
import com.eoi.jax.web.provider.JaxValidator;
import com.eoi.jax.web.provider.ProcessRunnerProvider;
import com.eoi.jax.web.provider.jar.JobJarClassPath;
import com.eoi.jax.web.provider.scanner.JaxScannerImpl;
import com.nimbusds.jose.util.StandardCharset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JaxValidatorImpl implements JaxValidator {
    private static final Logger logger = LoggerFactory.getLogger(JaxValidatorImpl.class);
    public static final String DAG = "dag";

    @Autowired
    private JarFileProvider jarFileProvider;

    @Autowired
    private ProcessRunnerProvider processRunnerProvider;

    @Autowired
    private JobJarClassPath jobJarClassPath;

    @Override
    public ValidateResult checkDag(PipelineResp lastPipeline, Pipeline currentPipeline) {
        List<String> paths = genPipelineClassPath(currentPipeline);
        String input = genPipelineDAG(lastPipeline, currentPipeline);
        String output = input + ".output";
        Map<String, String> env = Collections.singletonMap(
                JaxScannerImpl.JAX_TOOL_CLASS_PATH,
                StrUtil.join(":", paths)
        );
        List<String> args = genArgs(DAG, input, output);
        ProcessOutput processOutput = processRunnerProvider.jaxToolRunner(env).exec(args, logger::info);
        if (processOutput.getCode() != 0) {
            safeDelete(input, output);
            throw new BizException(ResponseCode.PIPELINE_INVALID_CONF);
        }
        String content = FileUtil.readString(output, StandardCharsets.UTF_8);
        safeDelete(input, output);
        ValidateResult result = JsonUtil.decode(content, ValidateResult.class);
        if (Boolean.TRUE.equals(result.getInvalid())) {
            logger.error("invalid pipeline {}:\n{}", currentPipeline.getPipelineName(), result.getStackTrace());
            throw new BizException(
                    ResponseCode.PIPELINE_INVALID_CONF.code,
                    ResponseCode.PIPELINE_INVALID_CONF.message + ":" + result.getMessage(),
                    new JaxException(result.getStackTrace()),
                    new InvalidPipeline().setInvalidJobId(result.getInvalidJobId())
                            .setInvalidJobConfig(result.getInvalidJobConfig())
            );
        }
        return result;
    }

    private String genPipelineDAG(PipelineResp lastPipeline, Pipeline currentPipeline) {
        long now = System.currentTimeMillis();
        String fileName = currentPipeline.getPipelineName() + "-" + now + ".dag.check";
        String input =  Common.pathsJoin(ConfigLoader.load().jax.getWork(), fileName);
        PipelineDAG dag = new PipelineDAG();
        dag.setPipelineName(currentPipeline.getPipelineName());
        List<PipelineDAG.ConfigNode> jobs = new ArrayList<>();
        for (PipelineJob item : currentPipeline.getPipelineConfig().getJobs()) {
            PipelineDAG.ConfigNode job = new PipelineDAG.ConfigNode();
            job.setId(item.getJobId());
            job.setEntry(item.getJobName());
            job.setConfig(item.getJobConfig());
            if (lastPipeline != null
                    && lastPipeline.getPipelineConfig() != null
                    && lastPipeline.getPipelineConfig().getJobs() != null) {
                job.setLastConfig(
                        lastPipeline.getPipelineConfig().getJobs().stream()
                                .filter(i -> i.getJobName().equals(item.getJobName()))
                                .findAny()
                                .map(PipelineJob::getJobConfig)
                                .orElse(null)
                );
            }
            jobs.add(job);
        }
        dag.setJobs(jobs);
        List<PipelineDAG.EdgeDescription> edges = new ArrayList<>();
        for (PipelineEdge item : currentPipeline.getPipelineConfig().getEdges()) {
            PipelineDAG.EdgeDescription edge = new PipelineDAG.EdgeDescription();
            edge.setFrom(item.getFrom());
            edge.setTo(item.getTo());
            edge.setFromSlot(item.getFromSlot());
            edge.setToSlot(item.getToSlot());
            edges.add(edge);
        }
        dag.setEdges(edges);
        String content = JsonUtil.encode(dag);
        FileUtil.writeString(content, input, StandardCharset.UTF_8);
        return input;
    }

    private List<String> genPipelineClassPath(Pipeline pipeline) {
        List<String> runLibJars = new ArrayList<>();
        List<String> jobLibJars = new ArrayList<>();
        if (PipelineType.isStreaming(pipeline.getPipelineType())) {
            runLibJars = Common.listJars(pipeline.getCluster().getFlinkOpts().getLib());
            jobLibJars = Common.listJars(pipeline.getCluster().getFlinkOpts().getJobLib());
        } else if (PipelineType.isBatch(pipeline.getPipelineType())) {
            runLibJars = Common.listJars(pipeline.getCluster().getSparkOpts().getLib());
            jobLibJars = Common.listJars(pipeline.getCluster().getSparkOpts().getJobLib());
        }
        List<String> jobJars = pipeline.getJobJars()
                .stream()
                .filter(Common.distinctByKey(
                        (Function<JobJar, String>) o ->
                                o.getJar().getJarName())
                )
                .map(jar ->
                        jarFileProvider.touchJarFile(jar.getJar(), jar.getCluster())
                )
                .collect(Collectors.toList());
        List<String> jars = new ArrayList<>();
        jars.addAll(runLibJars);
        jars.addAll(jobLibJars);
        jars.addAll(jobJars);
        return jars;
    }

    private List<String> genArgs(String mode, String path, String output) {
        List<String> args = new ArrayList<>();
        args.add("--action");
        args.add("validate");
        args.add("--mode");
        args.add(mode);
        args.add("--path");
        args.add(path);
        args.add("--output");
        args.add(output);
        return args;
    }

    private void safeDelete(String... paths) {
        for (String path : paths) {
            try {
                FileUtil.del(path);
            } catch (Exception ignore) {
            }
        }
    }
}
