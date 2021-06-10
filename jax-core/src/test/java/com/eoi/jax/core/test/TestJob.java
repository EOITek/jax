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

package com.eoi.jax.core.test;

import com.eoi.jax.api.Builder;
import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;

import java.util.Map;

public class TestJob implements Builder<TestJob.MockJobConfig> {

    @Override
    public MockJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        return new MockJobConfig(mapConfig);
    }

    static class MockJobConfig implements ConfigValidatable {

        private Map<String, Object> config;

        public MockJobConfig(Map<String, Object> config) {
            this.config = config;
        }

        @Override
        public void validate() throws JobConfigValidationException {
            if ((Boolean) this.config.getOrDefault("invalid", true))  {
                throw  new JobConfigValidationException();
            }
        }
    }
}
