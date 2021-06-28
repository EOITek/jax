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

package com.eoi.jax.web.debug;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.common.PythonHelper;
import com.eoi.jax.manager.flink.FlinkJobStartParam;
import com.eoi.jax.manager.spark.SparkJobStartParam;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.config.AppConfig;
import com.eoi.jax.web.common.config.ConfigLoader;
import com.eoi.jax.web.common.consts.PipelineType;
import com.eoi.jax.web.common.exception.JaxException;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.dao.entity.TbPipelineJob;
import com.eoi.jax.web.model.manager.JobJar;
import com.eoi.jax.web.model.manager.Pipeline;
import com.eoi.jax.web.model.pipeline.JobDebugOpts;
import com.eoi.jax.web.model.pipeline.PipelineEdge;
import com.eoi.jax.web.model.pipeline.PipelineJob;
import com.eoi.jax.web.model.pipeline.PipelineReq;
import com.eoi.jax.web.provider.FlinkPipelineManager;
import com.eoi.jax.web.provider.JarFileProvider;
import com.eoi.jax.web.provider.SparkPipelineManager;
import com.eoi.jax.web.provider.cluster.ClusterVariable;
import com.eoi.jax.web.service.PipelineService;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * flink mini cluster 子进程管理器
 *
 * 运行pipeline的子进程
 */
@Component
public class PipelineProcessManager {
    private static final Logger logger = LoggerFactory.getLogger(PipelineProcessManager.class);
    private static final String PYTHON_INTERPRETER_ENV = "PYTHON_INTERPRETER"; //flink的python环境
    private static final String PYSPARK_PYTHON = "PYSPARK_PYTHON"; //spark的python环境
    private static final String PYSPARK_DRIVER_PYTHON = "PYSPARK_DRIVER_PYTHON"; //spark driver的python环境
    private static final String PYTHON_PATH = "PYTHONPATH"; //python job文件

    private static Map<String, PipelineProcess> processes = new ConcurrentHashMap<>();

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private FlinkPipelineManager flinkPipelineManager;

    @Autowired
    private SparkPipelineManager sparkPipelineManager;

    @Autowired
    private JarFileProvider jarFileProvider;

    public void startPipeline(final String uuid, final PipelineReq pipelineReq, final Lifecycle lifecycle, final Console console) {
        if (processes.containsKey(uuid)) {
            throw new JaxException(ResponseCode.DEBUG_ALREADY_STARTED.message);
        }
        pipelineReq.setPipelineName("debug_" + uuid);
        PipelineProcess process;
        if (PipelineType.isStreaming(pipelineReq.getPipelineType())) {
            process = startFlinkPipeline(uuid, pipelineReq, lifecycle, console);
        } else if (PipelineType.isBatch(pipelineReq.getPipelineType())) {
            process = startSparkPipeline(uuid, pipelineReq, lifecycle, console);
        } else {
            throw new JaxException(ResponseCode.DEBUG_NOT_SUPPORT.message);
        }
        processes.put(uuid, process);
    }

    public void stopPipeline(final String uuid) {
        PipelineProcess process = processes.remove(uuid);
        if (process != null) {
            process.stop();
        }
    }

    public PipelineProcess startFlinkPipeline(final String uuid, final PipelineReq pipelineReq, final Lifecycle lifecycle, final Console console) {
        mockDebugJob(pipelineReq);
        registerDebugConfig(uuid, pipelineReq);
        Pipeline pipeline = genPipeline(pipelineReq);
        FlinkJobStartParam param = (FlinkJobStartParam) flinkPipelineManager.genStartParam(pipeline);
        PipelineProcess process = buildProcess(uuid, lifecycle, console, pipeline);
        List<String> arguments = genFlinkArguments(pipeline, param);
        process.exec(arguments);
        lifecycle.onStart(uuid);
        return process;
    }

