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
            label = "offset??????",
            description = "????????????????????????kafka offset, ?????????: group, earliest, latest, timestamp, "
                    + "??????group, ????????????auto.offset.reset???????????????earliest, ?????? latest, timestamp??????????????????offsetModeTimestamp,???unix????????????",
            optional = true,
            defaultValue = "group",
            candidates = {"earliest","latest","timestamp","group"}
    )
    private String offsetMode;

    @Parameter(
            label = "??????????????????",
            description = "???offsetMode???timestamp?????? ?????????????????????????????????unix???????????????",
            optional = true,
            defaultValue = "0",
            availableCondition = "offsetMode == 'timestamp'",
            requireCondition = "offsetMode == 'timestamp'"
    )
    private Long offsetModeTimestamp;

    @Parameter(
            label = "topics",
            description = "??????????????????topics??????"
    )
    private List<String> topics;

    @Parameter(
            label = "???????????????offset???checkpoint",
            description = "????????????offset???checkpoint????????????????????????????????????checkpoint???offset???????????????????????????checkpoint????????????kafka???",
            optional = true,
            defaultValue = "true"
    )
    private Boolean commitOffsetOnCheckPoint;

    @Parameter(
            label = "??????????????????????????????",
            description = "????????????slot???????????????????????????????????????????????????topic???partition???????????????????????????????????????",
            optional = true,
            defaultValue = "true"
    )
    private Boolean rebalancePartition;

    @Parameter(
            label = "?????????",
            description = "Kafka ????????????????????????"
    )
    private String groupId;

    @Parameter(
            label = "Kafka????????????",
            description = "Kafka server ???????????????,?????????????????????"
    )
    private List<String> bootstrapServers;


    @Parameter(
            label = "kafka????????????byte??????????????????",
            description = "kafka message???value?????????byte???????????????????????????byteArrayFieldName?????????????????????",
            optional = true,
            defaultValue = DEFAULT_BYTE_ARRAY_FIELD_NAME
    )
    private String byteArrayFieldName;

    @Parameter(
            label = "kafka?????????????????????",
            description = "kafka???????????????????????????metaFieldName???????????????",
            optional = true,
            defaultValue = DEFAULT_META_FIELD_NAME
    )
    private String metaFieldName;

    @Parameter(
            label = "kafka consumer?????????",
            description = "??????kafka consumer?????????\n"
                    + "????????????????????????https://kafka.apache.org/documentation/#consumerconfigs",
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

        // kafka consumer ?????????????????????
        setDefaultValues();

        // kafka consumer ?????????????????????
        if (kafkaConsumerProperties != null) {
            // ??????org.apache.kafka.common.config.ConfigDef???parseType??????
            // ??????????????????string??????????????????????????????
            properties.putAll(kafkaConsumerProperties);
        }
        properties.put(BOOTSTRAP_SERVERS_CONFIG, getBootstrapServersString());
        properties.put(GROUP_ID_CONFIG, groupId);

        // SASL ??? SSL??????????????????
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
                throw new JobConfigValidationException("??? offsetMode ???group??????auto.offset.reset ????????? latest ?????? earliest");
            }
        }
    }

    @Override
    public void validate() throws JobConfigValidationException {
        if (topics == null || topics.isEmpty()) {
            throw new JobConfigValidationException("kafka consumer topics??????");
        }

        if (properties == null) {
            throw new JobConfigValidationException("kafka consumer property ????????????");
        }

        checkOffsetMode();
    }
}
