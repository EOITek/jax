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

package com.eoi.jax.api;

import com.eoi.jax.api.tuple.Tuple2;

import java.util.Map;

/**
 * Spark Pipeline中实现可合并的Job的接口
 * 可合并的Job需要实现这个接口从而获得框架合并Job执行的能力
 * 目前可合并的Job必须具备以下条件
 * 1. 必须是一个SparkProcessJobBuilder
 * 2. 必须只有一个输入和输出，通常这个输入和输出都是DataFrame
 *
 * 框架实现的时候，采用类似reduce的方式，将多个可以互相合并的Job两两合并成一个，最终在不能合并的时候，统一执行已经合并的Job
 */
public interface SparkMergeableJobBuilder {
    /**
     * 判断当前Job是否能与下一个同样是SparkMergeableJobBuilder的Job合并
     * @param currentConfig 当前Job的配置项
     * @param next 下一个SparkMergeableJobBuilder
     * @param nextConfig 下一个Job的配置
     * @return 返回是否能够合并，如果返回false，框架将不会合并两个Job
     */
    boolean canMergeWith(
            Map<String, Object> currentConfig, SparkMergeableJobBuilder next, Map<String, Object> nextConfig);

    /**
     * 执行合并操作，并返回合并后的SparkMergeableJobBuilder和配置
     * @param currentConfig 当前Job的配置项
     * @param next 下一个SparkMergeableJobBuilder
     * @param nextConfig 下一个Job的配置
     * @return 并返回合并后的SparkMergeableJobBuilder和配置
     */
    Tuple2<SparkMergeableJobBuilder, Map<String, Object>> mergeWith(
            Map<String, Object> currentConfig, SparkMergeableJobBuilder next, Map<String, Object> nextConfig);
}
