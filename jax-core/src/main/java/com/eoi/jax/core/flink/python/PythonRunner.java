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

package com.eoi.jax.core.flink.python;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.eoi.jax.common.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.flink.api.common.state.ValueState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import py4j.GatewayServer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// TODO:
// 协议出现异常

/**
 * 负责启动Python worker进程，维持rpc和py4j的链路，将message发送给worker，worker处理完成后返回
 *
 * pythonScriptPath: python worker脚本的路径
 * pythonIntepreterPath: python解释器路径，默认是python
 * arguments: 启动的额外参数
 * environments: 启动的额外环境变量
 * entryPoint: py4j传递给python侧的环境变量
 */
public class PythonRunner {

    private static Logger logger = LoggerFactory.getLogger(PythonRunner.class);
    private static final int HANDSHAKE_TIMEOUT = 10000;
    private static final int MAX_RETRY = 10;

    // python intepreter path, default is python
    private String pythonIntepreterPath;
    // arguments when start python process
    private String[] arguments;
    // environments for python process
    private Map<String, String> environments;
    // entry point pass to python via py4j
    private Object entryPoint;
    private long stateSnapshotInterval;

    public String getPythonIntepreterPath() {
        return pythonIntepreterPath;
    }

    public PythonRunner setPythonIntepreterPath(String pythonIntepreterPath) {
        this.pythonIntepreterPath = pythonIntepreterPath;
        return this;
    }

    public String[] getArguments() {
        return arguments;
    }

    public PythonRunner setArguments(String[] arguments) {
        this.arguments = arguments;
        return this;
    }

    public Map<String, String> getEnvironments() {
        return environments;
    }

    public PythonRunner setEnvironments(Map<String, String> environments) {
        this.environments = environments;
        return this;
    }

    public PythonRunner putEnvironment(String key, String value) {
        if (this.environments == null) {
            this.environments = new HashMap<>();
        }
        this.environments.put(key, value);
        return this;
    }

    public Object getEntryPoint() {
        return entryPoint;
    }

    public PythonRunner setEntryPoint(Object entryPoint) {
        this.entryPoint = entryPoint;
        return this;
    }

    public long getStateSnapshotInterval() {
        return stateSnapshotInterval;
    }

    public PythonRunner setStateSnapshotInterval(long stateSnapshotInterval) {
        this.stateSnapshotInterval = stateSnapshotInterval;
        return this;
    }

    // real private
    // py4j gateway server
    private GatewayServer py4jServer;
    // rpc server socket
    private ServerSocket rpcServer;
    // rpc peer client, read or write through
    private Socket rpcClient;
    private DataOutputStream clientOut;
    private DataInputStream clientIn;
    // thread for pipe(stdout) read
    private Thread pipeReadThread;
    private volatile boolean closed;
    private int retry;
    private Map<String, Long> lastSnapshotTimeByKey;

    private void checkRequired() throws Exception {

    }

    public void open() throws Exception {
        checkRequired();
        launch();
    }

    public void close() throws Exception {
        closed = true;
        dispose();
    }

