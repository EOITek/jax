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

package com.eoi.jax.flink1_12.job.sink;

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkSinkJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.flink.job.common.AdvanceConfig;
import com.eoi.jax.flink.job.common.KafkaSinkJobConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Map;

@Job(
        name = "KafkaSinkJob",
        display = "输出到Kafka_1.12",
        description = "使用FlinkKafkaProducer,无需指定版本号, 由Flink自适应.",
        icon = "KafkaSinkJob.svg",
        doc = "KafkaSinkJob.md"
)
public class KafkaSinkJob extends AdvanceConfig implements FlinkSinkJobBuilder<DataStream<Map<String, Object>>, KafkaSinkJobConfig> {
    @Override
    public void build(FlinkEnvironment context, DataStream<Map<String, Object>> mapDataStream, KafkaSinkJobConfig config, JobMetaConfig metaConfig) throws Exception {

        FlinkKafkaProducer<Map<String, Object>> producer = new FlinkKafkaProducer<>(config.getTopic(),
                new MapKafkaSerializationSchema(config.getTopic(), config.kafkaPartitionTypeEnum, config.getWriteTimestampToKafka()),
                config.getProperties(),
                FlinkKafkaProducer.Semantic.valueOf(config.getSemantic()));

        producer.setLogFailuresOnly(config.getLogFailuresOnly());

        producer.setWriteTimestampToKafka(config.getWriteTimestampToKafka());

        config.getTopicCreateConfig().createTopicOrNot(config.getBootstrapServersString(), config.getTopic(), config.getAuthConfig().toMap());

        setAdvanceConfig(mapDataStream.addSink(producer).name(metaConfig.getJobEntry()).uid(metaConfig.getJobId()), metaConfig.getOpts());
    }

    @Override
    public KafkaSinkJobConfig configure(Map<String, Object> map) throws Exception {
        return new KafkaSinkJobConfig(map);
    }
}
