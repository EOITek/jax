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
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.manager.JaxManager;
import com.eoi.jax.manager.api.JobGetParam;
import com.eoi.jax.manager.api.JobListParam;
import com.eoi.jax.manager.api.JobStartParam;
import com.eoi.jax.manager.api.JobStopParam;
import com.eoi.jax.manager.spark.SparkJobStartParam;
import com.eoi.jax.manager.spark.SparkJobStopParam;
import com.eoi.jax.web.common.consts.ClusterType;
import com.eoi.jax.web.common.exception.JaxException;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.model.manager.FileUrl;
import com.eoi.jax.web.model.manager.JobJar;
import com.eoi.jax.web.model.manager.Pipeline;
import com.eoi.jax.web.model.manager.PipelineJar;
import com.eoi.jax.web.model.manager.SparkPipelineDefine;
import com.eoi.jax.web.provider.SparkPipelineManager;
import com.eoi.jax.web.provider.cluster.Cluster;
import com.eoi.jax.web.provider.cluster.ClusterSparkOpts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SparkPipelineManagerImpl extends BasePipelineManager implements SparkPipelineManager {

    @Autowired
    private JaxManagerProvider jaxManagerProvider;

    @Autowired
    private JarUrlProvider jarUrlProvider;

    private List<FileUrl> genJobJarUrls(Cluster cluster, List<JobJar> jobJars, List<PipelineJar> extJars) {
        List<FileUrl> urls = new ArrayList<>();
        urls.addAll(jarUrlProvider.getJobLibUrls(cluster.getSparkOpts().getJobLib()));
        urls.addAll(jarUrlProvider.getJobJarUrls(jobJars, extJars));
        return urls;
    }

    private List<FileUrl> genJobPyFileUrls(Cluster cluster, List<JobJar> jobJars) {
        List<FileUrl> urls = new ArrayList<>();
        urls.addAll(jarUrlProvider.genPythonLibUrl(cluster));
        urls.addAll(jarUrlProvider.getJobPyFileUrls(jobJars));
        return urls;
    }

    private List<String> genConfList(SparkPipelineDefine define, List<FileUrl> pyFileUrls) {
        List<String> confList = define.getConfList();
        Set<String> pyFileNames = pyFileUrls.stream().map(FileUrl::getName).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(pyFileNames)) {
            confList = confList.stream()
                    .map(conf -> {
                        if (StrUtil.contains(conf, '=') && conf.startsWith("spark.yarn.dist.archives")) {
                            String key = conf.split("=")[0];
                            String value = conf.split("=")[1];
                            List<String> files = Arrays.stream(value.split(","))
                                    .filter(file -> !pyFileNames.contains(FileUtil.getName(file)))
                                    .collect(Collectors.toList());
                            return key + "=" + String.join(",", files);
                        }
                        if (StrUtil.contains(conf, '=')
                                && (conf.startsWith("spark.yarn.appMasterEnv.PYTHONPATH")
                                || conf.startsWith("spark.executorEnv.PYTHONPATH"))) {
                            String key = conf.split("=")[0];
                            String value = conf.split("=")[1];
                            List<String> values = Arrays.stream(value.split(":"))
                                    .filter(v -> !pyFileNames.contains(v))
                                    .collect(Collectors.toList());
                            return key + "=" + String.join(":", values);
                        }
                        return conf;
                    })
                    .filter(conf -> !StrUtil.endWith(conf, "="))
                    .collect(Collectors.toList());
        }
        List<String> javaOpts = new ArrayList<>(define.getJavaOptions());
        javaOpts.add("-Dlog4j.pipelineId=" + define.getPipelineName());
        // extraJavaOptions的值不能使用引号（双引号，单引号都不可以）
        // 因为使用引号时，引号内的空格不会被分割，会被判定为一个整体
        // 如："-Dlog4j.pipelineId=xxx -Darg=yyy"，被判定为参数log4j.pipelineId的值为"xxx -Darg=yyy"
        confList.add(String.format("spark.driver.extraJavaOptions=%s",
                StrUtil.join(" ", javaOpts)));
        confList.add(String.format("spark.executor.extraJavaOptions=%s",
                StrUtil.join(" ", javaOpts)));
        return confList;
    }

    @Override
    public SparkPipelineDefine genDefine(Pipeline pipeline) {
        SparkPipelineDefine define = new SparkPipelineDefine();
        try {
            SparkPipelineDefine custom =
                    JsonUtil.decode(
                            JsonUtil.encode(pipeline.getPipelineConfig().getOpts()), SparkPipelineDefine.class);
            ClusterSparkOpts opts = pipeline.getCluster().getSparkOpts();
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
        define.setStateListenerUrl(String.format(
                "%s/api/v1/listener/spark/%s/state", jarUrlProvider.getListeningHttp(), pipeline.getPipelineName()));
        define.setEventListenerUrl(String.format(
                "%s/api/v1/listener/spark/%s/event", jarUrlProvider.getListeningHttp(), pipeline.getPipelineName()));
        List<FileUrl> jarUrls = genJobJarUrls(pipeline.getCluster(), pipeline.getJobJars(), pipeline.getExtJars());
        List<FileUrl> pyFileUrls = genJobPyFileUrls(pipeline.getCluster(), pipeline.getJobJars());
        List<String> confList = genConfList(define, pyFileUrls);
        define.setConfList(confList);
        define.setJars(jarUrls
                .stream().map(FileUrl::getUrl).distinct().collect(Collectors.toList()));
        define.setPyFiles(pyFileUrls
                .stream().map(FileUrl::getUrl).distinct().collect(Collectors.toList()));
        setDefineJobEde(define, pipeline);
        return define;
    }

    public JobStartParam genStartParam(Pipeline pipeline) {
        Cluster cluster = pipeline.getCluster();
        SparkPipelineDefine define = genDefine(pipeline);
        FileUrl jobFileUrl = genDefineFile(define);
        FileUrl appJarUrl = jarUrlProvider.genJarLibUrl(cluster.getSparkOpts().getEntryJar());
        SparkJobStartParam param = new SparkJobStartParam();
        BeanUtil.copyProperties(define, param, CopyOptions.create().ignoreNullValue());
        param.setFiles(Collections.singletonList(jobFileUrl.getUrl()));
        param.setVersion(cluster.getSparkOpts().getVersion());
        param.setMasterUrl(cluster.getSparkServer());
        param.setEntryClass(cluster.getSparkOpts().getEntryClass());
        param.setApplicationName(pipeline.getPipelineName());
        param.setParamList(new ArrayList<>(define.getOtherStartArgs()));
        param.setApplicationJar(appJarUrl.getUrl());
        param.setJobFile(jobFileUrl.getName());
        param.setMode(ClusterType.toClusterMode(cluster.getClusterType()).code);
        param.setEnableHive(define.getEnableHive());
        param.setOptimizePipeline(define.getOptimizePipeline());
        return param;
    }

    @Override
    public JobStopParam genStopParam(Pipeline pipeline) {
        Cluster cluster = pipeline.getCluster();
        SparkJobStopParam param = new SparkJobStopParam();
        param.setVersion(cluster.getSparkOpts().getVersion());
        param.setMasterUrl(cluster.getSparkServer());
        param.setRestUrl(pipeline.getSparkRestUrl());
        param.setSubmissionId(pipeline.getSparkSubmissionId());
        param.setApplicationId(pipeline.getSparkAppId());
        param.setYarnId(pipeline.getYarnAppId());
        return param;
    }

    @Override
    public JobGetParam genGetParam(Pipeline pipeline) {
        return null;
    }

    @Override
    public JobListParam genListParam(List<Pipeline> pipelines) {
        return null;
    }

    @Override
    public JobStopParam genDeleteParam(Pipeline pipeline) {
        return genStopParam(pipeline);
    }

    @Override
    public JaxManager genJaxManager(Pipeline pipeline) {
        return jaxManagerProvider.sparkJaxManager(pipeline.getCluster());
    }
}
