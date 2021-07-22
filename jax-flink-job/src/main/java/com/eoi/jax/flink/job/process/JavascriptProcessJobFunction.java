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

package com.eoi.jax.flink.job.process;

import com.eoi.jax.common.NashornUtil;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import java.util.Map;

public class JavascriptProcessJobFunction extends
        RichFlatMapFunction<Map<String, Object>, Map<String, Object>> {

    private transient ScriptEngine scriptEngine;
    private transient CompiledScript compiled;
    private String script;
    private boolean ignoreException;

    public JavascriptProcessJobFunction(String script, boolean ignoreException) {
        this.script = script;
        this.ignoreException = ignoreException;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        compiled = ((Compilable)scriptEngine).compile(this.script);
    }

    @Override
    public void flatMap(Map<String, Object> value, Collector<Map<String, Object>> out) throws Exception {
        try {
            Bindings bindings = scriptEngine.createBindings();
            bindings.put("doc", value);
            Object evalResult = compiled.eval(bindings);
            NashornUtil.accept(evalResult, out::collect);
        } catch (Exception ex) {
            if (!ignoreException) {
                throw ex;
            }
        }
    }
}
