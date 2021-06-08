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

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.common.PythonHelper;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.MeterView;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.flink.api.common.typeinfo.BasicArrayTypeInfo.BYTE_ARRAY_TYPE_INFO;

public class PythonWrapperJobFunction extends RichFlatMapFunction<Map<String, Object>, Map<String, Object>> {
    private static final Logger logger = LoggerFactory.getLogger(PythonWrapperJobFunction.class);

    private static final String PYTHON_INTERPRETER_ENV = "PYTHON_INTERPRETER";

    private transient ValueStateDescriptor<Byte[]> stateDescriptor;
    private PythonWrapperJobConfig config;
    private PythonRunner runner;
    private KeySelectorForMap keyExtractor;
    private long checkpointInterval;
    private transient Meter rate;

    /**
     * @param config PythonWrapperJobConfig
     * @param checkpointInterval checkpoint interval in ms, -1 if disabled
     */
    public PythonWrapperJobFunction(PythonWrapperJobConfig config, long checkpointInterval) {
        this.config = config;
        this.checkpointInterval = checkpointInterval;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        // init metrics
        rate = getRuntimeContext().getMetricGroup()
                .addGroup(String.format("python-runner-%s-%s", config.getPythonModule(), config.getPythonClass()))
                .meter("rate.meter", new MeterView(1));
        List<String> pyArtifacts = PythonHelper.extractPythonModules(getClass().getClassLoader());
        StringBuilder pythonPathEnv = new StringBuilder().append(".:");
        if (System.getenv("PYTHONPATH") != null) {
            pythonPathEnv.append(System.getenv("PYTHONPATH"));
        }
        for (String artifact: pyArtifacts) {
            pythonPathEnv.append(":");
            pythonPathEnv.append(artifact);
        }
        logger.info("built PYTHONPATH: {}", pythonPathEnv);
        stateDescriptor = new ValueStateDescriptor<>("python-wrapper-job-func", BYTE_ARRAY_TYPE_INFO);
        keyExtractor = new KeySelectorForMap(config.getGroupByFields(), "_default");
        String[] cmd = new String[]{"-m", "jax_python.flink_worker"};
        Map<String, String> envs = new HashMap<>();
        envs.put("PYTHONPATH", pythonPathEnv.toString());
        envs.put("MODULE_IMPORT", config.getPythonModule());
        envs.put("MODULE_CLASS", config.getPythonClass());
        PythonEntryPoint ep = new PythonEntryPoint();
        ep.customConfig = config.getAllConfig();
        // take python interpreter
        String python = System.getenv(PYTHON_INTERPRETER_ENV);
        if (StrUtil.isEmpty(python)) {
            python = System.getProperty(PYTHON_INTERPRETER_ENV);
        }
        runner = new PythonRunner()
                .setEntryPoint(ep)
                .setArguments(cmd)
                .setPythonIntepreterPath(python)
                .setEnvironments(envs)
                .setStateSnapshotInterval(checkpointInterval);
        runner.open();
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (runner != null) {
            runner.close();
        }
    }

    @Override
    public void flatMap(Map<String, Object> value, Collector<Map<String, Object>> out) throws Exception {
        rate.markEvent();
        ValueState<Byte[]> valueState = getRuntimeContext().getState(stateDescriptor);
        String key = keyExtractor.getKey(value);
        if (Boolean.TRUE.equals(config.getTimeConsumeObserve())) {
            value.put("jvm-start", System.currentTimeMillis());
        }
        List<Map<String, Object>> mapped = runner.process(value, key, valueState);
        for (Map<String, Object> entry: mapped) {
            if (Boolean.TRUE.equals(config.getTimeConsumeObserve())) {
                entry.put("jvm-end", System.currentTimeMillis());
            }
            out.collect(entry);
        }
    }
}
