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

import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理所有 websocket session
 *
 * 一次debug包含：一个ping session，多个pong session
 *
 * ping session 用于：
 *    1. 接收用户消息，包括：
 *      （1）接收要调试的pipeline
 *      （2）接收要发送给pipeline source job的数据
 *    2. 将消息发送给用户，包括：
 *      （1）pipeline运行日志信息
 *      （2）pipeline sinker的输出数据
 *
 * pong session 用于：
 *    1. 接收来自pipeline sinker的数据
 *    2. 发送数据给pipeline source job
 */
@Component
public class DebugSessionManager {
    private static final Logger logger = LoggerFactory.getLogger(DebugSessionManager.class);

    /**
     * key 为 uuid
     * value 为 Session
     */
    private static Map<String, DebugSession> sessions = new ConcurrentHashMap<>();

    public DebugSession open(Session ping) {
        DebugSession session = new DebugSession(ping);
        logger.info("open ping session {} - {}", session.getUuid(), ping.getId());
        sessions.put(session.getUuid(), session);
        return session;
    }

    public DebugSession close(Session ping) {
        Iterator<DebugSession> iterator = sessions.values().iterator();
        // 必须使用迭代器，dispose方法会删除集合元素
        while (iterator.hasNext()) {
            DebugSession session = iterator.next();
            if (session.getPing().equalSession(ping)) {
                // dispose方法会删除集合元素
                dispose(session.getUuid());
                return session;
            }
        }
        return null;
    }

    public DebugSession getByPing(Session ping) {
        Iterator<DebugSession> iterator = sessions.values().iterator();
        // 必须使用迭代器，防止集合元素发生变化
        while (iterator.hasNext()) {
            DebugSession session = iterator.next();
            if (session.getPing().equalSession(ping)) {
                return session;
            }
        }
        return null;
    }

    public DebugSession attach(String uuid, String jobId, String type, Session pong) {
        DebugSession session = sessions.get(uuid);
        if (session == null) {
            throw new BizException(ResponseCode.DEBUG_NOT_OPEN);
        }
        session.addPong(jobId, type, pong);
        logger.info("attach pong session {} - {} - {} - {}", uuid, pong.getId(), jobId, type);
        return session;
    }

    public void detach(Session pong) {
        Iterator<DebugSession> iterator = sessions.values().iterator();
        // 必须使用迭代器，防止集合元素发生变化
        while (iterator.hasNext()) {
            DebugSession session = iterator.next();
            session.getPongList().removeIf(item -> item.equalSession(pong));
            logger.info("detach pong session {} - {}", session.getUuid(), pong.getId());
        }
    }

    public DebugSession getByPong(Session pong) {
        Iterator<DebugSession> iterator = sessions.values().iterator();
        // 必须使用迭代器，防止集合元素发生变化
        while (iterator.hasNext()) {
            DebugSession session = iterator.next();
            Iterator<DebugSession.Pong> list = session.getPongList().iterator();
            while (list.hasNext()) {
                DebugSession.Pong item = list.next();
                if (item.equalSession(pong)) {
                    return session;
                }
            }
        }
        return null;
    }

    public void dispose(String uuid) {
        DebugSession session = sessions.get(uuid);
        if (session == null) {
            return;
        }
        DebugSession.Ping ping = session.getPing();
        ping.close();
        Iterator<DebugSession.Pong> list = session.getPongList().iterator();
        while (list.hasNext()) {
            DebugSession.Pong item = list.next();
            item.close();
        }
        sessions.remove(session.getUuid());
    }

    public void sendToPing(String uuid, String msg) {
        DebugSession session = sessions.get(uuid);
        if (session == null) {
            return;
        }
        session.getPing().send(msg);
    }

    public void sendToPong(String uuid, String jobId, String msg) {
        DebugSession session = sessions.get(uuid);
        if (session == null) {
            return;
        }
        DebugSession.Pong pong = session.getPongList().stream()
                .filter(i -> i.getJobId().equals(jobId))
                .max(Comparator.comparing(DebugSession.Pong::getConnectTime))
                .orElse(null);
        if (pong == null) {
            return;
        }
        pong.send(msg);
    }
}
