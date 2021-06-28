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

/**
 * FlinkProcessJobBuilder 定义了source类型的job的基础接口, 所有的process类型的job构建器，必须实现这个接口。
 *
 * <p>实现类需要具体声明 IN, OUT 和 C 的类型，这至关重要。整个编排器将相对严格的校验各个job之间的串联能否做到类型匹配，
 * 从而通过静态检查规避一些由于类型不匹配而带来的运行时问题。
 *
 * @param <IN>  声明输入类型，一般是DataStream或Table，如果希望定义多输入，使用Tuple。比如Tuple&lt;DataStream, DataStream>
 *              输入类型的声明原则上建议尽量宽泛一些，太严格的类型声明容易造成类型不匹配
 * @param <OUT> 声明输出类型。对于输出类型，推荐将类型声明为更具体一些。
 *              例如 DataStream&lt;Map&lt;String, Object>>，虽然大多数情况下声明为DataStream也是可以工作的。
 *              宽泛的输出类型更容易造成类型不匹配
 * @param <C>   配置类的类型, {@link Builder} 接口中的 configure 负责产生这个配置类
 */
public interface FlinkProcessJobBuilder<IN, OUT, C> extends StreamingProcessBuilder<IN, OUT, C> {
    /**
     * build方法用于通过context, in和config，构建出可向下游传递的对象
     *
     * @param context {@link FlinkEnvironment} 中包含必要的流对象和表对象
     * @param in      输入对象，上游输入的流对象或者表对象
     * @param config  配置对象, 保存构建需要的配置, 会通过configure接口实例化
     * @param metaConfig job的元数据信息，包含jobId, jobEntry等信息。
     * @return 可向下游传递的对象，通常是DataStream或者Table
     * @throws Exception 如果出现严重错误，请抛出异常
     */
    OUT build(FlinkEnvironment context, IN in, C config, JobMetaConfig metaConfig) throws Throwable;

    /**
     * 本方法用于验证当前config与上一次的config是否兼容，上层根据这个方法来确定是否使用savepoint
     * 默认实现为直接返回true，代表可以兼容。
     * @param lastConfig 上一次的config
     * @param currentConfig 本次的config
     */
    default boolean compatibleWithLastConfig(C lastConfig, C currentConfig) {
        return true;
    }
}
