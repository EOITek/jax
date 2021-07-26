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
import com.eoi.jax.api.FlinkSqlJobBuilder;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.common.OperatorCategory;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.internal.TableEnvironmentInternal;
import org.apache.flink.table.client.cli.SqlCommandParser;
import org.apache.flink.table.client.cli.SqlCommandParser.SqlCommandCall;
import org.apache.flink.table.client.gateway.SqlExecutionException;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.utils.PrintUtils;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 执行flink纯sql的job，每条sql语句用分号结束；
 * 支持语句包括：CREATE table/view 或 INSERT { INTO | OVERWRITE } table_name  select_statement；
 * 无法用于pipeline编排，仅内部使用；
 */
@Job(
        name = "SqlExecuteJob",
        display = "内置Sql执行器_1.12",
        description = "执行flink纯sql的job；无法用于pipeline编排，仅内部使用",
        isInternal = true,
        category = OperatorCategory.SCRIPT_PROCESSING
)
public class SqlExecuteJob implements FlinkSqlJobBuilder<SqlExecuteJobConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlExecuteJob.class);

    @Override
    public void build(FlinkEnvironment context, SqlExecuteJobConfig config, JobMetaConfig metaConfig) throws Throwable {
        String [] sqlList = config.getSql().split(";");

        final Parser parser = ((TableEnvironmentInternal) context.tableEnv).getParser();
        List<String> insertSqlList = new ArrayList<>();
        String selectSql = null;
        for (String sql : sqlList) {
            print("----------------------begin handling sql------------------------------");
            print(sql);
            Optional<SqlCommandCall> sqlCommandCall = parseCommand(parser, sql);
            if (sqlCommandCall.isPresent()) {
                String cmdName = String.valueOf(sqlCommandCall.get().command);
                print(String.format("Parsed cmd:%s", cmdName));
                if ("INSERT INTO".equals(cmdName) || "INSERT OVERRIDE".equals(cmdName)) {
                    insertSqlList.add(sql);
                    print("Added into execute queue.");
                } else if ("SELECT".equals(cmdName)) {
                    selectSql = sql;
                    print("Added into execute queue.");
                } else {
                    TableResult tableResult = context.tableEnv.executeSql(sql);
                    printTableResult(tableResult, true);
                }
            }
        }

        if (insertSqlList.size() > 0) {
            StatementSet stmtSet = context.tableEnv.createStatementSet();
            for (String sql : insertSqlList) {
                print("----------------------execute insert sql------------------------------");
                print(sql);
                stmtSet.addInsertSql(sql);
            }
            TableResult tableResult = stmtSet.execute();
            // 正常模式下提交任务到集群后直接退出； Debug模式下需要阻塞运行便于观察结果；
            printTableResult(tableResult, config.getDebug());
        }

        if (StrUtil.isNotEmpty(selectSql) && config.getDebug()) {
            print("----------------------execute query sql------------------------------");
            print(selectSql);
            TableResult tableResult = context.tableEnv.executeSql(selectSql);
            // 正常模式下提交任务到集群后直接退出； Debug模式下需要阻塞运行便于观察结果；
            printTableResult(tableResult, config.getDebug());
        }

        // 如果没有任何可执行sql语句，放弃任务启动；
        if (insertSqlList.size() == 0 && !config.getDebug()) {
            throw new JobConfigValidationException("没有发现任何可执行sql语句（INSERT语句），任务无法提交");
        }
        if (StrUtil.isEmpty(selectSql) && config.getDebug()) {
            throw new JobConfigValidationException("没有发现任何可执行sql语句（SELECT语句），任务无法调试");
        }
    }

    @Override
    public SqlExecuteJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        SqlExecuteJobConfig config = new SqlExecuteJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }

    private static void print(String info) {
        System.out.println(info);
    }

    public static void printTableResult(TableResult tableResult, boolean collectResult) throws Exception {
        print(String.format("Schema:%s", tableResult.getTableSchema().toString()));

        if (tableResult.getJobClient().isPresent()) {
            print("JobID:" + tableResult.getJobClient().get().getJobID().toString());
        }

        print("Result:");
        if (collectResult) {
            try (CloseableIterator<Row> it = tableResult.collect()) {
                while (it.hasNext()) {
                    print(String.join(",", PrintUtils.rowToString(it.next())));
                }
            }
        }
    }

    private Optional<SqlCommandParser.SqlCommandCall> parseCommand(Parser parser, String line) {
        final SqlCommandParser.SqlCommandCall parsedLine;
        try {
            parsedLine = SqlCommandParser.parse(parser, line);
        } catch (SqlExecutionException e) {
            print("PARSE ERROR : " + e.getMessage() + " Caused by:" + e.getCause().getLocalizedMessage());
            throw e;
        }
        return Optional.of(parsedLine);
    }

}
