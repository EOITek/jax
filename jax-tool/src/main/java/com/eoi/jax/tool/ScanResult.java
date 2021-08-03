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

package com.eoi.jax.tool;

import java.util.Map;

public class ScanResult {
    private boolean success;
    private Map<String, JobMeta> jobs;
    private String message;
    private String stackTrace;

    public boolean getSuccess() {
        return success;
    }

    public ScanResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public Map<String, JobMeta> getJobs() {
        return jobs;
    }

    public ScanResult setJobs(Map<String, JobMeta> jobs) {
        this.jobs = jobs;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ScanResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public ScanResult setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }
}
