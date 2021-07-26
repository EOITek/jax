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

package com.eoi.jax.flink1_12.job.source;

import com.eoi.jax.api.annotation.Parameter;

import java.io.Serializable;
import java.util.List;

public class KafkaOffsetSetting implements Serializable {

    public static final String OFFSETMODE_SPECIFIED = "specified";
    public static final String OFFSETMODE_LATEST = "latest";
    public static final String OFFSETMODE_EARLIEST = "earliest";
    public static final String OFFSETMODE_TIMESTAMP = "timestamp";
    public static final String OFFSETMODE_COMMITTED = "committed";

    @Parameter(
            label = "offset模式",
            description = "支持5种:latest,earliest,committed（上次保存的位置）,timestamp（指定时间点）,specified（指定每个topic和partition的某一位置）",
            optional = true,
            defaultValue = "committed",
            candidates = {
                    OFFSETMODE_SPECIFIED,
                    OFFSETMODE_LATEST,
                    OFFSETMODE_EARLIEST,
                    OFFSETMODE_TIMESTAMP,
                    OFFSETMODE_COMMITTED
            }
    )
    private String offsetMode;

    @Parameter(
            label = "指定消费开始时间",
            description = "当offsetMode为timestamp时， 指定的开始时间，格式：unix毫秒时间戳",
            optional = true
    )
    private Long timestamp;

    @Parameter(
            label = "指定offset设置",
            description = "当offsetMode为specified时，指定offset设置",
            optional = true
    )
    private List<KafkaSpecifiedOffset> specifiedOffsets;

    @Parameter(
            label = "重置offset策略",
            description = "当offsetMode为specified或committed时，指定offset相关设置无效时，默认重置offset",
            optional = true,
            candidates = {"LATEST","EARLIEST","NONE"},
            defaultValue = "LATEST"
    )
    private String offsetResetStrategy;

    public String getOffsetMode() {
        return offsetMode;
    }

    public void setOffsetMode(String offsetMode) {
        this.offsetMode = offsetMode;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<KafkaSpecifiedOffset> getSpecifiedOffsets() {
        return specifiedOffsets;
    }

    public void setSpecifiedOffsets(List<KafkaSpecifiedOffset> specifiedOffsets) {
        this.specifiedOffsets = specifiedOffsets;
    }

    public String getOffsetResetStrategy() {
        return offsetResetStrategy;
    }

    public void setOffsetResetStrategy(String offsetResetStrategy) {
        this.offsetResetStrategy = offsetResetStrategy;
    }
}
