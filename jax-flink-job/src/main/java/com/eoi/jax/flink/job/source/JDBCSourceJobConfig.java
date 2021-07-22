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

import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;

public class JDBCSourceJobConfig implements ConfigValidatable, Serializable {

    @Parameter(
            label = "jdbc Driver",
            description = "jdbc驱动的class name；需要确保对应的驱动jar包已放在 flink/lib 下"
    )
    private String jdbcDriver;

    @Parameter(
            label = "数据库连接字符串",
            description = "数据库连接字符串"
    )
    private String dataSourceUrl;

    @Parameter(
            label = "用户名",
            description = "数据库连接用户",
            optional = true
    )
    private String userName;

    @Parameter(
            label = "密码",
            description = "数据库连接密码",
            optional = true,
            inputType = InputType.PASSWORD
    )
    private String password;

    @Parameter(
            label = "sql脚本",
            description = "通过sql查询语句获取数据源",
            inputType = InputType.SQL
    )
    private String querySql;

    @Parameter(
            label = "最大连接数",
            description = "连接池最大连接数",
            optional = true,
            defaultValue = "5"
    )
    private int maximumPoolSize;

    @Parameter(
            label = "间隔查询时间",
            description = "间隔查询时间，单位为毫秒",
            optional = true,
            defaultValue = "60000"
    )
    private long interval;

    @Parameter(
            label = "自增列",
            description = "如果需要增量获取数据，需要指定自增列（数值类型且保持单调自增），每次查询会大于上次获取到的最大值",
            optional = true
    )
    private String incrementField;

    @Parameter(
            label = "自增列起始值",
            description = "可覆盖上次获取到的最大值，从指定位置开始获取数据",
            optional = true
    )
    private String incrementStart;

    public String getIncrementField() {
        return incrementField;
    }

    public void setIncrementField(String incrementField) {
        this.incrementField = incrementField;
    }

    public String getIncrementStart() {
        return incrementStart;
    }

    public void setIncrementStart(String incrementStart) {
        this.incrementStart = incrementStart;
    }

    public String getDataSourceUrl() {
        return dataSourceUrl;
    }

    public void setDataSourceUrl(String dataSourceUrl) {
        this.dataSourceUrl = dataSourceUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    @Override
    public void validate() throws JobConfigValidationException {
    }
}
