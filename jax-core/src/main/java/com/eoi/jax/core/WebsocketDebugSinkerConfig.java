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

package com.eoi.jax.core;

import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.api.annotation.Parameter;
import org.apache.flink.util.StringUtils;

import java.io.Serializable;

public class WebsocketDebugSinkerConfig implements ConfigValidatable, Serializable {
    @Parameter(description = "连接的uri")
    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public void validate() throws JobConfigValidationException {
        if (StringUtils.isNullOrWhitespaceOnly(this.uri)) {
            throw new JobConfigValidationException("the parameter `uri` is required");
        }
    }
}
