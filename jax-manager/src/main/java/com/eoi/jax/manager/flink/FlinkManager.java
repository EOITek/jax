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

import com.eoi.jax.manager.JaxManager;
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
import com.eoi.jax.manager.process.LineHandler;
import com.eoi.jax.manager.process.ProcessOutput;

public class FlinkManager implements JaxManager {
    private IProcessRunner runner;
    private LineHandler handler;

    @Override
    public IProcessRunner getRunner() {
        return runner;
    }

    @Override
    public FlinkManager setRunner(IProcessRunner runner) {
        this.runner = runner;
        return this;
    }

    @Override
    public VersionResult version(VersionParam param) {
        FlinkVersionParam versionParam = (FlinkVersionParam) param;
        FlinkVersiontResult result = new FlinkVersiontResult(versionParam);
        result.setHandler(handler);
        ProcessOutput output = runner.exec(versionParam.genArguments(), result);
        result.deserialize(output);
        return result;
    }

    public LineHandler getHandler() {
        return handler;
    }

    public FlinkManager setHandler(LineHandler handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public JobStartResult start(JobStartParam param) {
        BaseFlinkJobParam start = (BaseFlinkJobParam) param;
        FlinkJobStartResult result = new FlinkJobStartResult(start);
        result.setHandler(handler);
        ProcessOutput output = runner.exec(start.genArguments(), result);
        result.deserialize(output);
        return result;
    }

    @Override
    public JobStopResult stop(JobStopParam param) {
        FlinkJobStopParam stop = (FlinkJobStopParam) param;
        FlinkJobStopResult result = new FlinkJobStopResult(stop);
        result.setHandler(handler);
        ProcessOutput output = runner.exec(stop.genArguments(), result);
        result.deserialize(output);
        return result;
    }

    @Override
    public JobListResult list(JobListParam param) {
        FlinkJobListParam list = (FlinkJobListParam) param;
        FlinkJobListResult result = new FlinkJobListResult(list);
        ProcessOutput output = runner.exec(list.genArguments(), result);
        result.deserialize(output);
        return result;
    }

    @Override
    public JobGetResult get(JobGetParam param) {
        FlinkJobGetParam get = (FlinkJobGetParam) param;
        FlinkJobGetResult result = new FlinkJobGetResult(get);
        ProcessOutput output = runner.exec(get.genArguments(), result);
        result.deserialize(output);
        return result;
    }
}
