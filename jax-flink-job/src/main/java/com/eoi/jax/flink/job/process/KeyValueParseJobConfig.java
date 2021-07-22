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
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KeyValueParseJobConfig implements Serializable {

    @Parameter(
            label = "输入字段",
            description = "需要被转化为kv的字段"
    )
    private String sourceField;

    @Parameter(
            label = "字段分割符",
            description = "字段分割符，根据分隔符分割成多个字段组"
    )
    private String fieldSplit;

    @Parameter(
            label = "键值分隔符",
            description = "键值分隔符，根据符号分割字段名和字段值"
    )
    private String valueSplit;

    @Parameter(
            label = "值前后缀",
            description = "删除值的头跟尾存在的空格",
            optional = true
    )
    private Boolean trimValue;

    @Parameter(
            label = "字段名前后缀",
            description = "删除字段名的头跟尾中存在的空格",
            optional = true
    )
    private Boolean trimKey;

    @Parameter(
            label = "字段黑名单",
            description = "黑名单里的字段将被剔除",
            optional = true
    )
    private List<String> excludeKeys = Collections.emptyList();

    @Parameter(
            label = "字段白名单",
            description = "只保留白名单里的字段",
            optional = true
    )
    private List<String> includeKeys = Collections.emptyList();

    @Parameter(
            label = "输出字段",
            description = "解析结果输出到指定字段下，如果设置了输出字段名，解析结果将作为这个字段的子对象存在。如果不设置，解析结果将放置在最外层",
            optional = true
    )
    private String outputField;

    public String getSourceField() {
        return sourceField;
    }

    public KeyValueParseJobConfig setSourceField(String sourceField) {
        this.sourceField = sourceField;
        return this;
    }

    public String getFieldSplit() {
        return fieldSplit;
    }

    public KeyValueParseJobConfig setFieldSplit(String fieldSplit) {
        this.fieldSplit = fieldSplit;
        return this;
    }

    public String getValueSplit() {
        return valueSplit;
    }

    public KeyValueParseJobConfig setValueSplit(String valueSplit) {
        this.valueSplit = valueSplit;
        return this;
    }

    public Boolean getTrimValue() {
        return trimValue;
    }

    public KeyValueParseJobConfig setTrimValue(Boolean trimValue) {
        this.trimValue = trimValue;
        return this;
    }

    public Boolean getTrimKey() {
        return trimKey;
    }

    public KeyValueParseJobConfig setTrimKey(Boolean trimKey) {
        this.trimKey = trimKey;
        return this;
    }

    public List<String> getExcludeKeys() {
        return excludeKeys;
    }

    public KeyValueParseJobConfig setExcludeKeys(List<String> excludeKeys) {
        this.excludeKeys = excludeKeys;
        return this;
    }

    public List<String> getIncludeKeys() {
        return includeKeys;
    }

    public KeyValueParseJobConfig setIncludeKeys(List<String> includeKeys) {
        this.includeKeys = includeKeys;
        return this;
    }

    public String getOutputField() {
        return outputField;
    }

    public KeyValueParseJobConfig setOutputField(String outputField) {
        this.outputField = outputField;
        return this;
    }

    public static KeyValueParseJobConfig fromMap(Map<String, Object> configMap) {
        KeyValueParseJobConfig config = new KeyValueParseJobConfig();
        ParamUtil.configJobParams(config, configMap);
        return config;
    }
}
