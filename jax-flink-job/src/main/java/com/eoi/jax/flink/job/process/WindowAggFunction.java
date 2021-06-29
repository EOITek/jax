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
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import com.eoi.jax.common.JsonUtil;
import com.eoi.jax.flink.job.common.AviatorUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_AVG;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_CALC;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_COLLECT;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_COUNT;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_FIRST;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_LAST;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_MAX;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_MIN;
import static com.eoi.jax.flink.job.process.AggDef.AGG_METHOD_SUM;


public class WindowAggFunction implements AggregateFunction<Map<String,Object>, Map<String, WindowAggAccItem>, Map<String, WindowAggAccItem>> {
    private static final Logger LOG = LoggerFactory.getLogger(WindowAggFunction.class);

    private WindowAggJobConfig config;
    private static MD5 md5 = SecureUtil.md5();

    public WindowAggFunction(WindowAggJobConfig config) {
        this.config = config;
    }

    @Override
    public Map<String, WindowAggAccItem> createAccumulator() {
        return new HashMap<>();
    }

    @Override
    public Map<String, WindowAggAccItem> add(Map<String, Object> value, Map<String, WindowAggAccItem> accumulator) {

        for (AggDef aggDef : config.getAggDefs()) {
            // Skip agg method: calc， 在WindowProcessFunction统一处理
            if (AGG_METHOD_CALC.equals(aggDef.getAggMethod())) {
                continue;
            }

            String fieldName = aggDef.getFieldName();
            String outputFieldName = aggDef.getOutputFieldName();
            String aggMethod = aggDef.getAggMethod();
            Object objValue = value.get(fieldName);

            // 聚合字段的值使用表达式计算得出
            if (!StrUtil.isEmpty(aggDef.getFieldExp())) {
                objValue = AviatorUtil.eval(aggDef.getFieldExp(),value, value.get(fieldName));
            }

            // 按过滤表达式过滤数据
            if (!StrUtil.isEmpty(aggDef.getFilterExp())) {
                Map<String,Object> temp = value;
                if (!StrUtil.isEmpty(aggDef.getFieldExp())) {
                    temp = new HashMap<>(value);
                    temp.put("__valueByExp__", objValue);
                }
                boolean passFilter = AviatorUtil.eval(aggDef.getFilterExp(),temp,false);
                if (!passFilter) {
                    continue;
                }
            }

            // 初始化累加器
            WindowAggAccItem accItem = accumulator.getOrDefault(outputFieldName, null);
            boolean isFirst = false;
            if (accItem == null) {
                accItem = new WindowAggAccItem();
                accItem.setAggMethod(aggMethod);
                accItem.setScale(aggDef.getScale());
                accItem.setLimitCount(aggDef.getLimitCount());
                accItem.setCollectType(aggDef.getCollectType());
                accItem.setDistinct(aggDef.isDistinct());
                accItem.setDistinctSet(new HashSet<>());
                isFirst = true;
            }

            // 检查是否重复(除了collect)
            if (aggDef.isDistinct() && objValue != null && !AGG_METHOD_COLLECT.equals(aggDef.getAggMethod())) {
                if (!accItem.getDistinctSet().contains(objValue.toString())) {
                    accItem.getDistinctSet().add(objValue.toString());
                } else {
                    continue;
                }
            }

            if (AGG_METHOD_COUNT.equals(aggMethod)) {
                // 如果字段名为 * 则无条件count，否则需要对应字段的值不为null才count
                if (objValue != null || "*".equals(fieldName)) {
                    if (accItem.getCount() != null) {
                        accItem.setCount(accItem.getCount() + 1);
                    } else {
                        accItem.setCount(1L);
                    }
                } else {
                    if (accItem.getCount() == null) {
                        accItem.setCount(0L);
                    }
                }
            } else if (AGG_METHOD_LAST.equals(aggMethod)) {
                accItem.setLast(objValue);
            } else if (AGG_METHOD_FIRST.equals(aggMethod)) {
                if (isFirst) {
                    accItem.setFirst(objValue);
                }
            } else if (AGG_METHOD_COLLECT.equals(aggMethod)) {
                if (isFirst) {
                    accItem.setCollectList(new ArrayList<>());
                }

                Map<String,Object> fieldValueMap = new HashMap<>();
                for (String collectFieldName : aggDef.getCollectFieldNames()) {
                    fieldValueMap.put(collectFieldName, value.get(collectFieldName));
                }

                // 检查是否重复
                String listMd5 = null;
                try {
                    listMd5 = this.md5.digestHex(JsonUtil.encode(fieldValueMap));
                } catch (JsonProcessingException ignore) {

                }

                if (listMd5 != null && !accItem.getDistinctSet().contains(listMd5)) {
                    accItem.getDistinctSet().add(listMd5);
                } else {
                    continue;
                }

                accItem.getCollectList().add(fieldValueMap);
            } else {
                // 数值类型统计
                if (objValue == null) {
                    LOG.error("objValue is null for number agg");
                    continue;
                }

                Double itemValue = null;
                try {
                    itemValue = NumberUtil.parseNumber(objValue.toString()).doubleValue();
                } catch (Exception ex) {
                    LOG.error("parse num error:" + objValue.toString(), ex);
                    continue;
                }

                if (AGG_METHOD_MAX.equals(aggMethod)) {
                    if (!isFirst) {
                        if (itemValue > accItem.getMax()) {
                            accItem.setMax(itemValue);
                        }
                    } else {
                        accItem.setMax(itemValue);
                    }
                } else if (AGG_METHOD_MIN.equals(aggMethod)) {
                    if (!isFirst) {
                        if (itemValue < accItem.getMin()) {
                            accItem.setMin(itemValue);
                        }
                    } else {
                        accItem.setMin(itemValue);
                    }
                } else if (AGG_METHOD_SUM.equals(aggMethod)) {
                    if (!isFirst) {
                        accItem.setSum(accItem.getSum() + itemValue);
                    } else {
                        accItem.setSum(itemValue);
                    }
                } else if (AGG_METHOD_AVG.equals(aggMethod)) {
                    if (!isFirst) {
                        accItem.setCount(accItem.getCount() + 1);
                        accItem.setSum(accItem.getSum() + itemValue);
                    } else {
                        accItem.setCount(1L);
                        accItem.setSum(itemValue);
                    }
                }
            }
            accumulator.put(outputFieldName, accItem);
        }

        return accumulator;
    }

