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
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.Tuple;
import com.eoi.jax.web.common.config.ConfigLoader;
import com.eoi.jax.web.common.consts.ClusterType;
import com.eoi.jax.web.common.consts.PipelineStatus;
import com.eoi.jax.web.common.consts.PipelineType;
import com.eoi.jax.web.common.exception.BizException;
import com.eoi.jax.web.common.exception.JaxException;
import com.eoi.jax.web.common.util.Common;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.common.util.ZipUtil;
import com.eoi.jax.web.dao.entity.TbCluster;
import com.eoi.jax.web.dao.entity.TbOptsFlink;
import com.eoi.jax.web.dao.entity.TbOptsSpark;
import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.dao.entity.TbPipelineConsole;
import com.eoi.jax.web.dao.entity.TbPipelineJob;
import com.eoi.jax.web.dao.entity.TbPipelineLog;
import com.eoi.jax.web.dao.entity.TbSparkEvent;
import com.eoi.jax.web.dao.service.TbClusterService;
import com.eoi.jax.web.dao.service.TbOptsFlinkService;
import com.eoi.jax.web.dao.service.TbOptsSparkService;
import com.eoi.jax.web.dao.service.TbPipelineConsoleService;
import com.eoi.jax.web.dao.service.TbPipelineJobService;
import com.eoi.jax.web.dao.service.TbPipelineLogService;
import com.eoi.jax.web.dao.service.TbPipelineService;
import com.eoi.jax.web.dao.service.TbSparkEventService;
import com.eoi.jax.web.model.Paged;
import com.eoi.jax.web.model.manager.JobJar;
import com.eoi.jax.web.model.manager.Pipeline;
import com.eoi.jax.web.model.manager.PipelineJar;
import com.eoi.jax.web.model.pipeline.PipelineBuildIn;
import com.eoi.jax.web.model.pipeline.PipelineConfigOpts;
import com.eoi.jax.web.model.pipeline.PipelineExportReq;
import com.eoi.jax.web.model.pipeline.PipelineQueryReq;
import com.eoi.jax.web.model.pipeline.PipelineReq;
import com.eoi.jax.web.model.pipeline.PipelineResp;
import com.eoi.jax.web.provider.JaxValidator;
import com.eoi.jax.web.provider.cluster.Cluster;
import com.eoi.jax.web.provider.cluster.ClusterProvider;
import com.eoi.jax.web.provider.validator.ValidateResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class PipelineService {
    private static final Logger logger = LoggerFactory.getLogger(PipelineService.class);

    @Autowired
    private TbPipelineService tbPipelineService;

    @Autowired
    private TbPipelineJobService tbPipelineJobService;

    @Autowired
    private TbPipelineLogService tbPipelineLogService;

    @Autowired
    private TbPipelineConsoleService tbPipelineConsoleService;

    @Autowired
    private TbSparkEventService tbSparkEventService;

    @Autowired
    private TbOptsFlinkService tbOptsFlinkService;

    @Autowired
    private TbOptsSparkService tbOptsSparkService;

    @Autowired
    private TbClusterService tbClusterService;

    @Autowired
    private JaxValidator jaxValidator;

    @Autowired
    private ClusterProvider clusterProvider;

    @Autowired
    private JobService jobService;

    @Autowired
    private JarService jarService;

    public PipelineBuildIn listBuildIns() {
        PipelineBuildIn result = new PipelineBuildIn();
        List<String> list = new ArrayList<>();
        result.setFiles(list);
        String dir = ConfigLoader.load().jax.getBuildin().getPipelineDir();
        if (!FileUtil.exist(dir) || !FileUtil.isDirectory(dir)) {
            return result;
        }
        File[] files = FileUtil.ls(dir);
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            String fileName = file.getName();
            if (fileName.endsWith(".zip") || fileName.endsWith(".pipeline")) {
                list.add(fileName);
            }
        }
        return result;
    }

    public Paged<PipelineResp> listPipelineByPage(
            String search,
            String[] type,
            String[] status,
            String[] inStatus,
            Integer pageIndex,
            Integer pageSize,
            String sortBy,
            String order) {
        QueryWrapper<TbPipeline> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderBy(StrUtil.isNotBlank(sortBy), "asc".equals(order), sortBy);
        queryWrapper.lambda()
                .in(ArrayUtil.isNotEmpty(type), TbPipeline::getPipelineType, type)
                .in(ArrayUtil.isNotEmpty(status), TbPipeline::getPipelineStatus, status)
                .in(ArrayUtil.isNotEmpty(inStatus), TbPipeline::getInternalStatus, inStatus)
                .and(StrUtil.isNotBlank(search), q -> q
                        .like(TbPipeline::getPipelineName, search)
                        .or()
                        .like(TbPipeline::getPipeDescription, search));
        List<TbPipeline> pipelines;
        if (pageIndex != null && pageSize != null) {
            pipelines = tbPipelineService.getBaseMapper().selectPage(new Page<>(pageIndex + 1, pageSize), queryWrapper).getRecords();
        } else {
            pipelines = tbPipelineService.getBaseMapper().selectList(queryWrapper);
        }
        List<TbCluster> clusters = tbClusterService.list();
        long total = tbPipelineService.getBaseMapper().selectCount(queryWrapper);
        return new Paged<>(total, pipelines.stream().map(i -> {
            TbCluster cluster = clusters.stream().filter(f ->
                    f.getClusterName().equals(i.getClusterName()))
                    .findFirst().orElseThrow(() -> new BizException(ResponseCode.CLUSTER_NOT_EXIST)
                    );
            return new PipelineResp().respFrom(i, cluster);
        }).collect(Collectors.toList()));
    }

    public Paged<PipelineResp> queryPipeline(PipelineQueryReq req) {
        IPage<TbPipeline> paged = tbPipelineService.page(req.page(), req.query());
        List<TbCluster> clusters = tbClusterService.list();
        long total = paged.getTotal();
        List<TbPipeline> pipelines = paged.getRecords();
        return new Paged<>(total, pipelines.stream().map(i -> {
            TbCluster cluster = clusters.stream().filter(f ->
                    f.getClusterName().equals(i.getClusterName()))
                    .findFirst().orElseThrow(() -> new BizException(ResponseCode.CLUSTER_NOT_EXIST)
            );
            return new PipelineResp().respFrom(i, cluster);
        }).collect(Collectors.toList()));
    }

    public PipelineResp getPipeline(String pipelineName) {
        TbPipeline pipeline = byId(pipelineName);
        TbCluster cluster =  tbClusterService.getById(pipeline.getClusterName());
        return new PipelineResp().respFrom(pipeline, cluster);
    }

    public List<TbPipelineLog> logPipeline(String pipelineName) {
        return tbPipelineLogService.list(new LambdaQueryWrapper<TbPipelineLog>()
                .eq(TbPipelineLog::getPipelineName, pipelineName)
                .orderByDesc(TbPipelineLog::getCreateTime)
        );
    }

    public IPage<TbPipelineConsole> listPipelineConsole(
            String pipelineName,
            Integer pageIndex,
            Integer pageSize) {

        Page<TbPipelineConsole> page = new Page<>(pageIndex + 1, pageSize);
        return tbPipelineConsoleService.page(page,
                new LambdaQueryWrapper<TbPipelineConsole>()
                        .eq(TbPipelineConsole::getPipelineName, pipelineName)
                        // 按操作时间倒序输出，是最近的日志显示在最前面
                        .orderBy(true, false, TbPipelineConsole::getOpTime)
                        .orderBy(true, true, TbPipelineConsole::getCreateTime));
    }

    public boolean deletePipelineConsole(String pipelineName) {
        return tbPipelineConsoleService.remove(
                new LambdaQueryWrapper<TbPipelineConsole>()
                        .eq(TbPipelineConsole::getPipelineName, pipelineName)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public PipelineResp createDraft(PipelineReq req) {
        return createNew(req, PipelineStatus.DRAFT.code, false);
    }

    @Transactional(rollbackFor = Exception.class)
    public PipelineResp updateDraft(PipelineReq req) {
        TbPipeline entity = byId(req.getPipelineName());
        if (!PipelineStatus.DRAFT.isEqual(entity.getPipelineStatus())) {
            throw new BizException(ResponseCode.PIPELINE_CANNOT_DRAFT);
        }
        return updateExits(req, entity, PipelineStatus.DRAFT.code, false);
    }

    @Transactional(rollbackFor = Exception.class)
    public PipelineResp createStage(PipelineReq req) {
        return createNew(req, PipelineStatus.DRAFT.code, false);
    }

    @Transactional(rollbackFor = Exception.class)
    public PipelineResp updateStage(PipelineReq req, boolean autoCreate) {
        if (!exist(req.getPipelineName(), false) && autoCreate) {
            return createNew(req, PipelineStatus.DRAFT.code, false);
        }
        TbPipeline entity = byId(req.getPipelineName());
        if (PipelineStatus.WAITING_START.isEqual(entity.getPipelineStatus())
                || PipelineStatus.WAITING_STOP.isEqual(entity.getPipelineStatus())
                || PipelineStatus.STARTING.isEqual(entity.getPipelineStatus())
                || PipelineStatus.STOPPING.isEqual(entity.getPipelineStatus())
                || PipelineStatus.RESTARTING.isEqual(entity.getPipelineStatus())
                || PipelineStatus.DELETING.isEqual(entity.getPipelineStatus())
                || PipelineStatus.RUNNING.isEqual(entity.getPipelineStatus())
                || Boolean.TRUE.equals(entity.getProcessing())
                || Boolean.TRUE.equals(entity.getDeleting())) {
            JsonNode tree1 = JsonUtil.decode2Tree(JsonUtil.encode(req.getPipelineConfig()));
            JsonNode tree2 = JsonUtil.decode2Tree(entity.getPipelineConfig());
            if (!tree1.equals(tree2)) {
                throw new BizException(
                        ResponseCode.PIPELINE_CANNOT_UPDATE.code,
                        ResponseCode.PIPELINE_CANNOT_UPDATE.message + "(" + entity.getPipelineStatus() + ")"
                );
            }
        }
        return updateExits(req, entity, null, false);
    }

    @Transactional(rollbackFor = Exception.class)
    public PipelineResp createPipeline(PipelineReq req) {
        return createNew(req, PipelineStatus.WAITING_START.code, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public PipelineResp updatePipeline(PipelineReq req, boolean autoCreate) {
        if (!exist(req.getPipelineName(), false) && autoCreate) {
            return createNew(req, PipelineStatus.WAITING_START.code, true);
        }
        TbPipeline entity = byId(req.getPipelineName());
        if (PipelineStatus.WAITING_START.isEqual(entity.getPipelineStatus())
                || PipelineStatus.WAITING_STOP.isEqual(entity.getPipelineStatus())
                || PipelineStatus.STARTING.isEqual(entity.getPipelineStatus())
                || PipelineStatus.STOPPING.isEqual(entity.getPipelineStatus())
                || PipelineStatus.RESTARTING.isEqual(entity.getPipelineStatus())
                || PipelineStatus.DELETING.isEqual(entity.getPipelineStatus())
                || Boolean.TRUE.equals(entity.getProcessing())
                || Boolean.TRUE.equals(entity.getDeleting())) {
            throw new BizException(ResponseCode.PIPELINE_CANNOT_START);
        }
        return updateExits(req, entity, PipelineStatus.RESTARTING.code, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized PipelineResp startPipeline(String pipelineName, boolean forceStart) {
        TbPipeline entity = byId(pipelineName);
        if (!forceStart && (
                PipelineStatus.WAITING_START.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.WAITING_STOP.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.STARTING.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.STOPPING.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.RESTARTING.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.DELETING.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.RUNNING.isEqual(entity.getPipelineStatus())
                        || Boolean.TRUE.equals(entity.getProcessing())
                        || Boolean.TRUE.equals(entity.getDeleting())
                )
        ) {
            throw new BizException(ResponseCode.PIPELINE_CANNOT_START);
        }
        return updateStatus(entity, PipelineStatus.WAITING_START.code, null, true, forceStart);
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized PipelineResp stopPipeline(String pipelineName, boolean forceStop, boolean disableSavePoint) {
        TbPipeline entity = byId(pipelineName);
        if (!forceStop && (
                PipelineStatus.DRAFT.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.WAITING_START.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.WAITING_STOP.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.STARTING.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.STOPPING.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.RESTARTING.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.DELETING.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.FINISHED.isEqual(entity.getPipelineStatus())
                        || PipelineStatus.STOPPED.isEqual(entity.getPipelineStatus())
                        || Boolean.TRUE.equals(entity.getProcessing())
                        || Boolean.TRUE.equals(entity.getDeleting())
                )
        ) {
            throw new BizException(ResponseCode.PIPELINE_CANNOT_STOP);
        }
        if (PipelineStatus.START_FAILED.isEqual(entity.getPipelineStatus())) {
            return updateStatus(entity, PipelineStatus.STOPPED.code, disableSavePoint, false, forceStop);
        }
        return updateStatus(entity, PipelineStatus.WAITING_STOP.code, disableSavePoint, false, forceStop);
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized PipelineResp deletePipeline(String pipelineName, boolean forceDelete) {
        TbPipeline entity = byId(pipelineName);
        if (!forceDelete && (
                PipelineStatus.DELETING.isEqual(entity.getPipelineStatus())
                        || Boolean.TRUE.equals(entity.getDeleting()))
        ) {
            throw new BizException(ResponseCode.PIPELINE_CANNOT_DELETE);
        }
        delete2Db(entity, forceDelete);
        return new PipelineResp().respFrom(entity);
    }

    public void exportPipeline(PipelineExportReq req, OutputStream out) {
        if (req == null || CollUtil.isEmpty(req.getPipelineNames())) {
            return;
        }
        Collection<TbPipeline> exportingPipelines = tbPipelineService.listByIds(req.getPipelineNames());
        List<PipelineReq> pipelines = exportingPipelines.stream().map(PipelineReq::fromEntity).collect(Collectors.toList());
        try (ZipOutputStream zip = new ZipOutputStream(out)) {
            ZipUtil.addDir(zip, "export");
            for (PipelineReq pipeline: pipelines) {
                pipeline.setClusterName(null); // 不导出clusterName
                byte[] json = JsonUtil.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsBytes(pipeline);
                ZipUtil.addFile(zip, String.format("export/%s.pipeline", pipeline.getPipelineName()), json);
            }
            out.flush();
        } catch (IOException ex) {
            logger.warn("archive", ex);
        }
    }

    public List<Tuple<String, String>> importPipeline(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return importPipeline(inputStream, file.getOriginalFilename());
        } catch (Exception ex) {
            logger.error("import", ex);
            throw new BizException(ResponseCode.INVALID_PARAM, ex);
        }
    }

    public List<Tuple<String, String>> importPipeline(PipelineBuildIn req) {
        List<Tuple<String, String>> results = new ArrayList<>();
        if (CollUtil.isEmpty(req.getFiles())) {
            return results;
        }
        for (String file : req.getFiles()) {
            String buildInFilePath  = Common.pathsJoin(ConfigLoader.load().jax.getBuildin().getPipelineDir(), file);
            try (InputStream inputStream = FileUtil.getInputStream(buildInFilePath)) {
                results.addAll(importPipeline(inputStream, file));
            } catch (Exception ex) {
                logger.error("import " + file, ex);
                throw new BizException(ResponseCode.INVALID_PARAM, ex);
            }
        }
        return results;
    }

    private List<Tuple<String, String>> importPipeline(InputStream inputStream, String fileName) throws Exception {
        List<Tuple<String, String>> errorPair = new ArrayList<>();
        if (fileName == null || fileName.endsWith(".pipeline")) {
            // for single pipeline file
            try {
                byte[] bytes = IoUtil.readBytes(inputStream, false);
                PipelineReq req = JsonUtil.decode(bytes, PipelineReq.class);
                logger.info("import {}", req.getPipelineName());
                updateStage(req, true);
            } catch (Exception ex) {
                errorPair.add(Tuple.of(fileName, ExceptionUtil.getRootCauseMessage(ex)));
            }
        } else {
            // for zip file
            try (ZipInputStream zip = new ZipInputStream(inputStream)) {
                Set<String> entries = new HashSet<>();
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    if (entries.contains(entry.getName())) {
                        throw new JaxException("发现重复的entry, 可能是不合法的zip");
                    }
                    entries.add(entry.getName());
                    if (entry.isDirectory()) {
                        continue;
                    }
                    String targetName = entry.getName();
                    if (!targetName.endsWith(".pipeline")) {
                        continue;
                    }
                    try {
                        byte[] bytes = IoUtil.readBytes(zip, false);
                        PipelineReq req = JsonUtil.decode(bytes, PipelineReq.class);
                        logger.info("import {}", req.getPipelineName());
                        updateStage(req, true);
                    } catch (Exception ex) {
                        errorPair.add(Tuple.of(targetName, ExceptionUtil.getRootCauseMessage(ex)));
                    }
                }
            }
        }
        return errorPair;
    }

    /**
     * 创建Pipeline
     */
    private PipelineResp createNew(PipelineReq req, String status, boolean needValidate) {
        long now = System.currentTimeMillis();
        if (!Common.verifyName(req.getPipelineName())) {
            throw new BizException(ResponseCode.PIPELINE_INVALID_NAME);
        }
        exist(req.getPipelineName(), true);
        TbPipeline entity = req.toEntity(new TbPipeline());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        if (StrUtil.isNotEmpty(status)) {
            entity.setPipelineStatus(status);
        }
        //校验clusterName
        TbCluster cluster = validateCluster(entity);
        entity.setClusterName(cluster.getClusterName());
        List<TbPipelineJob> jobs = null;
        if (!req.isCustomCmd()) {
            jobs = req.getPipelineConfig().getJobs().stream()
                    .map(i -> i.toEntity(new TbPipelineJob()).setPipelineName(entity.getPipelineName()))
                    .collect(Collectors.toList());
        }
        if (needValidate) {
            validatePipeline(null, entity, jobs);
        }
        insert2Db(entity, jobs);

        return new PipelineResp().respFrom(entity);
    }

    /**
     * 更新Pipeline配置
     */
    private PipelineResp updateExits(PipelineReq req, TbPipeline pipeline, String pipelineStatus, boolean needValidate) {
        long now = System.currentTimeMillis();
        String pipelineName = pipeline.getPipelineName();
        String originStatus = pipeline.getPipelineStatus();
        PipelineResp last = getPipeline(pipelineName);
        TbPipeline entity = byId(pipelineName);
        if (!PipelineStatus.DRAFT.isEqual(entity.getPipelineStatus())) {
            //禁止修改clusterName
            req.setClusterName(entity.getClusterName());
        }
        entity = req.toEntity(entity);
        entity.setUpdateTime(now);
        entity.setPipelineStatus(pipelineStatus);

        List<TbPipelineJob> jobs = null;
        if (!req.isCustomCmd()) {
            jobs = req.getPipelineConfig().getJobs().stream()
                    .map(i -> i.toEntity(new TbPipelineJob()).setPipelineName(pipelineName))
                    .collect(Collectors.toList());
        }
        if (needValidate) {
            validatePipeline(last, entity, jobs);
        }
        update2Db(entity, jobs, pipelineName, originStatus);
        return new PipelineResp().respFrom(entity);
    }

    /**
     * 更新Pipeline状态
     */
    private PipelineResp updateStatus(TbPipeline pipeline, String pipelineStatus, Boolean disableSavePoint, boolean needValidate, boolean clearProcessing) {
        String pipelineName = pipeline.getPipelineName();
        String originStatus = pipeline.getPipelineStatus();
        PipelineResp last = getPipeline(pipelineName);
        TbPipeline entity = byId(pipelineName);
        entity.setPipelineStatus(pipelineStatus);
        entity.setFlinkSavePointDisable(disableSavePoint);
        List<TbPipelineJob> jobs = tbPipelineJobService.list(
                new LambdaQueryWrapper<TbPipelineJob>().eq(TbPipelineJob::getPipelineName, pipelineName)
        );
        if (needValidate) {
            validatePipeline(last, entity, jobs);
        }
        update2Db(entity, pipelineName, originStatus, clearProcessing);
        return new PipelineResp().respFrom(entity);
    }

    /**
     * 验证pipeline的配置
     * 1. pipelineType必须是streaming，batch
     * 2. jobs和edges不允许为空
     * 3. job参数必须合法
     * 4. 检测job参数的兼容性，设置flinkSavePointIncompatible标志位
     *    如果与上次不兼容，发送启动命令时清空savepoint（FlinkPipelineService.startPipeline）。
     */
    private void validatePipeline(PipelineResp lastPipeline, TbPipeline current, List<TbPipelineJob> jobs) {
        // 1. pipelineType必须是streaming，batch
        if (!PipelineType.isStreaming(current.getPipelineType())
                && !PipelineType.isBatch(current.getPipelineType())) {
            throw new BizException(ResponseCode.PIPELINE_INVALID_TYPE);
        }
        boolean isCustom =  PipelineType.FLINK_CMD.isEqual(current.getPipelineType());
        //自定义任务无需校验
        if (isCustom) {
            return;
        }
        boolean isSql = PipelineType.FLINK_SQL.isEqual(current.getPipelineType());
        PipelineResp currentPipeline = new PipelineResp().respFrom(current);
        // 2. jobs和edges不允许为空
        if (currentPipeline.getPipelineConfig() == null
                || CollUtil.isEmpty(currentPipeline.getPipelineConfig().getJobs())
                //sql job只有一个job，没有edge
                || (CollUtil.isEmpty(currentPipeline.getPipelineConfig().getEdges()) && !isSql)

        ) {
            throw new BizException(ResponseCode.INVALID_PARAM);
        }
        jobJars(jobs);
        // 3. job参数必须合法
        // 4. 检测job参数的兼容性
        ValidateResult validateResult = jaxValidator.checkDag(lastPipeline, genPipeline(current, jobs));
        boolean compatible = Boolean.TRUE.equals(validateResult.getCompatible());
        if (!compatible) {
            logger.warn("find incompatible config: {}", current.getPipelineName());
        }
        current.setFlinkSavePointIncompatible(!compatible);
    }

    /**
     * 检查选择的集群是否合法
     * 是：返回合法的集群
     * 否：抛出BizException(PIPELINE_INVALID_CLUSTER)
     *
     * 检验规则：
     * streaming 只能使用`yarn`和`flink_standalone`集群
     * batch 只能使用`yarn`和`spark_standalone`集群
     */
    private TbCluster validateCluster(TbPipeline pipeline) {
        TbCluster cluster = null;
        if (StrUtil.isEmpty(pipeline.getClusterName())) {
            //没设置集群时，使用默认集群(如果有)
            if (PipelineType.isStreaming(pipeline.getPipelineType())) {
                cluster = tbClusterService.getDefaultFlinkCluster();
            } else if (PipelineType.isBatch(pipeline.getPipelineType())) {
                cluster = tbClusterService.getDefaultSparkCluster();
            }
        } else {
            //检查集群
            cluster = tbClusterService.getById(pipeline.getClusterName());
            if (cluster == null) {
                throw new BizException(ResponseCode.CLUSTER_NOT_EXIST);
            }
        }
        if (cluster == null) {
            throw new BizException(ResponseCode.PIPELINE_UNDEFINED_CLUSTER);
        }
        ClusterType clusterType = ClusterType.fromString(cluster.getClusterType());
        if (clusterType == null) {
            throw new BizException(ResponseCode.PIPELINE_INVALID_CLUSTER);
        }
        if (PipelineType.isStreaming(pipeline.getPipelineType()) && !clusterType.supportFlink()) {
            throw new BizException(ResponseCode.PIPELINE_INVALID_CLUSTER);
        }
        if (PipelineType.isBatch(pipeline.getPipelineType()) && !clusterType.supportSpark()) {
            throw new BizException(ResponseCode.PIPELINE_INVALID_CLUSTER);
        }
        return cluster;
    }

    /**
     * 检查pipelineName是否存在
     */
    private boolean exist(String pipelineName, boolean notAndThrow) {
        int count = tbPipelineService.count(
                new LambdaQueryWrapper<TbPipeline>().eq(TbPipeline::getPipelineName, pipelineName)
        );
        if (count > 0 && notAndThrow) {
            throw new BizException(ResponseCode.PIPELINE_EXIST);
        }
        return count > 0;
    }

    /**
     * 验证pipelineName合法性
     * 必须在TbPipeline中存在
     */
    private TbPipeline byId(String pipelineName) {
        TbPipeline entity = tbPipelineService.getById(pipelineName);
        if (entity == null) {
            throw new BizException(ResponseCode.PIPELINE_NOT_EXIST);
        }
        return entity;
    }

    public Pipeline genPipeline(TbPipeline entity, List<TbPipelineJob> jobs) {
        Cluster cluster = genCluster(entity);
        Pipeline pipeline = new Pipeline(entity, jobs, cluster);
        PipelineConfigOpts opts = pipeline.getPipelineConfig() != null
                ? pipeline.getPipelineConfig().genOptsBean() : new PipelineConfigOpts();
        List<JobJar> jobJars = jobJars(jobs);
        pipeline.setJobJars(jobJars);
        List<PipelineJar> extJars = jarService.jarsFromNames(opts.getExtJars());
        pipeline.setExtJars(extJars);
        return pipeline;
    }

    public Cluster genCluster(TbPipeline entity) {
        if (StrUtil.isEmpty(entity.getClusterName())) {
            throw new JaxException(ResponseCode.CLUSTER_NOT_EXIST.message);
        }
        TbCluster tbCluster = tbClusterService.getById(entity.getClusterName());
        if (tbCluster == null) {
            throw new JaxException(ResponseCode.CLUSTER_NOT_EXIST.message);
        }
        TbOptsFlink tbOptsFlink = null;
        if (PipelineType.isStreaming(entity.getPipelineType()) && StrUtil.isNotEmpty(entity.getOptsName())) {
            tbOptsFlink = tbOptsFlinkService.getById(entity.getOptsName());
        }
        if (tbOptsFlink == null && StrUtil.isNotEmpty(tbCluster.getFlinkOptsName())) {
            tbOptsFlink = tbOptsFlinkService.getById(tbCluster.getFlinkOptsName());
        }
        TbOptsSpark tbOptsSpark = null;
        if (PipelineType.isBatch(entity.getPipelineType()) && StrUtil.isNotEmpty(entity.getOptsName())) {
            tbOptsSpark = tbOptsSparkService.getById(entity.getOptsName());
        }
        if (tbOptsSpark == null && StrUtil.isNotEmpty(tbCluster.getSparkOptsName())) {
            tbOptsSpark = tbOptsSparkService.getById(tbCluster.getSparkOptsName());
        }
        return clusterProvider.fromEntity(
                tbCluster,
                tbOptsFlink,
                tbOptsSpark
        );
    }

    /**
     * 验证job合法性
     * 1. 必须在TbJob中存在
     * 2. 必须找到关联的TbJar
     */
    public List<JobJar> jobJars(List<TbPipelineJob> jobs) {
        List<String> jobNames = jobs.stream().map(TbPipelineJob::getJobName).distinct().collect(Collectors.toList());
        return jobService.jarsFromJobs(jobNames);
    }

    /**
     * 数据库操作
     * 新建 TbPipeline，TbPipelineJob
     */
    private void insert2Db(TbPipeline entity, List<TbPipelineJob> jobs) {
        tbPipelineService.save(entity);
        if (jobs != null) {
            tbPipelineJobService.saveBatch(jobs);
        }
    }

    /**
     * 数据库操作
     * 更新 TbPipeline，TbPipelineJob 配置
     * 如果状态已发生变化，通过抛出异常触发事务回退
     */
    private void update2Db(TbPipeline entity, List<TbPipelineJob> jobs, String whereName, String whereStatus) {
        boolean success = tbPipelineService.update(entity,
                new LambdaUpdateWrapper<TbPipeline>()
                        .eq(TbPipeline::getPipelineName, whereName)
                        .eq(TbPipeline::getPipelineStatus, whereStatus)
        );
        if (!success) {
            throw new BizException(ResponseCode.UPDATE_ERROR);
        }
        tbPipelineJobService.remove(
                new LambdaQueryWrapper<TbPipelineJob>().eq(TbPipelineJob::getPipelineName, entity.getPipelineName())
        );
        if (jobs != null) {
            tbPipelineJobService.saveBatch(jobs);
        }
    }

    /**
     * 数据库操作
     * 仅更新状态相关
     * 如果状态已发生变化，通过抛出异常触发事务回退
     */
    private void update2Db(TbPipeline entity, String whereName, String whereStatus, boolean clearProcessing) {
        long now = System.currentTimeMillis();
        boolean success = tbPipelineService.update(
                new LambdaUpdateWrapper<TbPipeline>()
                        .set(TbPipeline::getPipelineStatus, entity.getPipelineStatus())
                        .set(TbPipeline::getFlinkSavePointIncompatible, entity.getFlinkSavePointIncompatible())
                        .set(TbPipeline::getFlinkSavePointDisable,  entity.getFlinkSavePointDisable())
                        .set(TbPipeline::getUpdateTime, now)
                        .set(clearProcessing, TbPipeline::getProcessing, null)
                        .eq(TbPipeline::getPipelineName, whereName)
                        .eq(TbPipeline::getPipelineStatus, whereStatus)
        );
        if (!success) {
            throw new BizException(ResponseCode.PIPELINE_UPDATE_CHANGED);
        }
    }

    /**
     * 数据库操作
     * 标记删除状态，异步删除
     * 如果状态已发生变化，通过抛出异常触发事务回退
     */
    private void delete2Db(TbPipeline entity, boolean clearProcessing) {
        long now = System.currentTimeMillis();
        boolean success = tbPipelineService.update(
                new LambdaUpdateWrapper<TbPipeline>()
                        .set(TbPipeline::getPipelineStatus, PipelineStatus.DELETING.code)
                        .set(TbPipeline::getDeleting, true)
                        .set(TbPipeline::getUpdateTime, now)
                        .set(clearProcessing, TbPipeline::getProcessing, null)
                        .eq(TbPipeline::getPipelineName, entity.getPipelineName())
                        .eq(TbPipeline::getPipelineStatus, entity.getPipelineStatus())
        );
        if (!success) {
            throw new BizException(ResponseCode.PIPELINE_UPDATE_CHANGED);
        }
    }

    public void clearPipeline(TbPipeline entity) {
        tbPipelineService.removeById(entity.getPipelineName());
        tbPipelineJobService.remove(
                new LambdaQueryWrapper<TbPipelineJob>().eq(TbPipelineJob::getPipelineName, entity.getPipelineName())
        );
        tbSparkEventService.remove(
                new LambdaQueryWrapper<TbSparkEvent>().eq(TbSparkEvent::getPipelineName, entity.getPipelineName())
        );
        tbPipelineLogService.remove(
                new LambdaQueryWrapper<TbPipelineLog>().eq(TbPipelineLog::getPipelineName, entity.getPipelineName())
        );
        tbPipelineConsoleService.remove(
                new LambdaQueryWrapper<TbPipelineConsole>().eq(TbPipelineConsole::getPipelineName, entity.getPipelineName())
        );
    }
}
