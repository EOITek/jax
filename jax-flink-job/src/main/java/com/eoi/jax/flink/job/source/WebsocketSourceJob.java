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

package com.eoi.jax.flink.job.source;

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.api.FlinkSourceJobBuilder;
import com.eoi.jax.api.JobMetaConfig;
import com.eoi.jax.api.annotation.Job;
import com.eoi.jax.api.reflect.ParamUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Job(
        name = "WebsocketDebugSourceJob",
        display = "Websocket消息源",
        description = "从指定的Websocket服务端处获取输入。连接到Websocket服务端后，该算子接收服务端推送过来的json字符串作为消息。"
                + "内部采用这个实现调试功能。",
        icon = "WebsocketSourceJob.svg",
        doc = "WebsocketSourceJob.md"
)
public class WebsocketSourceJob implements FlinkSourceJobBuilder<DataStream<Map<String, Object>>, WebsocketSourceJobConfig> {
    private static final Logger logger = LoggerFactory.getLogger(WebsocketSourceJob.class);

    @Override
    public DataStream<Map<String, Object>> build(FlinkEnvironment context, WebsocketSourceJobConfig config, JobMetaConfig metaConfig) throws Throwable {
        logger.info("building job {}",  metaConfig.getJobId());
        return context.streamEnv.addSource(new WebSocketMapSourceFunction(
                config.getUri()
        )).name(metaConfig.getJobEntry()).uid(metaConfig.getJobId());
    }

    @Override
    public WebsocketSourceJobConfig configure(Map<String, Object> mapConfig) throws Throwable {
        WebsocketSourceJobConfig config = new WebsocketSourceJobConfig();
        ParamUtil.configJobParams(config, mapConfig);
        return config;
    }
}
