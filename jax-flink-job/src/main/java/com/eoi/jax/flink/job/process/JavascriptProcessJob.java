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
import com.eoi.jax.common.OperatorCategory;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

@Job(
        name = "JavascriptProcessJob",
        display = "JS脚本处理",
        description = "使用javascript当成flatMap处理每一条数据，返回一条或多条数据",
        doc = "JavascriptProcessJob.md",
        icon = "JavascriptProcessJob.svg",
        category = OperatorCategory.SPECIAL_OPERATOR
)
public class JavascriptProcessJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        DataStream<Map<String, Object>>,
        JavascriptProcessJobConfig> {
    @Override
    public DataStream<Map<String, Object>> build(FlinkEnvironment context,
                                                 DataStream<Map<String, Object>> mapDataStream,
                                                 JavascriptProcessJobConfig config, JobMetaConfig metaConfig)
            throws Throwable {
        return mapDataStream.flatMap(new JavascriptProcessJobFunction(config.getScript(), config.getIgnoreException()))
                .name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());
    }

    @Override
    public JavascriptProcessJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        return JavascriptProcessJobConfig.fromMap(mapConfig);
    }
}
