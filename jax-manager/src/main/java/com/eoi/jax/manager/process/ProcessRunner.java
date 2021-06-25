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

package com.eoi.jax.manager.process;

import com.eoi.jax.manager.exception.InvalidParamException;
import com.eoi.jax.manager.exception.ProcessErrorException;
import com.eoi.jax.manager.util.LineOutputStream;
import com.eoi.jax.manager.util.StreamLineReader;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
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

/**
 * 子进程运行器
 * <p>
 * 负责运行子进程，并收集子进程的管道输出（stderr，stdout）
 * 可以指定环境变量，工作目录，参数等。
 */
public class ProcessRunner implements IProcessRunner {
    private static final Logger logger = LoggerFactory.getLogger(ProcessRunner.class);
    private static final ShutdownHookProcessDestroyer shutdown = new ShutdownHookProcessDestroyer();

    private Map<String, String> environment;
    private String directory;
    private String program;
    private int timeoutMillis;

    public ProcessRunner(String directory, String program, int timeoutMillis) {
        this.directory = directory;
        this.program = program;
        if (timeoutMillis <= 0) {
            throw new InvalidParamException("invalid timeoutMillis " + timeoutMillis);
        }
        this.timeoutMillis = timeoutMillis;
    }

    /**
     * 子进程的环境变量
     * <p>
     * 对子进程自定义的环境变量，继承的环境变量并不会返回。
     *
     * @return 子进程的环境变量
     */
    public Map<String, String> getEnvironment() {
        return environment;
    }

    /**
     * 设置子进程的环境变量
     * <p>
     * 对子进程自定义的环境变量，可以覆盖继承的环境变量。
     *
     * @param environment 子进程的环境变量
     * @return this
     */
    public ProcessRunner setEnvironment(Map<String, String> environment) {
        this.environment = environment;
        return this;
    }

    /**
     * 子进程的工作目录
     * <p>
     * 为空时，表示使用当前进程的工作目录
     * 参照 {@link ProcessBuilder#directory()}
     *
     * @return 子进程的工作目录
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * 设置子进程的工作目录
     * <p>
     * 为空时，表示使用当前进程的工作目录
     * 参照 {@link ProcessBuilder#directory(File file)}
     *
     * @param directory 子进程的工作目录
     * @return this
     */
    public ProcessRunner setDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    /**
     * 可执行文件或命令
     * <p>
     * 该参数和 arguments 组成 {@link ProcessBuilder#command()} ()}
     *
     * @return 可执行文件或命令
     */
    public String getProgram() {
        return program;
    }

    /**
     * 设置可执行文件或命令
     * <p>
     * 该参数和 arguments 组成 {@link ProcessBuilder#command()} ()}
     *
     * @param program 可执行文件或命令
     * @return this
     */
    public ProcessRunner setProgram(String program) {
        this.program = program;
        return this;
    }

    /**
     * 为子进程设置环境变量
     *
     * @param key   环境变量名
     * @param value 环境变量值
     * @return ProcessRunner
     */
    public ProcessRunner putEnvironment(String key, String value) {
        return putEnvironment(key, value, true);
    }

    public ProcessRunner putEnvironment(String key, String value, boolean condition) {
        if (this.environment == null) {
            this.environment = new HashMap<>();
        }
        if (condition) {
            this.environment.put(key, value);
        }
        return this;
    }

    /**
     * 调用子进程
     *
     * @return 子进程返回值
     */
    @Override
    public ProcessOutput exec(List<String> arguments, StreamLineReader reader) {
        ProcessOutput output = new ProcessOutput();
        if (program == null || program.length() == 0) {
            throw new InvalidParamException("missing program");
        }
        DefaultExecutor executor = new DefaultExecutor();
        LineOutputStream outputStream = new LineOutputStream(reader);
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        streamHandler.setStopTimeout(5000);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(timeoutMillis);
        try {
            executor.setProcessDestroyer(shutdown);
            executor.setStreamHandler(streamHandler);
            executor.setWorkingDirectory(new File(directory));
            executor.setWatchdog(watchdog);
            executor.setExitValues(null);
            Map<String, String> env = EnvironmentUtils.getProcEnvironment();
            if (environment != null && environment.size() > 0) {
                env.putAll(environment);
            }
            CommandLine command = new CommandLine(program);
            String cmd = program;
            if (arguments != null && !arguments.isEmpty()) {
                for (String argument : arguments) {
                    command.addArgument(argument, false);
                }
                cmd = cmd + " " + String.join(" ", arguments);
            }
            int exitValue = executor.execute(command, env);
            if (watchdog.killedProcess()) {
                logger.warn("process is timeout: {}", cmd);
            }
            output.setCli(cmd);
            output.setCode(exitValue);
            output.setTimeout(watchdog.killedProcess());
            output.setReader(reader);
        } catch (Exception e) {
            throw new ProcessErrorException(e);
        } finally {
            try {
                watchdog.stop();
            } catch (Exception e) {
                logger.warn("close watchdog", e);
            }
            try {
                streamHandler.stop();
            } catch (Exception e) {
                logger.warn("close std i/o", e);
            }
        }

        return output;
    }
}
