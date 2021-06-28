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

public enum FlinkStatus {
    RUNNING("RUNNING"),
    CREATED("CREATED"),
    RESTARTING("RESTARTING"),
    FINISHED("FINISHED"),
    FAILING("FAILING"),
    FAILED("FAILED"),
    CANCELLING("CANCELLING"),
    CANCELED("CANCELED"),
    SUSPENDING("SUSPENDING"),
    SUSPENDED("SUSPENDED"),
    RECONCILING("RECONCILING"),
    NOT_FOUND("NOT_FOUND");

    public final String code;

    FlinkStatus(String code) {
        this.code = code;
    }

    public boolean isEqual(String code) {
        return this.code.equals(code);
    }

    public static boolean isRunning(String code) {
        return RUNNING.isEqual(code)
                || CREATED.isEqual(code)
                || RESTARTING.isEqual(code)
                || SUSPENDING.isEqual(code)
                || SUSPENDED.isEqual(code)
                || RECONCILING.isEqual(code);
    }

    public static boolean isStopping(String code) {
        return CANCELLING.isEqual(code);
    }

    public static boolean isStopped(String code) {
        return CANCELED.isEqual(code) || FINISHED.isEqual(code);
    }

    public static boolean isFailed(String code) {
        return FAILING.isEqual(code) || FAILED.isEqual(code);
    }

    public static PipelineStatus mapStatus(String code) {
        if (isRunning(code)) {
            return PipelineStatus.RUNNING;
        } else if (isFailed(code)) {
            return PipelineStatus.FAILED;
        } else if (isStopping(code)) {
            return PipelineStatus.STOPPING;
        } else if (isStopped(code)) {
            return PipelineStatus.STOPPED;
        }
        return null;
    }
}
