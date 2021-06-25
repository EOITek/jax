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
import com.eoi.jax.manager.api.JobGetParam;
import com.eoi.jax.manager.exception.InvalidParamException;

import java.util.List;

public class FlinkJobGetParam extends FlinkJobListParam implements JobGetParam {
    private String jobId;

    public String getJobId() {
        return jobId;
    }

    public FlinkJobGetParam setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    @Override
    public List<String> genArguments() {
        return super.genArguments();
    }

    @Override
    public void verify() {
        super.verify();
        if (StrUtil.isEmpty(jobId)) {
            throw new InvalidParamException("miss jobId");
        }
    }
}
