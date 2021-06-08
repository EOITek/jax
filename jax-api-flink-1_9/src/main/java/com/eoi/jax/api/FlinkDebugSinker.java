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

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;

/**
 * FlinkDebugSinker用于在debug模式下，为当前节点的构造生成一个sink 用户可以选择将某个节点(source/process类型)设置开启debug，系统会自动将这个节点的输出连接到一个sink上 节点的输出可能是流，也可能是批，所以要同时支持对流和批的sink 不同的sink肯定有不同的配置需求，常见的sink可以是kafka,
 * jdbc, log等 用户配置示例如下： { "id": "ripple", "entry": "com.xx.xx.xxx.xx.XXXJob", "config": { ... }, "opts": { "enableDebug": true, "debugEntry": "xx.xx.xx.xx.FlinkDebugSinker",
 * "debugConfig": { ... } } } opts.enableDebug 开启对节点`ripple`开启debug opts.debugEntry 指定FlinkDebugSinker的实现类 opts.debugConfig 设置opts.debugEntry的配置,
 * 作为configure的map参数传入FlinkDebugSinker的实现类
 */
public interface FlinkDebugSinker<C> extends StreamingDebugSinker<C> {

    /**
     * whether sinker support DataStream debug sinker
     */
    boolean supportDataStream();

    /**
     * whether sinker support Table debug sinker
     */
    boolean supportTable();

    /**
     * sink a DataStream for debug
     */
    void sinkDataStream(FlinkEnvironment context, DataStream stream, C config, FlinkDebugSinkerMeta meta);

    /**
     * sink a Table for debug
     */
    void sinkTable(FlinkEnvironment context, Table table, C config, FlinkDebugSinkerMeta meta);
}
