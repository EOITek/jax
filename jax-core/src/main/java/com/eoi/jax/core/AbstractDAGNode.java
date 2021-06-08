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

/**
 * DAGNode，用于AbstractDAGVisitor的Node必须至少实现这个类
 */
// CHECKSTYLE.OFF:
public abstract class AbstractDAGNode {
    /**
     * 返回唯一的节点号，用于唯一定位节点
     * @return 节点号
     */
    public abstract String getId();
}
// CHECKSTYLE.ON:
