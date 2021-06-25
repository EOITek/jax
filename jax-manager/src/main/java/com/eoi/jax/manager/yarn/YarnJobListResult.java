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

import com.eoi.jax.manager.api.JobListResult;

import java.util.List;

public class YarnJobListResult extends BaseYarnJob implements JobListResult {
    private YarnJobListParam param;
    private int code;
    private String message;
    private List<YarnAppReport> reports;

    public YarnJobListResult(YarnJobListParam param) {
        this.param = param;
    }

    public YarnJobListParam getParam() {
        return param;
    }

    public YarnJobListResult setParam(YarnJobListParam param) {
        this.param = param;
        return this;
    }

    @Override
    public int getCode() {
        return code;
    }

    public YarnJobListResult setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public YarnJobListResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<YarnAppReport> getReports() {
        return reports;
    }

    public YarnJobListResult setReports(List<YarnAppReport> reports) {
        this.reports = reports;
        return this;
    }
}
