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

package com.eoi.jax.core.spark.debug;

import com.eoi.jax.api.SparkDebugSinker;
import com.eoi.jax.api.SparkDebugSinkerMeta;
import com.eoi.jax.api.SparkEnvironment;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.common.JsonUtil;
import com.eoi.jax.core.WebSocketClient;
import com.eoi.jax.core.WebsocketDebugSinkerConfig;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Job(
        name = "WebsocketDebugSinker",
        display = "WebsocketDebugSinker",
        description = "用于调试spark job"
)
public class WebsocketDebugSinker implements SparkDebugSinker<WebsocketDebugSinkerConfig> {
    private static final String F_JOB_ID = "job_id";
    private static final String F_SLOT = "slot";
    private static final String F_MESSAGE = "message";

    @Override
    public boolean supportDataFrame() {
        return true;
    }

    @Override
    public boolean supportRdd() {
        return true;
    }

    @Override
    public void sinkDataFrame(SparkEnvironment context, Dataset<Row> df, WebsocketDebugSinkerConfig config, SparkDebugSinkerMeta meta) {
        JavaRDD<Map<String, Object>> rdd = df.toJavaRDD().map(row -> {
            Map<String, Object> map = new LinkedHashMap<>();
            String[] names = row.schema().fieldNames();
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                map.put(name, row.get(i));
            }
            return map;
        });
        sinkWebsocket(rdd, config, meta);
    }

    @Override
    public void sinkRdd(SparkEnvironment context, RDD<Map<String, Object>> rdd, WebsocketDebugSinkerConfig config, SparkDebugSinkerMeta meta) {
        sinkWebsocket(rdd.toJavaRDD(), config, meta);
    }

    @Override
    public WebsocketDebugSinkerConfig configure(Map<String, Object> mapConfig) throws Throwable {
        WebsocketDebugSinkerConfig config = new WebsocketDebugSinkerConfig();
        ParamUtil.configJobParams(config, mapConfig);
        return config;
    }

    private void sinkWebsocket(JavaRDD<Map<String, Object>> rdd, WebsocketDebugSinkerConfig config, SparkDebugSinkerMeta meta) {
        final String uri = config.getUri();
        rdd.foreachPartition(partition -> {
            WebSocketClient client = new WebSocketClient(new URI(uri));
            try {
                client.connectBlocking(10, TimeUnit.SECONDS);
                while (partition.hasNext()) {
                    Map<String, Object> value = partition.next();
                    Map<String, Object> result = new HashMap<>();
                    result.put(F_JOB_ID, meta.metaConfig.getJobId());
                    result.put(F_SLOT, meta.slotIndex);
                    result.put(F_MESSAGE, JsonUtil.encode(value));
                    client.send(JsonUtil.encode(result));
                }
            } finally {
                client.close();
            }
        });
    }
}
