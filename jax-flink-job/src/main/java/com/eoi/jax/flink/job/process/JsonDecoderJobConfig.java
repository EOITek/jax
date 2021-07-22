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

import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.flink.job.common.DecoderJobConfig;

import java.io.Serializable;


public class JsonDecoderJobConfig extends DecoderJobConfig implements ConfigValidatable, Serializable {
    @Parameter(
            label = "字符编码",
            description = "例如标准编码：US-ASCII,ISO-8859-1,UTF-8,UTF-16BE,UTF-16LE,UTF-16",
            optional = true,
            defaultValue = "UTF-8"
    )
    private String charset;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public void validate() throws JobConfigValidationException {
        super.validate();
    }
}
