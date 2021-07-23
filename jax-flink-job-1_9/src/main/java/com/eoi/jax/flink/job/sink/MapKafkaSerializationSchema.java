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

import com.eoi.jax.common.JsonUtil;
import com.eoi.jax.flink.job.common.FlinkKafkaPartitionType;
import org.apache.flink.streaming.connectors.kafka.KafkaContextAware;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;

import java.util.Map;

public class MapKafkaSerializationSchema implements KafkaSerializationSchema<Map<String, Object>>, KafkaContextAware<Map<String, Object>> {

    FlinkKafkaPartitionType partitionType;
    int numParallelInstances;
    int parallelInstanceId;
    int[] partitions;
    String keyField;
    String topic;
    boolean writeTimestampToKafka;

    public MapKafkaSerializationSchema(String topic,
                                       FlinkKafkaPartitionType partitionType,
                                       String keyField,
                                       boolean writeTimestampToKafka) {
        this.partitionType = partitionType;
        this.topic = topic;
        this.keyField = keyField;
        this.writeTimestampToKafka = writeTimestampToKafka;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(Map<String, Object> element, @Nullable Long timestamp) {
        ProducerRecord<byte[], byte[]> producerRecord;
        if (!writeTimestampToKafka) {
            // 设置写入kafka record的timestamp为null，则kafka会自动设置当前时间为record的timestamp
            // 避免flink把数据event time的时间作为kafka记录时间写入，否则可能会造成数据很快过期被自动清理掉
            // 参考：https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/connectors/kafka.html#using-kafka-timestamps-and-flink-event-time-in-kafka-010
            timestamp = null;
        }
        try {
            if (partitionType == FlinkKafkaPartitionType.FIXED) {
                int pid = partitions[parallelInstanceId % partitions.length];
                producerRecord = new ProducerRecord<>(topic, pid, timestamp, null, JsonUtil.encode2Byte(element));
            } else if (partitionType == FlinkKafkaPartitionType.ROUND_ROBIN) {
                producerRecord = new ProducerRecord<>(topic, null, timestamp,null, JsonUtil.encode2Byte(element));
            } else {
                byte[] key = null;
                if (element.get(keyField) != null) {
                    key = element.get(keyField).toString().getBytes();
                }
                producerRecord = new ProducerRecord<>(topic, null, timestamp, key, JsonUtil.encode2Byte(element));
            }
        } catch (Exception ex) {
            producerRecord = new ProducerRecord<>(topic, null, timestamp, null, new byte[0]);
        }
        return producerRecord;
    }

    @Override
    public String getTargetTopic(Map<String, Object> element) {
        return null;
    }

    @Override
    public void setNumParallelInstances(int numParallelInstances) {
        this.numParallelInstances = numParallelInstances;
    }

    @Override
    public void setParallelInstanceId(int parallelInstanceId) {
        this.parallelInstanceId = parallelInstanceId;
    }

    @Override
    public void setPartitions(int[] partitions) {
        this.partitions = partitions;
    }
}
