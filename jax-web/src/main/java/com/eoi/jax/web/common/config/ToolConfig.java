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

package com.eoi.jax.web.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jax.tool")
public class ToolConfig implements ConfigToMap {
    private String jaxToolBin;
    private Integer jaxToolTimeoutMillis = 60000;

    public String getJaxToolBin() {
        return jaxToolBin;
    }

    public ToolConfig setJaxToolBin(String jaxToolBin) {
        this.jaxToolBin = jaxToolBin;
        return this;
    }

    public Integer getJaxToolTimeoutMillis() {
        return jaxToolTimeoutMillis == null ? 60000 : jaxToolTimeoutMillis;
    }

    public ToolConfig setJaxToolTimeoutMillis(Integer jaxToolTimeoutMillis) {
        this.jaxToolTimeoutMillis = jaxToolTimeoutMillis == null ? 60000 : jaxToolTimeoutMillis;
        return this;
    }
}
