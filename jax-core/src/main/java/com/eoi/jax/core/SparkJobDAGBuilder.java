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

package com.eoi.jax.core;

import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.SparkCacheableJobBuilder;
import com.eoi.jax.api.SparkDebugSinker;
import com.eoi.jax.api.SparkDebugSinkerMeta;
import com.eoi.jax.api.SparkEnvironment;
import com.eoi.jax.api.SparkMergeableJobBuilder;
import com.eoi.jax.api.SparkProcessJobBuilder;
import com.eoi.jax.api.SparkSinkJobBuilder;
import com.eoi.jax.api.SparkSourceJobBuilder;
import com.eoi.jax.api.tuple.Tuple2;
import com.eoi.jax.common.PythonHelper;
import com.eoi.jax.common.ReflectUtil;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.storage.StorageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_project.jetty.util.StringUtil;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// CHECKSTYLE.OFF:
public class SparkJobDAGBuilder extends BaseJobDAGBuilder<SparkJobNode> {
    // CHECKSTYLE.ON:

    private static Logger logger = LoggerFactory.getLogger(SparkJobDAGBuilder.class);

    private SparkEnvironment context;

    private String pipelineName;

    private final boolean enableMergeOptimize;

    public SparkEnvironment getContext() {
        return context;
    }

    public void setContext(SparkEnvironment context) {
        this.context = context;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public SparkJobDAGBuilder(SparkEnvironment context) {
        this(context, false, null);
    }

    public SparkJobDAGBuilder(SparkEnvironment context, boolean enableMergeOptimize) {
        this(context, enableMergeOptimize, null);
    }

    public SparkJobDAGBuilder(SparkEnvironment context, boolean enableMergeOptimize, String pipelineName) {
        this.context = context;
        this.enableMergeOptimize = enableMergeOptimize;
        this.pipelineName = pipelineName;
    }

    @Override
    void validateJobBuilder(SparkJobNode node, Object jobBuilder) throws JobBuildException {
        if (!(jobBuilder instanceof SparkSourceJobBuilder)
                && !(jobBuilder instanceof SparkProcessJobBuilder)
                && !(jobBuilder instanceof SparkSinkJobBuilder)) {
            throw new InvalidTopologyException(node, "type of jobBuilder is not recognized");
        }
    }

    @Override
    public void rewriteNode(SparkJobNode node) throws JobBuildException {
        String originalClassName = node.getEntry();
        if (!originalClassName.startsWith(PythonHelper.PYTHON_VIRTUAL_PACKAGE_SPARK)) {
            return;
        }
        // python.spark.<python module>.<python className>
        // <python module> may also have .
        String[] part = originalClassName.split("\\.");
        if (part.length < 4) {
            throw new JobBuildException(String.format("invalid class name for mock python job: `%s`, "
                    + "which must be `python.spark.<python module>.<python className>`", originalClassName));
        }
        int modulePartLength = part.length - 3;
        String[] moduleParts = new String[modulePartLength];
        for (int i = 2; i < modulePartLength + 2; i++) {
            moduleParts[i - 2] = part[i];
        }
        String moduleName = String.join(".", moduleParts);
        String className = part[part.length - 1];
        if (node.getConfig() == null) {
            node.setConfig(new HashMap<>());
        }
        node.getConfig().put("moduleName", moduleName);
        node.getConfig().put("algName", className);
        node.setEntry("com.eoi.jax.core.spark.python.BridgeJob");
    }

    @Override
    List<VisitResult> buildResults(Object jobBuilder, List parents, SparkJobNode node) throws JobBuildException {
        List<VisitResult> visitResults = null;
        if (jobBuilder instanceof SparkSourceJobBuilder) {
            visitResults = buildResultsOfSource((SparkSourceJobBuilder) jobBuilder, parents, node);
        } else if (jobBuilder instanceof SparkProcessJobBuilder) {
            visitResults = buildResultsOfProcess((SparkProcessJobBuilder) jobBuilder, parents, node);
        } else if (jobBuilder instanceof SparkSinkJobBuilder) {
            visitResults = buildResultsOfSink((SparkSinkJobBuilder) jobBuilder, parents, node);
        } else {
            throw new InvalidTopologyException(node, "type of jobBuilder is not recognized");
        }

        if (visitResults == null) {
            // sink 会返回 null
            return null;
        }
        String curNodeId = node.getId();
        // 获取当前节点的输出引用计数
        Map<Integer, Integer> refs = outputRefCountOf(node);
        for (Map.Entry<Integer, Integer> slot : refs.entrySet()) {
            if (slot.getValue() > 1) {
                visitResults.get(slot.getKey()).refCount = slot.getValue();
            }
        }
        // 尝试获取node的cacheLevel，默认用MEMORY_AND_DISK
        StorageLevel cacheLevel = StorageLevel.MEMORY_AND_DISK();
        boolean forceCache = false;
        SparkJobOpts opts = node.getOpts();
        if (opts != null && StringUtil.isNotBlank(opts.getCacheLevel())) {
            cacheLevel = StorageLevel.fromString(opts.getCacheLevel());
        }
        if (opts != null && opts.getCache() != null && opts.getCache()) {
            forceCache = true;
        }
        // 如果result被引用次数>1，调用"recommendCache"
        for (int i = 0; i < visitResults.size(); i++) {
            VisitResult visitResult = visitResults.get(i);
            // 如果某个visitResult的引用计数>1，或者配置了forceCache，那么需要调用job的recommendCache
            if (visitResult.refCount > 1 || forceCache) {
                logger.info("output of slot{} of {} is referenced by {} times or user force cache, call recommendCache",
                        i, curNodeId, visitResult.refCount);
                if (!(jobBuilder instanceof SparkCacheableJobBuilder)) {
                    logger.warn("target jobBuilder is not instance of SparkCacheableJobBuilder, skip cache");
                } else {
                    ((SparkCacheableJobBuilder) jobBuilder).recommendCache(visitResult.resultObject, i, cacheLevel, visitResult.refCount);
                }
            }
        }

        return visitResults;
    }

    @Override
    void registerTable(Object outputTableObject, String tableName, Integer slot) throws JobBuildException {
        logger.info("createOrReplaceTempView {} for {}", tableName, outputTableObject);
        if (outputTableObject instanceof Dataset) {
            // TODO: [M] 需要进一步检查Dataset的泛型类型是不是Row，才能相当于DataFrame
            Dataset dataset = (Dataset) outputTableObject;
            dataset.createOrReplaceTempView(tableName);
        } else {
            throw new JobBuildException(String.format("slot %d of job output is not suitable DataFrame", slot));
        }
    }

    private void setDebugSinkerIfNeeded(List<VisitResult> outputs, SparkJobNode node) throws JobBuildException {
        SparkJobOpts opts = Optional.ofNullable(node.getOpts()).orElse(new SparkJobOpts());
        if (!Boolean.TRUE.equals(opts.getEnableDebug())) {
            return;
        }
        String debugEntry = opts.getDebugEntry();
        if (debugEntry == null || debugEntry.isEmpty()) {
            new JobBuildException(String.format("Missing debugEntry when enable debug in node(%s)", node.id));
        }
        try {
            Map<String, Object> debugConfigMap = opts.getDebugConfig();
            SparkDebugSinker debugSinker;
            debugSinker = (SparkDebugSinker) Class.forName(debugEntry).newInstance();
            Object debugConfig = debugSinker.configure(debugConfigMap);
            for (int i = 0; i < outputs.size(); i++) {
                VisitResult visitResult = outputs.get(i);
                SparkDebugSinkerMeta sinkerMeta = new SparkDebugSinkerMeta(retrieveJobMetaConfig(node), i);
                if (visitResult.resultObject instanceof Dataset) {
                    if (debugSinker.supportDataFrame()) {
                        debugSinker.sinkDataFrame(context, (Dataset) visitResult.resultObject, debugConfig, sinkerMeta);
                        //为debug旁路设置cache
                        opts.setCache(true);
                    } else {
                        logger.warn("slot {} of node({}) can not be sink as debug, since {} not support DataFrame",
                                sinkerMeta.slotIndex, sinkerMeta.metaConfig.getJobId(), debugSinker.getClass().getName());
                    }
                } else if (visitResult.resultObject instanceof RDD) {
                    if (debugSinker.supportRdd()) {
                        debugSinker.sinkRdd(context, (RDD) visitResult.resultObject, debugConfig, sinkerMeta);
                        //为debug旁路设置cache
                        opts.setCache(true);
                    } else {
                        logger.warn("slot {} of node({}) can not be sink as debug, since {} not support RDD",
                                sinkerMeta.slotIndex, sinkerMeta.metaConfig.getJobId(), debugSinker.getClass().getName());
                    }
                } else {
                    logger.warn("slot {} of node({}) can not be sink as debug",
                            sinkerMeta.slotIndex, sinkerMeta.metaConfig.getJobId());
                }
            }
        } catch (Throwable ex) {
            throw new JobBuildException("failed to build debug sinker", ex);
        }

    }

    private List<VisitResult> buildResultsOfSource(SparkSourceJobBuilder jobBuilder, List parents, SparkJobNode node)
            throws JobBuildException {
        try {
            Map<String, Type> declaredTypes = ReflectUtil.scanParameterizedType(jobBuilder.getClass(), SparkSourceJobBuilder.class);
            if (declaredTypes == null) {
                throw new JobBuildException("failed to scan the ParameterizedType of SparkSourceJobBuilder");
            }
            // the first type is OUT's type
            Type outType = declaredTypes.get("OUT");
            Object config = jobBuilder.configure(node.getConfig());
            checkConfigValidatable(config);
            Object buildOutput = jobBuilder.build(context, config);
            List<VisitResult> outputs = normalizeOutput(config, buildOutput, outType, node);
            setDebugSinkerIfNeeded(outputs, node);
            return outputs;
        } catch (Throwable ex) {
            throw new JobBuildException("failed to invoke configure or build routine for " + jobBuilder.toString(), ex);
        }
    }

    private List<VisitResult> doBuildResultOfProcess(SparkProcessJobBuilder jobBuilder, List parents, SparkJobNode node)
            throws JobBuildException {
        try {
            Map<String, Type> declaredTypes = ReflectUtil.scanParameterizedType(jobBuilder.getClass(), SparkProcessJobBuilder.class);
            if (declaredTypes == null) {
                throw new JobBuildException("failed to scan the ParameterizedType of SparkSourceJobBuilder");
            }
            // the first type is IN's type
            Type inType = declaredTypes.get("IN");
            // the second type is OUT's type
            Type outType = declaredTypes.get("OUT");
            Object config = jobBuilder.configure(node.getConfig());
            checkConfigValidatable(config);
            Object builtInput = prepareInput(parents, inType, node);
            Object builtOutput = jobBuilder.build(context, builtInput, config);
            List<VisitResult> outputs = normalizeOutput(config, builtOutput, outType, node);
            //只在非merge节点实现debug
            setDebugSinkerIfNeeded(outputs, node);
            return outputs;
        } catch (Throwable ex) {
            throw new JobBuildException("failed to invoke configure or build routine for " + jobBuilder.toString(), ex);
        }
    }

    private List<VisitResult> buildResultsOfProcess(SparkProcessJobBuilder jobBuilder, List parents, SparkJobNode node)
            throws JobBuildException {
        assert parents != null && !parents.isEmpty();

        if (!enableMergeOptimize) {
            return doBuildResultOfProcess(jobBuilder, parents, node);
        }
        // 检查当前job是否存在输出被多引用的情况，如果存在则不能合并
        boolean referenceOverOne = false;
        Map<Integer, Integer> refs = outputRefCountOf(node);
        for (Map.Entry<Integer, Integer> ref : refs.entrySet()) {
            if (ref.getValue() > 1) {
                referenceOverOne = true;
                break;
            }
        }
        VisitResult input = (VisitResult) parents.get(0);
        if (input.mergedBuilder != null) {
            // mergeable && !referenceOverOne
            //   input.mergedBuilder.canMergeWith(input.mergedNode.config, jobBuilder, node.config)
            //     can: input.mergedBuidler.mergeWith(input.mergedNode.config, jobBuilder, node.config)
            //     can't: to else ->
            //   else
            // result = doBuildResultOfProcess(input.mergedBuilder, input, input.mergedNode)
            // doBuildResultOfProcess(jobBuilder, result, node
            if (jobBuilder instanceof SparkMergeableJobBuilder && !referenceOverOne) {
                SparkMergeableJobBuilder mergableJobBuilder = (SparkMergeableJobBuilder) jobBuilder;
                logger.info("Check whether job({}) can be merged with job({})",
                        jobBuilder.getClass().getCanonicalName(),
                        input.mergedBuilder.getClass().getCanonicalName()
                );
                boolean canMerge = input.mergedBuilder.canMergeWith(
                        input.mergedNode.config, mergableJobBuilder, node.config);
                if (canMerge) {
                    logger.info("Merging job({}) with job({})",
                            jobBuilder.getClass().getCanonicalName(),
                            input.mergedBuilder.getClass().getCanonicalName()
                    );
                    Tuple2<SparkMergeableJobBuilder, Map<String, Object>> mergeResult =
                            input.mergedBuilder.mergeWith(input.mergedNode.config, mergableJobBuilder, node.config);
                    // 合并原则: opts,id,entry,classloader 取下游, id取下游非常重要
                    input.mergedNode = input.mergedNode.mergeNextAndCreate(node);
                    input.mergedNode.setConfig(mergeResult.f1);
                    input.mergedBuilder = mergeResult.f0;

                    // 这里需要copy(即清空了refCount)，否则refCount会累加
                    VisitResult copy = new VisitResult(input.resultObject, input.resultType);
                    copy.mergedBuilder = input.mergedBuilder;
                    copy.mergedNode = input.mergedNode;

                    return Collections.singletonList(copy);

                } else {
                    List<VisitResult> pendingResult = doBuildResultOfProcess((SparkProcessJobBuilder) input.mergedBuilder,
                            parents, input.mergedNode);
                    // 这里需要copy(即清空了refCount)，否则refCount会累加
                    VisitResult copy = new VisitResult(pendingResult.get(0).resultObject, pendingResult.get(0).resultType);
                    copy.mergedBuilder = (SparkMergeableJobBuilder) jobBuilder;
                    copy.mergedNode = node;
                    return Collections.singletonList(copy);
                }
            }
            List<VisitResult> pendingResult = doBuildResultOfProcess((SparkProcessJobBuilder) input.mergedBuilder,
                    parents, input.mergedNode);
            return doBuildResultOfProcess(jobBuilder, pendingResult, node);
        } else {
            // mergeable && !referenceOverOne
            //  input.mergedBuilder = mergableJobBuilder;
            //  input.mergedNode = node;
            // else
            // doBuildResultOfProcess(jobBuilder, parents, node)
            if (jobBuilder instanceof SparkMergeableJobBuilder && !referenceOverOne) {
                logger.info("Pending the job({}) since it is SparkMergeableJobBuilder and is not refereced over one",
                        jobBuilder.getClass().getCanonicalName());
                SparkMergeableJobBuilder mergableJobBuilder = (SparkMergeableJobBuilder) jobBuilder;
                // 说明现在是第一个出现的mergableJob，不做build，直接把node和jobBuilder包装在VisitResult返回
                // 这里需要copy(即清空了refCount)，否则refCount会累加
                VisitResult copy = new VisitResult(input.resultObject, input.resultType);
                copy.mergedBuilder = mergableJobBuilder;
                copy.mergedNode = node;
                return Collections.singletonList(copy);
            } else {
                return doBuildResultOfProcess(jobBuilder, parents, node);
            }
        }
    }

    private List<VisitResult> buildResultsOfSink(SparkSinkJobBuilder jobBuilder, List parents, SparkJobNode node)
            throws JobBuildException {
        try {
            if (parents != null && !parents.isEmpty()) {
                VisitResult input = (VisitResult) parents.get(0);
                if (input.mergedBuilder != null && enableMergeOptimize) {
                    parents = doBuildResultOfProcess((SparkProcessJobBuilder) input.mergedBuilder, parents, input.mergedNode);
                }
            }
            Map<String, Type> declaredTypes = ReflectUtil.scanParameterizedType(jobBuilder.getClass(), SparkSinkJobBuilder.class);
            if (declaredTypes == null) {
                throw new JobBuildException("failed to scan the ParameterizedType of SparkSinkJobBuilder");
            }
            // the first type is IN's type
            Type inType = declaredTypes.get("IN");
            Object config = jobBuilder.configure(node.getConfig());
            checkConfigValidatable(config);
            Object builtInput = prepareInput(parents, inType, node);
            jobBuilder.build(context, builtInput, config);
            // no output for sink
            return null;
        } catch (Throwable ex) {
            throw new JobBuildException("failed to invoke configure or build routine for " + jobBuilder.toString(), ex);
        }
    }

    private JobMetaConfig retrieveJobMetaConfig(SparkJobNode node) {
        return new JobMetaConfig(node.id, node.entry, pipelineName);
    }
}
