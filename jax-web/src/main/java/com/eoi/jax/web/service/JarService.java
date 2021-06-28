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
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eoi.jax.common.VersionUtil;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.config.ConfigLoader;
import com.eoi.jax.web.common.consts.ClusterType;
import com.eoi.jax.web.common.consts.JarType;
import com.eoi.jax.web.common.exception.BizException;
import com.eoi.jax.web.common.exception.JaxException;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.common.util.ZipUtil;
import com.eoi.jax.web.dao.entity.TbCluster;
import com.eoi.jax.web.dao.entity.TbJar;
import com.eoi.jax.web.dao.entity.TbJob;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.dao.entity.TbPipelineJob;
import com.eoi.jax.web.dao.service.TbClusterService;
import com.eoi.jax.web.dao.service.TbJarService;
import com.eoi.jax.web.dao.service.TbJobService;
import com.eoi.jax.web.dao.service.TbPipelineJobService;
import com.eoi.jax.web.dao.service.TbPipelineService;
import com.eoi.jax.web.model.Paged;
import com.eoi.jax.web.model.jar.JarQueryReq;
import com.eoi.jax.web.model.jar.JarReq;
import com.eoi.jax.web.model.jar.JarResp;
import com.eoi.jax.web.model.manager.PipelineJar;
import com.eoi.jax.web.model.pipeline.PipelineConfig;
import com.eoi.jax.web.model.pipeline.PipelineConfigOpts;
import com.eoi.jax.web.provider.JarFileProvider;
import com.eoi.jax.web.provider.JaxScanner;
import com.eoi.jax.web.provider.cluster.Cluster;
import com.eoi.jax.web.provider.scanner.JobMeta;
import com.eoi.jax.web.provider.scanner.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

@Service
public class JarService {
    private static final Logger logger = LoggerFactory.getLogger(JarService.class);
    private static final String JAX_ARTIFACTS = "_jax_artifacts";
    private static final String JAX_ARTIFACTS_MANIFEST = JAX_ARTIFACTS + "/manifest";

    @Autowired
    private TbJarService tbJarService;

    @Autowired
    private TbJobService tbJobService;

    @Autowired
    private TbPipelineJobService tbPipelineJobService;

    @Autowired
    private TbPipelineService tbPipelineService;

    @Autowired
    private TbClusterService tbClusterService;

    @Autowired
    private JarFileProvider jarFileProvider;

    @Autowired
    private JaxScanner jaxScanner;

    public List<PipelineJar> jarsFromNames(List<String> jarNames) {
        if (CollUtil.isEmpty(jarNames)) {
            return new ArrayList<>();
        }
        final List<TbJar> jarList = tbJarService.list(
                new LambdaQueryWrapper<TbJar>().in(TbJar::getJarName, jarNames)
        );
        final List<TbCluster> clusterList = tbClusterService.list();
        return jarList.stream().map(jar -> {
            TbCluster cluster = clusterList.stream()
                    .filter(p -> p.getClusterName().equals(jar.getClusterName()))
                    .findFirst()
                    .orElse(null);
            return new PipelineJar(jar, cluster);
        }).collect(Collectors.toList());
    }

    public List<String> listBuildIns() {
        String dir = ConfigLoader.load().jax.getBuildin().getJobJarDir();
        return Common.listJars(dir);
    }

    public List<JarResp> listJar() {
        List<TbJar> jars = tbJarService.list();
        List<TbJob> jobs = tbJobService.listWithoutIconDoc();
        return jars.stream().map(i ->
                new JarResp().respFrom(i,
                        jobs.stream().filter(f ->
                                f.getJarName().equals(i.getJarName())).collect(Collectors.toList()
                        )
                )
        ).collect(Collectors.toList());
    }

