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

package com.eoi.jax.core.flink.python;

import cn.hutool.core.bean.BeanUtil;
import com.eoi.jax.api.annotation.FlatStringMap;
import com.eoi.jax.api.annotation.Parameter;
import com.eoi.jax.api.reflect.ParamUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PythonWrapperJobConfig implements Serializable {

    // 由FlinkJobDAGBuilder.rewriteNode自动带入
    private String pythonModule;

    // 由FlinkJobDAGBuilder.rewriteNode自动带入
    private String pythonClass;

    @Parameter(
            label = "分组字段",
            description = "数据是按照哪些字段分组的。由于算法状态需要按照分组进行保存，所以必须知道分组情况。内部会通过keyBy对数据进行洗牌",
            optional = true
    )
    private List<String> groupByFields;

    @Parameter(
            label = "状态快照存储",
            description = "使用何种机制存储状态快照。选择不同的状态存储可能需要额外配置'存储高级选项'，详情参考相关文档",
            optional = true,
            candidates = {"flink", "redis", "redis-sensor"},
            defaultValue = "flink"
    )
    private String stateBackend;

    @Parameter(
            label = "状态快照存储间隔时间",
            description = "每隔多长时间将快照数据写入存储，单位毫秒。小于等于0表示永远不存储状态快照",
            optional = true,
            defaultValue = "120000"
    )
    private Long stateFlushInterval;

    /**
     * for redis or redis-sensor:
     *  hosts: host:port,host:port
     *  mode: single|sentinel
     *  password:
     *  master: for sentinel mode
     *  keyPrefix: 写入redis的时候，除了使用groupByFields组合出key之外，再使用keyPrefix作为前缀
     */
    @Parameter(
            label = "存储高级选项",
            description = "不同的状态存储可能需要额外配置，详情参考相关文档",
            optional = true
    )
    private FlatStringMap stateBackendAdvance;

    @Parameter(
            label = "开启性能评估",
            description = "通过在数据中注入时间戳，分析查看每条数据的处理时间，仅用于验证python算子的性能",
            optional = true,
            defaultValue = "false"
    )
    private Boolean timeConsumeObserve;

    private Map<String, Object> allConfig; // 保存整个前端回传的config

    public String getPythonModule() {
        return pythonModule;
    }

    public void setPythonModule(String pythonModule) {
        this.pythonModule = pythonModule;
    }

    public String getPythonClass() {
        return pythonClass;
    }

    public void setPythonClass(String pythonClass) {
        this.pythonClass = pythonClass;
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

    public Long getStateFlushInterval() {
        return stateFlushInterval;
    }

    public void setStateFlushInterval(Long stateFlushInterval) {
        this.stateFlushInterval = stateFlushInterval;
    }

    public FlatStringMap getStateBackendAdvance() {
        return stateBackendAdvance;
    }

    public void setStateBackendAdvance(FlatStringMap stateBackendAdvance) {
        this.stateBackendAdvance = stateBackendAdvance;
    }

    public Boolean getTimeConsumeObserve() {
        return timeConsumeObserve;
    }

    public void setTimeConsumeObserve(Boolean timeConsumeObserve) {
        this.timeConsumeObserve = timeConsumeObserve;
    }

    public Map<String, Object> getAllConfig() {
        return allConfig;
    }

    public void setAllConfig(Map<String, Object> allConfig) {
        this.allConfig = allConfig;
    }

    public static PythonWrapperJobConfig fromMap(Map<String, Object> config) {
        PythonWrapperJobConfig pythonWrapperJobConfig = new PythonWrapperJobConfig();
        ParamUtil.configJobParams(pythonWrapperJobConfig, config);
        BeanUtil.setProperty(pythonWrapperJobConfig, "pythonModule", config.get("pythonModule"));
        BeanUtil.setProperty(pythonWrapperJobConfig, "pythonClass", config.get("pythonClass"));
        pythonWrapperJobConfig.allConfig = config;
        return pythonWrapperJobConfig;
    }
}
