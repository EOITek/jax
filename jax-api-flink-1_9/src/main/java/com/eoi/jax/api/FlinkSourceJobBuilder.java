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
 * FlinkSourceJobBuilder 定义了source类型的job的基础接口, 所有的source类型的job构建器，必须实现这个接口。
 *
 * <p>实现类需要具体声明 OUT 和 C 的类型，这至关重要。整个编排器将相对严格的校验各个job之间的串联能否做到类型匹配，
 * 从而通过静态检查规避一些由于类型不匹配而带来的运行时问题。
 *
 * <p>实现类在build方法中大概率要通过addSource方法返回一个DataStream或者Table
 *
 * @param <O> 声明输出类型。对于输出类型，推荐将类型声明为更具体一些。 例如 DataStream&lt;Map&lt;String, Object>>，虽然大多数情况下声明为DataStream也是可以工作的。 宽泛的输出类型更容易造成类型不匹配
 * @param <C>   配置类的类型, {@link Builder} 接口中的 configure 负责产生这个配置类
 */
public interface FlinkSourceJobBuilder<O, C> extends StreamingSourceBuilder<O, C> {

    /**
     * build方法用于通过context和config，构建出可向下游传递的对象
     *
     * @param context    {@link FlinkEnvironment} 中包含必要的流对象和表对象
     * @param config     配置对象, 保存构建需要的配置, 会通过configure接口实例化
     * @param metaConfig job的元数据信息，包含jobId, jobEntry等信息。
     * @return 可向下游传递的对象，通常是DataStream或者Table
     * @throws Exception 如果出现严重错误，请抛出异常
     */
    O build(FlinkEnvironment context, C config, JobMetaConfig metaConfig) throws Throwable;
}
