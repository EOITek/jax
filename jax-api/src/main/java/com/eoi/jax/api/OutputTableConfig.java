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
 * 当Job的部分输出需要命名表名的时候，Job配置需要实现该接口
 */
public interface OutputTableConfig {
    /**
     * 返回一个Map，key是输出的slot编号，value是表名字
     * @return 一个Map，key是输出的slot编号，value是表名字
     */
    Map<Integer, String> getSlotTableNames();
}