    public Paged<JarResp> queryJar(JarQueryReq req) {
        List<TbJob> jobs = tbJobService.listWithoutIconDoc();
        IPage<TbJar> paged = tbJarService.page(req.page(), req.query());
        long total = paged.getTotal();
        List<TbJar> jars = paged.getRecords();
        return new Paged<>(total, jars.stream().map(i ->
                new JarResp().respFrom(i,
                        jobs.stream().filter(f ->
                                f.getJarName().equals(i.getJarName())).collect(Collectors.toList()
                        )
                )
        ).collect(Collectors.toList()));
    }

    public JarResp getJar(String jarName) {
        TbJar entity = byId(jarName);
        List<TbJob> jobs = tbJobService.list(
                new LambdaQueryWrapper<TbJob>()
                        .eq(TbJob::getJarName, jarName)
        );
        return new JarResp().respFrom(entity, jobs);
    }


    @Transactional(rollbackFor = Exception.class)
    public JarResp createJar(MultipartFile file, JarReq req) {
        if (!Common.verifyName(req.getJarName())) {
            throw new BizException(ResponseCode.JAR_INVALID_NAME);
        }
        jarExits(req.getJarName());
        String fileExt = Common.getFileExtension(file.getOriginalFilename());
        String filePath = saveTmpFile(file, fileExt);
        return createJar(filePath, fileExt, req);
    }

    @Transactional(rollbackFor = Exception.class)
    public JarResp createJar(String buildInFileName, JarReq req) {
        if (!Common.verifyName(req.getJarName())) {
            throw new BizException(ResponseCode.JAR_INVALID_NAME);
        }
        jarExits(req.getJarName());
        String fileExt = Common.getFileExtension(buildInFileName);
        String buildInPath = Common.pathsJoin(ConfigLoader.load().jax.getBuildin().getJobJarDir(), buildInFileName);
        String filePath = Common.pathsJoin(ConfigLoader.load().jax.getJarTmp(), System.currentTimeMillis() + fileExt + ".tmp");
        FileUtil.copy(buildInPath, filePath, true);
        return createJar(filePath, fileExt, req);
    }

    /**
     * 上传Jar
     * 0. 对 jarName 重复检查
     * 1. 将jar保存至临时目录
     * 2. 分析检查jar包内容
     * 3. 将jar保存到工作目录下
     * 4. 更新数据库
     * 如果jarType为FLINK_SQL，表示sql扩展包，无需解析job信息
     */
    private JarResp createJar(String filePath, String fileExt, JarReq req) {
        long now = System.currentTimeMillis();
        List<TbJob> jobs = null;
        String supportVersion = null;
        boolean isSql = JarType.FLINK_SQL.isEqual(req.getJarType());
        if (!isSql) {
            ScanResult result = scan(filePath, req.getJarType());
            jobs = readJobs(req.getJarName(), result);
            if (CollUtil.isEmpty(jobs)) {
                throw new BizException(ResponseCode.JOB_EMPTY);
            }
            // 根据是否覆盖已有job的标志位，决定是否覆盖原来的job
            if (!req.isOverride()) {
                jobExistsThrow(jobs);
            } else {
                //覆盖模式下，先将所有job移除
                jobsDelete(jobs);
            }
            try {
                supportVersion = VersionUtil.loadSupportVersion(filePath, this.getClass().getClassLoader());
            } catch (IOException e) {
                throw new JaxException("scan error", e);
            }
        }
        Cluster cluster = getCluster(req.getClusterName());
        String jarPath = saveJarFile(filePath, req.getJarName(), cluster, fileExt);
        TbJar entity = req.toEntity(new TbJar())
                .setJarPath(jarPath)
                .setCreateTime(now)
                .setUpdateTime(now)
                .setSupportVersion(supportVersion);
        insert(entity, jobs);

        return getJar(entity.getJarName());
    }

    @Transactional(rollbackFor = Exception.class)
    public JarResp updateJar(MultipartFile file, JarReq req) {
        String fileExt = Common.getFileExtension(file.getOriginalFilename());
        String filePath = saveTmpFile(file, fileExt);
        return updateJar(filePath, fileExt, req);
    }

