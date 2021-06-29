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
import com.eoi.jax.flink.job.common.AviatorUtil;
import org.apache.flink.streaming.api.functions.windowing.delta.DeltaFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WindowTriggerDeltaFunction implements DeltaFunction<Map<String,Object>> {
    private static final Logger LOG = LoggerFactory.getLogger(WindowTriggerDeltaFunction.class);

    private String valueField;
    private String deltaExp;

    public WindowTriggerDeltaFunction(String valueField, String deltaExp) {
        this.valueField = valueField;
        this.deltaExp = deltaExp;
    }

    @Override
    public double getDelta(Map<String, Object> oldDataPoint, Map<String, Object> newDataPoint) {
        try {
            double oldValue = NumberUtil.parseNumber(oldDataPoint.getOrDefault(valueField, "0.0").toString()).doubleValue();
            double newValue = NumberUtil.parseNumber(newDataPoint.getOrDefault(valueField, "0.0").toString()).doubleValue();
            Map<String, Object> env = new HashMap<>();
            env.put("oldValue", oldValue);
            env.put("newValue", newValue);
            return AviatorUtil.eval(deltaExp, env, 0d);
        } catch (Exception ex) {
            LOG.error("getDelta error", ex);
            return 0d;
        }
    }
}
