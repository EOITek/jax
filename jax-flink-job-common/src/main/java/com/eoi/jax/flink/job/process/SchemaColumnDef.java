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

import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;

public class SchemaColumnDef implements Serializable {

    @Parameter(
            label = "源字段名",
            description = "源数据里的字段名"
    )
    private String columnName;

    @Parameter(
            label = "目标字段名",
            description = "定义table里的字段名;不填则默认同源字段名;Event模式下指定时间字段不能重命名。",
            optional = true
    )
    private String aliasName;

    @Parameter(
            label = "字段类型",
            description = "定义table里的字段类型",
            candidates = {"string","int","double","float","decimal","boolean","short","long","timestamp","time","date"}
    )
    private String columnType;

    public static SchemaColumnDef of(String columnName,String columnType) {
        SchemaColumnDef columnDef = new SchemaColumnDef();
        columnDef.setColumnType(columnType);
        columnDef.setColumnName(columnName);
        return columnDef;
    }

    public static SchemaColumnDef of(String columnName,String aliasName, String columnType) {
        SchemaColumnDef columnDef = new SchemaColumnDef();
        columnDef.setColumnType(columnType);
        columnDef.setColumnName(columnName);
        columnDef.setAliasName(aliasName);
        return columnDef;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
}
