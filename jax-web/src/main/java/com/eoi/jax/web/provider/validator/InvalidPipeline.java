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

public class InvalidPipeline {
    private String invalidJobId;
    private Map<String, String> invalidJobConfig;

    public String getInvalidJobId() {
        return invalidJobId;
    }

    public InvalidPipeline setInvalidJobId(String invalidJobId) {
        this.invalidJobId = invalidJobId;
        return this;
    }

    public Map<String, String> getInvalidJobConfig() {
        return invalidJobConfig;
    }

    public InvalidPipeline setInvalidJobConfig(Map<String, String> invalidJobConfig) {
        this.invalidJobConfig = invalidJobConfig;
        return this;
    }
}
