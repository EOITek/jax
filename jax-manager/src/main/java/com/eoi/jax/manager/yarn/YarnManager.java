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

import cn.hutool.core.exceptions.ExceptionUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class YarnManager implements JaxManager {
    private static final Logger logger = LoggerFactory.getLogger(YarnManager.class);

    private IProcessRunner runner;

    @Override
    public IProcessRunner getRunner() {
        return runner;
    }

    @Override
    public YarnManager setRunner(IProcessRunner runner) {
        this.runner = runner;
        return this;
    }

    @Override
    public VersionResult version(VersionParam param) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public JobStartResult start(JobStartParam param) {
        return null;
    }

    @Override
    public JobStopResult stop(JobStopParam param) {
        YarnJobStopParam delete = (YarnJobStopParam) param;
        JaxYarnParam jaxYarnParam = delete.genYarnParam();
        YarnJobStopResult result = new YarnJobStopResult(delete);
        String msg = delete.toString();
        try {
            JaxYarnClient.killApplication(jaxYarnParam);
            msg = msg + System.lineSeparator() + "success";
            result.setCode(0);
            result.setMessage(msg);
            return result;
        } catch (Exception e) {
            logger.error("[" + delete.getUuid() + "]", e);
            msg = msg + System.lineSeparator() + ExceptionUtil.stacktraceToString(e, 4096);
            result.setCode(-1);
            result.setMessage(msg);
            return result;
        }
    }

    @Override
    public JobListResult list(JobListParam param) {
        YarnJobListParam list = (YarnJobListParam) param;
        JaxYarnParam jaxYarnParam = list.genYarnParam();
        YarnJobListResult result = new YarnJobListResult(list);
        String msg = list.toString();
        try {
            List<YarnAppReport> reports = JaxYarnClient.listApplicationReport(jaxYarnParam);
            msg = msg + System.lineSeparator() + "success";
            result.setCode(0);
            result.setMessage(msg);
            result.setReports(reports);
            return result;
        } catch (Exception e) {
            logger.error("[" + list.getUuid() + "]", e);
            msg = msg + System.lineSeparator() + ExceptionUtil.stacktraceToString(e, 4096);
            result.setCode(-1);
            result.setMessage(msg);
            return result;
        }
    }

    @Override
    public JobGetResult get(JobGetParam param) {
        YarnJobGetParam get = (YarnJobGetParam) param;
        JaxYarnParam jaxYarnParam = get.genYarnParam();
        YarnJobGetResult result = new YarnJobGetResult(get);
        String msg = get.toString();
        try {
            YarnAppReport report = JaxYarnClient.getApplicationReport(jaxYarnParam);
            msg = msg + System.lineSeparator() + "success";
            result.setCode(0);
            result.setMessage(msg);
            result.setReport(report);
            return result;
        } catch (Exception e) {
            logger.debug("[" + get.getUuid() + "]", e);
            msg = msg + System.lineSeparator() + ExceptionUtil.stacktraceToString(e, 4096);
            result.setCode(-1);
            result.setMessage(msg);
            return result;
        }
    }
}
