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

package tool;

import com.eoi.jax.api.annotation.model.JobParamMeta;
import com.eoi.jax.core.JobInfo;

import java.util.List;

/**
 * copy from com.eoi.jax.core.JobMeta
 */
public class JobMeta {
    public String jobName;
    public List<com.eoi.jax.core.JobMeta.InOutTypeDescriptor> inTypes;
    public List<com.eoi.jax.core.JobMeta.InOutTypeDescriptor> outTypes;
    public com.eoi.jax.core.JobInfo jobInfo;
    public JobParameters parameters;

    public static JobMeta copy(com.eoi.jax.core.JobMeta meta) {
        JobMeta result = new JobMeta();
        result.setInTypes(meta.inTypes);
        result.setOutTypes(meta.outTypes);
        result.setJobInfo(meta.jobInfo);
        result.setParameters(new JobParameters().setParameters(meta.jobParameters));
        return result;
    }

    public String getJobName() {
        return jobName;
    }

    public JobMeta setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public List<com.eoi.jax.core.JobMeta.InOutTypeDescriptor> getInTypes() {
        return inTypes;
    }

    public JobMeta setInTypes(List<com.eoi.jax.core.JobMeta.InOutTypeDescriptor> inTypes) {
        this.inTypes = inTypes;
        return this;
    }

    public List<com.eoi.jax.core.JobMeta.InOutTypeDescriptor> getOutTypes() {
        return outTypes;
    }

    public JobMeta setOutTypes(List<com.eoi.jax.core.JobMeta.InOutTypeDescriptor> outTypes) {
        this.outTypes = outTypes;
        return this;
    }

    public JobInfo getJobInfo() {
        return jobInfo;
    }

    public JobMeta setJobInfo(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
        return this;
    }

    public JobParameters getParameters() {
        return parameters;
    }

    public JobMeta setParameters(JobParameters parameters) {
        this.parameters = parameters;
        return this;
    }

    public static class JobParameters {
        private List<com.eoi.jax.api.annotation.model.JobParamMeta> parameters;

        public List<JobParamMeta> getParameters() {
            return parameters;
        }

        public JobParameters setParameters(List<JobParamMeta> parameters) {
            this.parameters = parameters;
            return this;
        }
    }

}
