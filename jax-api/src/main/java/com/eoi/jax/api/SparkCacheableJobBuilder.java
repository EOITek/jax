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

import org.apache.spark.storage.StorageLevel;

/**
 * 支持cache的Job实现
 * jax会在需要的时候调用job的recommendCache，通常有两种情况会调用
 * 1. 当前的job的输出的引用计数>1
 * 2. 当前的job被用户指定为输出结果需要cache
 */
public interface SparkCacheableJobBuilder {
    /**
     * @param outputObject 当前已经经过计算的结果，实际就是当前job输出的某个slot上的结果，一般是RDD或DataFrame
     * @param slot 输出属于第几个solt
     * @param storageLevel spark定义的存储级别
     * @param refCount 被引用的次数
     */
    void recommendCache(Object outputObject, Integer slot, StorageLevel storageLevel, Integer refCount);
}
