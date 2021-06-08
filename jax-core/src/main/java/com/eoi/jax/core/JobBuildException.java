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

package com.eoi.jax.core;

public class JobBuildException extends DAGVisitException {

    private String invalidJobId;

    public String getInvalidJobId() {
        return invalidJobId;
    }

    public JobBuildException setInvalidJobId(String invalidJobId) {
        this.invalidJobId = invalidJobId;
        return this;
    }

    public JobBuildException() {
    }

    public JobBuildException(String message, String invalidJobId) {
        super(message);
        this.invalidJobId = invalidJobId;
    }

    public JobBuildException(String message) {
        super(message);
    }

    public JobBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobBuildException(String message, Throwable cause, String invalidJobId) {
        super(message, cause);
        this.invalidJobId = invalidJobId;
    }
}
