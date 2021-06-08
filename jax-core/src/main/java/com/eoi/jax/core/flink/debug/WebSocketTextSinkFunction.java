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

package com.eoi.jax.core.flink.debug;

import com.eoi.jax.core.WebSocketClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class WebSocketTextSinkFunction extends RichSinkFunction<String> {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketTextSinkFunction.class);

    private final String uri;
    private transient WebSocketClient client;

    public WebSocketTextSinkFunction(String uri) {
        this.uri = uri;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        logger.info("opening with {}", uri);
        this.client = new WebSocketClient(new URI(uri));
        this.client.connectBlocking(10, TimeUnit.SECONDS);
        logger.info("opened with {}", uri);
    }

    @Override
    public void invoke(String value, Context context) throws Exception {
        client.send(value);
    }

    @Override
    public void close() throws Exception {
        super.close();
        logger.info("closing with {}", uri);
        client.close();
        logger.info("closed with {}", uri);
    }
}
