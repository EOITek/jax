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

package com.eoi.jax.flink.job.common;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author bruce
 */
public class KafkaUtil {

    static Logger logger = LoggerFactory.getLogger(KafkaUtil.class.getName());

    /**
     * 创建topic
     */
    public static void createTopicByAdminClient(
            String bootstrapServers,
            int maxMessageSize,
            int numPartitions,
            int numReplications,
            String topicName,
            Map<String,Object> customOptions) {
        if (getTopicIsExists(bootstrapServers, topicName, customOptions)) {
            logger.warn("存在Topic[" + topicName + "]，跳过创建！");
            return;
        }
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        if (customOptions != null && !customOptions.isEmpty()) {
            config.putAll(customOptions);
        }
        AdminClient admin = AdminClient.create(config);
        Map<String, String> configs = new HashMap<>();
        configs.put(TopicConfig.MAX_MESSAGE_BYTES_CONFIG, getKafkaMaxMessageSize(maxMessageSize) + "");
        int partitions = numPartitions;
        short replication = (short) numReplications;
        try {
            CreateTopicsResult createTopicsResult = admin.createTopics(Arrays.asList(new NewTopic(topicName, partitions, replication).configs(configs)));
            createTopicsResult.values().get(topicName).get();
            logger.info("创建Topic[" + topicName + "]成功！");
        } catch (Exception ex) {
            logger.error("创建Topic[" + topicName + "]失败！, ex: " + ex.toString());
        } finally {
            admin.close();
        }
    }

    public static List<TopicPartition> topicPartitions(String topic, String bootstrapServers) {
        TopicDescription topicDescription = getTopicPartitions(topic, bootstrapServers);
        List<TopicPartition> result = new ArrayList<>();
        if (null != topicDescription) {
            List<TopicPartitionInfo> partitionInfos = topicDescription.partitions();
            for (TopicPartitionInfo partitionInfo : partitionInfos) {
                result.add(new TopicPartition(topic, partitionInfo.partition()));
            }
        }
        return result;
    }

