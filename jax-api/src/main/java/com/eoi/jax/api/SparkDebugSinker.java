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

import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Map;

/**
 * SparkDebugSinker用于在debug模式下，为当前节点的构造生成一个sink 用户可以选择将某个节点(source/process类型)设置开启debug，系统会自动将这个节点的输出连接到一个sink上
 * 节点的输出可能是流，也可能是批，所以要同时支持对流和批的sink 不同的sink肯定有不同的配置需求，常见的sink可以是kafka, jdbc, log等 用户配置示例如下： { "id": "ripple", "entry":
 * "com.xx.xx.xxx.xx.XXXJob", "config": { ... }, "opts": { "enableDebug": true, "debugEntry":
 * "xx.xx.xx.xx.SparkDebugSinker", "debugConfig": { ... } } } opts.enableDebug 开启对节点`ripple`开启debug opts.debugEntry
 * 指定SparkDebugSinker的实现类 opts.debugConfig 设置opts.debugEntry的配置, 作为configure的map参数传入SparkDebugSinker的实现类
 */
public interface SparkDebugSinker<C> extends Builder<C> {

    /**
     * whether sinker support DataFrame debug sinker
     *
     * @return return if support DataFrame
     */
    boolean supportDataFrame();

    /**
     * whether sinker support Rdd debug sinker
     *
     * @return return if support RDD
     */
    boolean supportRdd();

    /**
     * sink a DataStream for debug
     *
     * @param context Spark environment
     * @param df input DataFrame
     * @param config input configuration
     * @param meta input job metadata
     */
    void sinkDataFrame(SparkEnvironment context, Dataset<Row> df, C config, SparkDebugSinkerMeta meta);

    /**
     * sink a Table for debug
     *
     * @param context Spark environment
     * @param rdd input RDD
     * @param config input configuration
     * @param meta input job metadata
     */
    void sinkRdd(SparkEnvironment context, RDD<Map<String, Object>> rdd, C config, SparkDebugSinkerMeta meta);
}
