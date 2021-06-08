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

package com.eoi.jax.core.flink.debug;

import com.eoi.jax.api.FlinkDebugSinker;
import com.eoi.jax.api.FlinkDebugSinkerMeta;
import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.common.JsonUtil;
import com.eoi.jax.core.WebsocketDebugSinkerConfig;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Job(
        name = "WebsocketDebugSinker",
        display = "WebsocketDebugSinker",
        description = "用于调试flink job"
)
public class WebsocketDebugSinker implements FlinkDebugSinker<WebsocketDebugSinkerConfig> {

    private static final String F_JOB_ID = "job_id";
    private static final String F_SLOT = "slot";
    private static final String F_MESSAGE = "message";

    @Override
    public boolean supportDataStream() {
        return true;
    }

    @Override
    public boolean supportTable() {
        return true;
    }

    @Override
    public void sinkDataStream(FlinkEnvironment context, DataStream stream, WebsocketDebugSinkerConfig config, FlinkDebugSinkerMeta meta) {
        // 把原始的Map变成message(json格式)，增加元数据字段
        DataStream<String> normalizedStream = normalizeStream(stream, meta);
        normalizedStream.addSink(new WebSocketTextSinkFunction(config.getUri()));
    }

    @Override
    public void sinkTable(FlinkEnvironment context, Table table, WebsocketDebugSinkerConfig config, FlinkDebugSinkerMeta meta) {
        // 先把Table转成Stream，然后在normalize
        DataStream<Row> rowStream = context.tableEnv.toAppendStream(table, Row.class);
        DataStream mapStream = tableToStream(rowStream, Arrays.asList(table.getSchema().getFieldNames()));
        DataStream<String> normalizedStream = normalizeStream(mapStream, meta);
        normalizedStream.addSink(new WebSocketTextSinkFunction(config.getUri()));
    }

    @Override
    public WebsocketDebugSinkerConfig configure(Map<String, Object> mapConfig) throws Throwable {
        WebsocketDebugSinkerConfig config = new WebsocketDebugSinkerConfig();
        ParamUtil.configJobParams(config, mapConfig);
        return config;
    }

    private DataStream<String> normalizeStream(DataStream stream, FlinkDebugSinkerMeta meta) {
        return stream.map((MapFunction<Object, String>) value -> {
            Map<String, Object> result = new HashMap<>();
            result.put(F_JOB_ID, meta.metaConfig.getJobId());
            result.put(F_SLOT, meta.slotIndex);
            result.put(F_MESSAGE, JsonUtil.encode(value));
            return JsonUtil.encode(result);
        }).returns(new TypeHint<String>() {
        });
    }

    private DataStream<Map<String, Object>> tableToStream(DataStream<Row> stream, List<String> fields) {
        return stream.map((MapFunction<Row, Map<String, Object>>) value -> {
            int num = Math.min(value.getArity(), fields.size());
            Map<String, Object> result = new HashMap<>();
            for (int i = 0; i < num; i++) {
                Object fieldValue = value.getField(i);
                if (fields.get(i) != null) {
                    result.put(fields.get(i), fieldValue);
                }
            }
            return result;
        }).returns(new TypeHint<Map<String, Object>>() {
        });
    }
}
