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

import cn.hutool.core.map.MapUtil;
import com.eoi.jax.flink.job.common.AviatorUtil;
import com.googlecode.aviator.Expression;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AviatorProcessJobFunction extends
        ProcessFunction<Map<String, Object>, Map<String, Object>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AviatorProcessJobFunction.class);

    private String script;
    private boolean topLevel;
    final OutputTag<Map<String, Object>> errorTag;
    private transient Expression exp;

    public AviatorProcessJobFunction(String script, boolean topLevel, OutputTag<Map<String, Object>> errorTag) {
        this.script = script;
        this.topLevel = topLevel;
        this.errorTag = errorTag;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        exp = AviatorUtil.instance.compile(script);
    }

    @Override
    public void processElement(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
        Object result = null;
        try {
            Map<String, Object> env;
            if (!this.topLevel) {
                env = new HashMap<>(value);
            } else {
                env = MapUtil.builder(new HashMap<String, Object>())
                        .put("doc", value)
                        .build();
            }
            result = exp.execute(env);
        } catch (Exception ex) {
            Map<String, Object> retMap = new HashMap<>(value);
            String errorMsg = ex.getMessage();
            if (ex.getCause() != null) {
                errorMsg = ex.getCause().getMessage();
            }
            retMap.put("error_msg", errorMsg);
            ctx.output(errorTag, retMap);
            LOGGER.error("aviator error:", ex.getCause() != null ? ex.getCause() : ex);
        }

        collectAviatorResult(out, result, true);
    }

    public static void collectAviatorResult(Collector<Map<String, Object>> out, Object result, boolean ignoreException) {
        if (result != null) {
            if (result instanceof Map) {
                try {
                    Map<String, Object> ret = (Map<String, Object>) result;
                    out.collect(ret);
                } catch (Exception ex) {
                    LOGGER.error("aviator result error:" + result);
                    if (!ignoreException) {
                        throw ex;
                    }
                }
            } else if (result instanceof Collection) {
                Object [] resultList = ((Collection)result).toArray();
                for (Object o : resultList) {
                    if (o instanceof Map) {
                        try {
                            Map<String, Object> ret = (Map<String, Object>) o;
                            out.collect(ret);
                        } catch (Exception ex) {
                            LOGGER.error("aviator result error:" + result);
                            if (!ignoreException) {
                                throw ex;
                            }
                        }
                    } else {
                        out.collect(MapUtil.builder(new HashMap<String, Object>())
                                .put("__result__", o)
                                .build());
                    }
                }
            } else {
                out.collect(MapUtil.builder(new HashMap<String, Object>())
                        .put("__result__", result)
                        .build());
            }
        }
    }
}
