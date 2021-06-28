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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.manager.JaxManager;
import com.eoi.jax.manager.api.JobGetParam;
import com.eoi.jax.manager.api.JobListParam;
import com.eoi.jax.manager.api.JobStartParam;
import com.eoi.jax.manager.api.JobStopParam;
import com.eoi.jax.manager.flink.FlinkCmdStartParam;
import com.eoi.jax.manager.flink.FlinkJobGetParam;
import com.eoi.jax.manager.flink.FlinkJobListParam;
import com.eoi.jax.manager.flink.FlinkJobStartParam;
import com.eoi.jax.manager.flink.FlinkJobStopParam;
import com.eoi.jax.web.common.exception.JaxException;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.model.manager.FileUrl;
import com.eoi.jax.web.model.manager.FlinkPipelineDefine;
import com.eoi.jax.web.model.manager.JobJar;
import com.eoi.jax.web.model.manager.Pipeline;
import com.eoi.jax.web.model.manager.PipelineJar;
import com.eoi.jax.web.provider.FlinkPipelineManager;
import com.eoi.jax.web.provider.cluster.Cluster;
import com.eoi.jax.web.provider.cluster.ClusterFlinkOpts;
import com.eoi.jax.web.provider.cluster.ClusterVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlinkPipelineManagerImpl extends BasePipelineManager implements FlinkPipelineManager {

    @Autowired
    private JaxManagerProvider jaxManagerProvider;

    @Autowired
    private JarUrlProvider jarUrlProvider;

    private List<String> genLoadUrls(Cluster cluster, List<JobJar> jarList, List<PipelineJar> extJars) {
        List<String> urls = new ArrayList<>();
        urls.addAll(jarUrlProvider.getJobLibUrls(cluster.getFlinkOpts().getJobLib()).stream().map(i -> i.getUrl() + "?" + IdUtil.simpleUUID()).collect(Collectors.toList()));
        urls.addAll(jarUrlProvider.getJobJarUrls(jarList, extJars).stream().map(i -> i.getUrl() + "?" + IdUtil.simpleUUID()).collect(Collectors.toList()));
        urls.add(jarUrlProvider.getJobPyArchiveUrl(jarList));
        return urls;
    }

    private List<String> genYarnDefinitions(FlinkPipelineDefine define, Cluster cluster) {
        List<String> definitionList = new ArrayList<>(define.getConfList());
        if (StrUtil.isNotEmpty(cluster.getPythonEnv())) {
            String pythonBin = ClusterVariable.genPythonBin(cluster.getPythonEnv());
            definitionList.add(String.format("containerized.taskmanager.env.PYTHON_INTERPRETER=%s", pythonBin));
            definitionList.add(String.format("containerized.master.env.PYTHON_INTERPRETER=%s", pythonBin));
        }
        List<String> javaOpts = new ArrayList<>(define.getJavaOptions());
        javaOpts.add("-DpipelineId=" + define.getPipelineName());
        definitionList.add(String.format("env.java.opts=\"%s\"",
                StrUtil.join("\\ ", javaOpts)));//空格需要转义
        return definitionList;
    }

    @Override
    public FlinkPipelineDefine genDefine(Pipeline pipeline) {
        FlinkPipelineDefine define = new FlinkPipelineDefine();
        try {
            FlinkPipelineDefine custom = pipeline.isCustomCmd() ? new FlinkPipelineDefine() : JsonUtil.decode(
                    JsonUtil.encode(pipeline.getPipelineConfig().getOpts()),
                    FlinkPipelineDefine.class
            );
            ClusterFlinkOpts opts = pipeline.getCluster().getFlinkOpts();
            // !!!注意这个不是深拷贝，对于对象实例（特别是List）都是引用拷贝，需要特殊处理!!!
            BeanUtil.copyProperties(opts, define, CopyOptions.create().ignoreNullValue());
            BeanUtil.copyProperties(custom, define, CopyOptions.create().ignoreNullValue());
            //注意这里new ArrayList只是为了更改引用
            define.setConfList(define.getConfList() == null ? new ArrayList<>() : new ArrayList<>(define.getConfList()));
            define.setJavaOptions(define.getJavaOptions() == null ? new ArrayList<>() : new ArrayList<>(define.getJavaOptions()));
            define.setOtherStartArgs(define.getOtherStartArgs() == null ? new ArrayList<>() : new ArrayList<>(define.getOtherStartArgs()));
        } catch (Exception e) {
            throw new JaxException(e);
        }
        define.setPipelineName(pipeline.getPipelineName());
        define.setSavepointURI(pipeline.getFlinkSavePoint());
        define.setSqlJob(pipeline.getSqlJob() == null ? false : pipeline.getSqlJob());
        define.setStartCmd(pipeline.getStartCmd());
        define.setYarnDefinitions(genYarnDefinitions(define, pipeline.getCluster()));
        setDefineJobEde(define, pipeline);
        return define;
    }

    @Override
    public JobStartParam genStartParam(Pipeline pipeline) {
        Cluster cluster = pipeline.getCluster();
        if (StrUtil.isNotEmpty(pipeline.getStartCmd())) {
            FlinkCmdStartParam param = new FlinkCmdStartParam();
            param.setVersion(cluster.getFlinkOpts().getVersion());
            param.setJobManager(cluster.getFlinkServer());
            param.setStartCmd(pipeline.getStartCmd());
            return param;
        } else {
            FlinkPipelineDefine define = genDefine(pipeline);
            FileUrl jobFileUrl = genDefineFile(define);
            List<String> jarUrls = genLoadUrls(pipeline.getCluster(), pipeline.getJobJars(), pipeline.getExtJars());
            FlinkJobStartParam param = new FlinkJobStartParam();
            BeanUtil.copyProperties(define, param, CopyOptions.create().ignoreNullValue());
            param.setVersion(cluster.getFlinkOpts().getVersion());
            param.setJobManager(cluster.getFlinkServer());
            param.setEntryClass(cluster.getFlinkOpts().getEntryClass());
            param.setYarnName(pipeline.getPipelineName());
            param.setSavePoint(pipeline.getFlinkSavePoint());
            param.setParamList(new ArrayList<>(define.getOtherStartArgs()));
            param.setJarPath(cluster.getFlinkOpts().getEntryJar());
            param.setClasspath(jarUrls);
            param.setJobFile(jobFileUrl.getPath());
            return param;
        }
    }

    @Override
    public JobStopParam genStopParam(Pipeline pipeline) {
        Cluster cluster = pipeline.getCluster();
        FlinkJobStopParam param = new FlinkJobStopParam();
        param.setVersion(cluster.getFlinkOpts().getVersion());
        param.setJobManager(cluster.getFlinkServer());
        param.setSavePoint(cluster.getFlinkOpts().getSavepointURI());
        param.setJobId(pipeline.getFlinkJobId());
        param.setYarnId(pipeline.getYarnAppId());
        // https://ci.apache.org/projects/flink/flink-docs-release-1.12/deployment/cli.html
        param.setDrain(false);
        return param;
    }

    @Override
    public JobGetParam genGetParam(Pipeline pipeline) {
        Cluster cluster = pipeline.getCluster();
        FlinkJobGetParam param = new FlinkJobGetParam();
        param.setVersion(cluster.getFlinkOpts().getVersion());
        param.setJobManager(cluster.getFlinkServer());
        param.setJobId(pipeline.getFlinkJobId());
        param.setYarnId(pipeline.getYarnAppId());
        return param;
    }

    @Override
    public JobListParam genListParam(List<Pipeline> pipelines) {
        Pipeline pipeline = pipelines.get(0);
        Cluster cluster = pipeline.getCluster();
        FlinkJobListParam param = new FlinkJobListParam();
        param.setVersion(cluster.getFlinkOpts().getVersion());
        param.setJobManager(cluster.getFlinkServer());
        param.setYarnId(pipeline.getYarnAppId());
        return param;
    }

    @Override
    public JobStopParam genDeleteParam(Pipeline pipeline) {
        Cluster cluster = pipeline.getCluster();
        FlinkJobStopParam param = new FlinkJobStopParam();
        param.setVersion(cluster.getFlinkOpts().getVersion());
        param.setJobManager(cluster.getFlinkServer());
        param.setJobId(pipeline.getFlinkJobId());
        param.setYarnId(pipeline.getYarnAppId());
        return param;
    }

    @Override
    public JaxManager genJaxManager(Pipeline pipeline) {
        if (pipeline.isCustomCmd()) {
            return jaxManagerProvider.flinkCmdJaxManager(pipeline.getStartCmd(), pipeline.getCluster());
        } else {
            return jaxManagerProvider.flinkJaxManager(pipeline.getCluster());
        }
    }
}
