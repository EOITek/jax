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
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.flink.job.common.AdvanceConfig;
import com.eoi.jax.flink1_12.job.process.WatermarkJob;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.Map;


@Job(
        name = "KafkaSourceJob",
        display = "Kafka数据源接入【流批一体,BETA】",
        description = "Kafka Source 返回`Map<String,Object>`"
                + "1. kafka message的value数据为byte数组类型，通过参数byteArrayFieldName指定输出字段名（默认为bytes）\n"
                + "2. kafka的元数据输出到参数metaFieldName指定字段名（默认为meta）",
        icon = "KafkaByteSourceJob.svg",
        doc = "KafkaSourceJob.md"
)
public class KafkaSourceJob extends AdvanceConfig implements FlinkSourceJobBuilder<DataStream<Map<String,Object>>, KafkaSourceJobConfig> {

    @Override
    public KafkaSourceJobConfig configure(Map<String, Object> mapConfig) throws Exception {
        KafkaSourceJobConfig config = new KafkaSourceJobConfig(mapConfig);
        return config;
    }

    @Override
    public DataStream<Map<String,Object>> build(FlinkEnvironment context, KafkaSourceJobConfig config, JobMetaConfig metaConfig) throws JobConfigValidationException {
        config.getTopicCreateConfig().createTopicsOrNot(config.getBootstrapServersString(), config.getTopics(), config.getAuthConfig().toMap());

        // 开始构造KafkaSourceBuilder
        KafkaSourceBuilder<Map<String,Object>> builder = KafkaSource.<Map<String,Object>>builder()
                .setBootstrapServers(String.join(",", config.getBootstrapServers()))
                .setGroupId(config.getGroupId())
                .setTopics(config.getTopics())
                .setDeserializer(new KafkaSourceDeserialization(config.getByteArrayFieldName(),config.getMetaFieldName()))
                .setProperties(config.getProperties());

        // 设置读取开始的offset，默认为上次读取到的位置
        if (config.getStartingOffset() != null) {
            builder = builder.setStartingOffsets(getOffsetsInitializer(config.getStartingOffset()));
        } else {
            builder = builder.setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST));
        }

        // 设置读取结束的offset
        // streaming模式可选，batch模式必须设置
        if (config.getStoppingOffset() != null) {
            // batch模式
            if (config.getBounded()) {
                builder.setBounded(getOffsetsInitializer(config.getStoppingOffset()));
            } else {
                // streaming模式
                builder.setUnbounded(getOffsetsInitializer(config.getStoppingOffset()));
            }
        } else {
            // batch模式必须设置结束offset
            if (config.getBounded()) {
                throw new JobConfigValidationException("StoppingOffset should be set in bounded / batch mode");
            }
        }

        KafkaSource<Map<String,Object>> source = builder.build();

        // event模式下，可在source直接设置watermark，后续节点都会带上
        // 可设置最大idlenessTime避免某个分区长时间没有数据而影响到整体watermark
        WatermarkStrategy watermarkStrategy = WatermarkStrategy.noWatermarks();
        if (config.getWatermarkJobConfig() != null) {
            watermarkStrategy = WatermarkJob.getWatermarkStrategy(config.getWatermarkJobConfig());
        }

        DataStream<Map<String,Object>> sourceStream = setAdvanceConfig(context.streamEnv
                .fromSource(source,watermarkStrategy, metaConfig.getJobId())
                .name(metaConfig.getJobEntry()).uid(metaConfig.getJobId()), metaConfig.getOpts());

        // 是否数据平衡，避免热点
        if (config.getRebalancePartition()) {
            sourceStream = sourceStream.rebalance();
        }

        return sourceStream;
    }

    private OffsetsInitializer getOffsetsInitializer(KafkaOffsetSetting config) throws JobConfigValidationException {

        switch (config.getOffsetMode()) {
            case KafkaOffsetSetting.OFFSETMODE_EARLIEST:
                return OffsetsInitializer.earliest();
            case KafkaOffsetSetting.OFFSETMODE_LATEST:
                return OffsetsInitializer.latest();
            case KafkaOffsetSetting.OFFSETMODE_TIMESTAMP:
                return OffsetsInitializer.timestamp(config.getTimestamp());
            case KafkaOffsetSetting.OFFSETMODE_SPECIFIED: {
                if (config.getSpecifiedOffsets() != null && config.getSpecifiedOffsets().size() > 0) {
                    Map<TopicPartition, Long> offsets = new HashMap<>();
                    for (KafkaSpecifiedOffset o : config.getSpecifiedOffsets()) {
                        TopicPartition partition = new TopicPartition(o.getTopic(), o.getPartition());
                        offsets.put(partition, o.getOffset());
                    }
                    return OffsetsInitializer.offsets(offsets, OffsetResetStrategy.valueOf(config.getOffsetResetStrategy()));
                } else {
                    throw new JobConfigValidationException("SpecifiedOffsets should be set in specified mode");
                }
            }
            default:
                return OffsetsInitializer.committedOffsets(OffsetResetStrategy.valueOf(config.getOffsetResetStrategy()));
        }
    }
}
