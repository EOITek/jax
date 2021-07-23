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

package com.eoi.jax.flink.job.common;

import com.eoi.jax.common.JsonUtil;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.util.Collector;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hdfs sink map 主要把 event转成json或者是csv.
 */
public class FileSinkMapFunction extends RichFlatMapFunction<Map<String, Object>, String> {
    private List<String> fields;
    private String format;

    private MetricGroup transformMetricGroup;
    private Counter jsonEncodeErrorCounter;
    private Counter csvEncodeErrorCounter;
    public static final String GROUP_NAME = "fileSink";
    private static String METRIC_JSON_COUNT = "jsonEncodeErrorCounter";
    private static String METRIC_CSV_COUNT = "csvEncodeErrorCounter";


    public FileSinkMapFunction(List<String> fields, String format) {
        this.fields = fields;
        this.format = format;
    }

    private String generateCsvLine(Map<String, Object> event, List<String> header) {
        StringWriter output = new StringWriter();

        String[] values = new String[header.size()];

        try (ICsvListWriter listWriter = new CsvListWriter(output,
                CsvPreference.STANDARD_PREFERENCE)) {
            for (int i = 0; i < header.size(); i++) {
                Object x = event.get(header.get(i));
                if (x == null) {
                    values[i] = "";
                } else {
                    values[i] = String.valueOf(x);
                }
            }
            listWriter.write(values);
        } catch (IOException e) {
            csvEncodeErrorCounter.inc();
        }

        String s = output.toString();
        if (s.length() > 2) {
            return s.substring(0, s.length() - 2);
        }
        return s;
    }

    private String generateJsonLine(Map<String, Object> map) {
        Map<String, Object> newMap = new HashMap<>(fields.size());

        for (String field: fields) {
            newMap.put(field, map.get(field));
        }

        try {
            return JsonUtil.encode(newMap);
        } catch (Exception e) {
            jsonEncodeErrorCounter.inc();
        }
        return null;
    }

    @Override
    public void flatMap(Map<String, Object> value, Collector<String> out) throws Exception {
        String line = null;
        if (FileSinkJobConfig.DATA_FORMAT_JSON.equals(format)) {
            line = generateJsonLine(value);
        } else if (FileSinkJobConfig.DATA_FORMAT_CSV.equals(format)) {
            line = generateCsvLine(value, fields);
        }

        if (line != null) {
            out.collect(line);
        }
    }

    @Override
    public void open(Configuration parameters) {
        transformMetricGroup = getRuntimeContext().getMetricGroup().addGroup(GROUP_NAME);
        jsonEncodeErrorCounter = transformMetricGroup.counter(METRIC_JSON_COUNT);
        csvEncodeErrorCounter = transformMetricGroup.counter(METRIC_CSV_COUNT);
    }
}
