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

public class UnPivotJobConfig implements ConfigValidatable, Serializable {

    @Parameter(
            label = "输入字段",
            description = "需要被拆分的列。如果源数据没有该列，那么目标列也不会生成"
    )
    private String sourceField;

    @Parameter(
            label = "输出字段",
            description = "拆分后的列"
    )
    private String targetField;

    @Parameter(
            label = "分割符",
            description = "拆分列的分隔符,分隔符可以是一个正则表达式"
    )
    private String delimiter;

    public String getSourceField() {
        return sourceField;
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    public String getTargetField() {
        return targetField;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public void validate() throws JobConfigValidationException {
    }
}
