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
import com.eoi.jax.flink.job.common.AdvanceConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.sources.DefinedProctimeAttribute;
import org.apache.flink.table.sources.DefinedRowtimeAttributes;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MapStreamTableSource extends AdvanceConfig implements
        StreamTableSource<Row>,
        DefinedProctimeAttribute,
        DefinedRowtimeAttributes {

    private TableSchema tableSchema;

    private DataStream<Map<String, Object>> mapDataStream;

    private List<RowtimeAttributeDescriptor> rowtimeAttributeDescriptors;

    private Optional<String> proctimeAttribute;

    private String [] columnNames;

    private Map<String,Object> opts;



    public MapStreamTableSource(TableSchema schema,
                                List<SchemaColumnDef> columnDefs,
                                List<RowtimeAttributeDescriptor> rowtimeAttributeDescriptors,
                                Optional<String> proctimeAttribute,
                                DataStream<Map<String, Object>> streamSource,
                                Map<String,Object> opts) {
        tableSchema = schema;
        mapDataStream = streamSource;
        this.rowtimeAttributeDescriptors = rowtimeAttributeDescriptors;
        this.proctimeAttribute = proctimeAttribute;
        this.opts = opts;

        Map<String,String> columnNameMap = new HashMap<>();
        for (SchemaColumnDef columnDef : columnDefs) {
            if (!StrUtil.isEmpty(columnDef.getAliasName())) {
                columnNameMap.put(columnDef.getAliasName(),columnDef.getColumnName());
            }
        }

        columnNames = new String [tableSchema.getFieldNames().length];
        for (int i = 0; i < tableSchema.getFieldNames().length; i++) {
            String aliasColName = tableSchema.getFieldNames()[i];
            if (columnNameMap.containsKey(aliasColName)) {
                columnNames[i] = columnNameMap.get(aliasColName);
            } else {
                columnNames[i] = aliasColName;
            }
        }
    }

    @Override
    public DataType getProducedDataType() {
        return tableSchema.toRowDataType();
    }

    @Override
    public TypeInformation<Row> getReturnType() {
        return tableSchema.toRowType();
    }

    @Override
    public TableSchema getTableSchema() {
        return tableSchema;
    }

    @Override
    public List<RowtimeAttributeDescriptor> getRowtimeAttributeDescriptors() {
        return rowtimeAttributeDescriptors;
    }

    @Override
    public String getProctimeAttribute() {
        return proctimeAttribute.orElse(null);
    }

    @Override
    public DataStream<Row> getDataStream(StreamExecutionEnvironment execEnv) {
        return setAdvanceConfig(mapDataStream.map(new MapToRowMapFunction(columnNames, tableSchema.toRowType())),this.opts);
    }
}
