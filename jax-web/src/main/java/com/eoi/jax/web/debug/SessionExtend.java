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

package com.eoi.jax.web.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;

import java.io.IOException;

public class SessionExtend {
    private static final Logger logger = LoggerFactory.getLogger(SessionExtend.class);

    private final Session session;

    public SessionExtend(Session session) {
        this.session = session;
    }

    public void close() {
        try {
            this.session.close();
        } catch (IOException e) {
            logger.warn("close session {} failed", session.getId(), e);
        }
    }

    public void send(String msg) {
        if (msg == null) {
            return;
        }
        blockSend(msg);
    }

    private synchronized void blockSend(String msg) {
        try {
            if (this.session.isOpen()) {
                this.session.getBasicRemote().sendText(msg);
            }
        } catch (Exception e) {
            logger.warn("send to {} failed", session.getId(), e);
        }
    }
}
