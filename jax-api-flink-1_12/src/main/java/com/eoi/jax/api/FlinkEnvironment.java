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

package com.eoi.jax.api;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * Flink必要的上下文
 */
public class FlinkEnvironment {
    public final StreamExecutionEnvironment streamEnv;
    public final StreamTableEnvironment tableEnv;

    public FlinkEnvironment(StreamExecutionEnvironment streamEnv, StreamTableEnvironment tableEnv) {
        this(streamEnv,tableEnv,null);
    }

    public StatementSet statementSet;
    public int statementCount;

    public FlinkEnvironment(StreamExecutionEnvironment streamEnv, StreamTableEnvironment tableEnv, StatementSet statementSet) {
        this.streamEnv = streamEnv;
        this.tableEnv = tableEnv;
        this.statementSet = statementSet;
    }

    public void addExecuteStatement(String targetPath, Table table) {
        if (statementSet == null) {
            statementSet = tableEnv.createStatementSet();
        }
        statementSet.addInsert(targetPath, table);
        statementCount++;
    }
}
