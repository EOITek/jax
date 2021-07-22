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

import com.eoi.jax.flink.job.common.AviatorUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Map;

public class FilterFunction extends ProcessFunction<Map<String, Object>, Map<String, Object>> {

    private FilterJobConfig config;
    final OutputTag<Map<String, Object>> falseTag;

    // metric
    private transient MetricGroup group;
    private transient Counter trueCounter;
    private transient Counter falseCounter;
    private static final String GROUP_NAME = "filter";
    private static final String TRUE_COUNTER_NAME = "filter_true.counter";
    private static final String FALSE_COUNTER_NAME = "filter_false.counter";

    public FilterFunction(FilterJobConfig config, OutputTag<Map<String, Object>> falseTag) {
        this.config = config;
        this.falseTag = falseTag;
    }

    @Override
    public void processElement(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
        boolean passFiler = AviatorUtil.eval(config.getRule(), value, false);
        if (passFiler) {
            out.collect(value);
            trueCounter.inc();
        } else {
            ctx.output(falseTag, value);
            falseCounter.inc();
        }
    }

    @Override
    public void open(Configuration parameters) {
        group = getRuntimeContext().getMetricGroup().addGroup(GROUP_NAME);
        trueCounter = group.counter(TRUE_COUNTER_NAME);
        falseCounter = group.counter(FALSE_COUNTER_NAME);
    }
}
