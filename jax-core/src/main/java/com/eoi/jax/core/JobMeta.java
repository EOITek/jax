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

import com.eoi.jax.api.annotation.model.JobParamMeta;

import java.util.List;

public class JobMeta {

    public List<InOutTypeDescriptor> inTypes;

    public List<InOutTypeDescriptor> outTypes;

    public JobInfo jobInfo;

    public List<JobParamMeta> jobParameters;

    public static class InOutTypeDescriptor {

        public String raw;

        public List<String> parameterizedTypes;
    }
}
