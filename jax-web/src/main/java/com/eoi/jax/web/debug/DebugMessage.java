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

package com.eoi.jax.web.debug;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DebugMessage {
    @JsonProperty("code")
    private String code;

    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("slot")
    private String slot;

    @JsonProperty("message")
    private String message;

    public String getCode() {
        return code;
    }

    public DebugMessage setCode(String code) {
        this.code = code;
        return this;
    }

    public String getJobId() {
        return jobId;
    }

    public DebugMessage setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getSlot() {
        return slot;
    }

    public DebugMessage setSlot(String slot) {
        this.slot = slot;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public DebugMessage setMessage(String message) {
        this.message = message;
        return this;
    }
}
