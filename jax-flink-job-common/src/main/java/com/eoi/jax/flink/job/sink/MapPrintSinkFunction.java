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

package com.eoi.jax.flink.job.sink;

import com.eoi.jax.common.JsonUtil;
import org.apache.flink.api.common.functions.util.PrintSinkOutputWriter;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.operators.StreamingRuntimeContext;

import java.util.Map;

public class MapPrintSinkFunction<IN> extends RichSinkFunction<IN> {

    private final PrintSinkOutputWriter<String> writer;

    public MapPrintSinkFunction() {
        writer = new PrintSinkOutputWriter<>(false);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        StreamingRuntimeContext context = (StreamingRuntimeContext) getRuntimeContext();
        writer.open(context.getIndexOfThisSubtask(), context.getNumberOfParallelSubtasks());
    }

    @Override
    public void invoke(IN value, Context context) throws Exception {
        if (value instanceof Map) {
            writer.write(JsonUtil.encode(value));
        } else {
            writer.write(value.toString());
        }
    }
}
