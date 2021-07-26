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
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkSinkJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.flink.job.process.MapStreamTableSource;
import com.eoi.jax.flink.job.sink.JDBCSinkJobConfig;
import com.eoi.jax.flink1_12.job.process.StreamToTableJob;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.descriptors.ConnectorDescriptor;
import org.apache.flink.table.descriptors.DescriptorProperties;
import org.apache.flink.table.descriptors.JDBCValidator;
import org.apache.flink.table.descriptors.SchemaValidator;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.flink.table.descriptors.Schema.SCHEMA;

@Job(
        name = "JDBCSinkJob",
        display = "输出到JDBC数据库_1.12",
        description = "把数据通过jdbc批量写入到表中",
        doc = "JDBCSinkJob.md",
        icon = "JDBCSinkJob.svg"
)
public class JDBCSinkJob implements FlinkSinkJobBuilder<DataStream<Map<String, Object>>, JDBCSinkJobConfig> {
    private static MD5 md5 = SecureUtil.md5();

    @Override
    public void build(FlinkEnvironment context, DataStream<Map<String, Object>> mapStream, JDBCSinkJobConfig config, JobMetaConfig metaConfig) throws Exception {
        String sinkTable = config.getTable() + "_" + this.md5.digestHex(metaConfig.getJobId());
        String tableName = config.getTable();
        if (StrUtil.isNotEmpty(config.getSchemaName())) {
            tableName = config.getSchemaName() + "." + tableName;
        }

        // 根据columns定义指定table schema
        // 把sideOutputStream转换为Table,准备输出到ck
        Map<String, String> properties = config.schema.toProperties();
        DescriptorProperties dp = new DescriptorProperties(true);
        dp.putProperties(properties);
        TableSchema tableSchema = dp.getTableSchema(SCHEMA);
        List<RowtimeAttributeDescriptor> rowtimeAttributeDescriptors = SchemaValidator.deriveRowtimeAttributes(dp);
        Optional<String> proctimeAttribute = SchemaValidator.deriveProctimeAttribute(dp);
        MapStreamTableSource mapStreamTableSource = new MapStreamTableSource(
                tableSchema,
                config.getColumns(),
                rowtimeAttributeDescriptors,
                proctimeAttribute,
                mapStream,
                metaConfig.getOpts()
        );
        Table table = context.tableEnv.fromTableSource(mapStreamTableSource);

        // sink表定义，并创建临时表用于executeInsert
        String finalTableName = tableName;
        context.tableEnv.connect(new ConnectorDescriptor("jdbc", 1, false) {
            @Override
            protected Map<String, String> toConnectorProperties() {
                Map<String, String> props = new HashMap<>();
                props.put(JDBCValidator.CONNECTOR_URL, config.getDataSourceUrl());
                props.put(JDBCValidator.CONNECTOR_TABLE, finalTableName);
                if (StrUtil.isNotEmpty(config.getUserName())) {
                    props.put(JDBCValidator.CONNECTOR_USERNAME, config.getUserName());
                }
                if (StrUtil.isNotEmpty(config.getPassword())) {
                    props.put(JDBCValidator.CONNECTOR_PASSWORD, config.getPassword());
                }
                if (config.getFlushMaxSize() != null) {
                    props.put(JDBCValidator.CONNECTOR_WRITE_FLUSH_MAX_ROWS, config.getFlushMaxSize().toString());
                }
                if (config.getFlushIntervalMills() != null) {
                    props.put(JDBCValidator.CONNECTOR_WRITE_FLUSH_INTERVAL, config.getFlushIntervalMills().toString());
                }
                if (config.getMaxRetryTimes() != null) {
                    props.put(JDBCValidator.CONNECTOR_WRITE_MAX_RETRIES, config.getMaxRetryTimes().toString());
                }
                return props;
            }
        })
                .withSchema(config.schema)
                .createTemporaryTable(sinkTable);

        context.addExecuteStatement(sinkTable, table);
        // table.executeInsert(sinkTable);
    }

    @Override
    public JDBCSinkJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        JDBCSinkJobConfig config = new JDBCSinkJobConfig();
        ParamUtil.configJobParams(config, mapConfig);

        // 通过columns定义解析table schema
        config.schema = StreamToTableJob.parse(config.getColumns());

        return config;
    }
}
