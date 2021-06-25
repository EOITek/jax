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

package com.eoi.jax.manager.yarn;

import com.eoi.jax.manager.api.JobStopResult;
import com.eoi.jax.manager.process.ProcessOutput;

public class YarnJobStopResult extends BaseYarnJob implements JobStopResult {
    private YarnJobStopParam param;
    private int code;
    private String message;

    public YarnJobStopResult(YarnJobStopParam param) {
        this.param = param;
    }

    @Override
    public int getCode() {
        return code;
    }

    public YarnJobStopResult setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public YarnJobStopResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public YarnJobStopParam getParam() {
        return param;
    }

    public YarnJobStopResult setParam(YarnJobStopParam param) {
        this.param = param;
        this.setUuid(param.getUuid());
        return this;
    }

    public YarnJobStopResult deserialize(ProcessOutput output, YarnJobStopParam job) {
        this.code = output.getCode();
        this.message = output.getCli();
        return this;
    }
}
