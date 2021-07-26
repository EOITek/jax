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

package com.eoi.jax.flink1_12.job.sink;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.Parameter;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;

import java.io.Serializable;
import java.util.List;


public class FileSinkJobConfig implements ConfigValidatable, Serializable {
    public static final String FILE_FORMAT_ROW = "row";
    public static final String FILE_FORMAT_BULK = "bulk";
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
            label = "文件存储方式",
            description = "[1.12]存储文件方式，支持: row(支持rolling policy参数，不支持文件压缩), bulk(不支持rolling policy参数，支持文件压缩)",
            candidates = {FILE_FORMAT_ROW, FILE_FORMAT_BULK},
            defaultValue = FILE_FORMAT_ROW
    )
    private String fileFormat;

    @Parameter(
            label = "文件最大size（MB）",
            description = "[rolling policy]单位MB",
            optional = true,
            defaultValue = "128",
            availableCondition = "fileFormat == 'row'"
    )
    private Long maxPartSize;

    @Parameter(
            label = "文件滚动最大时间（毫秒）",
            description = "[rolling policy]从文件创建时间开始，超过最大时间就会滚动，单位毫秒",
            optional = true,
            defaultValue = "60000",
            availableCondition = "fileFormat == 'row'"
    )
    private Long rolloverInterval;

    @Parameter(
            label = "文件闲置最大时间（毫秒）",
            description = "[rolling policy]从文件更新时间开始，超过最大时间就会滚动，单位毫秒",
            optional = true,
            defaultValue = "60000",
            availableCondition = "fileFormat == 'row'"
    )
    private Long inactivityInterval;

    @Parameter(
            label = "文件压缩方式",
            description = "[1.12]只在存储文件方式为bulk时生效，不填表示不压缩；支持org.apache.hadoop.io.compress下的几种压缩方式",
            optional = true,
            candidates = {"BZip2","Deflate","Gzip","Lz4","Snappy","ZStandard"},
            availableCondition = "fileFormat == 'bulk'"
    )
    private String compressionCodec;

    @Parameter(
            label = "文件名前缀",
            description = "[1.12]输出的文件名前缀",
            optional = true
    )
    private String fileNamePrefix;

    @Parameter(
            label = "文件名后缀",
            description = "[1.12]输出的文件名后缀",
            optional = true
    )
    private String fileNameSuffix;

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getCompressionCodec() {
        return compressionCodec;
    }

    public void setCompressionCodec(String compressionCodec) {
        this.compressionCodec = compressionCodec;
    }

    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    public String getFileNameSuffix() {
        return fileNameSuffix;
    }

    public void setFileNameSuffix(String fileNameSuffix) {
        this.fileNameSuffix = fileNameSuffix;
    }

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

    public OutputFileConfig outputFileConfig() {
        OutputFileConfig.OutputFileConfigBuilder builder = OutputFileConfig.builder();

        if (StrUtil.isNotEmpty(getFileNamePrefix())) {
            builder = builder.withPartPrefix(getFileNamePrefix());
        }
        if (StrUtil.isNotEmpty(getFileNameSuffix())) {
            builder = builder.withPartSuffix(getFileNameSuffix());
        }
        return builder.build();
    }

    @Override
    public void validate() throws JobConfigValidationException {
    }
}
