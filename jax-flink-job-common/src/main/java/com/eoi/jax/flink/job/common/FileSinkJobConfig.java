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

package com.eoi.jax.flink.job.common;

import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;
import java.util.List;


public class FileSinkJobConfig implements ConfigValidatable, Serializable {
    public static final String DATA_FORMAT_JSON = "json";
    public static final String DATA_FORMAT_CSV = "csv";

    @Parameter(
            label = "存储路径",
            description = "存储进文件的路径，如：hdfs://192.168.31.133:9000/tmp/json"
    )
    private String basePath;

    @Parameter(
            label = "存储文件的周期",
            description = "存储文件的周期，在每个周期里会生成一目录, \n"
                    + "例如：分钟(yyyyMMddHHmm), 小时(yyyyMMddHH), 天(yyyyMMdd), 此处时间为服务器当前时间，并非事件时间",
            optional = true,
            defaultValue = "yyyyMMddHH"
    )
    private String bucketFormatString;

    @Parameter(
            label = "字段列表",
            description = "存储的字段列表"
    )
    private List<String> fields;

    @Parameter(
            label = "数据存储格式",
            description = "数据存储格式",
            candidates = {DATA_FORMAT_JSON, DATA_FORMAT_CSV},
            defaultValue = DATA_FORMAT_JSON
    )
    private String dataFormat;

    @Parameter(
            label = "文件最大size（MB）",
            description = "[rolling policy]单位MB",
            optional = true,
            defaultValue = "128"
    )
    private Long maxPartSize;

    @Parameter(
            label = "文件滚动最大时间（毫秒）",
            description = "[rolling policy]从文件创建时间开始，超过最大时间就会滚动，单位毫秒",
            optional = true,
            defaultValue = "60000"
    )
    private Long rolloverInterval;

    @Parameter(
            label = "文件闲置最大时间（毫秒）",
            description = "[rolling policy]从文件更新时间开始，超过最大时间就会滚动，单位毫秒",
            optional = true,
            defaultValue = "60000"
    )
    private Long inactivityInterval;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getBucketFormatString() {
        return bucketFormatString;
    }

    public void setBucketFormatString(String bucketFormatString) {
        this.bucketFormatString = bucketFormatString;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public Long getMaxPartSize() {
        return maxPartSize;
    }

    public void setMaxPartSize(Long maxPartSize) {
        this.maxPartSize = maxPartSize;
    }

    public Long getRolloverInterval() {
        return rolloverInterval;
    }

    public void setRolloverInterval(Long rolloverInterval) {
        this.rolloverInterval = rolloverInterval;
    }

    public Long getInactivityInterval() {
        return inactivityInterval;
    }

    public void setInactivityInterval(Long inactivityInterval) {
        this.inactivityInterval = inactivityInterval;
    }

    @Override
    public void validate() throws JobConfigValidationException {
    }
}
