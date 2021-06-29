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

package com.eoi.jax.flink.job.source;

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkSourceJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

@Job(
        name = "JavascriptSimulateMapSourceJob",
        display = "JS数据发生器",
        description = "设置javascript脚本，周期性运行，从而产生json数据",
        doc = "JavascriptSimulateMapSourceJob.md",
        icon = "JavascriptProcessJob.svg"
)
public class JavascriptSimulateMapSourceJob
        implements FlinkSourceJobBuilder<DataStream<Map<String, Object>>, JavascriptSimulateMapSourceJobConfig> {
    @Override
    public DataStream<Map<String, Object>> build(FlinkEnvironment context,
                                                 JavascriptSimulateMapSourceJobConfig config,
                                                 JobMetaConfig metaConfig) throws Throwable {
        return context.streamEnv.addSource(
                new JavascriptSimulateMapSourceFunction(
                        config.getScript(), config.getIgnoreException(), config.getInterval()));
    }

    @Override
    public JavascriptSimulateMapSourceJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        return JavascriptSimulateMapSourceJobConfig.fromMap(mapConfig);
    }
}
