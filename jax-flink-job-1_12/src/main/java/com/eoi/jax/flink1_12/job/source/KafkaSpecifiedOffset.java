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

public class KafkaSpecifiedOffset implements Serializable {

    @Parameter(
            label = "指定topic",
            description = "当offsetMode为specified时，指定topic",
            optional = true
    )
    private String topic;

    @Parameter(
            label = "指定partition",
            description = "当offsetMode为specified时，指定partition",
            optional = true
    )
    private Integer partition;

    @Parameter(
            label = "指定位置",
            description = "当offsetMode为specified时，指定位置",
            optional = true
    )
    private Long offset;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
}
