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
import java.util.List;
import java.util.Map;

public class GrokParseJobConfig implements Serializable {

    @Parameter(
            label = "输入字段",
            description = "需要正则解析字段名"
    )
    private String sourceField;

    @Parameter(
            label = "正则表达式",
            description = "使用正则表达式提取内容"
    )
    private List<String> matches;

    @Parameter(
            label = "输出字段",
            description = "解析结果输出到指定字段下，如果设置了输出字段名，解析结果将作为这个字段的子对象存在。如果不设置，解析结果将放置在最外层",
            optional = true
    )
    private String outputField;

    public String getSourceField() {
        return sourceField;
    }

    public GrokParseJobConfig setSourceField(String sourceField) {
        this.sourceField = sourceField;
        return this;
    }

    public List<String> getMatches() {
        return matches;
    }

    public GrokParseJobConfig setMatches(List<String> matches) {
        this.matches = matches;
        return this;
    }

    public String getOutputField() {
        return outputField;
    }

    public GrokParseJobConfig setOutputField(String outputField) {
        this.outputField = outputField;
        return this;
    }

    public static GrokParseJobConfig fromMap(Map<String, Object> mapConfig) {
        GrokParseJobConfig config = new GrokParseJobConfig();
        ParamUtil.configJobParams(config, mapConfig);
        return config;
    }
}
