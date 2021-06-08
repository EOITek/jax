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

package com.eoi.jax.core;

import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.URI;

public class WebSocketClient extends org.java_websocket.client.WebSocketClient implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

    private MessageReceiver receiver;

    public WebSocketClient(URI serverUri) {
        super(serverUri);
        this.setConnectionLostTimeout(0);
    }

    public WebSocketClient registerReceiver(MessageReceiver receiver) {
        this.receiver = receiver;
        return this;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("opened {} {} {}",
                getURI(),
                serverHandshake.getHttpStatus(),
                serverHandshake.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String s) {
        if (receiver != null) {
            try {
                receiver.receive(s);
            } catch (Exception e) {
                logger.error("message from " + getURI(), e);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("closed {} with code {} and reason {}", getURI(), code, reason);
    }

    @Override
    public void onError(Exception e) {
        logger.error("error " + getURI(), e);
    }

    @Override
    public void close() {
        try {
            this.close(1000, "Normal Closure");
            this.closeConnection(1000, "Normal Closure");
        } catch (Exception e) {
            logger.error("close " + getURI(), e);
        }
    }

    public interface MessageReceiver {

        void receive(String s);
    }
}
