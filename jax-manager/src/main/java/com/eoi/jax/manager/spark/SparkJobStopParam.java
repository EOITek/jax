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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.manager.api.JobStopParam;
import com.eoi.jax.manager.exception.InvalidParamException;

import java.util.ArrayList;
import java.util.List;

public class SparkJobStopParam extends BaseSparkJobParam implements JobStopParam {
    private String version;
    private String masterUrl;
    private String restUrl;
    private String submissionId;
    private String applicationId;
    private String yarnId;

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public SparkJobStopParam setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getMasterUrl() {
        return masterUrl;
    }

    @Override
    public SparkJobStopParam setMasterUrl(String masterUrl) {
        this.masterUrl = masterUrl;
        return this;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public SparkJobStopParam setRestUrl(String restUrl) {
        this.restUrl = restUrl;
        return this;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public SparkJobStopParam setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
        return this;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public SparkJobStopParam setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getYarnId() {
        return yarnId;
    }

    public SparkJobStopParam setYarnId(String yarnId) {
        this.yarnId = yarnId;
        return this;
    }

    public List<String> genArguments() {
        verify();
        List<String> arguments = new ArrayList<>();
        arguments.add("--master");
        arguments.add(masterUrl);
        arguments.add("--kill");
        arguments.add(submissionId);
        return arguments;
    }

    public void verify() {
        if (StrUtil.isEmpty(version)) {
            throw new InvalidParamException("miss version");
        }
        if (StrUtil.isEmpty(masterUrl)) {
            throw new InvalidParamException("miss masterUrl");
        }
        if (StrUtil.isEmpty(submissionId)) {
            throw new InvalidParamException("miss submissionId");
        }
    }
}
