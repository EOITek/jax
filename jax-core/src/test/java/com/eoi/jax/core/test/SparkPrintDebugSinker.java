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

package com.eoi.jax.core.test;

import com.eoi.jax.api.EmptyConfig;
import com.eoi.jax.api.SparkDebugSinker;
import com.eoi.jax.api.SparkDebugSinkerMeta;
import com.eoi.jax.api.SparkEnvironment;
import com.eoi.jax.common.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Map;

public class SparkPrintDebugSinker implements SparkDebugSinker<EmptyConfig> {

    @Override
    public boolean supportDataFrame() {
        return true;
    }

    @Override
    public boolean supportRdd() {
        return true;
    }

    @Override
    public void sinkDataFrame(SparkEnvironment context, Dataset<Row> df, EmptyConfig config, SparkDebugSinkerMeta meta) {
        System.out.println(">>>>>>>>> " + meta.metaConfig.getJobEntry() + " - " + meta.metaConfig.getJobId() + " - " + meta.slotIndex + " <<<<<<<<<<<<<<<<<<");
        df.show();
    }

    @Override
    public void sinkRdd(SparkEnvironment context, RDD<Map<String, Object>> rdd, EmptyConfig config, SparkDebugSinkerMeta meta) {
        System.out.println(">>>>>>>>> " + meta.metaConfig.getJobEntry() + " - " + meta.metaConfig.getJobId() + " - " + meta.slotIndex + " <<<<<<<<<<<<<<<<<<");
        rdd.toJavaRDD().collect().forEach(i -> {
            try {
                System.out.println(JsonUtil.encode(i));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public EmptyConfig configure(Map<String, Object> mapConfig) throws Throwable {
        return new EmptyConfig();
    }
}
