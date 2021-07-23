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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.Parameter;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;


public class StreamToTableJobConfig implements ConfigValidatable, Serializable {

    public transient Schema schema;

    public transient TableSchema tableSchema;

    public transient List<RowtimeAttributeDescriptor> rowtimeAttributeDescriptors;

    public transient Optional<String> proctimeAttribute;

    @Parameter(
            label = "字段定义",
            description = "转成的table需要哪些字段"
    )
    private List<SchemaColumnDef> columns;

    @Parameter(
            label = "是否在table中添加event_time",
            description = "是否在table中添加event_time",
            optional = true,
            defaultValue = "false"
    )
    private boolean eventTimeEnabled;

    @Parameter(
            label = "输出event时间戳字段名称",
            description = "在下游sql语句里可使用的字段名",
            optional = true,
            defaultValue = "event_time"
    )
    private String eventTimeOutputField;

    @Parameter(
            label = "业务时间最大延迟，单位毫秒",
            description = "event模式下，业务时间最大延迟，单位毫秒",
            optional = true,
            defaultValue = "10000"
    )
    private int allowedLateness;

    @Parameter(
            label = "时间字段",
            description = "event模式下，通过时间字段设置水位线；时间字段无法重命名为目标字段名",
            optional = true
    )
    private String timeFieldName;

    @Parameter(
            label = "时间格式",
            description = "event模式下，支持UNIX_S, UNIX_MS以及通用时间表达格式；",
            optional = true,
            defaultValue = "UNIX_MS"
    )
    private String timeFormat;

    @Parameter(
            label = "输入时间格式方言",
            description = "event模式下，支持用户设定时间方言，默认的时区为: en-US，可选的时区参见列表: https://www.andiamo.co.uk/resources/iso-language-codes",
            optional = true,
            defaultValue = "en-US"
    )
    private String timeLocale;

    @Parameter(
            label = "是否开启北京时间",
            description = "是否开启北京时间",
            optional = true,
            defaultValue = "false"
    )
    private boolean windowOffsetBeiJing;

    @Parameter(
            label = "是否复用上游的event time以及watermark",
            description = "是否在schema生成时，使用上游数据流已经生成的event time以及watermark",
            optional = true,
            defaultValue = "false"
    )
    private boolean eventTimeFromSource;

    @Parameter(
            label = "是否在table中添加proc_time",
            description = "是否在table中添加proc_time",
            optional = true,
            defaultValue = "false"
    )
    private boolean procTimeEnabled;

    @Parameter(
            label = "输出processing时间戳字段名称",
            description = "在下游sql语句里可使用的字段名，注意按照该类型生成的时间是UTC时间，与北京时间差8小时",
            optional = true,
            defaultValue = "proc_time"
    )
    String procTimeOutputField;

    @Parameter(
            label = "输出表名",
            description = "下游SqlJob可select from此表名"
    )
    private String tableName;


    public List<SchemaColumnDef> getColumns() {
        return columns;
    }

    public void setColumns(List<SchemaColumnDef> columns) {
        this.columns = columns;
    }

    public boolean isEventTimeEnabled() {
        return eventTimeEnabled;
    }

    public void setEventTimeEnabled(boolean eventTimeEnabled) {
        this.eventTimeEnabled = eventTimeEnabled;
    }

    public String getEventTimeOutputField() {
        return eventTimeOutputField;
    }

    public void setEventTimeOutputField(String eventTimeOutputField) {
        this.eventTimeOutputField = eventTimeOutputField;
    }

    public int getAllowedLateness() {
        return allowedLateness;
    }

    public void setAllowedLateness(int allowedLateness) {
        this.allowedLateness = allowedLateness;
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

    public String getTimeLocale() {
        return timeLocale;
    }

    public void setTimeLocale(String timeLocale) {
        this.timeLocale = timeLocale;
    }

    public boolean isEventTimeFromSource() {
        return eventTimeFromSource;
    }

    public void setEventTimeFromSource(boolean eventTimeFromSource) {
        this.eventTimeFromSource = eventTimeFromSource;
    }

    public boolean isProcTimeEnabled() {
        return procTimeEnabled;
    }

    public void setProcTimeEnabled(boolean procTimeEnabled) {
        this.procTimeEnabled = procTimeEnabled;
    }

    public String getProcTimeOutputField() {
        return procTimeOutputField;
    }

    public void setProcTimeOutputField(String procTimeOutputField) {
        this.procTimeOutputField = procTimeOutputField;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isWindowOffsetBeiJing() {
        return windowOffsetBeiJing;
    }

    public void setWindowOffsetBeiJing(boolean windowOffsetBeiJing) {
        this.windowOffsetBeiJing = windowOffsetBeiJing;
    }

    @Override
    public void validate() throws JobConfigValidationException {
        if (eventTimeEnabled) {
            if (StrUtil.isEmpty(timeFieldName) || StrUtil.isEmpty(timeFormat)) {
                throw new JobConfigValidationException("missing timeFieldName or timeFormat");
            }
        }
    }
}
