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

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.api.tuple.Tuple2;
import com.eoi.jax.common.JsonUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EnrichByAsyncIOFunction extends RichAsyncFunction<Map<String, Object>, Map<String, Object>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrichByAsyncIOFunction.class);

    transient HikariDataSource ds;
    transient ExecutorService directExecutor;

    private EnrichByAsyncIOJobConfig config;

    private static final String METRIC_GROUP_NAME = "asyncio";
    private static final String METRIC_FOUND_COUNT = "found";
    private static final String METRIC_NOT_FOUND_COUNT = "notfound";
    private static final String METRIC_ERROR_COUNT = "error";

    private transient MetricGroup metricGroup;
    private transient Counter foundCounter;
    private transient Counter notFoundCounter;
    private transient Counter errorCounter;

    public EnrichByAsyncIOFunction(EnrichByAsyncIOJobConfig config) {
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

        directExecutor = org.apache.flink.runtime.concurrent.Executors.newDirectExecutorService();

        metricGroup = getRuntimeContext().getMetricGroup().addGroup(METRIC_GROUP_NAME);
        foundCounter = metricGroup.counter(METRIC_FOUND_COUNT);
        notFoundCounter = metricGroup.counter(METRIC_NOT_FOUND_COUNT);
        errorCounter = metricGroup.counter(METRIC_ERROR_COUNT);
    }

    @Override
    public void close() throws Exception {
        super.close();

        if (ds != null && !ds.isClosed()) {
            ds.close();
        }

        if (directExecutor != null && !directExecutor.isShutdown()) {
            directExecutor.shutdownNow();
        }
    }

    @Override
    public void asyncInvoke(Map<String, Object> input, ResultFuture<Map<String, Object>> resultFuture) throws Exception {

        final Future<Map<String,Object>> result = querySingle(input);
        CompletableFuture.supplyAsync(new Supplier<Map<String,Object>>() {

            @Override
            public Map<String,Object> get() {
                try {
                    Map<String,Object> ret = result.get();
                    if (ret == null || ret.isEmpty()) {
                        notFoundCounter.inc();
                    } else {
                        foundCounter.inc();
                    }
                    return ret;
                } catch (Exception e) {
                    LOGGER.error("async query fail", e);
                    errorCounter.inc();
                    Map<String, Object> returnMap = new HashMap<>(input);
                    returnMap.put("__error_msg__", e.getLocalizedMessage());
                    return returnMap;
                }
            }
        }).thenAccept((Map<String,Object> dbResult) -> {
            Map<String, Object> returnMap = new HashMap<>(input);
            if (dbResult != null && !dbResult.isEmpty()) {
                // 在原始数据基础上加上关联表的字段
                returnMap.putAll(dbResult);
            }
            resultFuture.complete(Collections.singleton(returnMap));
        });

    }

    private Future<Map<String,Object>> querySingle(Map<String, Object> input) throws Exception {
        Callable<Map<String,Object>> queryTask = () -> {
            try (Connection con = ds.getConnection();
                    PreparedStatement pst = createQuerySingleStatement(con, input);
                    ResultSet rs = pst.executeQuery()) {
                Map<String, Object> dtSource = new HashMap<>();
                if (rs.next()) {
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        String colName = rs.getMetaData().getColumnLabel(i);
                        Object colValue = rs.getObject(i);
                        dtSource.put(colName, colValue);
                    }
                }
                return dtSource;
            }
        };

        return directExecutor.submit(queryTask);
    }

    private PreparedStatement createQuerySingleStatement(Connection con,Map<String, Object> input) throws Exception {

        String whereClause;
        List<Tuple2<String, Object>> params = new ArrayList<>();

        if (StrUtil.isEmpty(config.getWhereClause())) {
            StringBuilder conditionBuilder = getWhereClauseByFkDef(input, params);
            whereClause = conditionBuilder.toString().replaceAll("and $", "");
        } else {
            for (EnrichByAsyncIOKey foreignKey : config.getForeignKeys()) {
                Object val = input.get(foreignKey.getForeignKey());
                params.add(Tuple2.of(foreignKey.getType(), val));
            }
            whereClause = config.getWhereClause();
        }
        String sql = String.format("select * from %s where %s", config.getTableName(), whereClause);
        LOGGER.info("async io sql:{}, params:{}", sql, JsonUtil.encode(params));

        PreparedStatement pst = con.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            String type = params.get(i).f0;
            Object val = params.get(i).f1;
            setParam(pst, i + 1, type, val);
        }
        return pst;
    }

    private void setParam(PreparedStatement pst, int i, String type, Object val) throws SQLException {
        switch (type.toLowerCase()) {
            case "string":
                if (val == null) {
                    pst.setNull(i, Types.VARCHAR);
                } else {
                    pst.setString(i, val.toString());
                }
                break;
            case "int":
                if (val == null) {
                    pst.setNull(i, Types.INTEGER);
                } else {
                    pst.setInt(i, NumberUtil.parseNumber(val.toString()).intValue());
                }
                break;
            case "double":
                if (val == null) {
                    pst.setNull(i, Types.DOUBLE);
                } else {
                    pst.setDouble(i, NumberUtil.parseNumber(val.toString()).doubleValue());
                }
                break;
            case "float":
                if (val == null) {
                    pst.setNull(i, Types.FLOAT);
                } else {
                    pst.setFloat(i, NumberUtil.parseNumber(val.toString()).floatValue());
                }
                break;
            case "decimal":
                if (val == null) {
                    pst.setNull(i, Types.DECIMAL);
                } else {
                    pst.setBigDecimal(i, new BigDecimal(NumberUtil.parseNumber(val.toString()).doubleValue()));
                }
                break;
            case "boolean":
                if (val == null) {
                    pst.setNull(i, Types.BOOLEAN);
                } else {
                    pst.setBoolean(i, Boolean.parseBoolean(val.toString()));
                }
                break;
            case "short":
                if (val == null) {
                    pst.setNull(i, Types.SMALLINT);
                } else {
                    pst.setShort(i, NumberUtil.parseNumber(val.toString()).shortValue());
                }
                break;
            case "long":
                if (val == null) {
                    pst.setNull(i, Types.BIGINT);
                } else {
                    pst.setLong(i, NumberUtil.parseNumber(val.toString()).longValue());
                }
                break;
            case "timestamp":
                if (val == null) {
                    pst.setNull(i, Types.TIMESTAMP);
                } else {
                    pst.setTimestamp(i, new Timestamp(NumberUtil.parseNumber(val.toString()).longValue()));
                }
                break;
            case "time":
                if (val == null) {
                    pst.setNull(i, Types.TIME);
                } else {
                    pst.setTime(i, new Time(NumberUtil.parseNumber(val.toString()).longValue()));
                }
                break;
            case "date":
                if (val == null) {
                    pst.setNull(i, Types.DATE);
                } else {
                    pst.setDate(i, new Date(NumberUtil.parseNumber(val.toString()).longValue()));
                }
                break;
            default:
                if (val == null) {
                    pst.setNull(i, Types.VARCHAR);
                } else {
                    pst.setString(i, val.toString());
                }
                break;
        }
    }

    private StringBuilder getWhereClauseByFkDef(Map<String, Object> input, List<Tuple2<String, Object>> params) {
        StringBuilder conditionBuilder = new StringBuilder();
        for (EnrichByAsyncIOKey foreignKey : config.getForeignKeys()) {
            Object val = input.get(foreignKey.getForeignKey());

            // 原始日志没有条件值，查询条件 is null
            if (val == null) {
                conditionBuilder = conditionBuilder.append(foreignKey.getForeignKey()).append(" is null ").append(" and ");
            } else {
                // 原始日志条件值为LIST，查询条件 in (...)
                if (val instanceof List) {
                    List valList = (List) val;
                    if (valList.size() > 0) {
                        String inList = valList.stream().map(x -> {
                            if ("string".equals(foreignKey.getType())) {
                                return "'" + x + "'";
                            } else {
                                return x.toString();
                            }
                        }).collect(Collectors.joining(", ")).toString();

                        conditionBuilder = conditionBuilder.append(foreignKey.getForeignKey())
                                .append(" in (")
                                .append(inList)
                                .append(") ")
                                .append(" and ");
                    }
                } else {
                    // 原始日志条件值为普通值，查询条件 = ?
                    conditionBuilder = conditionBuilder.append(foreignKey.getForeignKey()).append(" = ? ").append(" and ");
                    params.add(Tuple2.of(foreignKey.getType(), val));
                }
            }
        }
        return conditionBuilder;
    }

    @Override
    public void timeout(Map<String, Object> input, ResultFuture<Map<String, Object>> resultFuture) throws Exception {
        Map<String, Object> out = new HashMap<>(input);
        out.put("__error_msg__", "request timeout");
        errorCounter.inc();
        resultFuture.complete(Collections.singleton(out));
    }
}