    public PipelineProcess startSparkPipeline(final String uuid, final PipelineReq pipelineReq, final Lifecycle lifecycle, final Console console) {
        mockDebugJob(pipelineReq);
        registerDebugConfig(uuid, pipelineReq);
        Pipeline pipeline = genPipeline(pipelineReq);
        SparkJobStartParam param = (SparkJobStartParam) sparkPipelineManager.genStartParam(pipeline);
        PipelineProcess process = buildProcess(uuid, lifecycle, console, pipeline);
        List<String> arguments = genSparkArguments(pipeline, param);
        process.exec(arguments);
        lifecycle.onStart(uuid);
        return process;
    }

    private Pipeline genPipeline(final PipelineReq pipelineReq) {
        TbPipeline tbPipeline = pipelineReq.toEntity(new TbPipeline());
        List<TbPipelineJob> jobs = pipelineReq.getPipelineConfig().getJobs().stream()
                .map(i -> i.toEntity(new TbPipelineJob()).setPipelineName(tbPipeline.getPipelineName()))
                .collect(Collectors.toList());
        return pipelineService.genPipeline(tbPipeline, jobs);
    }

    private PipelineProcess buildProcess(final String uuid, final Lifecycle lifecycle, final Console console, Pipeline pipeline) {
        String bin = PipelineType.isBatch(pipeline.getPipelineType())
                ? ConfigLoader.load().jax.getDebug().getSparkBin() : ConfigLoader.load().jax.getDebug().getBin();
        String work = ConfigLoader.load().jax.getWork();
        PipelineProcess process = new PipelineProcess(uuid,
                work,
                bin,
                (String line) -> console.console(uuid, line),
                new ExecuteResultHandler() {
                    @Override
                    public void onProcessComplete(int exitValue) {
                        onFinish();
                    }

                    @Override
                    public void onProcessFailed(ExecuteException e) {
                        onFinish();
                    }

                    private void onFinish() {
                        processes.remove(uuid);
                        lifecycle.onFinish(uuid);
                    }
                }
        );
        String pythonBin = ClusterVariable.genPythonBin(ConfigLoader.load().jax.getDebug().getPythonEnv());
        if (StrUtil.isNotBlank(pipeline.getCluster().getHadoopHome())) {
            process.putEnv(AppConfig.HADOOP_CONF_DIR, pipeline.getCluster().getHadoopConfig());
        }

        process.putEnv(PYTHON_INTERPRETER_ENV, pythonBin);
        process.putEnv(PYSPARK_PYTHON, pythonBin);
        process.putEnv(PYSPARK_DRIVER_PYTHON, pythonBin);
        process.putEnv(PYTHON_PATH, pyArchive(pipeline));
        return process;
    }

    private String pyArchive(Pipeline pipeline) {
        List<String> files = pipeline.getJobJars().stream()
                .filter(jar ->
                        PythonHelper.isPython(jar.getJob().getJobName())
                )
                .filter(Common.distinctByKey(
                        (Function<JobJar, String>) o ->
                                o.getJar().getJarName())
                )
                .map(jar -> jarFileProvider.touchJarFile(jar.getJar(), jar.getCluster()))
                .collect(Collectors.toList());
        files.add(ConfigLoader.load().jax.getJaxPythonPath());
        return files.stream().collect(Collectors.joining(":"));
    }

    private List<String> genFlinkArguments(Pipeline pipeline, FlinkJobStartParam param) {
        String entry = ConfigLoader.load().jax.getDebug().getEntry();
        List<String> classpath = new ArrayList<>();
        classpath.addAll(Common.listJars(pipeline.getCluster().getFlinkOpts().getLib()));
        classpath.addAll(Common.listJars(pipeline.getCluster().getFlinkOpts().getJobLib()));
        classpath.addAll(distinctJars(pipeline.getJobJars()));
        classpath.add(pipeline.getCluster().getFlinkOpts().getEntryJar());

        List<String> arguments = new ArrayList<>();
        arguments.add("-classpath");
        arguments.add(StrUtil.join(":", classpath));
        arguments.add(entry);
        arguments.add("-job_file");
        arguments.add(param.getJobFile());
        String host = "0.0.0.0";
        try {
            host = ConfigLoader.load().server.getListenAddress();
        } catch (Exception e) {
            logger.warn("get ip", e);
        }
        arguments.add("-host");
        arguments.add(host);
        return arguments;
    }

