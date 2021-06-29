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

package com.eoi.jax.flink.job.source;

import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.api.reflect.ParamUtil;

import java.util.Map;

public class JavascriptSimulateMapSourceJobConfig {
    @Parameter(
            label = "js脚本",
            description = "javascript脚本代码。脚本最后一行就是返回的json对象（不需要return关键字），该对象会被发往下游。"
                    + "可以通过返回数组一次性发送多条数据",
            inputType = InputType.JAVASCRIPT
    )
    private String script;

    @Parameter(
            label = "忽略异常",
            description = "是否忽略执行脚本过程过的异常。如果异常抛出可能会使得任务失败",
            optional = true,
            defaultValue = "true"
    )
    private Boolean ignoreException;

    @Parameter(
            label = "间隔执行时间(毫秒)",
            description = "执行脚本的间隔时间，单位为毫秒",
            optional = true,
            defaultValue = "1000"
    )
    private Integer interval;

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

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public static JavascriptSimulateMapSourceJobConfig fromMap(Map<String, Object> config) {
        JavascriptSimulateMapSourceJobConfig jobConfig = new JavascriptSimulateMapSourceJobConfig();
        ParamUtil.configJobParams(jobConfig, config);
        return jobConfig;
    }
}
