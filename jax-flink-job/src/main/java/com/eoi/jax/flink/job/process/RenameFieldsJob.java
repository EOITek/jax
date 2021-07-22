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

import static com.eoi.jax.flink.job.process.ColumnOpDef.METHOD_RENAME;


@Job(
        name = "RenameFieldsJob",
        display = "列操作重命名",
        description = "列操作重命名",
        icon = "RenameFieldsJob.svg",
        doc = "RenameFieldsJob.md",
        category = OperatorCategory.FIELD_PROCESSING
)
public class RenameFieldsJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        DataStream<Map<String, Object>>,
        RenameFieldsJobConfig> {

    @Override
    public DataStream<Map<String, Object>> build(
            FlinkEnvironment context,
            DataStream<Map<String,Object>> dataStream,
            RenameFieldsJobConfig config,
            JobMetaConfig metaConfig) throws Throwable {

        List<ColumnOpDef> opList = new ArrayList<>();
        for (RenameFieldsDef def : config.getFields()) {
            ColumnOpDef columnOpDef = new ColumnOpDef();
            columnOpDef.setColMethod(METHOD_RENAME)
                    .setInputFieldName(def.getOldField())
                    .setOutputFieldName(def.getNewField())
                    .setReplace(def.isReplace());
            opList.add(columnOpDef);
        }

        return dataStream.map(new ColumnOpFunction(opList))
                        .name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());

    }

    @Override
    public RenameFieldsJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        RenameFieldsJobConfig config = new RenameFieldsJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
