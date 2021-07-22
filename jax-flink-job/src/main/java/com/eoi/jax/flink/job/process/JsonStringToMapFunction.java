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

import com.eoi.jax.common.JsonUtil;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Map;

public class JsonStringToMapFunction extends ProcessFunction<String, Map<String, Object>> {
    final OutputTag<String> errorTag;

    public JsonStringToMapFunction(OutputTag<String> errorTag) {
        this.errorTag = errorTag;
    }

    @Override
    public void processElement(String value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
        Map<String, Object> map = null;
        boolean success;
        try {
            map = JsonUtil.decode2Map(value);
            success = true;
        } catch (Exception e) {
            success = false;
        }
        if (success) {
            out.collect(map);
        } else {
            ctx.output(errorTag, value);
        }
    }
}
