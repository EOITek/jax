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

package com.eoi.jax.web.provider.validator;

import java.util.Map;

public class ValidateResult {
    private boolean success;
    private boolean compatible;
    private boolean invalid;
    private String invalidJobId;
    private Map<String, String> invalidJobConfig;
    private String message;
    private String stackTrace;

    public boolean getSuccess() {
        return success;
    }

    public ValidateResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public boolean getCompatible() {
        return compatible;
    }

    public ValidateResult setCompatible(boolean compatible) {
        this.compatible = compatible;
        return this;
    }

    public boolean getInvalid() {
        return invalid;
    }

    public ValidateResult setInvalid(boolean invalid) {
        this.invalid = invalid;
        return this;
    }

    public String getInvalidJobId() {
        return invalidJobId;
    }

    public ValidateResult setInvalidJobId(String invalidJobId) {
        this.invalidJobId = invalidJobId;
        return this;
    }

    public Map<String, String> getInvalidJobConfig() {
        return invalidJobConfig;
    }

    public ValidateResult setInvalidJobConfig(Map<String, String> invalidJobConfig) {
        this.invalidJobConfig = invalidJobConfig;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ValidateResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public ValidateResult setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }
}
