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

package com.eoi.jax.api;

import java.io.Serializable;
import java.util.Map;

public class JobMetaConfig implements Serializable {
    private String pipelineName;
    private String jobId;
    private String jobEntry;
    private Map<String, Object> opts;

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobEntry() {
        return jobEntry;
    }

    public void setJobEntry(String jobEntry) {
        this.jobEntry = jobEntry;
    }

    public Map<String, Object> getOpts() {
        return opts;
    }

    public void setOpts(Map<String, Object> opts) {
        this.opts = opts;
    }

    public JobMetaConfig(String jobId, String jobEntry) {
        this.jobId = jobId;
        this.jobEntry = jobEntry;
    }

    public JobMetaConfig(String jobId, String jobEntry, String pipelineName) {
        this.jobId = jobId;
        this.jobEntry = jobEntry;
        this.pipelineName = pipelineName;
    }

    public JobMetaConfig(String jobId, String jobEntry, String pipelineName, Map<String, Object> opts) {
        this.jobId = jobId;
        this.jobEntry = jobEntry;
        this.pipelineName = pipelineName;
        this.opts = opts;
    }
}
