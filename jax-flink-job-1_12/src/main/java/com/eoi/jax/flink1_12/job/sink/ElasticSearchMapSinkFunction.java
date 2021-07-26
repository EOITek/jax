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

package com.eoi.jax.flink1_12.job.sink;

import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.flink.job.common.ElasticSearchIndexFormatter;
import com.eoi.jax.flink.job.common.TimestampConvertJobConfig;
import com.eoi.jax.flink.job.common.TimestampConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.joda.time.DateTime;

import javax.annotation.Nullable;

import java.util.Map;

public class ElasticSearchMapSinkFunction implements ElasticsearchSinkFunction<Map<String, Object>> {

    private ElasticSearchIndexFormatter formatter;
    private String index;
    private String indexType;
    private String docIdField;
    private String timeFieldName;

    private static final String METRIC_GROUP_NAME = "es_metric_group";
    private static final String METRIC_SINK_TOTAL_COUNT = "sink.total";

    private transient MetricGroup esMetricGroup;
    private transient Counter totalCounter;

    TimestampConvertUtil convertUtil;

    ElasticSearchMapSinkFunction(String indexPattern,
                                 @Nullable String indexType,
                                 @Nullable String docIdField,
                                 String timeFieldName,
                                 String timeFormat,
                                 String timeLocale) throws JobConfigValidationException {
        this.formatter = new ElasticSearchIndexFormatter(indexPattern);
        this.indexType = indexType;
        this.docIdField = docIdField;
        this.timeFieldName = timeFieldName;

        TimestampConvertJobConfig convertConfig = new TimestampConvertJobConfig();
        convertConfig.setFromFormat(timeFormat);
        convertConfig.setFromLocale(timeLocale);
        convertUtil = new TimestampConvertUtil(convertConfig);
    }

    @Override
    public void process(Map<String, Object> element, RuntimeContext ctx, RequestIndexer indexer) {
        DateTime eventTime = convertUtil.parseDateTimeFromSource(element.get(this.timeFieldName));

        index = formatter.getIndex(element, eventTime);

        IndexRequest ir = Requests.indexRequest(index).source(element);
        if (!StringUtils.isEmpty(indexType)) {
            ir = ir.type(indexType);
        }
        if (docIdField != null && element.get(docIdField) != null) {
            ir = ir.id(element.get(docIdField).toString());
        }
        indexer.add(ir);

        if (null == esMetricGroup) {
            esMetricGroup = ctx.getMetricGroup().addGroup(METRIC_GROUP_NAME);
            totalCounter = esMetricGroup.counter(METRIC_SINK_TOTAL_COUNT);
        }
        totalCounter.inc();
    }
}
