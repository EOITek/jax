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

import java.io.Serializable;


public class PartitionJobConfig implements ConfigValidatable, Serializable {
    @Parameter(
            label = "Partition类型",
            description = "可选：rebalance(Round-robin partitioning)，shuffle(Random partitioning), rescale(local Round-robin partitioning)",
            defaultValue = "rebalance",
            candidates = {"rebalance","shuffle","rescale"}
    )
    private String partitionType;

    public String getPartitionType() {
        return partitionType;
    }

    public void setPartitionType(String partitionType) {
        this.partitionType = partitionType;
    }

    @Override
    public void validate() throws JobConfigValidationException {
    }
}
