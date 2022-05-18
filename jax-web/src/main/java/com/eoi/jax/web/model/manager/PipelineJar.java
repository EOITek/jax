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

package com.eoi.jax.web.model.manager;

import com.eoi.jax.web.dao.entity.TbCluster;
import com.eoi.jax.web.dao.entity.TbJar;

public class PipelineJar {
    private TbJar jar;
    private TbCluster cluster;

    public PipelineJar(TbJar jar, TbCluster cluster) {
        this.jar = jar.cloneFromDB();
        this.cluster = cluster;
    }

    public TbJar getJar() {
        return jar;
    }

    public PipelineJar setJar(TbJar jar) {
        this.jar = jar;
        return this;
    }

    public TbCluster getCluster() {
        return cluster;
    }

    public PipelineJar setCluster(TbCluster cluster) {
        this.cluster = cluster;
        return this;
    }
}
