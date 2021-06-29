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


public class AviatorProcessJobConfig implements ConfigValidatable, Serializable {
    @Parameter(
            label = "aviator脚本",
            description = "aviator脚本代码处理数据。可在脚本中直接引用Map的Key作为变量，脚本的最后一行为结果返回给引擎（不要分号，也可以通过return语法返回）。\n"
                    + "如果最后以分号结束 或 最后一行为nil 表示返回null，视为丢弃这条记录（相当于过滤的功能）;\n"
                    + "可以用seq.map返回单个对象；或用数组seq.list返回多个对象;如果返回的不是Map类型结果，则统一使用 __result__ 字段来存储返回值；",
            inputType = InputType.AVIATOR_SCRIPT
    )
    private String script;

    @Parameter(
            label = "使用顶层doc字段",
            description = "把数据Map放在doc字段下，表达式里需要通过 doc.{columnName} 来引用",
            defaultValue = "false"
    )
    private Boolean topLevel;

    public Boolean getTopLevel() {
        return topLevel;
    }

    public void setTopLevel(Boolean topLevel) {
        this.topLevel = topLevel;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public void validate() throws JobConfigValidationException {
        try {
            AviatorUtil.instance.validate(script);
        } catch (Exception ex) {
            throw new JobConfigValidationException(ex.getMessage());
        }
    }
}
