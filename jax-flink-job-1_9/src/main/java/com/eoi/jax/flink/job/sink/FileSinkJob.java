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

package com.eoi.jax.flink.job.sink;

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkSinkJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.flink.job.common.AdvanceConfig;
import com.eoi.jax.flink.job.common.FileSinkJobConfig;
import com.eoi.jax.flink.job.common.FileSinkMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.util.Map;

@Job(
        name = "FileSinkJob",
        display = "输出文件到HDFS或文件系统_1.9",
        description = "输出json或csv格式文件到HDFS或文件系统",
        doc = "FileSinkJob.md",
        icon = "FileSinkJob.svg"
)
public class FileSinkJob extends AdvanceConfig implements FlinkSinkJobBuilder<DataStream<Map<String, Object>>, FileSinkJobConfig> {
    @Override
    public void build(FlinkEnvironment context, DataStream<Map<String, Object>> mapDataStream, FileSinkJobConfig config, JobMetaConfig metaConfig) throws Exception {
        DataStream<String> formatStream = mapDataStream.flatMap(new FileSinkMapFunction(config.getFields(), config.getDataFormat()));

        long minBucketCheckInterval = Math.min(60000L, Math.min(config.getRolloverInterval(), config.getInactivityInterval()));
        StreamingFileSink<String> sink = StreamingFileSink
                .forRowFormat(new Path(config.getBasePath()), new SimpleStringEncoder<String>("UTF-8"))
                .withBucketAssigner(new DateTimeBucketAssigner<>(config.getBucketFormatString()))
                .withBucketCheckInterval(minBucketCheckInterval)
                .withRollingPolicy(DefaultRollingPolicy.create()
                            .withRolloverInterval(config.getRolloverInterval())
                            .withInactivityInterval(config.getInactivityInterval())
                            .withMaxPartSize(config.getMaxPartSize() * 1024L * 1024L)
                            .build())
                .build();

        setAdvanceConfig(formatStream.addSink(sink).name(metaConfig.getJobEntry()).uid(metaConfig.getJobId()), metaConfig.getOpts());
    }

    @Override
    public FileSinkJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        FileSinkJobConfig config = new FileSinkJobConfig();
        ParamUtil.configJobParams(config,mapConfig);
        return config;
    }
}
