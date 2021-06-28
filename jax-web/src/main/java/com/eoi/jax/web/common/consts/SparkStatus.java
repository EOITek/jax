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

package com.eoi.jax.web.common.consts;

public enum SparkStatus {
    STARTING("STARTING"),
    STARTED("STARTED"),
    KILLED("KILLED"),
    FINISHED("FINISHED"),
    FAILED("FAILED");

    public final String code;

    SparkStatus(String code) {
        this.code = code;
    }

    public boolean isEqual(String code) {
        return this.code.equals(code);
    }

    public static boolean isStarting(String code) {
        return STARTING.isEqual(code);
    }

    public static boolean isRunning(String code) {
        return STARTED.isEqual(code);
    }

    public static boolean isFinished(String code) {
        return FINISHED.isEqual(code);
    }

    public static boolean isStopped(String code) {
        return KILLED.isEqual(code);
    }

    public static boolean isFailed(String code) {
        return FAILED.isEqual(code);
    }

    public static PipelineStatus mapStatus(String code) {
        if (isStarting(code)) {
            return PipelineStatus.STARTING;
        } else if (isRunning(code)) {
            return PipelineStatus.RUNNING;
        } else if (isFinished(code)) {
            return PipelineStatus.FINISHED;
        } else if (isStopped(code)) {
            return PipelineStatus.STOPPED;
        } else if (isFailed(code)) {
            return PipelineStatus.FAILED;
        }
        return null;
    }
}
