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

package com.eoi.jax.web.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jax.debug")
public class DebugConfig implements ConfigToMap {
    private String bin;
    private String entry = "com.eoi.jax.flink_entry.FlinkLocalMiniCluster";
    private String debugSinker = "com.eoi.jax.core.flink.debug.WebsocketDebugSinker";
    private String debugSource = "com.eoi.jax.flink.job.source.WebsocketSourceJob";
    private String pythonEnv = "/home/python_venv";
    private String sparkBin;
    private String sparkEntry = "com.eoi.jax.spark_entry.SparkLocalMiniCluster";
    private String sparkDebugSinker = "com.eoi.jax.core.spark.debug.WebsocketDebugSinker";
    private Integer messageRateLimitTps = 1000;

    public String getBin() {
        return bin;
    }

    public DebugConfig setBin(String bin) {
        this.bin = bin;
        return this;
    }

    public String getEntry() {
        return entry;
    }

    public DebugConfig setEntry(String entry) {
        this.entry = entry;
        return this;
    }

    public String getDebugSinker() {
        return debugSinker;
    }

    public DebugConfig setDebugSinker(String debugSinker) {
        this.debugSinker = debugSinker;
        return this;
    }

    public String getDebugSource() {
        return debugSource;
    }

    public DebugConfig setDebugSource(String debugSource) {
        this.debugSource = debugSource;
        return this;
    }

    public String getPythonEnv() {
        return pythonEnv;
    }

    public DebugConfig setPythonEnv(String pythonEnv) {
        this.pythonEnv = pythonEnv;
        return this;
    }

    public String getSparkBin() {
        return sparkBin;
    }

    public DebugConfig setSparkBin(String sparkBin) {
        this.sparkBin = sparkBin;
        return this;
    }

    public String getSparkEntry() {
        return sparkEntry;
    }

    public DebugConfig setSparkEntry(String sparkEntry) {
        this.sparkEntry = sparkEntry;
        return this;
    }

    public String getSparkDebugSinker() {
        return sparkDebugSinker;
    }

    public DebugConfig setSparkDebugSinker(String sparkDebugSinker) {
        this.sparkDebugSinker = sparkDebugSinker;
        return this;
    }

    public Integer getMessageRateLimitTps() {
        return messageRateLimitTps;
    }

    public DebugConfig setMessageRateLimitTps(Integer messageRateLimitTps) {
        this.messageRateLimitTps = messageRateLimitTps;
        return this;
    }
}
