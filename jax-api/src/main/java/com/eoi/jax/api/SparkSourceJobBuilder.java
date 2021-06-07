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

public interface SparkSourceJobBuilder<O, C> extends BatchSourceBuilder<O, C> {

    /**
     * @param context spark env
     * @param config 配置类
     * @return 输出对象
     * @throws Throwable 异常
     */
    O build(SparkEnvironment context, C config) throws Throwable;
}
