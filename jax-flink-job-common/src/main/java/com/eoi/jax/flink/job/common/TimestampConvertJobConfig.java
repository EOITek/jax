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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.Parameter;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;

public class TimestampConvertJobConfig implements ConfigValidatable, Serializable {
    static class TimestampConvertJobConfigConstants {
        private TimestampConvertJobConfigConstants() {
        }

        public static final String MS_FORMAT = "UNIX_MS";
        public static final String S_FORMAT = "UNIX_S";
    }

    @Parameter(
            label = "输入时间字段",
            description = "输入时间字段"
    )
    private String timestampField;

    @Parameter(
            label = "输入时间格式",
            description = "支持UNIX_S, UNIX_MS以及通用时间表达格式"
    )
    private String fromFormat;

    @Parameter(
            label = "输入时间格式语言",
            description = "支持用户设定时间方言，默认的时区为: en-US，可选的时区参见列表: https://www.andiamo.co.uk/resources/iso-language-codes",
            optional = true,
            defaultValue = "en-US"
    )
    private String fromLocale;

    @Parameter(
            label = "输入时区",
            description = "输入时间所在时区, 例如：`Europe/London`,`PST` or `GMT+5`",
            optional = true
    )
    private String fromTimezone;

    @Parameter(
            label = "输出时区",
            description = "输出时间所在时区, 例如：`Europe/London`,`PST` or `GMT+5`",
            optional = true
    )
    private String toTimezone;

    @Parameter(
            label = "输出时间字段",
            description = "默认不填则同输入时间字段名",
            optional = true
    )
    private String outputTimestampField;

    @Parameter(
            label = "输出时间格式",
            description = "转化后输出的目标格式，支持UNIX_S, UNIX_MS以及通用时间表达格式",
            optional = true,
            defaultValue = "UNIX_MS"
    )
    private String toFormat;

    @Parameter(
            label = "输出时间格式语言",
            description = "支持用户设定时间方言，默认的时区为: en-US，可选的时区参见列表: https://www.andiamo.co.uk/resources/iso-language-codes",
            optional = true,
            defaultValue = "en-US"
    )
    private String toLocale;

    public String getOutputTimestampField() {
        return outputTimestampField;
    }

    public void setOutputTimestampField(String outputTimestampField) {
        this.outputTimestampField = outputTimestampField;
    }

    public String getTimestampField() {
        return timestampField;
    }

    public void setTimestampField(String timestampField) {
        this.timestampField = timestampField;
    }

    public String getFromFormat() {
        return fromFormat;
    }

    public void setFromFormat(String fromFormat) {
        this.fromFormat = fromFormat;
    }

    public String getFromLocale() {
        return fromLocale;
    }

    public void setFromLocale(String fromLocale) {
        this.fromLocale = fromLocale;
    }

    public String getToTimezone() {
        return toTimezone;
    }

    public void setToTimezone(String toTimezone) {
        this.toTimezone = toTimezone;
    }

    public String getToFormat() {
        return toFormat;
    }

    public void setToFormat(String toFormat) {
        this.toFormat = toFormat;
    }

    public String getToLocale() {
        return toLocale;
    }

    public void setToLocale(String toLocale) {
        this.toLocale = toLocale;
    }

    public String getFromTimezone() {
        return fromTimezone;
    }

    public void setFromTimezone(String fromTimezone) {
        this.fromTimezone = fromTimezone;
    }

    @Override
    public void validate() throws JobConfigValidationException {
        if (StrUtil.isBlank(timestampField) || StrUtil.isBlank(fromFormat)) {
            throw new JobConfigValidationException("timestamp job config is invalid");
        }
        try {
            if (isStringFormat(fromFormat)) {
                DateTimeFormat.forPattern(fromFormat);
            }
            if (StrUtil.isNotBlank(toFormat) && isStringFormat(toFormat)) {
                DateTimeFormat.forPattern(toFormat);
            }
        } catch (Exception ex) {
            throw new JobConfigValidationException("timestamp format is invalid", ex);
        }
    }

    private boolean isStringFormat(String format) {
        return !format.equalsIgnoreCase(TimestampConvertJobConfigConstants.MS_FORMAT) && !format.equalsIgnoreCase(TimestampConvertJobConfigConstants.S_FORMAT);
    }
}
