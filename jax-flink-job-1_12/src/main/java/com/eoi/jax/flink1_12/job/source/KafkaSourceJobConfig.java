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

import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.FlatStringMap;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.api.annotation.Shift;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.flink.job.common.KafkaSecurityConfig;
import com.eoi.jax.flink.job.common.KafkaTopicCreateConfig;
import com.eoi.jax.flink1_12.job.process.WatermarkJobConfig;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

//CHECKSTYLE.OFF:
import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
//CHECKSTYLE.ON:

public class KafkaSourceJobConfig implements ConfigValidatable, Serializable {

    public static final String DEFAULT_BYTE_ARRAY_FIELD_NAME = "bytes";
    public static final String DEFAULT_META_FIELD_NAME = "meta";

    @Parameter(
            label = "Kafka地址列表",
            description = "Kafka server 服务器列表,多个用逗号分隔"
    )
    private List<String> bootstrapServers;

    @Parameter(
            label = "topics",
            description = "消费的数据源topics列表"
    )
    private List<String> topics;

    @Parameter(
            label = "消费组",
            description = "Kafka消费数据的消费组"
    )
    private String groupId;

    @Parameter(
            label = "开始消费offset配置",
            description = "开始消费offset配置，默认从上次消费到的位置开始",
            optional = true
    )
    private KafkaOffsetSetting startingOffset;

    @Parameter(
            label = "数据是否有限",
            description = "如果数据有限，则可以使用batch模式",
            optional = true,
            defaultValue = "false"
    )
    private Boolean bounded;

    @Parameter(
            label = "结束消费offset配置",
            description = "如果数据有限为true，需要配置结束消费offset",
            optional = true
    )
    private KafkaOffsetSetting stoppingOffset;

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

    @Parameter(
            label = "watermark配置项",
            description = "设置watermark，参考WatermarkJob的文档；",
            optional = true
    )
    private WatermarkJobConfig watermarkJobConfig;

    @Parameter(
            label = "是否平均分发分区数据",
            description = "确保每个slot数据量基本一致，这样并发度可以大于topic的partition数，便于提高任务整体并发度",
            optional = true,
            defaultValue = "true"
    )
    private Boolean rebalancePartition;

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

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public Boolean getRebalancePartition() {
        return rebalancePartition;
    }

    public void setRebalancePartition(Boolean rebalancePartition) {
        this.rebalancePartition = rebalancePartition;
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

    public WatermarkJobConfig getWatermarkJobConfig() {
        return watermarkJobConfig;
    }

    public void setWatermarkJobConfig(WatermarkJobConfig watermarkJobConfig) {
        this.watermarkJobConfig = watermarkJobConfig;
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getGroupId() {
        return properties.get(GROUP_ID_CONFIG).toString();
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

    public KafkaOffsetSetting getStartingOffset() {
        return startingOffset;
    }

    public void setStartingOffset(KafkaOffsetSetting startingOffset) {
        this.startingOffset = startingOffset;
    }

    public Boolean getBounded() {
        return bounded;
    }

    public void setBounded(Boolean bounded) {
        this.bounded = bounded;
    }

    public KafkaOffsetSetting getStoppingOffset() {
        return stoppingOffset;
    }

    public void setStoppingOffset(KafkaOffsetSetting stoppingOffset) {
        this.stoppingOffset = stoppingOffset;
    }

    public KafkaSourceJobConfig(Map<String, Object> config) throws Exception {
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
        properties.put(AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.put(FETCH_MAX_BYTES_CONFIG, DEFAULT_FETCH_MAX_BYTES);
        properties.put(MAX_PARTITION_FETCH_BYTES_CONFIG, DEFAULT_FETCH_MAX_BYTES);
        properties.put("flink.partition-discovery.interval-millis", 30000);
    }

    @Override
    public void validate() throws JobConfigValidationException {
    }
}
