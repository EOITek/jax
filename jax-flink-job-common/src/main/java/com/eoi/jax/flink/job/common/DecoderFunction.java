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

package com.eoi.jax.flink.job.common;

import cn.hutool.core.util.StrUtil;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class DecoderFunction extends
        ProcessFunction<Map<String, Object>, Map<String, Object>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DecoderFunction.class);

    private DecoderJobConfig config;
    final OutputTag<Map<String, Object>> errorTag;

    public DecoderFunction(DecoderJobConfig config, OutputTag<Map<String, Object>> errorTag) {
        this.config = config;
        this.errorTag = errorTag;
    }

    protected abstract Object decodedValue(byte [] bytes) throws Exception;

    @Override
    public void processElement(Map<String, Object> value, Context ctx, Collector<Map<String, Object>> out) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        boolean isSuccess = true;
        try {
            Object val = value.get(config.getByteArrayFieldName());
            if (!(val instanceof byte[])) {
                throw new DecoderException("target field should be byte []");
            }

            byte[] bytes = (byte[]) value.getOrDefault(config.getByteArrayFieldName(), null);
            if (bytes == null || bytes.length == 0) {
                throw new DecoderException("bytes [] is empty");
            }

            Object decoded = decodedValue(bytes);

            if (StrUtil.isEmpty(config.getOutputFieldName())) {
                if (config.getReserveOriginColumns()) {
                    Map<String, Object> result = new HashMap<>(value);
                    result.putAll((Map<String, Object>) decoded);
                    ret = result;
                } else {
                    ret = (Map<String, Object>) decoded;
                }
            } else {
                Map<String, Object> result = new HashMap<>(value);
                if (config.getRemoveByteArrayField()) {
                    result.remove(config.getByteArrayFieldName());
                }
                result.put(config.getOutputFieldName(), decoded);
                ret = result;
            }

        } catch (Exception ex) {
            isSuccess = false;
            Map<String, Object> result = new HashMap<>(value);
            String errorMsg = ex.getMessage();
            if (ex.getCause() != null) {
                errorMsg = ex.getCause().getMessage();
            }
            result.put("error_msg", errorMsg);
            ctx.output(errorTag, result);
            LOGGER.error("Decoder error:", ex);
        }

        if (isSuccess) {
            out.collect(ret);
        }
    }
}
