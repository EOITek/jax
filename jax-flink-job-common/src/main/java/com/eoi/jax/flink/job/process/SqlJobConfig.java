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
import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.OutputTableConfig;
import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SqlJobConfig implements ConfigValidatable, OutputTableConfig, Serializable {

    @Parameter(
            label = "sql语句",
            description = "请指定sql语句，可以支持过滤，聚合等操作",
            inputType = InputType.SQL
    )
    private String sql;

    @Parameter(
            label = "输出表的名称",
            description = "sql语句执行的结果表名",
            optional = true
    )
    private String tableName;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void validate() throws JobConfigValidationException {
    }

    @Override
    public Map<Integer, String> getSlotTableNames() {
        if (StrUtil.isEmpty(tableName)) {
            return null;
        } else {
            Map<Integer, String> map = new HashMap<>();
            map.put(0, tableName);
            return map;
        }
    }
}
