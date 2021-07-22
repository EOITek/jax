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

import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;
import java.util.List;


public class EnrichByAsyncIOJobConfig implements ConfigValidatable, Serializable {

    @Parameter(
            label = "异步请求超时时间",
            description = "单位：毫秒",
            optional = true,
            defaultValue = "3000"
    )
    private int timeout;

    @Parameter(
            label = "异步请求队列的最大长度",
            description = "超过最大长度则会触发反压",
            optional = true,
            defaultValue = "100"
    )
    private int capacity;

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
            label = "最大连接数",
            description = "连接池最大连接数",
            optional = true,
            defaultValue = "5"
    )
    private int maximumPoolSize;

    @Parameter(
            label = "密码",
            description = "数据库连接密码",
            optional = true,
            inputType = InputType.PASSWORD
    )
    private String password;

    @Parameter(
            label = "表名",
            description = "查询表名"
    )
    private String tableName;

    @Parameter(
            label = "关联列定义",
            description = "关联列定义"
    )
    private List<EnrichByAsyncIOKey> foreignKeys;

    @Parameter(
            label = "过滤条件",
            description = "自定义关联where语句，如果不填则按foreignKeys定义自动拼接；如果填了需要确保参数个数和顺序与foreignKeys定义保持一致",
            optional = true
    )
    private String whereClause;

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
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

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<EnrichByAsyncIOKey> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<EnrichByAsyncIOKey> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    @Override
    public void validate() throws JobConfigValidationException {
    }
}
