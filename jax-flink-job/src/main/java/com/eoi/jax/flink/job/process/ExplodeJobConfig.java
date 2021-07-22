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
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;
import java.util.List;

public class ExplodeJobConfig implements ConfigValidatable, Serializable {

    @Parameter(
            label = "输入字段",
            description = "宽表需要转换的列"
    )
    private List<String> sourceFields;

    @Parameter(
            label = "输出key字段",
            description = "合并到高表的字段名列名"
    )
    private String targetKeyField;

    @Parameter(
            label = "输出value字段",
            description = "合并到高表的字段值列名"
    )
    private String targetValueField;

    public String getTargetKeyField() {
        return targetKeyField;
    }

    public void setTargetKeyField(String targetKeyField) {
        this.targetKeyField = targetKeyField;
    }

    public String getTargetValueField() {
        return targetValueField;
    }

    public void setTargetValueField(String targetValueField) {
        this.targetValueField = targetValueField;
    }

    public List<String> getSourceFields() {
        return sourceFields;
    }

    public void setSourceFields(List<String> sourceFields) {
        this.sourceFields = sourceFields;
    }

    @Override
    public void validate() throws JobConfigValidationException {
    }
}
