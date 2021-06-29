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

package com.eoi.jax.flink.job.source;

import com.eoi.jax.common.NashornUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavascriptSimulateMapSourceFunction extends RichSourceFunction<Map<String, Object>> {

    private transient ScriptEngine scriptEngine;
    private transient CompiledScript compiled;
    private transient ExecutorService executor;
    private String script;
    private boolean ignoreException;
    private int intervalMs;
    private volatile boolean isCancel;

    public JavascriptSimulateMapSourceFunction(String script, boolean ignoreException, int intervalMs) {
        this.script = script;
        this.ignoreException = ignoreException;
        this.intervalMs = intervalMs;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        compiled = ((Compilable)scriptEngine).compile(this.script);
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run(SourceContext<Map<String, Object>> ctx) throws Exception {
        while (!isCancel) {
            if (isCancel) {
                return;
            }
            try {
                Object evalResult = compiled.eval();
                NashornUtil.accept(evalResult, ctx::collect);
            } catch (Exception ex) {
                if (!this.ignoreException) {
                    throw ex;
                }
            }
            executor.submit(() -> {
                try {
                    Thread.sleep(this.intervalMs);
                } catch (Exception ignore) {
                }
            }).get();
        }
    }

    @Override
    public void cancel() {
        isCancel = true;
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Override
    public void close() throws Exception {
        this.cancel();
    }
}
