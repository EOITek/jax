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

package com.eoi.jax.web.provider.validator;

import java.util.List;
import java.util.Map;

/**
 * copy from com.eoi.jax.tool.PipelineDAG
 */
public class PipelineDAG {
    private String pipelineName;
    private List<ConfigNode> jobs;
    private List<EdgeDescription> edges;

    public String getPipelineName() {
        return pipelineName;
    }

    public PipelineDAG setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
        return this;
    }

    public List<ConfigNode> getJobs() {
        return jobs;
    }

    public PipelineDAG setJobs(List<ConfigNode> jobs) {
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

    public static class ConfigNode {
        protected String id;
        protected String entry;
        protected Map<String, Object> config;
        protected Map<String, Object> lastConfig;

        public String getId() {
            return id;
        }

        public ConfigNode setId(String id) {
            this.id = id;
            return this;
        }

        public String getEntry() {
            return entry;
        }

        public ConfigNode setEntry(String entry) {
            this.entry = entry;
            return this;
        }

        public Map<String, Object> getConfig() {
            return config;
        }

        public ConfigNode setConfig(Map<String, Object> config) {
            this.config = config;
            return this;
        }

        public Map<String, Object> getLastConfig() {
            return lastConfig;
        }

        public ConfigNode setLastConfig(Map<String, Object> lastConfig) {
            this.lastConfig = lastConfig;
            return this;
        }
    }

    public static class EdgeDescription {
        private String from;
        private String to;
        private Integer toSlot;
        private Integer fromSlot;

        public String getFrom() {
            return from;
        }

        public EdgeDescription setFrom(String from) {
            this.from = from;
            return this;
        }

        public String getTo() {
            return to;
        }

        public EdgeDescription setTo(String to) {
            this.to = to;
            return this;
        }

        public Integer getToSlot() {
            return toSlot;
        }

        public EdgeDescription setToSlot(Integer toSlot) {
            this.toSlot = toSlot;
            return this;
        }

        public Integer getFromSlot() {
            return fromSlot;
        }

        public EdgeDescription setFromSlot(Integer fromSlot) {
            this.fromSlot = fromSlot;
            return this;
        }
    }
}
