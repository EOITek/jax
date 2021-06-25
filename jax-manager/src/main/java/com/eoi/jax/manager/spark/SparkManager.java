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

package com.eoi.jax.manager.spark;

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

public class SparkManager implements JaxManager {
    private IProcessRunner runner;
    private LineHandler handler;

    @Override
    public IProcessRunner getRunner() {
        return runner;
    }

    @Override
    public SparkManager setRunner(IProcessRunner runner) {
        this.runner = runner;
        return this;
    }

    @Override
    public VersionResult version(VersionParam param) {
        throw new RuntimeException("not implemented yet");
    }

    public LineHandler getHandler() {
        return handler;
    }

    public SparkManager setHandler(LineHandler handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public JobStartResult start(JobStartParam param) {
        SparkJobStartParam start = (SparkJobStartParam) param;
        SparkJobStartResult result = new SparkJobStartResult(start);
        result.setHandler(handler);
        ProcessOutput output = runner.exec(start.genArguments(), result);
        result.deserialize(output);
        return result;
    }

    @Override
    public JobStopResult stop(JobStopParam param) {
        SparkJobStopParam stop = (SparkJobStopParam) param;
        SparkJobStopResult result = new SparkJobStopResult(stop);
        result.setHandler(handler);
        ProcessOutput output = runner.exec(stop.genArguments(), result);
        result.deserialize(output);
        return result;
    }

    @Override
    public JobListResult list(JobListParam param) {
        return null;
    }

    @Override
    public JobGetResult get(JobGetParam param) {
        return null;
    }
}
