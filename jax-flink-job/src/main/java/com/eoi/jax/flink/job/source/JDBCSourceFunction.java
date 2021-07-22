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

package com.eoi.jax.flink.job.source;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JDBCSourceFunction extends RichSourceFunction<Map<String, Object>> implements CheckpointedFunction {
    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCSourceFunction.class);

    private JDBCSourceJobConfig config;
    private transient HikariDataSource ds;
    private volatile boolean isRunning = true;
    private transient ExecutorService executor;
    private String maxPos;

    private transient ListState<String> maxPosState;


    public JDBCSourceFunction(JDBCSourceJobConfig config) {
        this.config = config;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        // 初始化数据库连接池
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(config.getJdbcDriver());
        hikariConfig.setJdbcUrl(config.getDataSourceUrl());
        if (!StrUtil.isEmpty(config.getUserName())) {
            hikariConfig.setUsername(config.getUserName());
        }
        if (!StrUtil.isEmpty(config.getPassword())) {
            hikariConfig.setPassword(config.getPassword());
        }
        hikariConfig.setMaximumPoolSize(config.getMaximumPoolSize());
        hikariConfig.setReadOnly(true);
        ds = new HikariDataSource(hikariConfig);

        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run(SourceContext<Map<String, Object>> ctx) throws Exception {
        while (isRunning) {

            String sql = config.getQuerySql();
            if (!StrUtil.isEmpty(config.getIncrementField()) && this.maxPos != null) {
                sql = String.format("select * from (%s) __tmp_table__ where %s > %s", sql, config.getIncrementField(), this.maxPos);
            }
            LOGGER.info("fetch sql: {}", sql);

            try {
                try (Connection con = ds.getConnection();
                        PreparedStatement pst = createStatement(con, sql);
                        ResultSet rs = pst.executeQuery()) {
                    Map<String, Object> dtSource = new HashMap<>();
                    while (rs.next()) {
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            String colName = rs.getMetaData().getColumnLabel(i);
                            Object colValue = rs.getObject(i);
                            dtSource.put(colName, colValue);
                        }

                        // 维护自增列，用于增量获取数据
                        if (!StrUtil.isEmpty(config.getIncrementField()) && dtSource.containsKey(config.getIncrementField())) {
                            String updateValStr = dtSource.get(config.getIncrementField()).toString();
                            double updateVal = NumberUtil.parseNumber(updateValStr).doubleValue();
                            double nowVal = NumberUtil.parseNumber(this.maxPos).doubleValue();
                            if (this.maxPos == null || updateVal > nowVal) {
                                this.maxPos = updateValStr;
                            }
                        }
                        ctx.collect(dtSource);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("JDBCSourceFunction error", ex);
            }

            if (!isRunning) {
                break;
            }

            executor.submit(() -> {
                try {
                    Thread.sleep(config.getInterval());
                } catch (Exception ignore) { }
            }).get();
        }
    }

    private PreparedStatement createStatement(Connection con, String sql) throws SQLException {
        PreparedStatement pst = con.prepareStatement(sql);
        return pst;
    }

    @Override
    public void cancel() {
        isRunning = false;

        if (executor != null && !executor.isShutdown()) {
            try {
                executor.shutdownNow();
            } catch (Exception ex) {
                LOGGER.error("shutdown executor fail",ex);
            }
        }

        if (ds != null && !ds.isClosed()) {
            try {
                ds.close();
            } catch (Exception ex) {
                LOGGER.error("shutdown data source fail",ex);
            }

        }
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        if (!StrUtil.isEmpty(this.maxPos)) {
            this.maxPosState.clear();
            this.maxPosState.add(this.maxPos);
            LOGGER.info("Save snapshotState:{}", this.maxPos);
        }
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        this.maxPosState = context.getOperatorStateStore()
                .getListState(new ListStateDescriptor<>("maxPosState", String.class));

        if (config.getIncrementStart() != null) {
            this.maxPos = config.getIncrementStart();
            LOGGER.info("Init snapshotState:{}", this.maxPos);
        } else {
            if (context.isRestored()) {
                for (String pos : this.maxPosState.get()) {
                    this.maxPos = pos;
                }
                LOGGER.info("Restored snapshotState:{}", this.maxPos);
            } else {
                maxPos = null;
                LOGGER.info("No Restored snapshotState");
            }
        }
    }
}
