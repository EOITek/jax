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
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.Window;

import java.util.Map;

public class WindowTriggerByExp<W extends Window> extends Trigger<Object, W> {
    private static final long serialVersionUID = 1L;
    private String exp;
    private final TriggerResult triggerResult;

    private WindowTriggerByExp(String exp, TriggerResult triggerResult) {
        this.exp = exp;
        this.triggerResult = triggerResult;
    }

    @Override
    public TriggerResult onElement(Object element, long timestamp, W window, TriggerContext ctx) throws Exception {
        if (element instanceof Map) {
            Map<String, Object> mapValue = (Map<String, Object>) element;

            if (AviatorUtil.eval(exp, mapValue, false)) {
                return triggerResult;
            }
        }
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onEventTime(long time, W window, TriggerContext ctx) {
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, W window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(W window, TriggerContext ctx) throws Exception {
    }

    @Override
    public boolean canMerge() {
        return true;
    }

    @Override
    public void onMerge(W window, OnMergeContext ctx) throws Exception {
    }

    @Override
    public String toString() {
        return "WindowTriggerByExp(" +  exp + ")";
    }

    public static <W extends Window> WindowTriggerByExp<W> of(String exp, TriggerResult triggerResult) {
        return new WindowTriggerByExp<>(exp, triggerResult);
    }

}

