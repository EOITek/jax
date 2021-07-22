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
import com.eoi.jax.flink.job.common.DecoderFunction;
import org.apache.flink.util.OutputTag;

import java.util.Map;

public class JsonDecoderFunction extends DecoderFunction {

    private JsonDecoderJobConfig config;
    final OutputTag<Map<String, Object>> errorTag;

    public JsonDecoderFunction(JsonDecoderJobConfig config, OutputTag<Map<String, Object>> errorTag) {
        super(config, errorTag);
        this.config = config;
        this.errorTag = errorTag;
    }

    @Override
    protected Object decodedValue(byte[] bytes) throws Exception {
        String str = new String(bytes, config.getCharset());
        return JsonUtil.decode2Map(str);
    }

}
