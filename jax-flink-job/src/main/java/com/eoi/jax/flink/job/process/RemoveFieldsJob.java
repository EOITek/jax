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

import static com.eoi.jax.flink.job.process.ColumnOpDef.METHOD_REMOVE;


@Job(
        name = "RemoveFieldsJob",
        display = "列操作删除",
        description = "列操作删除",
        icon = "RemoveFieldsJob.svg",
        doc = "RemoveFieldsJob.md",
        category = OperatorCategory.FIELD_PROCESSING
)
public class RemoveFieldsJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        DataStream<Map<String, Object>>,
        RemoveFieldsJobConfig> {

    @Override
    public DataStream<Map<String, Object>> build(
            FlinkEnvironment context,
            DataStream<Map<String,Object>> dataStream,
            RemoveFieldsJobConfig config,
            JobMetaConfig metaConfig) throws Throwable {

        List<ColumnOpDef> opList = new ArrayList<>();
        for (String col : config.getFields()) {
            ColumnOpDef columnOpDef = new ColumnOpDef();
            columnOpDef.setColMethod(METHOD_REMOVE)
                    .setInputFieldName(col);
            opList.add(columnOpDef);
        }

        return dataStream.map(new ColumnOpFunction(opList))
                        .name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());

    }

    @Override
    public RemoveFieldsJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        RemoveFieldsJobConfig config = new RemoveFieldsJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
