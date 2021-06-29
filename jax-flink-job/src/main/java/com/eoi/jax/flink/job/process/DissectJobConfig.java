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
import com.eoi.jax.api.reflect.ParamUtil;

import java.io.Serializable;
import java.util.Map;

public class DissectJobConfig implements Serializable {
    @Parameter(
            label = "输入字段",
            description = "解构源字段中的文本"
    )
    private String sourceField;

    @Parameter(
            label = "解构模式匹配",
            description = "用于匹配源字段文本内容的模式表达式"
    )
    private String dissectPattern;

    @Parameter(
            label = "是否删除源字段",
            description = "是否顺便删除源字段",
            optional = true,
            defaultValue = "false"
    )
    private Boolean removeSourceField;

    @Parameter(
            label = "输出字段",
            description = "解构的结果输出到哪个字段。如果设置一个输出字段名的话，解构的结果将作为这个字段的子对象存在。"
                    + "如果不设置解构的结果将放置在最外层",
            optional = true
    )
    private String outputField;

    @Parameter(
            label = "拼接操作连接符",
            description = "对于使用+做拼接提取的时候，多个匹配的值通过什么字符进行连接，默认通过空格符连接",
            optional = true,
            defaultValue = " "
    )
    private String appendSeparator;

    public String getSourceField() {
        return sourceField;
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    public String getDissectPattern() {
        return dissectPattern;
    }

    public void setDissectPattern(String dissectPattern) {
        this.dissectPattern = dissectPattern;
    }

    public Boolean getRemoveSourceField() {
        return removeSourceField;
    }

    public void setRemoveSourceField(Boolean removeSourceField) {
        this.removeSourceField = removeSourceField;
    }

    public String getOutputField() {
        return outputField;
    }

    public void setOutputField(String outputField) {
        this.outputField = outputField;
    }

    public String getAppendSeparator() {
        return appendSeparator;
    }

    public void setAppendSeparator(String appendSeparator) {
        this.appendSeparator = appendSeparator;
    }

    public static DissectJobConfig fromMap(Map<String, Object> configMap) {
        DissectJobConfig dissectJobConfig = new DissectJobConfig();
        ParamUtil.configJobParams(dissectJobConfig, configMap);
        return dissectJobConfig;
    }
}
