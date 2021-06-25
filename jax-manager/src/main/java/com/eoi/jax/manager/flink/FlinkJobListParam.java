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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.manager.api.JobListParam;
import com.eoi.jax.manager.exception.InvalidParamException;

import java.util.ArrayList;
import java.util.List;

public class FlinkJobListParam extends BaseFlinkJobParam implements JobListParam {
    private String version;
    private String jobManager;
    private String yarnId;

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlinkJobListParam setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getJobManager() {
        return jobManager;
    }

    @Override
    public FlinkJobListParam setJobManager(String jobManager) {
        this.jobManager = jobManager;
        return this;
    }

    public String getYarnId() {
        return yarnId;
    }

    public FlinkJobListParam setYarnId(String yarnId) {
        this.yarnId = yarnId;
        return this;
    }

    public List<String> genArguments() {
        verify();
        //参数请查阅 https://ci.apache.org/projects/flink/flink-docs-stable/ops/cli.html
        List<String> arguments = new ArrayList<>();
        arguments.add("list");
        arguments.add("-m");
        arguments.add(jobManager);
        if (StrUtil.isNotEmpty(yarnId)) {
            arguments.add("-yid");
            arguments.add(yarnId);
        }
        arguments.add("-a");
        return arguments;
    }

    public void verify() {
        if (StrUtil.isEmpty(version)) {
            throw new InvalidParamException("miss version");
        }
        if (StrUtil.isEmpty(jobManager)) {
            throw new InvalidParamException("miss jobManager");
        }
        if (isYarnCluster() && StrUtil.isEmpty(yarnId)) {
            throw new InvalidParamException("miss yarnId");
        }
    }
}
