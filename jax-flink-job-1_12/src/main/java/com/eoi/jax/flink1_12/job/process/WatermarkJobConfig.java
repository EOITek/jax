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

package com.eoi.jax.flink1_12.job.process;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;


public class WatermarkJobConfig implements ConfigValidatable, Serializable {
    public static final String WATERMARK_SOURCE_TIME_FIELD = "time_field";
    public static final String WATERMARK_SOURCE_SYS_TIME = "sys_time";

    @Parameter(
            label = "获取时间来源",
            description = "来源包括：数据内时间字段（time_field）或系统时间（sys_time）",
            optional = true,
            defaultValue = "time_field",
            candidates = {WATERMARK_SOURCE_TIME_FIELD, WATERMARK_SOURCE_SYS_TIME}
    )
    private String watermarkSource;

    @Parameter(
            label = "时间字段",
            description = "通过时间字段设置水位线；watermarkSource为time_field时必填",
            optional = true
    )
    private String timeFieldName;

    @Parameter(
            label = "时间格式",
            description = "支持UNIX_S, UNIX_MS以及通用时间表达格式；watermarkSource为time_field时必填",
            optional = true
    )
    private String timeFormat;

    @Parameter(
            label = "输入时间格式方言",
            description = "支持用户设定时间方言，默认的时区为: en-US，可选的时区参见列表: https://www.andiamo.co.uk/resources/iso-language-codes",
            optional = true,
            defaultValue = "en-US"
    )
    private String timeLocale;

    @Parameter(
            label = "最大可容忍乱序时间（秒）",
            description = "生成的watermark等于当前数据时间减去此配置时间，也就是聚合窗口延迟关闭，可容忍数据晚到时间范围",
            defaultValue = "0"
    )
    private Long lagBehindTime;

    @Parameter(
            label = "最大可跳跃时间（秒）",
            description = "数据时间可超过当前watermark的最大时间，如果超过则不设置watermark，0表示不限制",
            defaultValue = "0"
    )
    private Long maxGapTime;

    @Parameter(
            label = "最大可偏离时间（秒）",
            description = "数据时间可偏离当前系统时间的最大值，如果超过则不设置watermark，0表示不限制",
            defaultValue = "0"
    )
    private Long maxDeviateTime;

    @Parameter(
            label = "最大没有输入数据时间（秒）",
            description = "当某个分区没有数据超过此设置时，系统会生成watermark时会忽略此分区的事件时间"
                    + "参考：https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/event_timestamps_watermarks.html#dealing-with-idle-sources",
            defaultValue = "0"
    )
    private Long idlenessTime;

    @Parameter(
            label = "是否复用已有的watermark",
            description = "当数据流之前已经设置过watermark，是否复用",
            defaultValue = "false"
    )
    private boolean reuseWatermark;

    public boolean isReuseWatermark() {
        return reuseWatermark;
    }

    public void setReuseWatermark(boolean reuseWatermark) {
        this.reuseWatermark = reuseWatermark;
    }

    public Long getIdlenessTime() {
        return idlenessTime;
    }

    public void setIdlenessTime(Long idlenessTime) {
        this.idlenessTime = idlenessTime;
    }

    public String getTimeLocale() {
        return timeLocale;
    }

    public void setTimeLocale(String timeLocale) {
        this.timeLocale = timeLocale;
    }

    public Long getMaxDeviateTime() {
        return maxDeviateTime;
    }

    public void setMaxDeviateTime(Long maxDeviateTime) {
        this.maxDeviateTime = maxDeviateTime;
    }

    public Long getMaxGapTime() {
        return maxGapTime;
    }

    public void setMaxGapTime(Long maxGapTime) {
        this.maxGapTime = maxGapTime;
    }

    public String getWatermarkSource() {
        return watermarkSource;
    }

    public void setWatermarkSource(String watermarkSource) {
        this.watermarkSource = watermarkSource;
    }

    public String getTimeFieldName() {
        return timeFieldName;
    }

    public void setTimeFieldName(String timeFieldName) {
        this.timeFieldName = timeFieldName;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public Long getLagBehindTime() {
        return lagBehindTime;
    }

    public void setLagBehindTime(Long lagBehindTime) {
        this.lagBehindTime = lagBehindTime;
    }

    @Override
    public void validate() throws JobConfigValidationException {
        if (WATERMARK_SOURCE_TIME_FIELD.equals(watermarkSource)) {
            if (StrUtil.isEmpty(timeFieldName) || StrUtil.isEmpty(timeFormat)) {
                throw new JobConfigValidationException("missing timeFieldName or timeFormat");
            }
        }
    }
}
