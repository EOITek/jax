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
import com.eoi.jax.api.annotation.FlatStringMap;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.api.annotation.Shift;
import com.eoi.jax.api.reflect.ParamUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.DEFAULT_FETCH_MAX_BYTES;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_BYTES_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG;

public class KafkaByteSourceJobConfig implements ConfigValidatable, Serializable {

    public static final String OFFSETMODE_GROUP = "group";
    public static final String OFFSETMODE_LATEST = "latest";
    public static final String OFFSETMODE_EARLIEST = "earliest";
    public static final String OFFSETMODE_TIMESTAMP = "timestamp";

    public static final String AUTO_OFFSET_RESET_CONFIG_LATEST = "latest";
    public static final String AUTO_OFFSET_RESET_CONFIG_EARLIEST = "earliest";

    public static final String KEY_BYTE_ARRAY_FIELD_NAME = "byteArrayFieldName";
    public static final String DEFAULT_BYTE_ARRAY_FIELD_NAME = "bytes";

    public static final String KEY_META_FIELD_NAME = "metaFieldName";
    public static final String DEFAULT_META_FIELD_NAME = "meta";

    @Parameter(
            label = "offset模式",
            description = "支持四种模式读取kafka offset, 分别是: group, earliest, latest, timestamp, "
                    + "其中group, 需要配置auto.offset.reset，它的值为earliest, 或者 latest, timestamp模式需要配置offsetModeTimestamp,为unix毫秒格式",
            optional = true,
            defaultValue = "group",
            candidates = {"earliest","latest","timestamp","group"}
    )
    private String offsetMode;

    @Parameter(
            label = "消费开始时间",
            description = "当offsetMode为timestamp时， 指定的开始时间，格式：unix毫秒时间戳",
            optional = true,
            defaultValue = "0",
            availableCondition = "offsetMode == 'timestamp'",
            requireCondition = "offsetMode == 'timestamp'"
    )
    private Long offsetModeTimestamp;

    @Parameter(
            label = "topics",
            description = "消费的数据源topics列表"
    )
    private List<String> topics;

    @Parameter(
            label = "是否要提交offset到checkpoint",
            description = "如果提交offset到checkpoint，异常时可恢复到上一次的checkpoint的offset重新消费，但只有在checkpoint时更新到kafka上",
            optional = true,
            defaultValue = "true"
    )
    private Boolean commitOffsetOnCheckPoint;

    @Parameter(
            label = "是否平均分发分区数据",
            description = "确保每个slot数据量基本一致，这样并发度可以大于topic的partition数，便于提高任务整体并发度",
            optional = true,
            defaultValue = "true"
    )
    private Boolean rebalancePartition;

    @Parameter(
            label = "消费组",
            description = "Kafka 消费数据的消费组"
    )
    private String groupId;

    @Parameter(
            label = "Kafka地址列表",
            description = "Kafka server 服务器列表,多个用逗号分隔"
    )
    private List<String> bootstrapServers;


    @Parameter(
            label = "kafka消息值的byte数组输出字段",
            description = "kafka message的value数据为byte数组类型，通过参数byteArrayFieldName指定输出字段名",
            optional = true,
            defaultValue = DEFAULT_BYTE_ARRAY_FIELD_NAME
    )
    private String byteArrayFieldName;

    @Parameter(
            label = "kafka元数据输出字段",
            description = "kafka的元数据输出到参数metaFieldName指定字段名",
            optional = true,
            defaultValue = DEFAULT_META_FIELD_NAME
    )
    private String metaFieldName;

    @Parameter(
            label = "kafka consumer配置项",
            description = "透传kafka consumer配置项\n"
                    + "可选填项请参考：https://kafka.apache.org/documentation/#consumerconfigs",
            optional = true
    )
    private FlatStringMap kafkaConsumerProperties;

    private Properties properties;

    private KafkaSecurityConfig authConfig;

    @Shift
    private KafkaTopicCreateConfig topicCreateConfig;

    public FlatStringMap getKafkaConsumerProperties() {
        return kafkaConsumerProperties;
    }

