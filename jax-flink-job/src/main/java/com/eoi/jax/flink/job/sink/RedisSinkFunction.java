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
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.streaming.connectors.redis.common.container.RedisCommandsContainer;
import org.apache.flink.streaming.connectors.redis.common.container.RedisCommandsContainerBuilder;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 继承于RedisSink，添加异常处理和sink相关的metrics
 */
public class RedisSinkFunction<IN> extends RichSinkFunction<IN> {
    private static final Logger LOG = LoggerFactory.getLogger(RedisSinkFunction.class);

    private static final String METRIC_GROUP_NAME = "redis_sink";
    private static final String METRIC_SINK_TOTAL_COUNT = "sink.total";
    private static final String METRIC_SINK_ERROR_COUNT = "sink.error";

    private transient MetricGroup redisMetricGroup;
    private transient Counter totalCounter;
    private transient Counter errorCounter;

    private String additionalKey;
    private RedisMapper<IN> redisSinkMapper;
    private RedisCommand redisCommand;

    private FlinkJedisConfigBase flinkJedisConfigBase;
    private RedisCommandsContainer redisCommandsContainer;
    private RedisSinkJobConfig config;


    public RedisSinkFunction(FlinkJedisConfigBase flinkJedisConfigBase, RedisMapper<IN> redisSinkMapper, RedisSinkJobConfig config) {
        this.flinkJedisConfigBase = flinkJedisConfigBase;
        this.redisSinkMapper = redisSinkMapper;
        RedisCommandDescription redisCommandDescription = redisSinkMapper.getCommandDescription();
        this.redisCommand = redisCommandDescription.getCommand();
        this.additionalKey = redisCommandDescription.getAdditionalKey();
        this.config = config;
    }

    /**
     * 在原来的invoke方法上handle exception, 记录sink相关的metrics
     */
    @Override
    public void invoke(IN input) {
        try {
            String key = redisSinkMapper.getKeyFromData(input);
            String value = redisSinkMapper.getValueFromData(input);

            switch (redisCommand) {
                case RPUSH:
                    this.redisCommandsContainer.rpush(key, value);
                    break;
                case LPUSH:
                    this.redisCommandsContainer.lpush(key, value);
                    break;
                case SADD:
                    this.redisCommandsContainer.sadd(key, value);
                    break;
                case SET:
                    this.redisCommandsContainer.set(key, value);
                    break;
                case PFADD:
                    this.redisCommandsContainer.pfadd(key, value);
                    break;
                case PUBLISH:
                    this.redisCommandsContainer.publish(key, value);
                    break;
                case ZADD:
                    this.redisCommandsContainer.zadd(this.additionalKey, value, key);
                    break;
                case HSET: {
                    if (!config.isSaveAsJson() && input instanceof Map) {
                        Map<String,Object> valueMap = (Map<String,Object>)input;
                        String outKey = this.additionalKey + key;
                        for (String fieldName : valueMap.keySet()) {
                            Object fieldValue = valueMap.get(fieldName);
                            String fieldValueStr = "";
                            if (fieldValue != null) {
                                if (fieldValue instanceof List || fieldValue instanceof Map) {
                                    fieldValueStr = JsonUtil.encode(fieldValue);
                                } else {
                                    fieldValueStr = String.valueOf(fieldValue);
                                }
                            }
                            this.redisCommandsContainer.hset(outKey, fieldName, fieldValueStr);
                        }
                    } else {
                        this.redisCommandsContainer.hset(this.additionalKey, key, value);
                    }
                }
                    break;
                default:
                    throw new IllegalArgumentException("Cannot process such data type: " + redisCommand);
            }
            totalCounter.inc();
        } catch (Exception ex) {
            LOG.error("redis sink error", ex);
            errorCounter.inc();
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        redisMetricGroup = getRuntimeContext().getMetricGroup().addGroup(METRIC_GROUP_NAME);
        totalCounter = redisMetricGroup.counter(METRIC_SINK_TOTAL_COUNT);
        errorCounter = redisMetricGroup.counter(METRIC_SINK_ERROR_COUNT);

        this.redisCommandsContainer = RedisCommandsContainerBuilder.build(this.flinkJedisConfigBase);

    }

    @Override
    public void close() throws IOException {
        if (redisCommandsContainer != null) {
            redisCommandsContainer.close();
        }
    }
}

