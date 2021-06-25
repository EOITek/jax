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

package com.eoi.jax.manager.flink;

import com.eoi.jax.manager.api.VersionParam;

import java.util.Arrays;
import java.util.List;

public class FlinkVersionParam extends BaseFlinkJobParam implements VersionParam {
    private String version;
    private String jobManager;

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlinkVersionParam setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getJobManager() {
        return jobManager;
    }

    @Override
    public FlinkVersionParam setJobManager(String jobManager) {
        this.jobManager = jobManager;
        return this;
    }

    @Override
    public List<String> genArguments() {
        return Arrays.asList("-v");
    }
}
