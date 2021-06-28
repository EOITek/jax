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

package com.eoi.jax.flink_entry;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import com.eoi.jax.api.CustomStatementSet;
import com.eoi.jax.api.CustomStreamTableEnvironmentImpl;
import com.eoi.jax.api.FlinkEnvironment;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.utils.PrintUtils;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class FlinkMainEntry {
    
    public static void main(String[] args) throws Throwable {
        try {
            // 参数初始化
            String jobJson = "";
            ParameterTool params = ParameterTool.fromArgs(args);
            if (params.has("job_file")) {
                String jobFile = params.get("job_file");
                if (jobFile.toLowerCase().startsWith("http")) {
                    jobJson = HttpUtil.get(jobFile, CharsetUtil.CHARSET_UTF_8);
                } else {
                    jobJson = new String(Files.readAllBytes(new File(jobFile).toPath()), StandardCharsets.UTF_8);
                }
            } else if (params.has("job_def")) {
                jobJson = params.get("job_def");
            }
            if ("".equals(jobJson)) {
                throw new FlinkParamException("请指定配置文件或者直接传入定义的内容");
            }
            FlinkPipelineDescription description = FlinkContextMainBuilder.description(jobJson);
            FlinkEnvironment context = FlinkContextMainBuilder.build(description);

            // 把TableEnv里的insert operations合并取出，添加到streamEnv下
            if (context.statementSet != null && context.statementCount > 0) {
                Planner planner = ((CustomStreamTableEnvironmentImpl)context.tableEnv).planner;
                CustomStatementSet statementSet = ((CustomStatementSet)context.statementSet);
                List<Transformation<?>> transformations = planner.translate(statementSet.getOperations());

                for (Transformation<?> transformation : transformations) {
                    context.streamEnv.addOperator(transformation);
                }
            }

            if (!FlinkContextMainBuilder.isSqlJob(description)) {
                System.out.println(context.streamEnv.getExecutionPlan());
                context.streamEnv.execute(description.getPipelineName());
            }
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

    private static void print(String info) {
        // LOGGER.info(info);
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

}
