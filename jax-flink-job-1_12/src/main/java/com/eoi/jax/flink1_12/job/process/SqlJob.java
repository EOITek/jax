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

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.common.OperatorCategory;
import com.eoi.jax.flink.job.process.SqlJobConfig;
import org.apache.flink.table.api.Table;

import java.util.Map;

@Job(
        name = "SqlJob",
        display = "执行sql_1.12",
        description = "上游StreamToTable转为table后，再执行sql; 下游可以通过TableToStreamJob再转回DataStream",
        icon = "SqlJob.svg",
        doc = "SqlJob.md",
        category = OperatorCategory.SCRIPT_PROCESSING
)
public class SqlJob implements FlinkProcessJobBuilder<Table, Table, SqlJobConfig> {
    @Override
    public Table build(FlinkEnvironment context, Table table, SqlJobConfig config, JobMetaConfig metaConfig) {
        return context.tableEnv.sqlQuery(config.getSql());
    }

    @Override
    public SqlJobConfig configure(Map<String, Object> mapConfig) {
        SqlJobConfig config = new SqlJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
