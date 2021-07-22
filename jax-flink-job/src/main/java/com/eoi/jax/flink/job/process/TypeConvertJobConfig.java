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

import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;
import java.util.List;

public class TypeConvertJobConfig implements Serializable {

    @Parameter(
            label = "转换字段",
            description = "转换字段列表"
    )
    private List<TypeConvertJobParam> fields;

    public List<TypeConvertJobParam> getFields() {
        return fields;
    }

    public TypeConvertJobConfig setFields(List<TypeConvertJobParam> fields) {
        this.fields = fields;
        return this;
    }
}