    @Transactional(rollbackFor = Exception.class)
    public JarResp updateJar(String buildInFileName, JarReq req) {
        String fileExt = Common.getFileExtension(buildInFileName);
        String buildInPath = Common.pathsJoin(ConfigLoader.load().jax.getBuildin().getJobJarDir(), buildInFileName);
        String filePath = Common.pathsJoin(ConfigLoader.load().jax.getJarTmp(), System.currentTimeMillis() + fileExt + ".tmp");
        FileUtil.copy(buildInPath, filePath, true);
        return updateJar(filePath, fileExt, req);
    }

    /**
     * 更新Jar
     * 0. 对 jarName 存在检查
     * 1. 将jar保存至临时目录
     * 2. 分析检查jar包内容
     * 3. 将工作目录下原jar删除
     * 4. 将jar保存到工作目录下
     * 5. 更新数据库
     * 如果jarType为FLINK_SQL，表示sql扩展包，无需解析job信息
     */
    private JarResp updateJar(String filePath, String fileSuffix, JarReq req) {
        long now = System.currentTimeMillis();
        TbJar entity = byId(req.getJarName());
        String supportVersion = "[[\"*\"]]";
        List<TbJob> jobs = null;
        boolean isSql = JarType.FLINK_SQL.isEqual(req.getJarType());
        if (!isSql) {
            ScanResult result = scan(filePath, req.getJarType());
            jobs = readJobs(req.getJarName(), result);
            if (CollUtil.isEmpty(jobs)) {
                throw new BizException(ResponseCode.JOB_EMPTY);
            }
            List<TbJob> origins = tbJobService.list(new LambdaQueryWrapper<TbJob>()
                    .eq(TbJob::getJarName, req.getJarName())
            );
            List<TbJob> finalJobs = jobs;
            List<TbJob> deletes = origins.stream()
                    .filter(a -> finalJobs.stream().noneMatch(b -> b.getJobName().equals(a.getJobName())))
                    .collect(Collectors.toList());
            for (TbJob job : deletes) {
                jobUsed(job);
            }
            List<TbJob> news = jobs.stream()
                    .filter(a -> origins.stream().noneMatch(b -> b.getJobName().equals(a.getJobName())))
                    .collect(Collectors.toList());

            if (!req.isOverride()) {
                jobExistsThrow(news);
            } else {
                //覆盖模式下，先将所有job移除
                jobsDelete(jobs);
            }
        }
        Cluster newCluster = getCluster(req.getClusterName());
        Cluster oldCluster = getCluster(entity.getClusterName());
        deleteJarFile(entity.getJarPath(), oldCluster);
        String jarPath = saveJarFile(filePath, req.getJarName(), newCluster,fileSuffix);
        if (!isSql) {
            try {
                supportVersion = VersionUtil.loadSupportVersion(filePath, this.getClass().getClassLoader());
            } catch (IOException e) {
                throw new JaxException("scan error", e);
            }
        }
        entity = req.toEntity(entity)
                .setJarPath(jarPath)
                .setUpdateTime(now)
                .setSupportVersion(supportVersion);
        update(entity, jobs);
        return getJar(entity.getJarName());
    }

