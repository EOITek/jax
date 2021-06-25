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
import com.eoi.jax.manager.api.JobStopParam;
import com.eoi.jax.manager.exception.InvalidParamException;

import java.util.ArrayList;
import java.util.List;

public class FlinkJobStopParam extends BaseFlinkJobParam implements JobStopParam {
    private String version;
    private String jobManager;
    private String jobId;
    private String yarnId;
    private String savePoint;
    // if true, Send MAX_WATERMARK before taking the Send MAX_WATERMARK before taking the.
    private Boolean drain;

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlinkJobStopParam setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getJobManager() {
        return jobManager;
    }

    @Override
    public FlinkJobStopParam setJobManager(String jobManager) {
        this.jobManager = jobManager;
        return this;
    }

    public String getJobId() {
        return jobId;
    }

    public FlinkJobStopParam setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getYarnId() {
        return yarnId;
    }

    public FlinkJobStopParam setYarnId(String yarnId) {
        this.yarnId = yarnId;
        return this;
    }

    public String getSavePoint() {
        return savePoint;
    }

    public FlinkJobStopParam setSavePoint(String savePoint) {
        this.savePoint = savePoint;
        return this;
    }

    public Boolean getDrain() {
        return drain;
    }

    public FlinkJobStopParam setDrain(Boolean drain) {
        this.drain = drain;
        return this;
    }

    @Override
    public List<String> genArguments() {
        verify();
        //参数请查阅 https://ci.apache.org/projects/flink/flink-docs-stable/ops/cli.html
        List<String> arguments = new ArrayList<>();
        if (StrUtil.isEmpty(savePoint)) {
            // 经验证，未指定savepoint，只能cancel
            arguments.add("cancel");
            arguments.add("-m");
            arguments.add(jobManager);
        } else {
            if (isBeforeV190()) {
                arguments.add("cancel");
                arguments.add("-m");
                arguments.add(jobManager);
                arguments.add("-s");
                arguments.add(savePoint);
            } else {
                arguments.add("stop");
                arguments.add("-m");
                arguments.add(jobManager);
                arguments.add("-p");
                arguments.add(savePoint);
                if (drain != null && drain) {
                    arguments.add("-d");
                }
            }
        }
        if (StrUtil.isNotEmpty(yarnId)) {
            arguments.add("-yid");
            arguments.add(yarnId);
        }
        arguments.add(jobId);
        return arguments;
    }

    public void verify() {
        if (StrUtil.isEmpty(version)) {
            throw new InvalidParamException("miss version");
        }
        if (StrUtil.isEmpty(jobManager)) {
            throw new InvalidParamException("miss jobManager");
        }
        if (StrUtil.isEmpty(jobId)) {
            throw new InvalidParamException("miss jobId");
        }
        if (isYarnCluster() && StrUtil.isEmpty(yarnId)) {
            throw new InvalidParamException("miss yarnId");
        }
    }
}
