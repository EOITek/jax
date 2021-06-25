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

import com.eoi.jax.manager.api.JobGetResult;

public class YarnJobGetResult extends BaseYarnJob implements JobGetResult {
    private YarnJobGetParam param;
    private int code;
    private String message;
    private YarnAppReport report;

    public YarnJobGetResult(YarnJobGetParam param) {
        setParam(param);
    }

    public YarnJobGetParam getParam() {
        return param;
    }

    public YarnJobGetResult setParam(YarnJobGetParam param) {
        this.param = param;
        this.setUuid(param.getUuid());
        return this;
    }

    @Override
    public int getCode() {
        return code;
    }

    public YarnJobGetResult setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public YarnJobGetResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public YarnAppReport getReport() {
        return report;
    }

    public YarnJobGetResult setReport(YarnAppReport report) {
        this.report = report;
        return this;
    }
}
