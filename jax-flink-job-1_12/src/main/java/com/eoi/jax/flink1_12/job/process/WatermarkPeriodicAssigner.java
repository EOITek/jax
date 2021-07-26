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

package com.eoi.jax.flink1_12.job.process;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;

import java.io.Serializable;


public class WatermarkPeriodicAssigner<T> implements WatermarkGenerator<T>, Serializable {

    /** The maximum timestamp encountered so far. */
    private long maxTimestamp;

    /** The maximum out-of-orderness that this watermark generator assumes. */
    private final long outOfOrdernessMillis;

    private final long maxGapTime;

    private final long maxDeviateTime;

    public WatermarkPeriodicAssigner(long maxOutOfOrderness, long maxGapTime, long maxDeviateTime) {
        if (maxOutOfOrderness < 0) {
            throw new RuntimeException("Tried to set the maximum allowed "
                    + "lateness to " + maxOutOfOrderness + ". This parameter cannot be negative.");
        }
        this.outOfOrdernessMillis = maxOutOfOrderness;
        // start so that our lowest watermark would be Long.MIN_VALUE.
        this.maxTimestamp = Long.MIN_VALUE + outOfOrdernessMillis + 1;
        this.maxGapTime = maxGapTime;
        this.maxDeviateTime = maxDeviateTime;
    }

    @Override
    public void onEvent(T event, long eventTimestamp, WatermarkOutput output) {
        if (!isExceed(eventTimestamp)) {
            maxTimestamp = Math.max(maxTimestamp, eventTimestamp);
        }
    }

    @Override
    public void onPeriodicEmit(WatermarkOutput output) {
        output.emitWatermark(new Watermark(maxTimestamp - outOfOrdernessMillis - 1));
    }

    private boolean isExceed(long now) {
        boolean isExceedGap = maxGapTime > 0 && (now - maxTimestamp) > maxGapTime;
        boolean isExceedDeviate = maxDeviateTime > 0 && Math.abs(now - System.currentTimeMillis()) > maxDeviateTime;

        if (isExceedGap || isExceedDeviate) {
            return true;
        } else {
            return false;
        }
    }
}
