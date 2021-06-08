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

import com.eoi.jax.api.BatchSourceBuilder;
import com.eoi.jax.api.Builder;
import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.StreamingProcessBuilder;
import com.eoi.jax.api.StreamingSourceBuilder;
import com.eoi.jax.common.PythonHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * this implementation of DAGVisitor only run the validate() of the Job witch support ConfigValidatable call check() to visit DAG and run validate()
 */
// CHECKSTYLE.OFF:
public class CheckConfigDAGBuilder extends AbstractDAGVisitor<CheckConfigNode> {
    // CHECKSTYLE.ON:

    private List<JobBuildException> invalidations;
    private boolean compatible;

    public CheckConfigDAGBuilder() {
        this.invalidations = new ArrayList<>();
        this.compatible = true;
    }

    public void check() throws Throwable {
        getChain();
        if (isInvalid()) {
            throw invalidations.get(0);
        }
    }

    public List<JobBuildException> getInvalidations() {
        return invalidations;
    }

    public boolean isInvalid() {
        return !invalidations.isEmpty();
    }

    public boolean isCompatible() {
        return compatible;
    }


    @Override
    protected List visit(CheckConfigNode node, List parents) {
        String className = node.getEntry();

        //头节点不能为非source
        if ((parents == null || parents.isEmpty()) && PythonHelper.isPython(className)) {
            invalidations
                    .add(new JobBuildException("failed to visit instance of class " + className, new NoSourceNodeException("the first node must be a source job"), node.getId()));
        }

        if (!PythonHelper.isPython(className)) {
            Object jobBuilder;
            try {
                if (node.getLoader() != null) {
                    jobBuilder = Class.forName(className, true, node.getLoader()).newInstance();
                } else {
                    jobBuilder = Class.forName(className).newInstance();
                }

                if (parents == null || parents.isEmpty()) {
                    if (!(jobBuilder instanceof BatchSourceBuilder) && !(jobBuilder instanceof StreamingSourceBuilder)) {
                        throw new NoSourceNodeException("the first node must be a source job");
                    }
                }

                Builder builder = (Builder) jobBuilder;
                Object config = builder.configure(node.getConfig());
                // 只要有一个不兼容的node，pipeline就是不兼容了
                // 只有flink需要检测兼容性
                // 只有旧配置需要检测兼容性
                if (this.compatible
                        && (builder instanceof StreamingProcessBuilder)
                        && node.getLastConfig() != null
                ) {
                    Object lastConfig = builder.configure(node.getLastConfig());
                    // 检测配置兼容性
                    this.compatible = ((StreamingProcessBuilder) builder).compatibleWithLastConfig(
                            lastConfig,
                            config
                    );
                }
                // 检测配置合法性
                checkConfigValidatable(config);
            } catch (Throwable ex) {
                // 不直接抛出异常，防止中断图遍历
                invalidations.add(new JobBuildException("failed to visit instance of class " + className, ex).setInvalidJobId(node.getId()));
            }
        }

        //TODO
        return Arrays.asList(null, null, null, null, null, null, null, null, null, null);
    }

    private void checkConfigValidatable(Object config) throws JobConfigValidationException {
        if (config instanceof ConfigValidatable) {
            ((ConfigValidatable) config).validate();
        }
    }
}
