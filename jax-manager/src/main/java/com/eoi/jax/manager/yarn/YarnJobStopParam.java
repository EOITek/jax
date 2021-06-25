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

package com.eoi.jax.manager.yarn;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.manager.api.JobStopParam;
import com.eoi.jax.manager.exception.InvalidParamException;

import java.util.ArrayList;
import java.util.List;

public class YarnJobStopParam extends BaseYarnJob implements JobStopParam {
    private Integer timeOutMs;
    private String applicationId;
    private String principal;
    private String keytab;

    public Integer getTimeOutMs() {
        return timeOutMs;
    }

    public YarnJobStopParam setTimeOutMs(Integer timeOutMs) {
        this.timeOutMs = timeOutMs;
        return this;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public YarnJobStopParam setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getPrincipal() {
        return principal;
    }

    public YarnJobStopParam setPrincipal(String principal) {
        this.principal = principal;
        return this;
    }

    public String getKeytab() {
        return keytab;
    }

    public YarnJobStopParam setKeytab(String keytab) {
        this.keytab = keytab;
        return this;
    }

    public List<String> genArguments() {
        verify();
        List<String> arguments = new ArrayList<>();
        arguments.add("application");
        arguments.add("--kill");
        arguments.add(applicationId);
        return arguments;
    }

    public JaxYarnParam genYarnParam() {
        verify();
        JaxYarnParam yarnParam = new JaxYarnParam();
        yarnParam.setApplicationId(applicationId);
        yarnParam.setPrincipal(principal);
        yarnParam.setKeytab(keytab);
        yarnParam.setTimeOutMs(timeOutMs);
        yarnParam.setHadoopConfDir(getHadoopConfDir());
        return yarnParam;
    }

    public void verify() {
        if (StrUtil.isEmpty(applicationId)) {
            throw new InvalidParamException("miss applicationId");
        }
        if (timeOutMs == null || timeOutMs <= 0) {
            throw new InvalidParamException("invalid timeOutMs " + timeOutMs);
        }
    }
}
