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

public class JsonParseJobConfig implements Serializable {
    @Parameter(
            label = "输入字段",
            description = "需要解析的json字段名"
    )
    private String sourceField;

    @Parameter(
            label = "平铺拼接符",
            description = "将嵌套的json结构平铺，使用平铺拼接符拼接生成字段名",
            optional = true
    )
    private String flatDelimiter;

    @Parameter(
            label = "输出字段",
            description = "解析结果输出到指定字段下，如果设置了输出字段名，解析结果将作为这个字段的子对象存在。如果不设置，解析结果将放置在最外层",
            optional = true
    )
    private String outputField;

    public String getSourceField() {
        return sourceField;
    }

    public JsonParseJobConfig setSourceField(String sourceField) {
        this.sourceField = sourceField;
        return this;
    }

    public String getFlatDelimiter() {
        return flatDelimiter;
    }

    public JsonParseJobConfig setFlatDelimiter(String flatDelimiter) {
        this.flatDelimiter = flatDelimiter;
        return this;
    }

    public String getOutputField() {
        return outputField;
    }

    public JsonParseJobConfig setOutputField(String outputField) {
        this.outputField = outputField;
        return this;
    }

    public static JsonParseJobConfig fromMap(Map<String, Object> configMap) {
        JsonParseJobConfig config = new JsonParseJobConfig();
        ParamUtil.configJobParams(config, configMap);
        return config;
    }
}