    public void setKafkaConsumerProperties(FlatStringMap kafkaConsumerProperties) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
    }

    public String getOffsetMode() {
        return offsetMode;
    }

    public void setOffsetMode(String offsetMode) {
        this.offsetMode = offsetMode;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public void setCommitOffsetOnCheckPoint(Boolean commitOffsetOnCheckPoint) {
        this.commitOffsetOnCheckPoint = commitOffsetOnCheckPoint;
    }

    public Boolean getRebalancePartition() {
        return rebalancePartition;
    }

    public void setRebalancePartition(Boolean rebalancePartition) {
        this.rebalancePartition = rebalancePartition;
    }

    public void setOffsetModeTimestamp(Long offsetModeTimestamp) {
        this.offsetModeTimestamp = offsetModeTimestamp;
    }

    public String getByteArrayFieldName() {
        return byteArrayFieldName;
    }

    public void setByteArrayFieldName(String byteArrayFieldName) {
        this.byteArrayFieldName = byteArrayFieldName;
    }

    public String getMetaFieldName() {
        return metaFieldName;
    }

    public void setMetaFieldName(String metaFieldName) {
        this.metaFieldName = metaFieldName;
    }

    public KafkaSecurityConfig getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(KafkaSecurityConfig authConfig) {
        this.authConfig = authConfig;
    }

    public KafkaTopicCreateConfig getTopicCreateConfig() {
        return topicCreateConfig;
    }

    public void setTopicCreateConfig(KafkaTopicCreateConfig topicCreateConfig) {
        this.topicCreateConfig = topicCreateConfig;
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getGroupId() {
        return properties.get(GROUP_ID_CONFIG).toString();
    }

    public Boolean getCommitOffsetOnCheckPoint() {
        return commitOffsetOnCheckPoint;
    }

    public Long getOffsetModeTimestamp() {
        return offsetModeTimestamp;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public String getBootstrapServersString() {
        return String.join(",",bootstrapServers);
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public KafkaByteSourceJobConfig(Map<String, Object> config) throws Exception {
        ParamUtil.configJobParams(this, config);
        properties = new Properties();

        // kafka consumer 参数默认值配置
        setDefaultValues();

        // kafka consumer 自定义参数配置
        if (kafkaConsumerProperties != null) {
            // 参考org.apache.kafka.common.config.ConfigDef的parseType方法
            // 所有配置值为string会自动转换为对应类型
            properties.putAll(kafkaConsumerProperties);
        }
        properties.put(BOOTSTRAP_SERVERS_CONFIG, getBootstrapServersString());
        properties.put(GROUP_ID_CONFIG, groupId);

        // SASL 和 SSL相关参数设置
        authConfig = new KafkaSecurityConfig();
        authConfig.authConfig(properties, config);
    }

    private void setDefaultValues() throws Exception {
        properties.put(MAX_POLL_RECORDS_CONFIG, 500);
        properties.put(MAX_POLL_INTERVAL_MS_CONFIG, 300000);
        properties.put(SESSION_TIMEOUT_MS_CONFIG, 10000);
        properties.put(HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        properties.put(ENABLE_AUTO_COMMIT_CONFIG, true);
        properties.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, 5000);
        properties.put(AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET_CONFIG_LATEST);
        properties.put(FETCH_MAX_BYTES_CONFIG, DEFAULT_FETCH_MAX_BYTES);
        properties.put(MAX_PARTITION_FETCH_BYTES_CONFIG, DEFAULT_FETCH_MAX_BYTES);
        properties.put("flink.partition-discovery.interval-millis", 30000);
    }

    private void checkOffsetMode() throws JobConfigValidationException {
        if (offsetMode.equals(OFFSETMODE_GROUP)) {
            String x = properties.getProperty(AUTO_OFFSET_RESET_CONFIG);
            if (!x.equals(AUTO_OFFSET_RESET_CONFIG_EARLIEST) && !x.equals(AUTO_OFFSET_RESET_CONFIG_LATEST)) {
                throw new JobConfigValidationException("当 offsetMode 为group时，auto.offset.reset 必须为 latest 或者 earliest");
            }
        }
    }

    @Override
    public void validate() throws JobConfigValidationException {
        if (topics == null || topics.isEmpty()) {
            throw new JobConfigValidationException("kafka consumer topics为空");
        }

        if (properties == null) {
            throw new JobConfigValidationException("kafka consumer property 参数为空");
        }

        checkOffsetMode();
    }
}
