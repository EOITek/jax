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


package com.eoi.jax.common;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KafkaUtil {

    private KafkaUtil() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(KafkaUtil.class);

    private static AdminClient getAdminClient(String bootstrapServers, Map<String, Object> configuration) {
        Map<String, Object> conf = new HashMap<>();
        conf.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        if (configuration != null) {
            conf.putAll(configuration);
        }
        return AdminClient.create(conf);
    }

    public static void createTopicIfNotExist(
            String bootstrapServers,
            String topicName) {
        createTopicIfNotExist(bootstrapServers, topicName, 1, (short) 1);
    }

    public static void createTopicIfNotExist(
            String bootstrapServers,
            String topicName,
            int numPartitions,
            short numReplications) {
        createTopicIfNotExist(bootstrapServers, topicName, numPartitions, numReplications, null);
    }

    public static void createTopicIfNotExist(
            String bootstrapServers,
            String topicName,
            int numPartitions,
            short numReplications,
            Long maxMessageSize) {
        createTopicIfNotExist(bootstrapServers, topicName, numPartitions, numReplications, maxMessageSize, null);
    }

    /**
     * 创建topic，如果不存在的话
     */
    public static void createTopicIfNotExist(
            String bootstrapServers,
            String topicName,
            int numPartitions,
            short numReplications,
            Long maxMessageSize,
            Map<String, Object> customProperties) {
        if (topicIsExists(bootstrapServers, topicName, customProperties)) {
            return;
        }
        try (AdminClient adminClient = getAdminClient(bootstrapServers, customProperties)) {
            Map<String, String> configs = new HashMap<>();
            if (maxMessageSize != null) {
                configs.put(TopicConfig.MAX_MESSAGE_BYTES_CONFIG, maxMessageSize + "");
            }
            NewTopic newTopic = new NewTopic(topicName, numPartitions, numReplications).configs(configs);
            CreateTopicsResult createTopicsResult = adminClient.createTopics(Arrays.asList(newTopic));
            createTopicsResult.values().get(topicName).get();
            LOG.info("创建Topic[{}]成功！", topicName);
        } catch (Exception ex) {
            LOG.error("创建Topic[{}]失败！, ex: {}", topicName, ex.toString());
        }
    }

    /**
     * 检查topic是否存在
     */
    public static boolean topicIsExists(String bootstrapServers, String topicName, Map<String, Object> customOptions) {
        List<String> topicList = new ArrayList<>();
        topicList.add(topicName);
        try (AdminClient adminClient = getAdminClient(bootstrapServers, customOptions)) {
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(topicList);
            describeTopicsResult.all().get();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
