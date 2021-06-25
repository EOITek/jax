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

import com.eoi.jax.manager.util.StreamLineReader;

public class ProcessOutput {
    private int code;
    private String cli;
    private boolean timeout;
    private StreamLineReader reader;

    /**
     * 子进程返回code
     *
     * @return 子进程返回code
     */
    public int getCode() {
        return code;
    }

    public ProcessOutput setCode(int code) {
        this.code = code;
        return this;
    }

    /**
     * 子进程的启动命令
     * @return 子进程的启动命令
     */
    public String getCli() {
        return cli;
    }

    public ProcessOutput setCli(String cli) {
        this.cli = cli;
        return this;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public ProcessOutput setTimeout(boolean timeout) {
        this.timeout = timeout;
        return this;
    }

    public StreamLineReader getReader() {
        return reader;
    }

    public ProcessOutput setReader(StreamLineReader reader) {
        this.reader = reader;
        return this;
    }
}