    private List<String> genSparkArguments(Pipeline pipeline, SparkJobStartParam param) {
        String entry = ConfigLoader.load().jax.getDebug().getSparkEntry();
        List<String> classpath = new ArrayList<>();
        classpath.addAll(Common.listJars(pipeline.getCluster().getSparkOpts().getLib()));
        classpath.addAll(Common.listJars(pipeline.getCluster().getSparkOpts().getJobLib()));
        classpath.addAll(distinctJars(pipeline.getJobJars()));
        classpath.add(pipeline.getCluster().getSparkOpts().getEntryJar());

        List<String> arguments = new ArrayList<>();
        arguments.add("-classpath");
        arguments.add(StrUtil.join(":", classpath));
        arguments.add(entry);
        arguments.add("-job_file");
        arguments.add(param.getJobFile());
        return arguments;
    }

    private PipelineReq mockDebugJob(PipelineReq req) {
        String debugSourceJob = ConfigLoader.load().jax.getDebug().getDebugSource();
        for (PipelineEdge edge : req.getPipelineConfig().getEdges()) {
            if (Boolean.TRUE.equals(edge.getEnableMock())) {
                //todo slot 处理
                String mockId = edge.getMockId();
                edge.setFrom(mockId);
                PipelineJob mockJob = new PipelineJob();
                mockJob.setJobId(mockId);
                mockJob.setJobName(debugSourceJob);
                req.getPipelineConfig().getJobs().add(mockJob);
            }
        }
        return req;
    }
    
    private PipelineReq registerDebugConfig(String uuid, PipelineReq req) {
        String debugSourceJob = ConfigLoader.load().jax.getDebug().getDebugSource();
        String debugSinker = PipelineType.isBatch(req.getPipelineType())
                ? ConfigLoader.load().jax.getDebug().getSparkDebugSinker() : ConfigLoader.load().jax.getDebug().getDebugSinker();
        for (PipelineJob job : req.getPipelineConfig().getJobs()) {
            try {
                if (PipelineType.FLINK_SQL.isEqual(req.getPipelineType())) {
                    // sql job 无需添加debug sink
                    // sql job select语句运行结果会输出到运行日志中
                    // sql job 需再jobConfig里添加参数debug为true，使得client端会持续阻塞等待select语句运行的输出结果
                    job.getJobConfig().put("debug", true);
                } else {
                    // 注册debug sink
                    Map<String, Object> opts = job.getJobOpts();
                    JobDebugOpts debugJob = new JobDebugOpts().fromMap(opts);
                    if (debugJob != null
                            && Boolean.TRUE.equals(debugJob.getEnableDebug())) {
                        String uri = String.format("%s/ws/pipeline/pong/%s/%s/%s",
                                ConfigLoader.load().server.getListenWebsocket(),
                                uuid,
                                job.getJobId(),
                                PongSessionType.SINK.code
                        );
                        debugJob.setDebugEntry(debugSinker);
                        debugJob.setDebugConfig(Collections.singletonMap("uri", uri));
                        BeanUtil.copyProperties(debugJob, opts);
                    }
                    if (debugSourceJob.equalsIgnoreCase(job.getJobName())) {
                        String uri = String.format("%s/ws/pipeline/pong/%s/%s/%s",
                                ConfigLoader.load().server.getListenWebsocket(),
                                uuid,
                                job.getJobId(),
                                PongSessionType.SOURCE.code
                        );
                        job.setJobConfig(Collections.singletonMap("uri", uri));
                    }
                }
            } catch (Exception e) {
                throw new JaxException(e);
            }
        }
        return req;
    }

    private List<String> distinctJars(List<JobJar> list) {
        return list.stream()
                .filter(Common.distinctByKey(
                        (Function<JobJar, String>) o ->
                                o.getJar().getJarName())
                )
                .map(jar ->
                        jarFileProvider.touchJarFile(jar.getJar(), jar.getCluster())
                )
                .collect(Collectors.toList());
    }

    public interface Console {
        void console(String uuid, String msg);
    }

    public interface Lifecycle {
        default void onStart(String uuid) {
        }

        default void onFinish(String uuid) {
        }
    }
}
