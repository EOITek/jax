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
import com.eoi.jax.api.JobConfigValidationException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.eoi.jax.flink.job.common.TimestampConvertJobConfig.TimestampConvertJobConfigConstants.MS_FORMAT;
import static com.eoi.jax.flink.job.common.TimestampConvertJobConfig.TimestampConvertJobConfigConstants.S_FORMAT;

public class TimestampConvertUtil implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(TimestampConvertUtil.class);

    TimestampConvertJobConfig convertConfig;
    boolean sourceMSFormat = false;
    boolean sourceSFormat = false;
    boolean sourceStringFormat = false;
    private transient DateTimeFormatter dateTimeSourceFormatter = null;

    boolean toMSFormat = false;
    boolean toSFormat = false;
    boolean toStringFormat = false;
    private transient DateTimeFormatter dateTimeToFormatter = null;

    Locale fromLocale;
    Locale toLocale;
    DateTimeZone fromTimezone;
    DateTimeZone toTimezone;

    public TimestampConvertUtil(TimestampConvertJobConfig config) throws JobConfigValidationException {
        this.convertConfig = config;

        try {
            if (!StrUtil.isEmpty(config.getFromLocale())) {
                this.fromLocale = Locale.forLanguageTag(config.getFromLocale());
            }
            if (!StrUtil.isEmpty(config.getToLocale())) {
                this.toLocale = Locale.forLanguageTag(config.getToLocale());
            }
            if (!StrUtil.isEmpty(config.getFromTimezone())) {
                this.fromTimezone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(ZoneId.of(config.getFromTimezone())));
            }
            if (!StrUtil.isEmpty(config.getToTimezone())) {
                this.toTimezone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(ZoneId.of(config.getToTimezone())));
            }
        } catch (Exception ex) {
            throw new JobConfigValidationException(ex.getMessage());
        }

        if (!StrUtil.isEmpty(config.getFromFormat())) {
            if (this.convertConfig.getFromFormat().equalsIgnoreCase(MS_FORMAT)) {
                this.sourceMSFormat = true;
            } else if (this.convertConfig.getFromFormat().equalsIgnoreCase(S_FORMAT)) {
                this.sourceSFormat = true;
            } else {
                this.sourceStringFormat = true;
            }
        }

        if (!StrUtil.isEmpty(config.getToFormat())) {
            if (this.convertConfig.getToFormat().equalsIgnoreCase(MS_FORMAT)) {
                this.toMSFormat = true;
            } else if (this.convertConfig.getToFormat().equalsIgnoreCase(S_FORMAT)) {
                this.toSFormat = true;
            } else {
                this.toStringFormat = true;
            }
        }
    }


    public void setDateTimeFormatter() {
        if (this.sourceStringFormat) {
            this.dateTimeSourceFormatter = DateTimeFormat.forPattern(convertConfig.getFromFormat()).withLocale(this.fromLocale);
            if (this.fromTimezone != null) {
                this.dateTimeSourceFormatter = this.dateTimeSourceFormatter.withZone(this.fromTimezone);
            }
        }
        if (this.toStringFormat && StrUtil.isNotBlank(convertConfig.getToFormat())) {
            this.dateTimeToFormatter = DateTimeFormat.forPattern(convertConfig.getToFormat()).withLocale(this.toLocale);
        }
    }

    public DateTime parseDateTimeFromSource(Object sourceObj) {
        if (sourceObj == null) {
            return null;
        }

        if (Boolean.TRUE.equals(this.sourceStringFormat) && sourceObj instanceof String) {
            if (dateTimeSourceFormatter == null) {
                setDateTimeFormatter();
            }
            return DateTime.parse(sourceObj.toString(), dateTimeSourceFormatter);
        } else if (Boolean.TRUE.equals(this.sourceMSFormat) && sourceObj instanceof Number) {
            return new DateTime(((Number) sourceObj).longValue());
        } else if (Boolean.TRUE.equals(this.sourceSFormat) && sourceObj instanceof Number) {
            return new DateTime(((Number) sourceObj).longValue() * 1000);
        } else if (sourceObj instanceof DateTime) {
            return (DateTime) sourceObj;
        } else if (sourceObj instanceof Date) {
            return new DateTime(((Date) sourceObj).getTime());
        } else {
            return null;
        }
    }

    public Object formatDateTime(DateTime dt) {
        if (dt == null) {
            return null;
        }

        DateTime dtWithTimezone = dt.toDateTime(this.toTimezone);
        if (this.toMSFormat) {
            return dtWithTimezone.getMillis();
        } else if (this.toSFormat) {
            return dtWithTimezone.getMillis() / 1000;
        } else if (Boolean.TRUE.equals(this.toStringFormat)) {
            if (dateTimeToFormatter == null) {
                this.setDateTimeFormatter();
            }
            return dtWithTimezone.toString(dateTimeToFormatter);
        } else {
            return dtWithTimezone.toString();
        }
    }
}
