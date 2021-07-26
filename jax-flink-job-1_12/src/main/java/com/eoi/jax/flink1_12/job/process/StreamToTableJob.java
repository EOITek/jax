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
import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.common.OperatorCategory;
import com.eoi.jax.flink.job.common.AdvanceConfig;
import com.eoi.jax.flink.job.common.TimestampConvertJobConfig;
import com.eoi.jax.flink.job.common.TimestampConvertUtil;
import com.eoi.jax.flink.job.process.EventFilter;
import com.eoi.jax.flink.job.process.EventTimeExtract;
import com.eoi.jax.flink.job.process.MapStreamTableSource;
import com.eoi.jax.flink.job.process.SchemaColumnDef;
import com.eoi.jax.flink.job.process.StreamToTableJobConfig;
import com.eoi.jax.flink.job.process.TimestampMapper;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.descriptors.DescriptorProperties;
import org.apache.flink.table.descriptors.Rowtime;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.descriptors.SchemaValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.flink.table.descriptors.Schema.SCHEMA;

@Job(
        name = "StreamToTableJob",
        display = "Map转Table_1.12",
        description = "按指定schema转成Table，下游可以用SqlJob进行数据操作",
        icon = "StreamToTableJob.svg",
        doc = "SqlJob.md",
        category = OperatorCategory.DATA_TYPE_CONVERSION
)
public class StreamToTableJob extends AdvanceConfig implements FlinkProcessJobBuilder<DataStream<Map<String, Object>>, Table, StreamToTableJobConfig> {

    @Override
    public Table build(FlinkEnvironment context, DataStream<Map<String, Object>> mapDataStream, StreamToTableJobConfig config, JobMetaConfig metaConfig) throws Throwable {
        DataStream<Map<String, Object>> stream = mapDataStream;

        /*
          处理窗口时间
         */
        if (config.isEventTimeEnabled()) {
            context.streamEnv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

            TimestampConvertJobConfig convertConfig = new TimestampConvertJobConfig();
            convertConfig.setFromFormat(config.getTimeFormat());
            convertConfig.setFromLocale(config.getTimeLocale());
            TimestampConvertUtil convertUtil = new TimestampConvertUtil(convertConfig);

            /*
                过滤非法数据
            */
            DataStream<Map<String, Object>> filterStream = setAdvanceConfig(
                    mapDataStream.filter(new EventFilter(config.getTimeFieldName(),convertUtil))
                            .name(metaConfig.getJobEntry() + ".EventFilter")
                            .uid(metaConfig.getJobId() + ".EventFilter"), metaConfig.getOpts());
            DataStream<Map<String, Object>> e = setAdvanceConfig(
                    filterStream.map(new TimestampMapper(config.getTimeFieldName(), config.isWindowOffsetBeiJing(),convertUtil))
                            .name(metaConfig.getJobEntry() + ".TimestampMapper")
                            .uid(metaConfig.getJobId() + ".TimestampMapper"), metaConfig.getOpts());
            stream = setAdvanceConfig(e.assignTimestampsAndWatermarks(new EventTimeExtract(Time.milliseconds(config.getAllowedLateness()))), metaConfig.getOpts());
        }

        MapStreamTableSource mapStreamTableSource = new MapStreamTableSource(
                config.tableSchema,
                config.getColumns(),
                config.rowtimeAttributeDescriptors,
                config.proctimeAttribute,
                stream,
                metaConfig.getOpts()
        );

        String tableName = config.getTableName();

        Table table = context.tableEnv.fromTableSource(mapStreamTableSource);
        context.tableEnv.registerTable(tableName, table);
        return table;
    }

    @Override
    public StreamToTableJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        StreamToTableJobConfig config = new StreamToTableJobConfig();
        ParamUtil.configJobParams(config, mapConfig);

        try {
            List<SchemaColumnDef> columns = config.getColumns();
            if (!StrUtil.isEmpty(config.getTimeFieldName())) {
                // 时间字段无法重命名
                Optional<SchemaColumnDef> timeCol = columns.stream().filter(x -> config.getTimeFieldName().equals(x.getColumnName())).findFirst();
                if (timeCol.isPresent()) {
                    timeCol.get().setAliasName(null);
                }
            }
            config.schema = parse(columns);

            if (config.isEventTimeEnabled()) {
                if (!StrUtil.isEmpty(config.getTimeFieldName()) && !config.isEventTimeFromSource()) {
                    config.schema = config.schema.field(config.getEventTimeOutputField(), Types.SQL_TIMESTAMP)
                            .rowtime(new Rowtime().timestampsFromField(config.getTimeFieldName())
                                    .watermarksPeriodicBounded(config.getAllowedLateness()));
                } else {
                    config.schema = config.schema.field(config.getEventTimeOutputField(), Types.SQL_TIMESTAMP)
                            .rowtime(new Rowtime().timestampsFromSource().watermarksFromSource());
                }
            }
            if (config.isProcTimeEnabled()) {
                config.schema = config.schema.field(config.getProcTimeOutputField(), Types.SQL_TIMESTAMP).proctime();
            }
            Map<String,String> properties = config.schema.toProperties();
            DescriptorProperties dp = new DescriptorProperties(true);
            dp.putProperties(properties);
            config.tableSchema = dp.getTableSchema(SCHEMA);
            config.rowtimeAttributeDescriptors = SchemaValidator.deriveRowtimeAttributes(dp);
            config.proctimeAttribute = SchemaValidator.deriveProctimeAttribute(dp);
        } catch (Exception ex) {
            throw new JobConfigValidationException(ex.getLocalizedMessage());
        }
        return config;
    }

    public static Schema parse(List<SchemaColumnDef> columns) {
        Schema schema = new Schema();
        for (SchemaColumnDef column : columns) {
            TypeInformation<?> type = parseType(column.getColumnType());
            if (StrUtil.isEmpty(column.getAliasName())) {
                schema.field(column.getColumnName(), type);
            } else {
                schema.field(column.getAliasName(), type);
            }
        }
        return schema;
    }

    private static TypeInformation<?> parseType(String type) {
        switch (type.toLowerCase()) {
            case "string":
                return Types.STRING;
            case "int":
                return Types.INT;
            case "double":
                return Types.DOUBLE;
            case "float":
                return Types.FLOAT;
            case "decimal":
                return Types.BIG_DEC;
            case "boolean":
                return Types.BOOLEAN;
            case "short":
                return Types.SHORT;
            case "long":
                return Types.LONG;
            case "timestamp":
                return Types.SQL_TIMESTAMP;
            case "time":
                return Types.SQL_TIME;
            case "date":
                return Types.SQL_DATE;
            default:
                return Types.STRING;
        }
    }
}
