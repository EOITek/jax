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
import com.eoi.jax.manager.util.LineOutputStream;
import com.eoi.jax.manager.util.StreamLineReader;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineProcess {
    private static final Logger logger = LoggerFactory.getLogger(PipelineProcess.class);
    private static final ShutdownHookProcessDestroyer shutdown = new ShutdownHookProcessDestroyer();

    private final String uuid;
    private final Map<String, String> environment;
    private final String directory;
    private final String program;
    private final int timeoutMillis;
    private final StreamLineReader reader;
    private final ExecuteResultHandler handler;

    private PumpStreamHandler streamHandler;
    private ExecuteWatchdog watchdog;
    private boolean destroyed;

    public PipelineProcess(String uuid,
                           String directory,
                           String program,
                           StreamLineReader reader,
                           ExecuteResultHandler handler) {
        this(uuid, new HashMap<>(), directory, program, 6 * 3600 * 1000, reader, handler);
    }

    public PipelineProcess(String uuid,
                           Map<String, String> environment,
                           String directory,
                           String program,
                           int timeoutMillis,
                           StreamLineReader reader,
                           ExecuteResultHandler handler) {
        this.uuid = uuid;
        this.environment = environment;
        this.directory = directory;
        this.program = program;
        this.timeoutMillis = timeoutMillis;
        this.reader = reader;
        this.handler = handler;
        this.destroyed = false;
    }

    public void putEnv(String key, String value) {
        environment.put(key, value);
    }

    public String getUuid() {
        return uuid;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public String getDirectory() {
        return directory;
    }

    public String getProgram() {
        return program;
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    /**
     * Non blocking
     */
    public void exec(List<String> arguments) {
        String processArgs =  StrUtil.join(" ", arguments);
        logger.info("debug run process {}", processArgs);
        LineOutputStream outputStream = new LineOutputStream(reader);
        DefaultExecutor executor = new DefaultExecutor();
        streamHandler = new PumpStreamHandler(outputStream);
        streamHandler.setStopTimeout(5000);
        watchdog = new ExecuteWatchdog(timeoutMillis);
        try {
            Map<String, String> env = EnvironmentUtils.getProcEnvironment();
            if (environment != null && environment.size() > 0) {
                env.putAll(environment);
            }
            executor.setProcessDestroyer(shutdown);
            executor.setStreamHandler(streamHandler);
            executor.setWorkingDirectory(new File(directory));
            executor.setWatchdog(watchdog);
            executor.setExitValues(null);
            CommandLine command =  new CommandLine(program);
            if (arguments != null && !arguments.isEmpty()) {
                for (String argument : arguments) {
                    command.addArgument(argument, false);
                }
            }
            executor.execute(command, env, new ExecuteResultHandler() {
                @Override
                public void onProcessComplete(int exitValue) {
                    logger.info("process complete {} : {}", uuid, exitValue);
                    handler.onProcessComplete(exitValue);
                    onClose();
                }

                @Override
                public void onProcessFailed(ExecuteException e) {
                    logger.error("process failed " + uuid, e);
                    handler.onProcessFailed(e);
                    onClose();
                }

                private void onClose() {
                    stop();
                }
            });

        } catch (Exception e) {
            logger.error("process error", e);
            stop();
        }
    }

    public void stop() {
        if (destroyed) {
            return;
        }
        logger.info("process cleaning {}", uuid);
        try {
            watchdog.destroyProcess();
        } catch (Exception ex) {
            logger.warn("process close error", ex);
        }
        logger.info("process cleaned {}", uuid);
        this.destroyed = true;
    }
}
