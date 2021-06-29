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

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 代码copy自 BoundedOutOfOrdernessTimestampExtractor，添加maxGapTime和maxDeviateTime判断
 * 由于BoundedOutOfOrdernessTimestampExtractor的extractTimestamp方法是final的，所以无法直接继承来override
 */
public abstract class WatermarkPeriodicAssigner<T> implements AssignerWithPeriodicWatermarks<T> {

    private static final long serialVersionUID = 1L;

    /** The current maximum timestamp seen so far. */
    private long currentMaxTimestamp;

    /** The timestamp of the last emitted watermark. */
    private long lastEmittedWatermark = Long.MIN_VALUE;

    /**
     * The (fixed) interval between the maximum seen timestamp seen in the records
     * and that of the watermark to be emitted.
     */
    private final long maxOutOfOrderness;

    private final long maxGapTime;

    private final long maxDeviateTime;

    public WatermarkPeriodicAssigner(Time maxOutOfOrderness, WatermarkJobConfig config) {
        if (maxOutOfOrderness.toMilliseconds() < 0) {
            throw new RuntimeException("Tried to set the maximum allowed "
                    + "lateness to " + maxOutOfOrderness + ". This parameter cannot be negative.");
        }
        this.maxOutOfOrderness = maxOutOfOrderness.toMilliseconds();
        this.currentMaxTimestamp = Long.MIN_VALUE + this.maxOutOfOrderness;
        this.maxGapTime = Time.seconds(config.getMaxGapTime()).toMilliseconds();
        this.maxDeviateTime = Time.seconds(config.getMaxDeviateTime()).toMilliseconds();
    }

    @Override
    public final Watermark getCurrentWatermark() {
        // this guarantees that the watermark never goes backwards.
        long potentialWM = currentMaxTimestamp - maxOutOfOrderness;
        if (potentialWM >= lastEmittedWatermark) {
            lastEmittedWatermark = potentialWM;
        }
        return new Watermark(lastEmittedWatermark);
    }

    /**
     * Extracts the timestamp from the given element.
     *
     * @param element The element that the timestamp is extracted from.
     * @return The new timestamp.
     */
    public abstract long extractTimestamp(T element);

    @Override
    public final long extractTimestamp(T element, long previousElementTimestamp) {
        long timestamp = extractTimestamp(element);
        if (timestamp > currentMaxTimestamp && !isExceed(timestamp)) {
            currentMaxTimestamp = timestamp;
        }
        return timestamp;
    }

    private boolean isExceed(long now) {
        boolean isExceedGap = maxGapTime > 0 && (now - currentMaxTimestamp) > maxGapTime;
        boolean isExceedDeviate = maxDeviateTime > 0 && Math.abs(now - System.currentTimeMillis()) > maxDeviateTime;

        if (isExceedGap || isExceedDeviate) {
            return true;
        } else {
            return false;
        }
    }
}
