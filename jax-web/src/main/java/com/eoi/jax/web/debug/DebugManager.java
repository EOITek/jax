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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.util.JsonUtil;
import com.eoi.jax.web.model.pipeline.PipelineReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 调度websocket消息：
 *
 *   启动pipeline进程
 *   将ping转发给pong
 *   将pong转发给ping
 */
@Component
public class DebugManager {
    private static final Logger logger = LoggerFactory.getLogger(DebugManager.class);
    private static final String flinkWebUrlKeyWord = "Web frontend listening at ";
    private static final Pattern flinkWebUrlPattern = Pattern.compile(flinkWebUrlKeyWord + "(?<URL>http://.*:\\d+)");
    private static final String sparkWebUrlKeyWord = "SparkUI: Bound SparkUI to ";
    private static final Pattern sparkWebUrlPattern = Pattern.compile(" and started at (?<URL>http://.*:\\d+)");


    @Autowired
    private DebugSessionManager debugSessionManager;

    @Autowired
    private PipelineProcessManager pipelineProcessManager;

    public void start(Session ping) {
        debugSessionManager.open(ping);
    }

    public void stop(Session ping) {
        DebugSession session = debugSessionManager.close(ping);
        if (session != null) {
            pipelineProcessManager.stopPipeline(session.getUuid());
        }
    }

    public void attach(Session pong, String uuid, String jobId, String type) {
        debugSessionManager.attach(uuid, jobId, type, pong);
    }

    public void detach(Session pong) {
        debugSessionManager.detach(pong);
    }

    public void ping(Session ping, String msg) {
        DebugSession session = debugSessionManager.getByPing(ping);
        if (session == null) {
            new SessionExtend(ping).send(error(ResponseCode.DEBUG_FAILED.message));
            return;
        }
        DebugMessage debugMessage;
        try {
            debugMessage = JsonUtil.decode(msg, DebugMessage.class);
        } catch (Exception e) {
            logger.error("json", e);
            debugSessionManager.sendToPing(session.getUuid(), error(ResponseCode.DEBUG_INVALID_MSG.message));
            // 非法消息，中断调试！
            debugSessionManager.dispose(session.getUuid());
            return;
        }
        if (MsgCode.SOURCE_DATA.isEqual(debugMessage.getCode())) {
            try {
                debugSessionManager.sendToPong(session.getUuid(), debugMessage.getJobId(), debugMessage.getMessage());
            } catch (Exception e) {
                logger.error("send " + session.getUuid(), e);
                debugSessionManager.sendToPing(session.getUuid(), error(ResponseCode.DEBUG_FAILED.message));
            }
        }
        if (MsgCode.START_DEBUG.isEqual(debugMessage.getCode())) {
            try {
                PipelineReq pipelineReq = JsonUtil.decode(debugMessage.getMessage(), PipelineReq.class);
                pipelineProcessManager.startPipeline(session.getUuid(), pipelineReq, new PipelineProcessManager.Lifecycle() {
                    @Override
                    public void onFinish(String uuid) {
                        debugSessionManager.dispose(uuid);
                    }
                }, this::console);
            } catch (Exception e) {
                logger.error("debug " + session.getUuid(), e);
                debugSessionManager.sendToPing(session.getUuid(), error(ResponseCode.DEBUG_FAILED.message));
                // 调试进程失败，中断调试！
                debugSessionManager.dispose(session.getUuid());
            }
        }
    }

    public void pong(Session pong, String msg) {
        DebugSession session = debugSessionManager.getByPong(pong);
        if (session == null) {
            return;
        }
        try {
            DebugMessage pongMsg = JsonUtil.decode(msg, DebugMessage.class);
            pongMsg.setCode(MsgCode.SINK_DATA.code);
            debugSessionManager.sendToPing(session.getUuid(), JsonUtil.encode(pongMsg));
        } catch (Exception e) {
            logger.warn("debug " + session.getUuid(), e);
        }
    }

    private void console(String uuid, String msg) {
        DebugMessage debugMessage = new DebugMessage();
        debugMessage.setCode(MsgCode.DEBUG_LOG.code);
        debugMessage.setMessage(msg);
        debugSessionManager.sendToPing(uuid, JsonUtil.encode(debugMessage));

        // 通过关键字判断是否包含 flink web url 信息
        String webUrl = findWebUrl(msg, flinkWebUrlKeyWord, flinkWebUrlPattern);
        if (webUrl == null) {
            webUrl = findWebUrl(msg, sparkWebUrlKeyWord, sparkWebUrlPattern);
        }
        if (StrUtil.isNotEmpty(webUrl)) {
            DebugMessage urlMessage = new DebugMessage();
            urlMessage.setCode(MsgCode.WEB_URL.code);
            urlMessage.setMessage(webUrl);
            debugSessionManager.sendToPing(uuid, JsonUtil.encode(urlMessage));
        }
    }

    private String findWebUrl(String msg, String keyword, Pattern pattern) {
        if (msg.contains(keyword)) {
            // 从日志中解析除 flink web url
            // 示例：2020-12-16 16:18:44.409 [main] INFO org.apache.flink.runtime.dispatcher.DispatcherRestEndpoint - Web frontend listening at http://172.16.128.38:55908.
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {
                return matcher.group("URL");
            }
        }
        return null;
    }

    private String error(String msg) {
        DebugMessage debugMessage = new DebugMessage();
        debugMessage.setCode(MsgCode.ERROR.code);
        debugMessage.setMessage(msg);
        return JsonUtil.encode(msg);
    }
}
