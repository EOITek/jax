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
import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.InputType;
import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;
import java.util.List;


public class RedisSinkJobConfig implements ConfigValidatable, Serializable {

    public static final String REDIS_MODE_SINGLE = "single";
    public static final String REDIS_MODE_SENTINEL = "sentinel";
    public static final String REDIS_MODE_CLUSTER = "cluster";

    public static final String REDIS_CMD_LPUSH = "LPUSH";
    public static final String REDIS_CMD_RPUSH = "RPUSH";
    public static final String REDIS_CMD_SADD = "SADD";
    public static final String REDIS_CMD_SET = "SET";
    public static final String REDIS_CMD_PFADD = "PFADD";
    public static final String REDIS_CMD_PUBLISH = "PUBLISH";
    public static final String REDIS_CMD_ZADD = "ZADD";
    public static final String REDIS_CMD_HSET = "HSET";


    @Parameter(
            label = "redis命令",
            description = "redis命令，决定存储redis的数据结构；默认用HSET",
            optional = true,
            defaultValue = REDIS_CMD_HSET,
            candidates = {REDIS_CMD_LPUSH,REDIS_CMD_RPUSH,REDIS_CMD_SADD,REDIS_CMD_SET,REDIS_CMD_PFADD,REDIS_CMD_PUBLISH,REDIS_CMD_ZADD,REDIS_CMD_HSET}
    )
    private String redisCommand;

    @Parameter(
            label = "外层Key",
            description = "HSET或ZADD命令时，使用的外层key",
            optional = true,
            requireCondition = "['HSET','ZADD'].includes(redisCommand)",
            availableCondition = "['HSET','ZADD'].includes(redisCommand)"
    )
    private String additionKey;

    @Parameter(
            label = "key字段名",
            description = "如果不填，默认则是Map的Json md5值"
    )
    private String keyField;

    @Parameter(
            label = "redis的类型",
            description = "redis的类型，可选为 single, sentinel, cluster",
            defaultValue = "single",
            candidates = {REDIS_MODE_SINGLE, REDIS_MODE_SENTINEL, REDIS_MODE_CLUSTER}
    )
    private String redisMode;

    @Parameter(
            label = "redis主机地址",
            description = "格式为 host1:port1,例如: 192.168.31.46:6379"
    )
    private List<String> hosts;

    @Parameter(
            label = "redis的master名称",
            description = "当为sentinel模式时，需要提供redis的master名称",
            optional = true,
            availableCondition = "redisMode=='sentinel'",
            requireCondition = "redisMode=='sentinel'"
    )
    private String master;

    @Parameter(
            label = "redis的连接密码",
            description = "redis的连接密码",
            optional = true,
            inputType = InputType.PASSWORD
    )
    private String password;

    @Parameter(
            label = "redis存储是否压缩",
            description = "redis存储是否压缩",
            optional = true,
            defaultValue = "false"
    )
    private boolean enableCompress;

    @Parameter(
            label = "redis存储是否用json格式",
            description = "redis存储是否用json格式",
            optional = true,
            defaultValue = "false"
    )
    private boolean saveAsJson;

    public boolean isSaveAsJson() {
        return saveAsJson;
    }

    public void setSaveAsJson(boolean saveAsJson) {
        this.saveAsJson = saveAsJson;
    }

    public boolean isEnableCompress() {
        return enableCompress;
    }

    public void setEnableCompress(boolean enableCompress) {
        this.enableCompress = enableCompress;
    }

    public String getAdditionKey() {
        return additionKey;
    }

    public String getRedisCommand() {
        return redisCommand;
    }

    public void setRedisCommand(String redisCommand) {
        this.redisCommand = redisCommand;
    }

    public void setAdditionKey(String additionKey) {
        this.additionKey = additionKey;
    }

    public String getRedisMode() {
        return redisMode;
    }

    public void setRedisMode(String redisMode) {
        this.redisMode = redisMode;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }

    @Override
    public void validate() throws JobConfigValidationException {
        if (REDIS_MODE_SENTINEL.equals(redisMode)) {
            if (StrUtil.isEmpty(master)) {
                throw new JobConfigValidationException("missing master in SENTINEL mode");
            }
        }

        if (REDIS_CMD_ZADD.equals(redisCommand) || REDIS_CMD_HSET.equals(redisCommand)) {
            if (StrUtil.isEmpty(additionKey)) {
                throw new JobConfigValidationException("missing additionKey");
            }
        }

        try {
            RedisSinkJob.parseHosts(hosts);
        } catch (Exception ex) {
            throw new JobConfigValidationException("hosts invalid", ex);
        }
    }
}
