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
import com.eoi.jax.api.reflect.ParamUtil;

import java.util.Map;

public class JavascriptProcessJobConfig {

    @Parameter(
            label = "js脚本",
            description = "javascript脚本代码处理数据。可在脚本中使用`doc`作为输入的json对象，脚本的最后一行为结果返回给引擎（不需要return关键字）。"
                    + "可以用数组返回多个对象，相当于flatMap。如果返回null，视为丢弃这条记录（相当于过滤的功能）",
            inputType = InputType.JAVASCRIPT
    )
    private String script;

    @Parameter(
            label = "忽略异常",
            description = "是否忽略执行脚本过程过的异常。如果异常抛出可能会使得任务失败。建议在调试阶段设置为false，生产运行设置为true",
            optional = true,
            defaultValue = "true"
    )
    private Boolean ignoreException;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Boolean getIgnoreException() {
        return ignoreException;
    }

    public void setIgnoreException(Boolean ignoreException) {
        this.ignoreException = ignoreException;
    }

    public static JavascriptProcessJobConfig fromMap(Map<String, Object> config) {
        JavascriptProcessJobConfig javascriptProcessJobConfig = new JavascriptProcessJobConfig();
        ParamUtil.configJobParams(javascriptProcessJobConfig, config);
        return javascriptProcessJobConfig;
    }
}
