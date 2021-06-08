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

import com.eoi.jax.api.FlinkDebugSinker;
import com.eoi.jax.api.FlinkDebugSinkerMeta;
import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.FlinkSinkJobBuilder;
import com.eoi.jax.api.FlinkSourceJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.common.PythonHelper;
import com.eoi.jax.common.ReflectUtil;
import com.eoi.jax.core.flink.python.PythonWrapperJob;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// CHECKSTYLE.OFF:
public class FlinkJobDAGBuilder extends BaseJobDAGBuilder<FlinkJobNode> {
    // CHECKSTYLE.ON:

    private static Logger logger = LoggerFactory.getLogger(FlinkJobDAGBuilder.class);
    private FlinkEnvironment context;
    private String pipelineName;

    public FlinkJobDAGBuilder(FlinkEnvironment context) {
        this.context = context;
    }

    public FlinkJobDAGBuilder(FlinkEnvironment context, String pipelineName) {
        this.context = context;
        this.pipelineName = pipelineName;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    @Override
    public void rewriteNode(FlinkJobNode node) throws JobBuildException {
        String originalClassName = node.getEntry();
        if (!originalClassName.startsWith(PythonHelper.PYTHON_VIRTUAL_PACKAGE_FLINK)) {
            return;
        }
        // python.flink.<python module>.<python className>
        // <python module> may also have .
        String[] part = originalClassName.split("\\.");
        if (part.length < 4) {
            throw new JobBuildException(String.format("invalid class name for mock python job: `%s`, "
                    + "which must be `python.flink.<python module>.<python className>`", originalClassName));
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
        node.getConfig().put("pythonModule", moduleName);
        node.getConfig().put("pythonClass", className);
        node.setEntry(PythonWrapperJob.class.getCanonicalName());
    }

    @Override
    void validateJobBuilder(FlinkJobNode node, Object jobBuilder) throws JobBuildException {
        if (!(jobBuilder instanceof FlinkSourceJobBuilder)
                && !(jobBuilder instanceof FlinkProcessJobBuilder)
                && !(jobBuilder instanceof FlinkSinkJobBuilder)) {
            throw new InvalidTopologyException(node, "type of jobBuilder is not recognized");
        }
    }

    @Override
    List<VisitResult> buildResults(Object jobBuilder, List parents, FlinkJobNode node) throws JobBuildException {
        if (jobBuilder instanceof FlinkSourceJobBuilder) {
            return buildResultsOfSource((FlinkSourceJobBuilder) jobBuilder, parents, node);
        }
        if (jobBuilder instanceof FlinkProcessJobBuilder) {
            return buildResultsOfProcess((FlinkProcessJobBuilder) jobBuilder, parents, node);
        }
        if (jobBuilder instanceof FlinkSinkJobBuilder) {
            return buildResultsOfSink((FlinkSinkJobBuilder) jobBuilder, parents, node);
        }
        throw new InvalidTopologyException(node, "type of jobBuilder is not recognized");
    }

    @Override
    void registerTable(Object outputTableObject, String tableName, Integer slot) throws JobBuildException {
        if (outputTableObject instanceof Table) {
            context.tableEnv.registerTable(tableName, (Table) outputTableObject);
        } else {
            throw new JobBuildException(String.format("slot %d of job output is not suitable Table", slot));
        }
    }

    private void setDebugSinkerIfNeeded(List<VisitResult> outputs, FlinkJobNode node) throws JobBuildException {
        FlinkJobOpts opts = Optional.ofNullable(node.getOpts()).orElse(new FlinkJobOpts());
        if (!Boolean.TRUE.equals(opts.getEnableDebug())) {
            return;
        }
        try {
            String debugEntry = Optional.ofNullable(opts.getDebugEntry()).orElseThrow(() ->
                    new JobBuildException(String.format("Missing debugEntry when enable debug in node(%s)", node.id)));
            Map<String, Object> debugConfigMap = opts.getDebugConfig();
            FlinkDebugSinker debugSinker;
            debugSinker = (FlinkDebugSinker) Class.forName(debugEntry).newInstance();
            Object debugConfig = debugSinker.configure(debugConfigMap);
            for (int i = 0; i < outputs.size(); i++) {
                VisitResult visitResult = outputs.get(i);
                FlinkDebugSinkerMeta meta = new FlinkDebugSinkerMeta(retrieveJobMetaConfig(node), i);
                if (visitResult.resultObject instanceof DataStream) {
                    if (debugSinker.supportDataStream()) {
                        debugSinker.sinkDataStream(context, (DataStream) visitResult.resultObject, debugConfig, meta);
                    } else {
                        logger.warn("slot {} of node({}) can not be sink as debug, since {} not support DataStream",
                                meta.slotIndex, meta.metaConfig.getJobId(), debugSinker.getClass().getName());
                    }
                } else if (visitResult.resultObject instanceof Table) {
                    if (debugSinker.supportTable()) {
                        debugSinker.sinkTable(context, (Table) visitResult.resultObject, debugConfig, meta);
                    } else {
                        logger.warn("slot {} of node({}) can not be sink as debug, since {} not support Table",
                                meta.slotIndex, meta.metaConfig.getJobId(), debugSinker.getClass().getName());
                    }
                }
            }
        } catch (JobBuildException jbe) {
            throw jbe;
        } catch (Throwable ex) {
            throw new JobBuildException("failed to build debug sinker", ex);
        }
    }

    private List<VisitResult> buildResultsOfSource(FlinkSourceJobBuilder jobBuilder, List parents, FlinkJobNode node)
            throws JobBuildException {
        try {
            Map<String, Type> declaredTypes = ReflectUtil.scanParameterizedType(jobBuilder.getClass(), FlinkSourceJobBuilder.class);
            if (declaredTypes == null) {
                throw new JobBuildException("failed to scan the ParameterizedType of FlinkSourceJobBuilder");
            }
            // the first type is OUT's type
            Type outType = declaredTypes.get("OUT");
            Object config = jobBuilder.configure(node.getConfig());
            checkConfigValidatable(config);
            Object buildOutput = jobBuilder.build(context, config, retrieveJobMetaConfig(node));
            List<VisitResult> outputs = normalizeOutput(config, buildOutput, outType, node);
            setDebugSinkerIfNeeded(outputs, node);
            return outputs;
        } catch (JobBuildException jbe) {
            throw jbe;
        } catch (Throwable ex) {
            throw new JobBuildException("failed to invoke configure or build routine for " + jobBuilder.toString(), ex);
        }
    }

    private List<VisitResult> buildResultsOfProcess(FlinkProcessJobBuilder jobBuilder, List parents, FlinkJobNode node)
            throws JobBuildException {
        try {
            Map<String, Type> declaredTypes = ReflectUtil.scanParameterizedType(jobBuilder.getClass(), FlinkProcessJobBuilder.class);
            if (declaredTypes == null) {
                throw new JobBuildException("failed to scan the ParameterizedType of FlinkProcessJobBuilder");
            }
            // the first type is IN's type
            Type inType = declaredTypes.get("IN");
            // the second type is OUT's type
            Type outType = declaredTypes.get("OUT");
            Object config = jobBuilder.configure(node.getConfig());
            checkConfigValidatable(config);
            Object builtInput = prepareInput(parents, inType, node);
            Object builtOutput = jobBuilder.build(context, builtInput, config, retrieveJobMetaConfig(node));
            List<VisitResult> outputs = normalizeOutput(config, builtOutput, outType, node);
            setDebugSinkerIfNeeded(outputs, node);
            return outputs;
        } catch (Throwable ex) {
            throw new JobBuildException("failed to invoke configure or build routine for " + jobBuilder.toString(), ex);
        }
    }

    private List<VisitResult> buildResultsOfSink(FlinkSinkJobBuilder jobBuilder, List parents, FlinkJobNode node)
            throws JobBuildException {
        try {
            Map<String, Type> declaredTypes = ReflectUtil.scanParameterizedType(jobBuilder.getClass(), FlinkSinkJobBuilder.class);
            if (declaredTypes == null) {
                throw new JobBuildException("failed to scan the ParameterizedType of FlinkSinkJobBuilder");
            }
            // the first type is IN's type
            Type inType = declaredTypes.get("IN");
            Object config = jobBuilder.configure(node.getConfig());
            checkConfigValidatable(config);
            Object builtInput = prepareInput(parents, inType, node);
            jobBuilder.build(context, builtInput, config, retrieveJobMetaConfig(node));
            // no output for sink
            return null;
        } catch (Throwable ex) {
            throw new JobBuildException("failed to invoke configure or build routine for " + jobBuilder.toString(), ex);
        }
    }

    private JobMetaConfig retrieveJobMetaConfig(FlinkJobNode node) {
        return new JobMetaConfig(node.id, node.entry, pipelineName);
    }
}
