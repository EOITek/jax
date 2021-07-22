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

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import com.eoi.jax.common.JsonUtil;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RedisSinkMapper implements RedisMapper<Map<String, Object>> {

    public static Logger LOGGER = LoggerFactory.getLogger(RedisSinkMapper.class);

    private RedisSinkJobConfig config;
    private static MD5 md5 = SecureUtil.md5();

    public RedisSinkMapper(RedisSinkJobConfig config) {
        this.config = config;
    }

    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(RedisCommand.valueOf(config.getRedisCommand()), config.getAdditionKey());
    }

    @Override
    public String getKeyFromData(Map<String, Object> map) {
        String key = "";
        if (map.get(config.getKeyField()) != null) {
            key = map.get(config.getKeyField()).toString();
        } else {
            try {
                key = md5.digestHex(JsonUtil.encode(map));
            } catch (Exception ignore) {

            }
        }
        return key;
    }

    @Override
    public String getValueFromData(Map<String, Object> map) {
        if (map == null) {
            return "";
        }

        try {
            if (config.isEnableCompress()) {
                return JsonUtil.encodeWithGzip(map);
            } else {
                return JsonUtil.encode(map);
            }
        } catch (Exception ex) {
            LOGGER.error("getValueFromData error", ex);
            return "";
        }
    }
}
