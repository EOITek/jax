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

import com.eoi.jax.web.common.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint("/ws/pipeline/pong/{uuid}/{jobId}/{type}")
public class WebsocketPongEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(WebsocketPongEndpoint.class);

    private DebugManager debugManager() {
        return ContextHolder.getBean(DebugManager.class);
    }


    /**
     * 当客户端打开连接
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("uuid") String uuid,
                       @PathParam("jobId") String jobId,
                       @PathParam("type") String type
    ) {
        logger.info("open - {} - {} - {} - {}", session.getId(), uuid, jobId, type);
        debugManager().attach(session, uuid, jobId, type);
    }

    /**
     * 当客户端发送消息：1.获取它的用户名和消息 2.发送消息给所有人
     * <p>
     * PS: 这里约定传递的消息为JSON字符串 方便传递更多参数！
     */
    @OnMessage
    public void onMessage(Session session, String msg) {
        debugManager().pong(session, msg);
    }

    /**
     * 当连接关闭
     */
    @OnClose
    public void onClose(Session session) {
        logger.info("close - {}", session.getId());
        debugManager().detach(session);
    }

    /**
     * 当通信发生异常：打印错误日志
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.warn("error - " + session.getId(), error);
    }
}
