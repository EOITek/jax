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
import com.eoi.jax.flink.job.common.AviatorUtil;

import java.io.Serializable;
import java.util.List;


public class AddFieldsJobConfig implements ConfigValidatable, Serializable {
    @Parameter(
            label = "新增字段",
            description = "新增字段操作定义列表，按顺序执行"
    )
    private List<AddFieldsDef> fields;

    public List<AddFieldsDef> getFields() {
        return fields;
    }

    public void setFields(List<AddFieldsDef> fields) {
        this.fields = fields;
    }

    @Override
    public void validate() throws JobConfigValidationException {
        if (fields == null || fields.isEmpty()) {
            throw new JobConfigValidationException("数据列操作定义列表不可为空");
        }
        for (AddFieldsDef field : fields) {
            try {
                AviatorUtil.instance.validate(field.getExpression());
            } catch (Exception ex) {
                throw new JobConfigValidationException(ex.getMessage());
            }
        }
    }
}
