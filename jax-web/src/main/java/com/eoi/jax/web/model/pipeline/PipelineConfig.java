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

package com.eoi.jax.web.model.pipeline;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineConfig {
    private List<PipelineJob> jobs = new ArrayList<>();
    private List<PipelineEdge> edges = new ArrayList<>();
    private Map<String, Object> opts = new HashMap<>();

    public List<PipelineJob> getJobs() {
        return jobs;
    }

    public PipelineConfig setJobs(List<PipelineJob> jobs) {
        this.jobs = jobs;
        return this;
    }

    public List<PipelineEdge> getEdges() {
        return edges;
    }

    public PipelineConfig setEdges(List<PipelineEdge> edges) {
        this.edges = edges;
        return this;
    }

    public Map<String, Object> getOpts() {
        return opts;
    }

    public PipelineConfig setOpts(Map<String, Object> opts) {
        this.opts = opts;
        return this;
    }

    public PipelineConfigOpts genOptsBean() {
        if (CollUtil.isNotEmpty(opts)) {
            return BeanUtil.mapToBean(
                    opts,
                    PipelineConfigOpts.class,
                    true);
        }
        return new PipelineConfigOpts();
    }
}
