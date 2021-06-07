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

import java.util.Map;

/**
 * 所有Builder的基类型，主要定义配置处理的接口
 *
 * @param <C> 输出的配置类型
 */
public interface Builder<C> {
    /**
     * 将mapConfig反序列化成一个配置类
     *
     * @param mapConfig 传入的配置项
     * @return 配置类
     */
    C configure(Map<String, Object> mapConfig) throws Throwable;
}
