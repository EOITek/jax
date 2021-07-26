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

package com.eoi.jax.flink1_12.job.source;

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkSourceJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.flink.job.common.AdvanceConfig;
import com.eoi.jax.flink.job.common.KafkaByteSourceJobConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;

import java.util.Map;


@Job(
        name = "KafkaByteSourceJob",
        display = "Kafka数据源接入_1.12",
        description = "Kafka Source 返回`Map<String,Object>`"
                + "1. kafka message的value数据为byte数组类型，通过参数byteArrayFieldName指定输出字段名（默认为bytes）\n"
                + "2. kafka的元数据输出到参数metaFieldName指定字段名（默认为meta）",
        icon = "KafkaByteSourceJob.svg",
        doc = "KafkaByteSourceJob.md"
)
public class KafkaByteSourceJob extends AdvanceConfig implements FlinkSourceJobBuilder<DataStream<Map<String,Object>>, KafkaByteSourceJobConfig> {

    @Override
    public KafkaByteSourceJobConfig configure(Map<String, Object> mapConfig) throws Exception {
        return new KafkaByteSourceJobConfig(mapConfig);
    }

    @Override
    public DataStream<Map<String,Object>> build(FlinkEnvironment context, KafkaByteSourceJobConfig config, JobMetaConfig metaConfig) {
        config.getTopicCreateConfig().createTopicsOrNot(config.getBootstrapServersString(), config.getTopics(), config.getAuthConfig().toMap());

        KafkaDeserializationSchema<Map<String,Object>> deserialization = new KafkaByteDeserialization(config.getByteArrayFieldName(), config.getMetaFieldName());
        FlinkKafkaConsumer<Map<String,Object>> kafkaConsumer;

        kafkaConsumer = new FlinkKafkaConsumer<>(config.getTopics(), deserialization, config.getProperties());
        setKafkaConsumer(config, kafkaConsumer);

        DataStream<Map<String,Object>> sourceStream =
                setAdvanceConfig(context.streamEnv.addSource(kafkaConsumer).name(metaConfig.getJobEntry()).uid(metaConfig.getJobId()), metaConfig.getOpts());
        if (config.getRebalancePartition()) {
            sourceStream = sourceStream.rebalance();
        }
        return sourceStream;
    }

    private void setKafkaConsumer(KafkaByteSourceJobConfig config, FlinkKafkaConsumer<?> kafkaConsumer) {
        // Flink kafka consumer支持三种offset， group, latest, earliest.
        switch (config.getOffsetMode()) {
            case KafkaByteSourceJobConfig.OFFSETMODE_EARLIEST:
                kafkaConsumer.setStartFromEarliest();
                break;

            case KafkaByteSourceJobConfig.OFFSETMODE_LATEST:
                kafkaConsumer.setStartFromLatest();
                break;

            case KafkaByteSourceJobConfig.OFFSETMODE_TIMESTAMP:
                kafkaConsumer.setStartFromTimestamp(config.getOffsetModeTimestamp());
                break;

            default:
                kafkaConsumer.setStartFromGroupOffsets();
                break;
        }

        if (Boolean.TRUE.equals(config.getCommitOffsetOnCheckPoint())) {
            kafkaConsumer.setCommitOffsetsOnCheckpoints(true);
        } else {
            kafkaConsumer.setCommitOffsetsOnCheckpoints(false);
        }
    }
}
