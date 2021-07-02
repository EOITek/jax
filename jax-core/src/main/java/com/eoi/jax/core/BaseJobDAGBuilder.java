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

import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.OutputTableConfig;
import com.eoi.jax.api.SparkMergeableJobBuilder;
import com.eoi.jax.api.tuple.Tuple;
import com.eoi.jax.common.ReflectUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class BaseJobDAGBuilder<C extends BaseJobNode> extends AbstractDAGVisitor<C> {

    public void build() throws Throwable {
        getChain();
    }

    @Override
    protected List visit(C node, List parents) throws DAGVisitException {
        rewriteNode(node);
        // new the job builder from node.entry
        String className = node.getEntry();
        Object jobBuilder;
        try {
            jobBuilder = Class.forName(className).newInstance();
        } catch (Exception ex) {
            throw new JobBuildException("failed to create instance of class " + className, ex);
        }
        // check if the jobBuilder is supported
        validateJobBuilder(node, jobBuilder);
        return buildResults(jobBuilder, parents, node);
    }

    protected void checkConfigValidatable(Object config) throws JobConfigValidationException {
        if (config instanceof ConfigValidatable) {
            ((ConfigValidatable) config).validate();
        }
    }

    abstract void validateJobBuilder(C node, Object jobBuilder) throws JobBuildException;

    abstract List<VisitResult> buildResults(Object jobBuilder, List parents, C node) throws JobBuildException;

    abstract void registerTable(Object outputTableObject, String tableName, Integer slot) throws JobBuildException;

    // reconfigure the node before init job class if needed
    public void rewriteNode(C node) throws JobBuildException {
    }

    protected Object prepareInput(List inputObjects, Type declaredInputType, C node) throws JobBuildException {
        if (ReflectUtil.isAssignableBetween(Tuple.class, declaredInputType)) {
            // the Tuple must declared as ParameterizedType, i.e: Tuple2<DataStream, DataStream>
            if (!(declaredInputType instanceof ParameterizedType)) {
                throw new InvalidTopologyException(node, "input of tuple type must be declared generic");
            }
            Type[] requiredTypes = ((ParameterizedType) declaredInputType).getActualTypeArguments();
            Tuple tupleParents = Tuple.newInstance(requiredTypes.length);
            //if (tupleParents.getArity() < requiredTypes.length) {
            // 实际的输入流数量少于需要的
            //    throw new InvalidTopologyException(node, "the number of upstream is different from what is needed");
            //}
            for (int i = 0; i < requiredTypes.length; i++) {
                boolean isEmptyInput = i >= inputObjects.size() || inputObjects.get(i) == null;
                // parent的对象类型不是声明的类型或子类
                if (!isEmptyInput && !checkResult(inputObjects, i, requiredTypes[i])) {
                    throw new InvalidTopologyException(node, "one of upstream type does not meet the requirements");
                }
                if (isEmptyInput) {
                    tupleParents.setField(null, i);
                } else {
                    tupleParents.setField(((VisitResult) inputObjects.get(i)).resultObject, i);
                }
            }
            return tupleParents;
        } else {
            if (checkResult(inputObjects, 0, declaredInputType)) {
                return ((VisitResult) inputObjects.get(0)).resultObject;
            } else {
                // 输入不匹配需求
                throw new InvalidTopologyException(node, "upstream output cannot match downstream input");
            }
        }
    }

    protected List<VisitResult> normalizeOutput(Object config, Object buildOutput, Type declaredOutputType, C node) throws JobBuildException {
        List outputAsList = new ArrayList();
        if (buildOutput instanceof Tuple) {
            for (int i = 0; i < ((Tuple) buildOutput).getArity(); i++) {
                Object outputItem = ((Tuple) buildOutput).getField(i);
                outputAsList.add(outputItem);
            }
        } else {
            outputAsList.add(buildOutput);
        }
        // if config is declared as OutputTableConfig, means some DataFrame should be createOrReplaceTempView
        if (config instanceof OutputTableConfig) {
            OutputTableConfig outputTableConfig = (OutputTableConfig) config;
            if (outputTableConfig.getSlotTableNames() != null) {
                for (Map.Entry<Integer, String> item : outputTableConfig.getSlotTableNames().entrySet()) {
                    Integer slot = item.getKey();
                    Object outputItem = outputAsList.get(slot);
                    registerTable(outputItem, item.getValue(), slot);
                }
            }
        }

        return toVisitResultList(outputAsList, declaredOutputType, node);
    }

    protected List<VisitResult> toVisitResultList(List resultList, Type declaredOutputType, C node) throws JobBuildException {
        List<VisitResult> visitResultList = new ArrayList<>();
        List<Type> outputTypes = null;
        if (ReflectUtil.isAssignableBetween(Tuple.class, declaredOutputType)) {
            if (!(declaredOutputType instanceof ParameterizedType)) {
                throw new InvalidTopologyException(node, "output of tuple type must be declared generic");
            }
            outputTypes = Arrays.asList(((ParameterizedType) declaredOutputType).getActualTypeArguments());
        } else {
            outputTypes = new ArrayList<>();
            outputTypes.add(declaredOutputType);
        }

        for (int i = 0; i < resultList.size(); i++) {
            visitResultList.add(new VisitResult(resultList.get(i), outputTypes.get(i)));
        }
        return visitResultList;
    }

    /**
     * 对比第index个parent的类型，是否与需要的类型requireType兼容
     */
    protected boolean checkResult(List parent, int index, Type requiredType) {
        boolean ok;
        if (index >= parent.size()) {
            return true;
        }
        VisitResult vr = (VisitResult) parent.get(index);
        // 先对比擦除泛型之后是否匹配
        ok = ReflectUtil.isAssignableBetween(requiredType, vr.resultType);
        if (!ok) {
            return false;
        }

        // 如果需要的类型本身不是泛型，直接就可以了
        if (requiredType instanceof Class) {
            return true;
        }

        // 到这里resultType必须是泛型了
        if (vr.resultType instanceof Class) {
            return false;
        }
        ParameterizedType requiredParameterizedType = (ParameterizedType) requiredType;
        Type[] requiredInnerTypes = requiredParameterizedType.getActualTypeArguments();
        ParameterizedType resultParameterizedType = (ParameterizedType) vr.resultType;
        Type[] resultInnnerTypes = resultParameterizedType.getActualTypeArguments();
        if (requiredInnerTypes.length != resultInnnerTypes.length) {
            return false;
        }

        // 进一步对比每个位置上的泛型参数是否一致
        for (int i = 0; i < resultInnnerTypes.length; i++) {
            // TODO: [?] 这里实际并不是严格相同的类型，而是允许requiredInnerTypes是resultInnnerTypes的基类，是否合理
            if (!ReflectUtil.isAssignableBetween(requiredInnerTypes[i], resultInnnerTypes[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * wrap the result object ant the object type during visit since the generic type info will be erased, so we have to passing the result object as well as the type resultType
     * should be Class or ParameterizedType
     */
    static final class VisitResult {

        public final Object resultObject;

        public final Type resultType;

        public Integer refCount; // 增加引用计数，以实现SparkCacheableJobBuilder

        public SparkJobNode mergedNode;

        public SparkMergeableJobBuilder mergedBuilder;

        public VisitResult(Object resultObject, Type resultType) {
            this.resultObject = resultObject;
            this.resultType = resultType;
            this.refCount = 0;
        }
    }
}
