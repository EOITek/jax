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

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.storage.StorageLevel;

/**
 * 表示一个DataFrame输出的SouceBuilder
 * 由于确定了输出，所以默认实现了SparkCacheableJobBuilder
 * @param <C> 配置类
 */
public interface SparkSourceTableBuilder<C> extends SparkSourceJobBuilder<Dataset<Row>, C>, SparkCacheableJobBuilder {
    @Override
    default void recommendCache(Object outputObject, Integer slot, StorageLevel storageLevel, Integer refCount) {
        ((Dataset<Row>)outputObject).persist(storageLevel);
    }
}
