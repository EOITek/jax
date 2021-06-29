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

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.flink.job.common.AviatorUtil;
import com.eoi.jax.flink.job.common.TimestampConvertJobConfig;
import com.eoi.jax.flink.job.common.TimestampConvertUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_AVG;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_CALC;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_COLLECT;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_COUNT;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_FIRST;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_LAST;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_MAX;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_MIN;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_SUM;
import static com.eoi.jax.flink.job.process.WindowAggJobConfig.KEY_SPLITTER;
import static com.eoi.jax.flink.job.process.WindowAggJobConfig.WINDOW_END;
import static com.eoi.jax.flink.job.process.WindowAggJobConfig.WINDOW_TYPE_GLOBAL;

public class WindowProcessFunction extends ProcessWindowFunction<Map<String, WindowAggAccItem>, Map<String, Object>, String, TimeWindow> {

    private static final Logger LOG = LoggerFactory.getLogger(WindowProcessFunction.class);

    private WindowAggJobConfig config;
    private TimestampConvertUtil convertUtil;

    public WindowProcessFunction(WindowAggJobConfig config) {
        this.config = config;
    }

    @Override
    public void process(String groupStr,
                        Context context,
                        Iterable<Map<String, WindowAggAccItem>> inputItems,
                        Collector<Map<String, Object>> out) throws Exception {
        Map<String, WindowAggAccItem> outputItems = inputItems.iterator().next();
        if (outputItems == null || outputItems.isEmpty()) {
            return;
        }

        Map<String, Object> outMap = new HashMap<>();
        // 输出聚合统计值
        for (String outputFieldName : outputItems.keySet()) {
            WindowAggAccItem item = outputItems.get(outputFieldName);
            String aggMethod = item.getAggMethod();
            Object value = null;

            if (AGG_METHOD_LAST.equals(aggMethod)) {
                value = item.getLast();
            } else if (AGG_METHOD_FIRST.equals(aggMethod)) {
                value = item.getFirst();
            } else if (AGG_METHOD_MAX.equals(aggMethod)) {
                value = item.getMax();
            } else if (AGG_METHOD_MIN.equals(aggMethod)) {
                value = item.getMin();
            } else if (AGG_METHOD_COUNT.equals(aggMethod)) {
                value = item.getCount();
            } else if (AGG_METHOD_SUM.equals(aggMethod)) {
                value = item.getSum();
            } else if (AGG_METHOD_AVG.equals(aggMethod)) {
                if (item.getCount() > 0) {
                    double avg = item.getSum() / item.getCount();
                    if (item.getScale() != null && item.getScale() > 0) {
                        avg = NumberUtil.round(avg, item.getScale()).doubleValue();
                    }
                    value = avg;
                }
            } else if (AGG_METHOD_COLLECT.equals(aggMethod)) {
                if (!StrUtil.isEmpty(item.getCollectType()) && item.getLimitCount() != null && item.getLimitCount() > 0) {
                    int listSize = item.getCollectList().size();
                    if ("top".equals(item.getCollectType())) {
                        value = new ArrayList<>(item.getCollectList().subList(0, Math.min(item.getLimitCount(), listSize)));
                    }
                    if ("bottom".equals(item.getCollectType())) {
                        value = new ArrayList<>(item.getCollectList().subList(Math.max(listSize - item.getLimitCount(), 0), listSize));
                    }
                    if ("random".equals(item.getCollectType())) {
                        value = getRandomItemsFromList(item.getCollectList(), item.getLimitCount());
                    }
                } else {
                    value = item.getCollectList();
                }
            }
            outMap.put(outputFieldName, value);
        }

        // 输出窗口时间
        if (!WINDOW_TYPE_GLOBAL.equals(config.getWindowType())) {
            long windowTime = context.window().getStart();
            if (WINDOW_END.equals(config.getWindowTimeOffsetType())) {
                windowTime = context.window().getEnd();
            }
            outMap.put(config.getWindowTimeColumn(), convertUtil.formatDateTime(new DateTime(windowTime)));
        }

        // 输出分组字段
        if (!StrUtil.isEmpty(groupStr)) {
            String [] groupValues = groupStr.split(KEY_SPLITTER);
            for (int i = 0; i < groupValues.length; i++) {
                String groupKey = config.getGroupKeys().get(i);
                outMap.put(groupKey, groupValues[i]);
            }
        }

        // 通过表达式计算变量
        for (AggDef aggDef : config.getAggDefs()) {
            if (AGG_METHOD_CALC.equals(aggDef.getAggMethod()) && !StrUtil.isEmpty(aggDef.getCalc())) {
                Object calcValue = AviatorUtil.eval(aggDef.getCalc(), outMap);
                outMap.put(aggDef.getOutputFieldName(), calcValue);
            }
        }

        out.collect(outMap);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        TimestampConvertJobConfig convertConfig = new TimestampConvertJobConfig();
        convertConfig.setToTimezone(config.getWindowTimezone());
        convertConfig.setToFormat(config.getWindowTimeFormat());
        convertConfig.setToLocale(config.getWindowTimeLocale());
        convertUtil = new TimestampConvertUtil(convertConfig);
    }

    private List<Map<String,Object>> getRandomItemsFromList(List<Map<String,Object>> list, int number) {
        Random rand = new Random();

        List<Map<String,Object>> origin = new ArrayList<>(list);
        List<Map<String,Object>> results = new ArrayList<>();
        for (int i = 0; i < Math.min(number,list.size()); i++) {
            int randomIndex = rand.nextInt(origin.size());
            results.add(origin.get(randomIndex));
            origin.remove(randomIndex);
        }
        return results;
    }
}
