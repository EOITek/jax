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

import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.api.reflect.ParamUtil;

import java.util.List;
import java.util.Map;

public class FilterFieldJobConfig {

    @Parameter(
            label = "字段白名单",
            description = "仅保留的字段列表",
            optional = true
    )
    private List<String> whiteListFields;

    @Parameter(
            label = "字段黑名单",
            description = "仅过滤的字段。如果黑名单中包含白名单字段，该字段优先属于黑名单",
            optional = true
    )
    private List<String> blackListFields;

    public List<String> getBlackListFields() {
        return blackListFields;
    }

    public void setBlackListFields(List<String> blackListFields) {
        this.blackListFields = blackListFields;
    }

    public List<String> getWhiteListFields() {
        return whiteListFields;
    }

    public void setWhiteListFields(List<String> whiteListFields) {
        this.whiteListFields = whiteListFields;
    }

    public static FilterFieldJobConfig fromMap(Map<String,Object> map) {
        FilterFieldJobConfig config = new FilterFieldJobConfig();
        ParamUtil.configJobParams(config,map);
        return config;
    }
}
