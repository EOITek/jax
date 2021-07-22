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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkSinkJobBuilder;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import com.eoi.jax.api.tuple.Tuple2;
import com.eoi.jax.flink.job.common.AdvanceConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisClusterConfig;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisSentinelConfig;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.eoi.jax.flink.job.sink.RedisSinkJobConfig.REDIS_MODE_CLUSTER;
import static com.eoi.jax.flink.job.sink.RedisSinkJobConfig.REDIS_MODE_SENTINEL;
import static com.eoi.jax.flink.job.sink.RedisSinkJobConfig.REDIS_MODE_SINGLE;

@Job(
        name = "RedisSinkJob",
        display = "输出到redis",
        description = "根据redisCommand输出到redis的数据结构中",
        icon = "RedisSinkJob.svg",
        doc = "RedisSinkJob.md"
)
public class RedisSinkJob extends AdvanceConfig implements FlinkSinkJobBuilder<DataStream<Map<String, Object>>, RedisSinkJobConfig> {
    @Override
    public void build(
            FlinkEnvironment context,
            DataStream<Map<String, Object>> mapDataStream,
            RedisSinkJobConfig config,
            JobMetaConfig metaConfig) throws Exception {

        List<Tuple2<String, Integer>> hosts = parseHosts(config.getHosts());
        FlinkJedisConfigBase conf;

        if (REDIS_MODE_SINGLE.equals(config.getRedisMode())) {
            FlinkJedisPoolConfig.Builder redisBuilder = new FlinkJedisPoolConfig.Builder().setHost(hosts.get(0).f0).setPort(hosts.get(0).f1);
            if (!StrUtil.isEmpty(config.getPassword())) {
                redisBuilder = redisBuilder.setPassword(config.getPassword());
            }
            conf = redisBuilder.build();
        } else if (REDIS_MODE_SENTINEL.equals(config.getRedisMode())) {
            Set<String> sentinels = new HashSet<>(config.getHosts());
            FlinkJedisSentinelConfig.Builder redisBuilder = new FlinkJedisSentinelConfig.Builder().setSentinels(sentinels).setMasterName(config.getMaster());
            if (!StrUtil.isEmpty(config.getPassword())) {
                redisBuilder = redisBuilder.setPassword(config.getPassword());
            }
            conf = redisBuilder.build();
        } else if (REDIS_MODE_CLUSTER.equals(config.getRedisMode())) {
            Set<InetSocketAddress> nodes = new HashSet<>();
            for (Tuple2<String, Integer> hostPort : parseHosts(config.getHosts())) {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(hostPort.f0, hostPort.f1);
                nodes.add(inetSocketAddress);
            }

            FlinkJedisClusterConfig.Builder redisBuilder = new FlinkJedisClusterConfig.Builder().setNodes(nodes);
            conf = redisBuilder.build();
        } else {
            throw new JobConfigValidationException("unknown redis mode" + config.getRedisMode());
        }

        RedisSinkFunction redisSink = new RedisSinkFunction(conf, new RedisSinkMapper(config), config);
        setAdvanceConfig(mapDataStream.addSink(redisSink).name(metaConfig.getJobEntry()).uid(metaConfig.getJobId()), metaConfig.getOpts());
    }

    @Override
    public RedisSinkJobConfig configure(Map<String, Object> map) throws Exception {
        RedisSinkJobConfig config = new RedisSinkJobConfig();
        ParamUtil.configJobParams(config,map);
        return config;
    }

    public static List<Tuple2<String, Integer>> parseHosts(List<String> hosts) {
        List<Tuple2<String, Integer>> hostList = new ArrayList<>();
        for (String hostAndPort : hosts) {
            hostList.add(parseHostAndPort(hostAndPort));
        }
        return hostList;
    }

    private static Tuple2<String, Integer> parseHostAndPort(String hostAndPort) {
        String[] hostAndPortArray = hostAndPort.split(":");
        return Tuple2.of(hostAndPortArray[0], Integer.parseInt(hostAndPortArray[1]));
    }
}
