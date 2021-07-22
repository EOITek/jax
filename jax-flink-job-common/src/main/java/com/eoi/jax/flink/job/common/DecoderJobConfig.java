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
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;


public class DecoderJobConfig implements ConfigValidatable, Serializable {
    @Parameter(
            label = "输入字段",
            description = "从上游的Map中指定目标字段名，字段值需要是二进制数组(byte[])类型",
            defaultValue = "bytes"
    )
    private String byteArrayFieldName;

    @Parameter(
            label = "输出字段",
            description = "如果指定Decoder后内容输出到指定的字段名，如果不指定则输出为整个顶层Map",
            optional = true,
            defaultValue = ""
    )
    private String outputFieldName;

    @Parameter(
            label = "移除输入字段",
            description = "是否移除目标字段",
            optional = true,
            defaultValue = "true"
    )
    private Boolean removeByteArrayField;

    @Parameter(
            label = "是否保留原有字段",
            description = "当未指定输出字段时，输出为整个顶层Map，是否同时保留原有字段",
            optional = true,
            defaultValue = "false"
    )
    private Boolean reserveOriginColumns;

    public Boolean getReserveOriginColumns() {
        return reserveOriginColumns;
    }

    public void setReserveOriginColumns(Boolean reserveOriginColumns) {
        this.reserveOriginColumns = reserveOriginColumns;
    }

    public Boolean getRemoveByteArrayField() {
        return removeByteArrayField;
    }

    public void setRemoveByteArrayField(Boolean removeByteArrayField) {
        this.removeByteArrayField = removeByteArrayField;
    }

    public String getByteArrayFieldName() {
        return byteArrayFieldName;
    }

    public void setByteArrayFieldName(String byteArrayFieldName) {
        this.byteArrayFieldName = byteArrayFieldName;
    }

    public String getOutputFieldName() {
        return outputFieldName;
    }

    public void setOutputFieldName(String outputFieldName) {
        this.outputFieldName = outputFieldName;
    }

    @Override
    public void validate() throws JobConfigValidationException {
    }
}