    public static TopicDescription getTopicPartitions(String topic, String bootstrapServers) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        AdminClient admin = AdminClient.create(config);
        try {
            TopicDescription description = admin.describeTopics(Arrays.asList(topic)).all().get(10, TimeUnit.SECONDS).get(topic);
            logger.info("获取Topic:[{}]Partitions成功！", topic);
            return description;
        } catch (Exception ex) {
            logger.warn("获取Topic:[{}]Partitions失败:{}", topic, ex.getMessage());
            return null;
        } finally {
            admin.close();
        }
    }

    /**
     * 检查topic是否存在
     */
    public static boolean getTopicIsExists(String bootstrapServers, String topicName, Map<String,Object> customOptions) {
        List<String> topicList = new ArrayList<>();
        topicList.add(topicName);
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        if (customOptions != null && !customOptions.isEmpty()) {
            config.putAll(customOptions);
        }
        AdminClient admin = AdminClient.create(config);
        Boolean ret = false;
        try {
            DescribeTopicsResult describeTopicsResult = admin.describeTopics(topicList);
            describeTopicsResult.all().get();
            ret = true;
        } catch (Exception ex) {
            logger.error("get topic fail, bootsrtrapservers: " + bootstrapServers + ", topicName: " + topicName + ", ex: " + ex.toString());
            ret = false;
        } finally {
            admin.close();
        }

        return ret;
    }

    /**
     * 增加分区数
     */
    public static void addPartitions(String bootstrapServers, String topic, Integer numPartitions) {
        Properties config = new Properties();
        config.put(AdminClientConfig
                .BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        AdminClient admin = AdminClient.create(config);
        NewPartitions newPartitions = NewPartitions.increaseTo(numPartitions);
        Map<String, NewPartitions> map = new HashMap<>(1, 1);
        map.put(topic, newPartitions);
        try {
            admin.createPartitions(map).all().get();
            logger.info("增加分区数成功！");
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("org.apache.kafka.common.errors.UnknownTopicOrPartitionException") >= 0) {
                logger.error("更新 topic[" + topic + "]在对应的[" + bootstrapServers + "]上不存在," + ex.getMessage());
            } else if (ex.getMessage().indexOf("org.apache.kafka.common.errors.InvalidPartitionsException") >= 0) {
                logger.error("新分区数只允许扩大，不允许减小," + ex.getMessage());
            } else {
                logger.error("增加分区数失败," + ex.getMessage());
            }
        } finally {
            admin.close();
        }
    }

    /**
     * 删除Topic
     */
    public static void delete(String bootstrapServers, String topic) {
        // 服务端server.properties需要设置delete.topic.enable=true，才可以使用同步删除，否则只是将主题标记为删除
        Properties config = new Properties();
        config.put(AdminClientConfig
                .BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        AdminClient admin = AdminClient.create(config);
        try {
            admin.deleteTopics(Arrays.asList(topic)).all().get();
            logger.info("删除Topic[" + topic + "]成功！");
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("org.apache.kafka.common.errors.UnknownTopicOrPartitionException") >= 0) {
                logger.error("删除 topic[" + topic + "]在对应的[" + bootstrapServers + "]上不存在," + ex.getMessage());
            } else {
                logger.error("删除Topic[" + topic + "]失败！" + ex.getMessage());
            }
        } finally {
            admin.close();
        }
    }

    /**
     * 修改消息最大长度
     */
    public static void alterConfigs(String bootstrapServers, String topic, int maxMessageSize) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        AdminClient admin = AdminClient.create(config);
        Config topicConfig = new Config(Arrays.asList(new ConfigEntry(TopicConfig.MAX_MESSAGE_BYTES_CONFIG, getKafkaMaxMessageSize(maxMessageSize) + "")));
        try {
            admin.alterConfigs(Collections.singletonMap(new ConfigResource(ConfigResource.Type.TOPIC, topic), topicConfig)).all().get();
            logger.info("修改Topic[" + topic + "]最大消息长度成功！");
        } catch (Exception ex) {
            logger.error("修改Topic[" + topic + "]最大消息长度失败！");
        } finally {
            admin.close();
        }
    }

    /**
    * 删除 kafka 消费组
    */
    public static void deleteConsumerGroup(String bootstrapServers, String consumerGroup) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        AdminClient admin = AdminClient.create(config);
        List<String> consumerGroups = new ArrayList<>();
        consumerGroups.add(consumerGroup);
        try {
            admin.deleteConsumerGroups(consumerGroups);
            logger.info("删除 consumer group [" + consumerGroup + "] 成功 ！");
        } catch (Exception ex) {
            logger.info("删除 consumer group [" + consumerGroup + "] 失败 ！");
        } finally {
            admin.close();
        }
    }

    public static void send(String url, String topic, String key, String value, int partition, Long timestamp) {
        Properties props = new Properties();
        props.put("bootstrap.servers", url);
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer producer = new KafkaProducer<String, String>(props);
        producer.send(new ProducerRecord<String, String>(topic, partition, timestamp, key, value));
        producer.close();
    }

    public static void read(String url, String topic, String groupId, String autoOffsetReset) {
        Properties props = new Properties();
        props.put("bootstrap.servers", url);
        props.put("group.id", groupId);
        props.put("auto.offset.reset", autoOffsetReset);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer consumer = new KafkaConsumer<Long, String>(props);
        consumer.subscribe(Collections.singletonList(topic));

        final int giveUp = 100;
        int noRecordsCount = 0;
        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(1000);

            if (consumerRecords.count() == 0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) {
                    break;
                }
            }

            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%d, %s, %d, %d, %d)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset(),record.timestamp());
            });

            consumer.commitAsync();
        }

        consumer.close();
    }

    public static int getKafkaMaxMessageSize(int size) {
        return size * 1024 * 1024;
    }
}
