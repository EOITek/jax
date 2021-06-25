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

package com.eoi.jax.manager.flink;

import com.eoi.jax.manager.api.JobGetResult;
import com.eoi.jax.manager.process.ProcessOutput;

import java.util.List;

public class FlinkJobGetResult extends FlinkJobListResult implements JobGetResult {
    private FlinkJobState job;

    public FlinkJobGetResult(BaseFlinkJobParam param) {
        super(param);
    }

    public FlinkJobState getJob() {
        return job;
    }

    public FlinkJobGetResult setJob(FlinkJobState job) {
        this.job = job;
        return this;
    }

    public String getJobState() {
        return job == null ? null : job.getJobState();
    }

    public boolean found() {
        return job != null;
    }

    @Override
    public FlinkJobGetResult deserialize(ProcessOutput output) {
        super.deserialize(output);
        FlinkJobGetParam get = (FlinkJobGetParam) getParam();
        List<FlinkJobState> items = getJobList();
        for (FlinkJobState item : items) {
            if (get.getJobId().equalsIgnoreCase(item.getJobId())) {
                this.job = item;
            }
        }
        return this;
    }
}
