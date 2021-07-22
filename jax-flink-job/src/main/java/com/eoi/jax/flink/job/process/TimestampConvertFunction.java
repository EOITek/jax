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

import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.common.JsonUtil;
import com.eoi.jax.flink.job.common.TimestampConvertJobConfig;
import com.eoi.jax.flink.job.common.TimestampConvertUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TimestampConvertFunction extends ProcessFunction<Map<String, Object>, Map<String, Object>> {

    private static final Logger LOG = LoggerFactory.getLogger(TimestampConvertFunction.class);

    TimestampConvertJobConfig jobConfig;
    TimestampConvertUtil convertUtil;

    private transient MetricGroup timestampConvertMetricGroup;
    private transient Counter totalCounter;
    private transient Counter invalidCounter;
    private transient Counter successCounter;
    final OutputTag<Map<String, Object>> errorTag;

    public TimestampConvertFunction(TimestampConvertJobConfig jobConfig, OutputTag<Map<String, Object>> errorTag) throws JobConfigValidationException {
        this.jobConfig = jobConfig;
        this.errorTag = errorTag;

        this.convertUtil = new TimestampConvertUtil(jobConfig);
    }

    @Override
    public void processElement(Map<String, Object> value,
                               Context ctx,
                               Collector<Map<String, Object>> out) throws Exception {
        try {
            totalCounter.inc();
            DateTime dt = parseDateTimeFromSource(value);
            if (dt != null) {
                Map<String, Object> result = new HashMap<>(value);
                result.put(jobConfig.getOutputTimestampField(), formatDateTime(dt));
                out.collect(result);
                successCounter.inc();
            } else {
                invalidCounter.inc();
                collectError(value, ctx, "unknown time format, convert to null");
            }
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("timestamp function error, " + JsonUtil.safeEncodePretty(value), ex);
            }
            collectError(value, ctx, ex.getMessage());
        }
    }

    private void collectError(Map<String, Object> value, Context ctx, String errMsg) {
        Map<String, Object> result = new HashMap<>(value);
        result.put("error_msg", errMsg);
        ctx.output(errorTag, result);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        timestampConvertMetricGroup = getRuntimeContext().getMetricGroup().addGroup(TIMESTAMP_CONVERT_METRIC_GROUP);
        totalCounter = timestampConvertMetricGroup.counter(TOTAL_COUNT);
        successCounter = timestampConvertMetricGroup.counter(SUCCESS_COUNT);
        invalidCounter = timestampConvertMetricGroup.counter(INVALID_COUNT);
    }

    public DateTime parseDateTimeFromSource(Map<String, Object> value) {
        return convertUtil.parseDateTimeFromSource(value.get(jobConfig.getTimestampField()));
    }

    public Object formatDateTime(DateTime dt) {
        return convertUtil.formatDateTime(dt);
    }

    public static final String TIMESTAMP_CONVERT_METRIC_GROUP = "timestampConvertJob";
    public static final String TOTAL_COUNT = "total";
    public static final String INVALID_COUNT = "invalid";
    public static final String SUCCESS_COUNT = "success";

}
