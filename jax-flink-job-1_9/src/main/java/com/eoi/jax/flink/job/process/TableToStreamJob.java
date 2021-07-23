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

import com.eoi.jax.api.EmptyConfig;
import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.common.OperatorCategory;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;

import java.util.Arrays;
import java.util.Map;

@Job(
        name = "TableToStreamJob",
        display = "Table转Map流_1.9",
        description = "Table转Map流",
        icon = "TableToStreamJob.svg",
        doc = "SqlJob.md",
        category = OperatorCategory.DATA_TYPE_CONVERSION
)
public class TableToStreamJob implements FlinkProcessJobBuilder<Table, DataStream<Map<String, Object>>, EmptyConfig> {

    @Override
    public EmptyConfig configure(Map<String, Object> mapConfig) throws Throwable {
        return new EmptyConfig();
    }

    @Override
    public DataStream<Map<String, Object>> build(FlinkEnvironment context, Table table, EmptyConfig config, JobMetaConfig metaConfig) throws Throwable {
        DataStream<Row> rowDataStream = context.tableEnv.toAppendStream(table, Row.class);
        return rowDataStream.map(new TableToStreamMapFunction(Arrays.asList(table.getSchema().getFieldNames()))).name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());
    }
}