    /**
     * 删除Jar
     * 0. 对 jarName 存在检查
     * 1. 对jar下的job的 jobName 引用检查
     * 2. 将工作目录下原jar删除
     * 3. 更新数据库
     */
    @Transactional(rollbackFor = Exception.class)
    public JarResp deleteJar(String jarName) {
        JarResp resp = getJar(jarName);
        boolean isSql = JarType.FLINK_SQL.isEqual(resp.getJarType());
        if (!isSql) {
            List<TbJob> jobs = tbJobService.list(new LambdaQueryWrapper<TbJob>().eq(TbJob::getJarName, jarName));
            for (TbJob job : jobs) {
                jobUsed(job);
            }
        } else {
            List<TbPipeline> pipelines = tbPipelineService.list(new LambdaQueryWrapper<TbPipeline>()
                    .eq(TbPipeline::getPipelineType, JarType.FLINK_SQL.code)
                    .like(TbPipeline::getPipelineConfig, jarName)
            );
            pipelines = pipelines.stream().filter(x -> {
                PipelineConfig config = JsonUtil.decode(x.getPipelineConfig(), PipelineConfig.class);
                PipelineConfigOpts opts = config.genOptsBean();
                if (CollUtil.contains(opts.getExtJars(), jarName)) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(pipelines)) {
                throw new BizException(
                        ResponseCode.JAR_USED.code,
                        ResponseCode.JAR_USED.message + ": " + pipelines.stream()
                                .map(TbPipeline::getPipelineName)
                                .collect(Collectors.joining(","))
                );
            }
        }

        Cluster cluster = getCluster(resp.getClusterName());
        deleteJarFile(resp.getJarPath(), cluster);
        delete(jarName);
        return resp;
    }

    public void archive(String jarNames, OutputStream out) {
        if (StrUtil.isEmpty(jarNames)) {
            return;
        }
        String[] list = StrUtil.split(jarNames, ",");
        List<TbJar> jars = tbJarService.list(
                new LambdaQueryWrapper<TbJar>().in(TbJar::getJarName, new ArrayList<>(Arrays.asList(list)))
        );
        if (CollUtil.isEmpty(jars)) {
            return;
        }
        String manifest = jars.stream()
                .map(i -> i.getJarName() + Common.getFileExtension(i.getJarPath()))
                .collect(Collectors.joining("\n"));
        manifest = ConfigLoader.load().jax.getJaxPythonName() + "\n" + manifest;
        try (ZipOutputStream zip = new ZipOutputStream(out)) {
            ZipUtil.addDir(zip, JAX_ARTIFACTS);
            ZipUtil.addFile(zip, JAX_ARTIFACTS_MANIFEST, manifest.getBytes(StandardCharsets.UTF_8));
            ZipUtil.addFile(zip, JAX_ARTIFACTS + "/" + ConfigLoader.load().jax.getJaxPythonName(), ConfigLoader.load().jax.getJaxPythonPath());
            for (TbJar jar : jars) {
                TbCluster cluster = null;
                if (StrUtil.isNotEmpty(jar.getClusterName())) {
                    cluster = tbClusterService.getById(jar.getClusterName());
                }
                String path = jarFileProvider.touchJarFile(jar, cluster);
                InputStream in = FileUtil.getInputStream(path);
                String name = JAX_ARTIFACTS + "/" + jar.getJarName() + Common.getFileExtension(jar.getJarPath());
                ZipUtil.addFile(zip, name, in);
            }
            out.flush();
        } catch (IOException e) {
            logger.warn("archive", e);
        }
    }

    private TbJar byId(String jarName) {
        TbJar entity = tbJarService.getById(jarName);
        if (entity == null) {
            throw new BizException(ResponseCode.JAR_NOT_EXIST);
        }
        return entity;
    }

    private void jarExits(String jarName) {
        int count = tbJarService.count(new LambdaQueryWrapper<TbJar>().eq(TbJar::getJarName, jarName));
        if (count > 0) {
            throw new BizException(ResponseCode.JAR_EXIST);
        }
    }

    private void jobUsed(TbJob job) {
        int count = tbPipelineJobService.count(
                new LambdaQueryWrapper<TbPipelineJob>().eq(TbPipelineJob::getJobName, job.getJobName())
        );
        if (count > 0) {
            throw new BizException(
                    ResponseCode.JOB_USED.code,
                    ResponseCode.JOB_USED.message + ": " + job.getJobName()
            );
        }
    }

    private void jobExistsThrow(List<TbJob> jobs) {
        for (TbJob job : jobs) {
            int count = tbJobService.count(
                    new LambdaQueryWrapper<TbJob>().eq(TbJob::getJobName, job.getJobName())
            );
            if (count > 0) {
                throw new BizException(ResponseCode.JOB_EXIST);
            }
        }
    }

    private void jobsDelete(List<TbJob> jobs) {
        for (TbJob job : jobs) {
            tbJobService.removeById(job.getJobName());
        }
    }

    private Cluster getCluster(String clusterName) {
        if (StrUtil.isEmpty(clusterName)) {
            return null;
        }
        // 检查集群
        TbCluster cluster = tbClusterService.getById(clusterName);
        if (cluster == null) {
            throw new BizException(ResponseCode.CLUSTER_NOT_EXIST);
        }
        if (!ClusterType.YARN.isEqual(cluster.getClusterType())) {
            throw new BizException(ResponseCode.INVALID_UPLOAD_CLUSTER);
        }
        return new Cluster(cluster, null, null);
    }

    private void insert(TbJar entity, List<TbJob> jobs) {
        tbJarService.save(entity);
        if (CollUtil.isNotEmpty(jobs)) {
            tbJobService.saveBatch(jobs);
        }
    }

    private void update(TbJar entity, List<TbJob> jobs) {
        tbJarService.updateById(entity);
        tbJobService.remove(new LambdaQueryWrapper<TbJob>().eq(TbJob::getJarName, entity.getJarName()));
        if (CollUtil.isNotEmpty(jobs)) {
            tbJobService.saveBatch(jobs);
        }
    }

    private void delete(String jarName) {
        tbJarService.removeById(jarName);
        tbJobService.remove(new LambdaQueryWrapper<TbJob>().eq(TbJob::getJarName, jarName));
    }

    private String saveTmpFile(MultipartFile file, String fileExt) {
        try {
            return jarFileProvider.saveTempFile(file,
                    Common.pathsJoin(ConfigLoader.load().jax.getJarTmp(), System.currentTimeMillis() + fileExt + ".tmp")
            );
        } catch (Exception e) {
            logger.error("error", e);
            throw new BizException(ResponseCode.UPLOAD_ERROR);
        }
    }

    private String saveJarFile(String tmpPath, String jarName, Cluster cluster, String fileExt) {
        try {
            String savePath = cluster != null
                    ? ConfigLoader.load().jax.getJarHdfsDir() : ConfigLoader.load().jax.getJarDir();
            return jarFileProvider.moveJavaFile(tmpPath,
                    Common.pathsJoin(
                            savePath,
                            Common.genJarName(jarName, fileExt)
                    ),
                    cluster
            );
        } catch (Exception e) {
            logger.error("error", e);
            throw new BizException(ResponseCode.UPLOAD_ERROR);
        }
    }

    private Boolean deleteJarFile(String jarPath, Cluster cluster) {
        try {
            return jarFileProvider.deleteJarFile(jarPath, cluster);
        } catch (Exception e) {
            logger.error("error", e);
            throw new BizException(ResponseCode.UPLOAD_ERROR);
        }
    }

    private ScanResult scan(String path, String jobType) {
        try {
            if (JarType.PYTHON.isEqual(jobType)) {
                return jaxScanner.scanPython(path);
            }
            return jaxScanner.scanJar(path);
        } catch (Exception e) {
            logger.error("error", e);
            throw new BizException(ResponseCode.JOB_ERROR, e);
        }
    }

    private List<TbJob> readJobs(String jarName, ScanResult result) {
        List<TbJob> list = new ArrayList<>();
        if (CollUtil.isEmpty(result.getJobs())) {
            return list;
        }
        for (JobMeta meta : result.getJobs().values()) {
            String jobRole = null;
            Boolean internal = null;
            if (meta.getJobInfo() != null) {
                jobRole = meta.getJobInfo().getRole();
                internal = meta.getJobInfo().getInternal();
            }
            String jobMeta = JsonUtil.encode(meta);
            TbJob job = new TbJob()
                    .setJarName(jarName)
                    .setJobType(meta.getJobInfo().getType())
                    .setJobName(meta.getJobName())
                    .setJobRole(jobRole)
                    .setInternal(internal)
                    .setIcon(meta.getIcon())
                    .setDoc(meta.getDoc())
                    .setJobMeta(jobMeta);
            list.add(job);
        }
        return list;
    }
}
