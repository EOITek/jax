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

package com.eoi.jax.flink.job.process;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.types.Row;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableToStreamMapFunction extends RichMapFunction<Row, Map<String, Object>> {
    List<String> fields;
    // metric
    private transient MetricGroup group;
    private transient Counter outCounter;
    private static final String GROUP_NAME = "tableToStream";
    private static final String OUT_COUNTER_NAME = "output.counter";

    public TableToStreamMapFunction(List<String> fields) {
        this.fields = fields;
    }

    @Override
    public Map<String, Object> map(Row value) throws Exception {
        Map<String, Object> newMap = new HashMap<>();
        int num = Math.min(value.getArity(), fields.size());
        for (int i = 0; i < num; i++) {
            Object fieldValue = value.getField(i);
            if (fields.get(i) != null) {
                newMap.put(fields.get(i), fieldValue);
            }
        }
        outCounter.inc();
        return newMap;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        group = getRuntimeContext().getMetricGroup().addGroup(GROUP_NAME);
        outCounter = group.counter(OUT_COUNTER_NAME);
    }
}
