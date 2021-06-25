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

package com.eoi.jax.manager;

import com.eoi.jax.manager.api.JobGetParam;
import com.eoi.jax.manager.api.JobGetResult;
import com.eoi.jax.manager.api.JobListParam;
import com.eoi.jax.manager.api.JobListResult;
import com.eoi.jax.manager.api.JobStartParam;
import com.eoi.jax.manager.api.JobStartResult;
import com.eoi.jax.manager.api.JobStopParam;
import com.eoi.jax.manager.api.JobStopResult;
import com.eoi.jax.manager.api.VersionParam;
import com.eoi.jax.manager.api.VersionResult;
import com.eoi.jax.manager.process.IProcessRunner;

public interface JaxManager {
    JobStartResult start(JobStartParam param);

    JobStopResult stop(JobStopParam param);

    JobListResult list(JobListParam param);

    JobGetResult get(JobGetParam param);

    IProcessRunner getRunner();

    JaxManager setRunner(IProcessRunner runner);

    VersionResult version(VersionParam param);
}
