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

package com.eoi.jax.flink.job.common;

import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.common.JsonUtil;

import java.io.IOException;
import java.io.Serializable;


public class AvroDecoderJobConfig extends DecoderJobConfig implements ConfigValidatable, Serializable {
    @Parameter(
            label = "avro schema定义",
            description = "avro schema定义,json格式表示",
            inputType = InputType.JSON
    )
    private String avroSchema;

    public String getAvroSchema() {
        return avroSchema;
    }

    public void setAvroSchema(String avroSchema) {
        this.avroSchema = avroSchema;
    }

    @Override
    public void validate() throws JobConfigValidationException {
        super.validate();
        try {
            // avroSchema should be valid json format
            JsonUtil.decode2Map(avroSchema);
        } catch (IOException e) {
            throw new JobConfigValidationException("avroSchema should be json format:" + avroSchema);
        }
    }
}
