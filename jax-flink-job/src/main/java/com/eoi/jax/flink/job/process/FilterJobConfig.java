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

package com.eoi.jax.flink.job.process;

import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.flink.job.common.AviatorUtil;

import java.io.Serializable;

public class FilterJobConfig implements ConfigValidatable, Serializable {

    @Parameter(
            label = "过滤条件",
            description = "使用aviator表达式，返回boolean值",
            inputType = InputType.AVIATOR_SCRIPT
    )
    private String rule;

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    @Override
    public void validate() throws JobConfigValidationException {
        try {
            AviatorUtil.instance.validate(rule);
        } catch (Exception ex) {
            throw new JobConfigValidationException(ex.getMessage());
        }
    }
}
