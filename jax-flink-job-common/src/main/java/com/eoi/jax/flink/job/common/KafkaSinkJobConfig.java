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
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.DEFAULT_FETCH_MAX_BYTES;
import static org.apache.kafka.clients.producer.ProducerConfig.MAX_REQUEST_SIZE_CONFIG;


public class KafkaSinkJobConfig implements ConfigValidatable, Serializable {

    @Shift
    private KafkaTopicCreateConfig topicCreateConfig;

    private KafkaSecurityConfig authConfig;
    private Properties properties;

    @Parameter(
            label = "kafka Producer配置项",
            description = "透传kafka Producer配置项;\n"
                    + "可选填项请参考：https://kafka.apache.org/documentation/#producerconfigs",
            optional = true
    )
    private FlatStringMap kafkaProducerProperties;

    @Parameter(
            label = "topic",
            description = "输出到的topic名"
    )
    private String topic;

    @Parameter(
            label = "Kafka地址列表",
            description = "Kafka server 服务器列表"
    )
    private List<String> bootstrapServers;

    @Parameter(
            label = "FlinkKafkaProducer模式",
            description = "支持: EXACTLY_ONCE， AT_LEAST_ONCE， NONE",
            optional = true,
            defaultValue = "AT_LEAST_ONCE",
            candidates = {"EXACTLY_ONCE", "AT_LEAST_ONCE", "NONE"}
    )
    private String semantic;

    @Parameter(
            label = "是否忽略异常",
            description = "当写kafka失败时，例如消息过大等错误时，是忽略异常只记录日志（true），还是任务失败（false）",
            optional = true,
            defaultValue = "true"
    )
    private Boolean logFailuresOnly;

    @Parameter(
            label = "是否把数据的时间作为kafka记录的时间",
            description = "设置为false避免flink把数据event time的时间作为kafka记录时间写入，否则可能会造成历史数据很快过期被自动清理掉",
            optional = true,
            defaultValue = "false"
    )
    private Boolean writeTimestampToKafka;

    @Parameter(
            label = "kafka 分区方法",
            description = "当生成kafka记录时的分区方法，可设置为FIXED, ROUND_ROBIN, BY_KEY",
            optional = true,
            defaultValue = "ROUND_ROBIN",
            candidates = {"FIXED", "ROUND_ROBIN", "BY_KEY"}
    )
    private String kafkaPartitionType;

    @Parameter(
            label = "kafka 分区key字段",
            description = "当partitionType为BY_KEY时，该字段为必填字段，需要设置采用哪个字段作为kafka record的分区key",
            optional = true,
            availableCondition = "kafkaPartitionType == 'BY_KEY'",
            requireCondition = "kafkaPartitionType == 'BY_KEY'"
    )
    private String partitionKey;

    public FlinkKafkaPartitionType kafkaPartitionTypeEnum;

    public KafkaTopicCreateConfig getTopicCreateConfig() {
        return topicCreateConfig;
    }

    public void setTopicCreateConfig(KafkaTopicCreateConfig topicCreateConfig) {
        this.topicCreateConfig = topicCreateConfig;
    }

    public KafkaSecurityConfig getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(KafkaSecurityConfig authConfig) {
        this.authConfig = authConfig;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public FlatStringMap getKafkaProducerProperties() {
        return kafkaProducerProperties;
    }

    public void setKafkaProducerProperties(FlatStringMap kafkaProducerProperties) {
        this.kafkaProducerProperties = kafkaProducerProperties;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getSemantic() {
        return semantic;
    }

    public void setSemantic(String semantic) {
        this.semantic = semantic;
    }

    public Boolean getLogFailuresOnly() {
        return logFailuresOnly;
    }

    public void setLogFailuresOnly(Boolean logFailuresOnly) {
        this.logFailuresOnly = logFailuresOnly;
    }

    public Boolean getWriteTimestampToKafka() {
        return writeTimestampToKafka;
    }

    public void setWriteTimestampToKafka(Boolean writeTimestampToKafka) {
        this.writeTimestampToKafka = writeTimestampToKafka;
    }

    public String getKafkaPartitionType() {
        return kafkaPartitionType;
    }

    public void setKafkaPartitionType(String kafkaPartitionType) {
        this.kafkaPartitionType = kafkaPartitionType;
    }

    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    public KafkaSinkJobConfig(Map<String, Object> config) throws Exception {
        ParamUtil.configJobParams(this, config);
        properties = new Properties();

        // kafka producer 参数默认值配置
        setDefaultValues();


        // kafka producer 自定义参数配置
        if (kafkaProducerProperties != null) {
            // 参考org.apache.kafka.common.config.ConfigDef的parseType方法
            // 所有配置值为string会自动转换为对应类型
            properties.putAll(kafkaProducerProperties);
        }
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServersString());

        // SASL 和 SSL相关参数设置
        authConfig = new KafkaSecurityConfig();
        authConfig.authConfig(properties, config);

        kafkaPartitionTypeEnum = FlinkKafkaPartitionType.valueOf(kafkaPartitionType);
    }

    private void setDefaultValues() {
        //最佳实践为lz4压缩，带宽降低6倍，cpu消耗也有下降
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        //最佳实践为batch.size = 128K, linger.ms = 5ms
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 131072);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        properties.put(MAX_REQUEST_SIZE_CONFIG, DEFAULT_FETCH_MAX_BYTES);
    }

    public String getBootstrapServersString() {
        return String.join(",",bootstrapServers);
    }

    @Override
    public void validate() throws JobConfigValidationException {
        if (topic == null || "".equals(topic)) {
            throw new JobConfigValidationException("kafka producer topic为空");
        }
    }
}
