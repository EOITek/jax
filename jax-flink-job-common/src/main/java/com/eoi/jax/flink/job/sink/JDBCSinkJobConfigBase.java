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

package com.eoi.jax.flink.job.sink;

import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;

public class JDBCSinkJobConfigBase implements ConfigValidatable, Serializable {
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
            label = "库名",
            description = "库名",
            optional = true
    )
    private String schemaName;

    @Parameter(
            label = "表名",
            description = "表名"
    )
    private String table;

    @Parameter(
            label = "批量写入最大数",
            description = "批量写入最大数",
            optional = true,
            defaultValue = "1000"
    )
    private Integer flushMaxSize;

    @Parameter(
            label = "批量写入间隔时间",
            description = "批量写入间隔时间（毫秒）",
            optional = true,
            defaultValue = "1000"
    )
    private Long flushIntervalMills;

    @Parameter(
            label = "写入失败重试最大数",
            description = "写入失败重试最大数",
            optional = true,
            defaultValue = "3"
    )
    private Integer maxRetryTimes;

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

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Integer getFlushMaxSize() {
        return flushMaxSize;
    }

    public void setFlushMaxSize(Integer flushMaxSize) {
        this.flushMaxSize = flushMaxSize;
    }

    public Long getFlushIntervalMills() {
        return flushIntervalMills;
    }

    public void setFlushIntervalMills(Long flushIntervalMills) {
        this.flushIntervalMills = flushIntervalMills;
    }

    public Integer getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(Integer maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    @Override
    public void validate() throws JobConfigValidationException {
    }
}
