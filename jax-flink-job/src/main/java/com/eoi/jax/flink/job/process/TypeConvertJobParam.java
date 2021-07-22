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
import com.eoi.jax.common.converter.BooleanConverter;
import com.eoi.jax.common.converter.ConvertOption;
import com.eoi.jax.common.converter.DoubleConverter;
import com.eoi.jax.common.converter.FloatConverter;
import com.eoi.jax.common.converter.IntegerConverter;
import com.eoi.jax.common.converter.LongConverter;
import com.eoi.jax.common.converter.StringConverter;

import java.io.Serializable;

public class TypeConvertJobParam implements Serializable {

    @Parameter(
            label = "转换字段",
            description = "需要转换的字段名"
    )
    private String field;

    @Parameter(
            label = "目标类型",
            description = "将转换字段的值转化成该类型",
            candidates = {
                    BooleanConverter.NAME,
                    DoubleConverter.NAME,
                    FloatConverter.NAME,
                    LongConverter.NAME,
                    IntegerConverter.NAME,
                    StringConverter.NAME
            }
    )
    private String type;

    @Parameter(
            label = "浮点数小数位保留位数",
            description = "用于截断浮点数小数位长度",
            optional = true
    )
    private Integer roundingScale;

    @Parameter(
            label = "进位模式",
            description = "截断浮点数小数位时使用的进位模式",
            candidates = {
                    "UP",
                    "DOWN",
                    "HALF_UP",
                    "HALF_DOWN",
                    "CEILING",
                    "FLOOR"
            },
            defaultValue = "HALF_UP",
            optional = true
    )
    private String roundingMode;

    @Parameter(
            label = "输出字段",
            description = "转化结果输出到指定字段（默认为空）。不设置转化结果输出到原字段上（原字段被替换）；否则转化结果输出指定的字段上（原字段保留）",
            optional = true
    )
    private String output;

    public String getField() {
        return field;
    }

    public TypeConvertJobParam setField(String field) {
        this.field = field;
        return this;
    }

    public String getType() {
        return type;
    }

    public TypeConvertJobParam setType(String type) {
        this.type = type;
        return this;
    }

    public Integer getRoundingScale() {
        return roundingScale;
    }

    public TypeConvertJobParam setRoundingScale(Integer roundingScale) {
        this.roundingScale = roundingScale;
        return this;
    }

    public String getRoundingMode() {
        return roundingMode;
    }

    public TypeConvertJobParam setRoundingMode(String roundingMode) {
        this.roundingMode = roundingMode;
        return this;
    }

    public String getOutput() {
        return output;
    }

    public TypeConvertJobParam setOutput(String output) {
        this.output = output;
        return this;
    }

    public ConvertOption convertOption() {
        return new ConvertOption().setRoundingScale(roundingScale).setRoundingMode(roundingMode);
    }
}
