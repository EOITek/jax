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

import com.eoi.jax.api.annotation.FlatStringMap;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.api.reflect.ParamUtil;

import java.io.Serializable;
import java.util.Map;

public class TranslateJobConfig implements Serializable {
    @Parameter(
            label = "输入字段",
            description = "需要进行字典替换的列"
    )
    private String sourceField;

    @Parameter(
            label = "字典",
            description = "典替换所需要的字典，若被替换的值不在字典中，则保留原来的值"
    )
    private FlatStringMap dictionary;

    @Parameter(
            label = "输出字段",
            description = "转化结果输出到指定字段（默认为空）。不设置转化结果输出到原字段上（原字段被替换）；否则转化结果输出指定的字段上（原字段保留）",
            optional = true
    )
    private String outputField;

    public String getSourceField() {
        return sourceField;
    }

    public TranslateJobConfig setSourceField(String sourceField) {
        this.sourceField = sourceField;
        return this;
    }

    public FlatStringMap getDictionary() {
        return dictionary;
    }

    public TranslateJobConfig setDictionary(FlatStringMap dictionary) {
        this.dictionary = dictionary;
        return this;
    }

    public String getOutputField() {
        return outputField;
    }

    public TranslateJobConfig setOutputField(String outputField) {
        this.outputField = outputField;
        return this;
    }

    public static TranslateJobConfig fromMap(Map<String, Object> mapConfig) {
        TranslateJobConfig config = new TranslateJobConfig();
        ParamUtil.configJobParams(config, mapConfig);
        return config;
    }
}
