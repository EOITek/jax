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

package com.eoi.jax.core.spark.python;

import cn.hutool.core.bean.BeanUtil;
import com.eoi.jax.api.ConfigValidatable;
import com.eoi.jax.api.annotation.FlatStringMap;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.api.reflect.ParamUtil;

import java.util.List;
import java.util.Map;

public class BridgeJobConfig implements ConfigValidatable {

    private String moduleName;

    private String algName;

    private Map<String, Object> paramMap;

    @Parameter(
            label = "分组字段",
            description = "数据的分组字段",
            optional = true
    )
    private List<String> groupByFields;

    @Parameter(
            label = "算法模型存储",
            description = "算法模型存储使用的存储介质，详情参考相关文档",
            optional = true,
            candidates = {"redis", "redis-sensor"},
            defaultValue = "redis"
    )
    private String stateBackend;

    /**
     * for redis or redis-sensor: hosts: host:port,host:port mode: single|sentinel password: master: for sentinel mode keyPattern: 每个实例数据生成的模型写入redis的时候，使用keyPattern来生成对应的key。例如:
     * 182h9aghag_${item_guid}表示以 固定字符串为前缀，item_guid字段的值为后缀生成key。注意对字段的引用需采用${}包裹，并且该字段必须存在于groupByFields中 如果不设置keyPattern，默认将全部分组字段的值通过下划线拼接起来作为key
     */
    @Parameter(
            label = "模型存储高级选项",
            description = "不同的存储可能需要额外配置，详情参考相关文档",
            optional = true
    )
    private FlatStringMap stateBackendAdvance;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getAlgName() {
        return algName;
    }

    public void setAlgName(String algName) {
        this.algName = algName;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public List<String> getGroupByFields() {
        return groupByFields;
    }

    public void setGroupByFields(List<String> groupByFields) {
        this.groupByFields = groupByFields;
    }

    public String getStateBackend() {
        return stateBackend;
    }

    public void setStateBackend(String stateBackend) {
        this.stateBackend = stateBackend;
    }

    public FlatStringMap getStateBackendAdvance() {
        return stateBackendAdvance;
    }

    public void setStateBackendAdvance(FlatStringMap stateBackendAdvance) {
        this.stateBackendAdvance = stateBackendAdvance;
    }

    @Override
    public void validate() {
    }

    public static BridgeJobConfig fromMap(Map<String, Object> config) {
        BridgeJobConfig bridgeJobConfig = new BridgeJobConfig();
        ParamUtil.configJobParams(bridgeJobConfig, config);
        bridgeJobConfig.setParamMap(config);
        BeanUtil.setProperty(bridgeJobConfig, "moduleName", config.get("moduleName"));
        BeanUtil.setProperty(bridgeJobConfig, "algName", config.get("algName"));
        // used for debug
        if (config.containsKey("disableLogAccumulator")) {
            bridgeJobConfig.setDisableLogAccumulator(true);
        }
        return bridgeJobConfig;
    }

    // for debug purpose, `print` in python code can be see directly instead of collect by accumulator
    // set true when you run spark in local/embedded mode
    private Boolean disableLogAccumulator;

    public Boolean getDisableLogAccumulator() {
        return disableLogAccumulator;
    }

    public void setDisableLogAccumulator(Boolean disableLogAccumulator) {
        this.disableLogAccumulator = disableLogAccumulator;
    }
}
