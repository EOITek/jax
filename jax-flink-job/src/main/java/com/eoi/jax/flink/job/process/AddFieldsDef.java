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

import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;


public class AddFieldsDef implements Serializable {

    @Parameter(
            label = "输出字段",
            description = "指定输出字段"
    )
    private String key;

    @Parameter(
            label = "是否用表达式计算输出字段",
            description = "如果为true,则key作为aviator表达式进行计算得出输出字段名",
            optional = true,
            defaultValue = "false"
    )
    private boolean keyIsExp;

    @Parameter(
            label = "aviator表达式",
            description = "aviator表达式",
            inputType = InputType.AVIATOR_SCRIPT
    )
    private String expression;

    @Parameter(
            label = "出错填充",
            description = "如果执行表达式错误，用该值代替",
            optional = true,
            defaultValue = ""
    )
    private String fallback;

    @Parameter(
            label = "是否覆盖",
            description = "如果添加的字段已存在，是否覆盖原先的值",
            defaultValue = "false"
    )
    private boolean isReplace;

    public boolean isReplace() {
        return isReplace;
    }

    public void setReplace(boolean replace) {
        isReplace = replace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isKeyIsExp() {
        return keyIsExp;
    }

    public void setKeyIsExp(boolean keyIsExp) {
        this.keyIsExp = keyIsExp;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getFallback() {
        return fallback;
    }

    public void setFallback(String fallback) {
        this.fallback = fallback;
    }
}
