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

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.streaming.api.functions.windowing.delta.DeltaFunction;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.Window;

public class WindowTriggerByDelta<T, W extends Window> extends Trigger<T, W> {
    private static final long serialVersionUID = 1L;

    private final DeltaFunction<T> deltaFunction;
    private final double threshold;
    private final ValueStateDescriptor<T> stateDesc;
    private final TriggerResult triggerResult;

    private WindowTriggerByDelta(double threshold, DeltaFunction<T> deltaFunction, TypeSerializer<T> stateSerializer,TriggerResult triggerResult) {
        this.deltaFunction = deltaFunction;
        this.threshold = threshold;
        stateDesc = new ValueStateDescriptor<>("last-element", stateSerializer);
        this.triggerResult = triggerResult;
    }

    @Override
    public TriggerResult onElement(T element, long timestamp, W window, TriggerContext ctx) throws Exception {
        ValueState<T> lastElementState = ctx.getPartitionedState(stateDesc);
        if (lastElementState.value() == null) {
            lastElementState.update(element);
            return TriggerResult.CONTINUE;
        }
        if (deltaFunction.getDelta(lastElementState.value(), element) > this.threshold) {
            lastElementState.update(element);
            return this.triggerResult;
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
        ctx.getPartitionedState(stateDesc).clear();
    }

    @Override
    public String toString() {
        return "DeltaTrigger(" +  deltaFunction + ", " + threshold + ")";
    }

    public static <T, W extends Window> WindowTriggerByDelta<T, W> of(
            double threshold, DeltaFunction<T> deltaFunction,
            TypeSerializer<T> stateSerializer,
            TriggerResult triggerResult) {
        return new WindowTriggerByDelta<>(threshold, deltaFunction, stateSerializer, triggerResult);
    }

}

