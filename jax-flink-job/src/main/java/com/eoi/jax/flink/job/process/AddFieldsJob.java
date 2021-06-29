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

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.common.OperatorCategory;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.eoi.jax.flink.job.process.ColumnOpDef.METHOD_ADD;

@Job(
        name = "AddFieldsJob",
        display = "列操作新增",
        description = "根据aviator表达式计算新增列",
        icon = "AddFieldsJob.svg",
        doc = "AddFieldsJob.md",
        category = OperatorCategory.FIELD_PROCESSING
)
public class AddFieldsJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        DataStream<Map<String, Object>>,
        AddFieldsJobConfig> {

    @Override
    public DataStream<Map<String, Object>> build(
            FlinkEnvironment context,
            DataStream<Map<String,Object>> dataStream,
            AddFieldsJobConfig config,
            JobMetaConfig metaConfig) throws Throwable {

        List<ColumnOpDef> opList = new ArrayList<>();
        for (AddFieldsDef def : config.getFields()) {
            ColumnOpDef columnOpDef = new ColumnOpDef();
            columnOpDef.setColMethod(METHOD_ADD)
                    .setReplace(def.isReplace())
                    .setFallback(def.getFallback())
                    .setOutputValueExp(def.getExpression())
                    .setOutputFieldName(def.getKey())
                    .setOutputFieldNameIsExp(def.isKeyIsExp());
            opList.add(columnOpDef);
        }

        return dataStream.map(new ColumnOpFunction(opList))
                        .name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());

    }

    @Override
    public AddFieldsJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        AddFieldsJobConfig config = new AddFieldsJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
