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

package com.eoi.jax.core.flink.python;

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkProcessJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

@Job(
        isInternal = true,
        display = "PythonWrapperJob",
        description = "用于适配任意基于python实现的异常检测算法的job，配合jax_python包的flink_worker.py工作。"
                + "包含python算法的代码必须实现python中定义的RealtimeAnomalyDetect接口，并打包成一个可被找到，并import的package。"
                + "指定import的包名，class名，时间字段，值字段，分组字段以及python需要的参数（map形式）。"
)
public class PythonWrapperJob implements FlinkProcessJobBuilder<
        DataStream<Map<String, Object>>,
        DataStream<Map<String, Object>>,
        PythonWrapperJobConfig> {

    @Override
    public DataStream<Map<String, Object>> build(
            FlinkEnvironment context,
            DataStream<Map<String, Object>> mapDataStream,
            PythonWrapperJobConfig config, JobMetaConfig metaConfig) throws Throwable {
        long checkpointInterval = 0;
        if (config.getStateFlushInterval() != null) {
            checkpointInterval = config.getStateFlushInterval();
        }
        return mapDataStream
                .keyBy(new KeySelectorForMap(config.getGroupByFields(), "_default"))
                .flatMap(new PythonWrapperJobFunction(config, checkpointInterval));
    }

    @Override
    public PythonWrapperJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        return PythonWrapperJobConfig.fromMap(mapConfig);
    }
}