    private void launch() throws Exception {
        try {
            lastSnapshotTimeByKey = new HashMap<>();
            String secret = UUID.randomUUID().toString();
            GatewayServer.GatewayServerBuilder py4jServerBuilder = new GatewayServer.GatewayServerBuilder();
            py4jServerBuilder = py4jServerBuilder.authToken(secret).javaPort(0);
            if (this.entryPoint != null) {
                py4jServerBuilder = py4jServerBuilder.entryPoint(this.entryPoint);
            }
            py4jServer = py4jServerBuilder.build();
            py4jServer.start();

            // start rpc server, and wait for client
            rpcServer = new ServerSocket(0, 1, InetAddress.getByName("localhost"));
            rpcServer.setSoTimeout(0);
            int port = rpcServer.getLocalPort();
            logger.info("starting rpc server :{}", port);
            ExecutorService executor = Executors.newFixedThreadPool(1);
            Future<Boolean> acceptFuture = executor.submit(() -> {
                rpcClient = rpcServer.accept();
                rpcClient.setSoTimeout(0);
                rpcClient.setSendBufferSize(64 * 1024);
                rpcClient.setTcpNoDelay(true);
                clientOut = new DataOutputStream(rpcClient.getOutputStream());
                clientIn = new DataInputStream(rpcClient.getInputStream());
                handshake(clientIn, clientOut, secret);
                return true;
            });
            // start python process and launch pipeReadThread
            List<String> command = new ArrayList<>();
            if (!StrUtil.isEmpty(this.pythonIntepreterPath)) {
                command.add(this.pythonIntepreterPath);
            } else {
                command.add("python");
            }
            if (this.arguments != null && this.arguments.length > 0) {
                command.addAll(Arrays.stream(this.arguments).collect(Collectors.toList()));
            }
            ProcessBuilder pb = new ProcessBuilder(command);
            Map<String, String> env = pb.environment();
            env.put("PY4J_SECRET", secret);
            env.put("PY4J_PORT", String.format("%d", py4jServer.getListeningPort()));
            env.put("PYTHON_WORKER_FACTORY_PORT", String.format("%d", port));
            env.put("PYTHONUNBUFFERED", "YES");
            if (this.environments != null && this.environments.size() > 0) {
                env.putAll(this.environments);
            }
            pb.redirectErrorStream(true);
            logger.info("starting python process: {}", ArrayUtil.join(command.toArray(), " "));
            Process subProcess = pb.start();
            pipeReadThread = new Thread(() -> {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(subProcess.getInputStream()));
                String line;
                try {
                    line = bufferedReader.readLine();
                    while (line != null) {
                        logger.info("[flink_python] {}", line);
                        line = bufferedReader.readLine();
                    }
                } catch (Exception ex) {
                    logger.warn("got exception when read python stdout", ex);
                } finally {
                    try {
                        acceptFuture.cancel(true);
                        subProcess.waitFor(5, TimeUnit.SECONDS);
                        if (subProcess.isAlive()) {
                            subProcess.destroy();
                        }
                    } catch (Exception ignore) {
                    }
                }
            });
            pipeReadThread.start();
            logger.info("waiting for connection finish from python process");
            acceptFuture.get(HANDSHAKE_TIMEOUT, TimeUnit.MILLISECONDS);
            executor.shutdown();
        } catch (Exception ex) {
            throw new HandshakeException(ex);
        }
    }

    private void dispose() throws Exception {
        if (py4jServer != null) {
            py4jServer.shutdown();
        }
        if (pipeReadThread != null) {
            pipeReadThread.interrupt();
        }
        if (rpcServer != null) {
            rpcServer.close();
        }
        if (rpcClient != null) {
            rpcClient.close();
        }
    }

    public List<Map<String, Object>> process(Map<String, Object> eventIn, String key, ValueState<Byte[]> valueState)
            throws Exception {
        if (eventIn == null) {
            return Collections.emptyList();
        }
        // 计算当前key的，距离上次快照时间的偏差，如果大于间隔时间，需要发送DATA_FEED_SNAPSHOT
        long pastTimeAfterLastSnapshot = 0;
        if (!lastSnapshotTimeByKey.containsKey(key)) {
            lastSnapshotTimeByKey.put(key, System.currentTimeMillis());
        } else {
            pastTimeAfterLastSnapshot = System.currentTimeMillis() - lastSnapshotTimeByKey.get(key);
        }
        int transitionState = Constants.TRANSITION_STATE_START;
        try {
            while (transitionState != Constants.TRANSITION_STATE_DATA_RET) {
                if (transitionState == Constants.TRANSITION_STATE_START) {
                    if (stateSnapshotInterval > 0 && pastTimeAfterLastSnapshot > stateSnapshotInterval) {
                        // send DATA_FEED_SNAPSHOT instead of DATA_FEED if we need trigger state snapshot
                        clientOut.writeInt(Constants.DATA_FEED_SNAPSHOT);
                        // set currentTimeMillis back to map
                        lastSnapshotTimeByKey.put(key, System.currentTimeMillis());
                    } else {
                        clientOut.writeInt(Constants.DATA_FEED);
                    }
                    sendEvent(clientOut, eventIn, key);
                    transitionState = Constants.TRANSITION_STATE_WAIT;
                } else if (transitionState == Constants.TRANSITION_STATE_WAIT) {
                    int flag = clientIn.readInt();
                    if (flag == Constants.STATE_REQ) {
                        transitionState = Constants.TRANSITION_STATE_STATE_FEED;
                    } else if (flag == Constants.STATE_UPDATE) {
                        transitionState = Constants.TRANSITION_STATE_STATE_UPDATE;
                    } else if (flag == Constants.DATA_RET) {
                        transitionState = Constants.TRANSITION_STATE_DATA_RET;
                    }
                } else if (transitionState == Constants.TRANSITION_STATE_STATE_FEED) {
                    clientIn.readInt(); // 0 length for STATE_REQ
                    // client need get state
                    logger.debug("=============== feed state to client");
                    clientOut.writeInt(Constants.STATE_FEED);
                    if (valueState == null || valueState.value() == null) {
                        clientOut.writeInt(0);
                    } else {
                        Byte[] state = valueState.value();
                        clientOut.writeInt(state.length);
                        clientOut.write(toPrimitives(state));
                    }
                    transitionState = Constants.TRANSITION_STATE_WAIT;
                } else {
                    // transitionState == Constants.TRANSITION_STATE_STATE_UPDATE
                    int stateLen = clientIn.readInt();
                    byte[] rawState = new byte[stateLen];
                    clientIn.readFully(rawState);
                    if (valueState != null) {
                        logger.debug("=============== update state {}", new String(rawState, StandardCharsets.UTF_8));
                        valueState.update(toObjects(rawState));
                    }
                    transitionState = Constants.TRANSITION_STATE_WAIT;
                }
            }
            return readEvents(clientIn);
        } catch (EOFException | SocketException ex) {
            if (closed) {
                return Collections.emptyList();
            }
            // if closed is not true, and eof catched
            logger.warn("got EOFException, probably means python process terminated. try to restart", ex);
            retry++;
            if (retry > MAX_RETRY) {
                logger.warn("break retry, since retry count exceed MAX_RETRY");
                throw ex;
            }
            dispose();
            launch();
            return process(eventIn, key, valueState);
        }
    }

    private void handshake(DataInputStream inputStream, DataOutputStream outputStream, String token) throws Exception {
        int rl = inputStream.readInt();
        byte[] requestToken = new byte[rl];
        inputStream.readFully(requestToken);
        if (!token.equals(new String(requestToken, StandardCharsets.UTF_8))) {
            throw new Exception("token not match");
        }
        outputStream.writeInt(rl);
        outputStream.write(requestToken);
    }

    private void sendEvent(DataOutputStream outputStream, Map<String, Object> event, String key)
            throws Exception {
        assert outputStream != null;
        assert key != null;
        assert event != null;
        String jsonEvent = JsonUtil.encode(event);
        byte[] jsonBody = jsonEvent.getBytes(StandardCharsets.UTF_8);
        byte[] keyBody = key.getBytes(StandardCharsets.UTF_8);
        // first 4 bytes means json body length
        outputStream.writeInt(jsonBody.length);
        outputStream.write(jsonBody);
        // second 4 bytes means key body length
        outputStream.writeInt(keyBody.length);
        outputStream.write(keyBody);
        outputStream.flush();
    }

    private List<Map<String, Object>> readEvents(DataInputStream inputStream) throws Exception {
        // the first 4 bytes means returned event count
        int count = inputStream.readInt();
        List<Map<String, Object>> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int eachLen = inputStream.readInt();
            byte[] each = new byte[eachLen];
            inputStream.readFully(each);
            try {
                Map<String, Object> eachDecoded = JsonUtil.decode(each, new TypeReference<Map<String, Object>>() { });
                result.add(eachDecoded);
            } catch (Exception ignore) {
            }
        }
        return result;
    }

    Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];

        int i = 0;
        for (byte b : bytesPrim) {
            bytes[i++] = b; // Autoboxing
        }

        return bytes;
    }

    byte[] toPrimitives(Byte[] outBytes) {
        byte[] bytes = new byte[outBytes.length];
        for (int i = 0; i < outBytes.length; i++) {
            bytes[i] = outBytes[i];
        }
        return bytes;
    }

    public static class HandshakeException extends Exception {
        public HandshakeException(String message, Throwable ex) {
            super(message, ex);
        }

        public HandshakeException(String message) {
            super(message);
        }

        public HandshakeException(Throwable ex) {
            super(ex);
        }
    }
}
