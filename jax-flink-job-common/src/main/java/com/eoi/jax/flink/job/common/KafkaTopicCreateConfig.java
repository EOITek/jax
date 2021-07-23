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

import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class KafkaTopicCreateConfig implements ConfigValidatable, Serializable {
    public static final String KEY_AUTO_CREATE_TOPICS = "autoCreateTopic";
    public static final String KEY_AUTO_CREATE_TOPICS_PARTITIONS = "autoCreateTopicPartitions";
    public static final String KEY_AUTO_CREATE_TOPICS_REPLICATION_FACTOR = "autoCreateTopicReplicationFactor";

    /**
     * 默认 1024MB
     */
    public static final Integer DEFAULT_TOPIC_SIZE = 1024;

    @Parameter(name = KEY_AUTO_CREATE_TOPICS,
            label = "自动创建topic",
            description = "是否自动创建topic,  true: 自动，false:不创建, 默认: false",
            defaultValue = "false"
    )
    private Boolean autoCreateTopic;

    @Parameter(name = KEY_AUTO_CREATE_TOPICS_PARTITIONS,
            label = "自动创建topics分区数",
            description = "autoCreateTopic=true时，自动创建topics分区数,默认1",
            defaultValue = "1",
            optional = true,
            availableCondition = "autoCreateTopic == true"
    )
    private Integer autoCreateTopicPartitions;

    @Parameter(name = KEY_AUTO_CREATE_TOPICS_REPLICATION_FACTOR,
            label = "自动创建topics副本数",
            description = "autoCreateTopic=true时，自动创建topics副本数,默认1",
            defaultValue = "1",
            optional = true,
            availableCondition = "autoCreateTopic == true"
    )
    private Integer autoCreateTopicReplications;

    public Boolean getAutoCreateTopic() {
        return autoCreateTopic;
    }

    public Integer getAutoCreateTopicPartitions() {
        return autoCreateTopicPartitions;
    }

    public Integer getAutoCreateTopicReplications() {
        return autoCreateTopicReplications;
    }

    public void createTopicOrNot(String bootstrapServers, String topic, Map<String,Object> customOptions) {
        if (Boolean.TRUE.equals(autoCreateTopic)) {
            KafkaUtil.createTopicByAdminClient(bootstrapServers, DEFAULT_TOPIC_SIZE, autoCreateTopicPartitions, autoCreateTopicReplications, topic, customOptions);
        }
    }

    public void createTopicsOrNot(String bootstrapServers, List<String> topics,Map<String,Object> customOptions) {
        if (Boolean.TRUE.equals(autoCreateTopic)) {
            topics.forEach(topic ->
                    KafkaUtil.createTopicByAdminClient(bootstrapServers, DEFAULT_TOPIC_SIZE, autoCreateTopicPartitions, autoCreateTopicReplications, topic, customOptions));
        }
    }

    @Override
    public void validate() throws JobConfigValidationException {

    }
}