    @Override
    public Map<String, WindowAggAccItem> getResult(Map<String, WindowAggAccItem> accumulator) {
        return accumulator;
    }

    @Override
    public Map<String, WindowAggAccItem> merge(Map<String, WindowAggAccItem> a,  Map<String, WindowAggAccItem> b) {
        Map<String, List<WindowAggAccItem>> mergeMap = new HashMap<>();
        Map<String, WindowAggAccItem> result = new HashMap<>();

        // 添加所有a 和 有共同的b
        for (String fieldName : a.keySet()) {
            List<WindowAggAccItem> accItems = new ArrayList<>();
            accItems.add(a.get(fieldName));
            if (b.containsKey(fieldName)) {
                accItems.add(b.get(fieldName));
            }
            mergeMap.put(fieldName, accItems);
        }

        // 添加只有b的
        for (String fieldName : b.keySet()) {
            if (!mergeMap.containsKey(fieldName)) {
                List<WindowAggAccItem> accItems = new ArrayList<>();
                accItems.add(b.get(fieldName));
                mergeMap.put(fieldName, accItems);
            }
        }

        //  只有一项直接取值，有两项的merge a和b
        for (String key : mergeMap.keySet()) {
            List<WindowAggAccItem> accItems = mergeMap.get(key);
            if (accItems.size() > 1) {
                WindowAggAccItem item1 = accItems.get(0);
                WindowAggAccItem item2 = accItems.get(1);
                String aggMethod = item1.getAggMethod();

                // merge item2 to item1
                if (AGG_METHOD_LAST.equals(aggMethod)) {
                    item1.setLast(item2.getLast());
                } else if (AGG_METHOD_FIRST.equals(aggMethod)) {
                    // just item.getFirst()
                } else if (AGG_METHOD_MAX.equals(aggMethod)) {
                    if (item2.getMax() > item1.getMax()) {
                        item1.setMax(item2.getMax());
                    }
                } else if (AGG_METHOD_MIN.equals(aggMethod)) {
                    if (item2.getMin() < item1.getMin()) {
                        item1.setMin(item2.getMin());
                    }
                } else if (AGG_METHOD_COUNT.equals(aggMethod)) {
                    item1.setCount(item1.getCount() + item2.getCount());
                } else if (AGG_METHOD_SUM.equals(aggMethod)) {
                    item1.setSum(item1.getSum() + item2.getSum());
                } else if (AGG_METHOD_AVG.equals(aggMethod)) {
                    item1.setCount(item1.getCount() + item2.getCount());
                    item1.setSum(item1.getSum() + item2.getSum());
                } else if (AGG_METHOD_COLLECT.equals(aggMethod)) {
                    item1.getCollectList().addAll(item2.getCollectList());
                }

                result.put(key, item1);
            } else {
                result.put(key, accItems.get(0));
            }
        }
        return  result;
    }

}
