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

import com.eoi.jax.core.CheckConfigNode;
import com.eoi.jax.core.entry.EdgeDescription;

import java.util.List;

public class PipelineDAG {
    private String pipelineName;
    private List<CheckConfigNode> jobs;
    private List<EdgeDescription> edges;

    public String getPipelineName() {
        return pipelineName;
    }

    public PipelineDAG setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
        return this;
    }

    public List<CheckConfigNode> getJobs() {
        return jobs;
    }

    public PipelineDAG setJobs(List<CheckConfigNode> jobs) {
        this.jobs = jobs;
        return this;
    }

    public List<EdgeDescription> getEdges() {
        return edges;
    }

    public PipelineDAG setEdges(List<EdgeDescription> edges) {
        this.edges = edges;
        return this;
    }
}
