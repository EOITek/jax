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

import com.eoi.jax.common.JsonUtil;
import com.eoi.jax.core.WebSocketClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class WebSocketMapSourceFunction extends RichSourceFunction<Map<String, Object>> {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketMapSourceFunction.class);

    private final String uri;
    private transient WebSocketClient client;
    private transient Semaphore semaphore;
    private boolean isRunning = true;

    public WebSocketMapSourceFunction(String uri) {
        this.uri = uri;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        logger.info("opening with {}", uri);
        this.client = new WebSocketClient(new URI(uri)) {
            @Override
            public void onClose(int code, String reason, boolean remote) {
                super.onClose(code, reason, remote);
                release();
            }
        };
        this.client.connectBlocking(10, TimeUnit.SECONDS);
        this.semaphore = new Semaphore(0);
        logger.info("opened with {}", uri);
    }

    @Override
    public void close() throws Exception {
        super.close();
        logger.info("closing with {}", uri);
        release();
        logger.info("closed with {}", uri);
    }

    @Override
    public void run(SourceContext<Map<String, Object>> ctx) throws Exception {
        if (isRunning) {
            client.registerReceiver(s -> {
                Map<String, Object> map = null;
                try {
                    map = JsonUtil.decode2Map(s);
                } catch (Exception e) {
                    logger.warn("json error", e);
                }
                if (map != null) {
                    ctx.collect(map);
                }
            });
            // block the current thread
            semaphore.acquire();
        }
    }

    @Override
    public void cancel() {
        logger.info("canceling with {}", uri);
        release();
        logger.info("canceled with {}", uri);
    }

    private void release() {
        semaphore.release();
        isRunning = false;
    }
}
