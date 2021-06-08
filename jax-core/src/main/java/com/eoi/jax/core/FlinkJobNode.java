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

package com.eoi.jax.core;

import java.util.Map;

public class FlinkJobNode extends BaseJobNode {

    private FlinkJobOpts opts;

    public FlinkJobOpts getOpts() {
        return opts;
    }

    public void setOpts(FlinkJobOpts opts) {
        this.opts = opts;
    }

    /**
     * create flink job node with id, specified full class name
     *
     * @param id    id
     * @param entry the full class name
     * @return new instance of FlinkJobNode with increased node id
     */
    public static FlinkJobNode newFlinkJobNode(String id, String entry) {
        return newFlinkJobNode(id, entry, null);
    }

    /**
     * create flink job node with id, specified full class name, config
     *
     * @param id     id
     * @param entry  the full class name
     * @param config the config map
     * @return new instance of FlinkJobNode with increased node id
     */
    public static FlinkJobNode newFlinkJobNode(String id, String entry, Map<String, Object> config) {
        return (FlinkJobNode) new FlinkJobNode()
                .setId(id)
                .setEntry(entry)
                .setConfig(config);
    }
}
