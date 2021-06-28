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

import cn.hutool.core.util.IdUtil;
import com.eoi.jax.web.common.config.ConfigLoader;
import com.google.common.util.concurrent.RateLimiter;

import javax.websocket.Session;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * websocket session
 *
 * uuid：全局唯一id
 * ping：浏览器创建的session
 * pongList：pipeline进程创建的session
 *
 * 一个 ping session 对应多个 pong session：
 * 1.一个pipeline中有多个job，
 * 2.一个job有多个并发，
 * 3.有的job既有source有需要sink
 */
public class DebugSession {
    // 浏览器创建的session
    private final String uuid;
    private final Ping ping;
    private final List<Pong> pongList;

    public DebugSession(Session ping) {
        this(new Ping(ping));
    }

    public DebugSession(Ping ping) {
        this.uuid = IdUtil.simpleUUID();
        this.ping = ping;
        this.pongList = new CopyOnWriteArrayList<>();
    }

    public String getUuid() {
        return uuid;
    }

    public Ping getPing() {
        return ping;
    }

    public List<Pong> getPongList() {
        return pongList;
    }

    public void addPong(String jobId, String type, Session pong) {
        this.pongList.add(new Pong(jobId, type, pong));
    }

    static class Ping {
        private final RateLimiter limiter;
        private final Session session;
        private final SessionExtend sessionExtend;
        private final long connectTime;

        public Ping(Session session) {
            this.session = session;
            this.connectTime = System.currentTimeMillis();
            this.sessionExtend = new SessionExtend(session);
            this.limiter = RateLimiter.create(ConfigLoader.load().jax.getDebug().getMessageRateLimitTps());
        }

        public Session getSession() {
            return session;
        }

        public String getSessionId() {
            return session.getId();
        }

        public long getConnectTime() {
            return connectTime;
        }

        public void close() {
            sessionExtend.close();
        }

        public void send(String msg) {
            limiter.acquire();
            sessionExtend.send(msg);
        }

        public boolean equalSession(Session session) {
            return this.session.equals(session);
        }
    }

    static class Pong {
        private final RateLimiter limiter;
        private final String jobId;
        private final String type;
        private final Session session;
        private final long connectTime;
        private final SessionExtend sessionExtend;

        public Pong(String jobId, String type, Session session) {
            this.jobId = jobId;
            this.type = type;
            this.session = session;
            this.connectTime = System.currentTimeMillis();
            this.sessionExtend = new SessionExtend(session);
            this.limiter = RateLimiter.create(ConfigLoader.load().jax.getDebug().getMessageRateLimitTps());
        }

        public String getJobId() {
            return jobId;
        }

        public String getType() {
            return type;
        }

        public Session getSession() {
            return session;
        }

        public String getSessionId() {
            return session.getId();
        }

        public long getConnectTime() {
            return connectTime;
        }

        public void close() {
            sessionExtend.close();
        }

        public void send(String msg) {
            limiter.acquire();
            sessionExtend.send(msg);
        }

        public boolean equalSession(Session session) {
            return this.session.equals(session);
        }
    }
}
