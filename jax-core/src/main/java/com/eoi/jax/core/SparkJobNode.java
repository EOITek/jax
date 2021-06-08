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

public class SparkJobNode extends BaseJobNode {

    private SparkJobOpts opts;

    public SparkJobOpts getOpts() {
        return opts;
    }

    public void setOpts(SparkJobOpts opts) {
        this.opts = opts;
    }

    /**
     * 为了实现SparkMergable，SparkJobNode也需要合并 合并原则: opts,id,entry,classloader 取下游
     */
    public SparkJobNode mergeNextAndCreate(SparkJobNode next) {
        SparkJobNode newNode = new SparkJobNode();
        newNode.opts = next.opts;
        if (next.opts != null) {
            newNode.opts = next.opts;
        } else {
            newNode.opts = this.opts;
        }
        newNode.id = next.id;
        newNode.entry = next.entry;
        newNode.loader = next.loader;
        return newNode;
    }
}
