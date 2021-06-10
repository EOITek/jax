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

package com.eoi.jax.core.test;

import com.eoi.jax.api.EmptyConfig;
import com.eoi.jax.api.SparkCacheableJobBuilder;
import com.eoi.jax.api.SparkEnvironment;
import com.eoi.jax.api.SparkMergeableJobBuilder;
import com.eoi.jax.api.SparkProcessJobBuilder;
import com.eoi.jax.api.SparkSinkJobBuilder;
import com.eoi.jax.api.SparkSourceJobBuilder;
import com.eoi.jax.api.tuple.Tuple2;
import com.eoi.jax.core.JobBuildException;
import com.eoi.jax.core.SparkJobDAGBuilder;
import com.eoi.jax.core.SparkJobNode;
import org.apache.spark.storage.StorageLevel;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SparkJobDAGBuilderTest {

    public static class SparkTestSourceJob implements SparkSourceJobBuilder<Object, EmptyConfig> {

        @Override
        public Object build(SparkEnvironment context, EmptyConfig config) throws Throwable {
            return new Object();
        }

        @Override
        public EmptyConfig configure(Map<String, Object> mapConfig) throws Throwable {
            return new EmptyConfig();
        }
    }

    public static class SparkTestSinkJob implements SparkSinkJobBuilder<Object, EmptyConfig> {
        @Override
        public void build(SparkEnvironment context, Object rowDataset, EmptyConfig config)
                throws Throwable {
        }

        @Override
        public EmptyConfig configure(Map<String, Object> mapConfig) throws Throwable {
            return new EmptyConfig();
        }
    }

    public static class SparkTestNoMergeableJob implements SparkProcessJobBuilder<Object, Object, EmptyConfig> {

        @Override
        public Object build(SparkEnvironment context, Object o, EmptyConfig config) throws Throwable {
            return o;
        }

        @Override
        public EmptyConfig configure(Map<String, Object> mapConfig) throws Throwable {
            return new EmptyConfig();
        }
    }

    public static class SparkTestMergeableJob implements
            SparkProcessJobBuilder<Object, Object, EmptyConfig>,
            SparkMergeableJobBuilder,
            SparkCacheableJobBuilder {

        public Map<String, Object> config;

        @Override
        public Object build(SparkEnvironment context, Object rowDataset, EmptyConfig config)
                throws Throwable {
            for (Map.Entry<String, Object> item: this.config.entrySet()) {
                System.out.println(item.getKey() + ":" + item.getValue());
            }
            System.out.println();
            return this.config;
        }

        @Override
        public EmptyConfig configure(Map<String, Object> mapConfig)
                throws Throwable {
            config = mapConfig;
            return new EmptyConfig();
        }

        @Override
        public boolean canMergeWith(Map<String, Object> currentConfig, SparkMergeableJobBuilder next, Map<String, Object> nextConfig) {
            if (currentConfig.containsKey("no_merge")) {
                return false;
            }
            return true;
        }

        @Override
        public Tuple2<SparkMergeableJobBuilder, Map<String, Object>> mergeWith(
                Map<String, Object> currentConfig, SparkMergeableJobBuilder next, Map<String, Object> nextConfig) {
            currentConfig.putAll(nextConfig);
            return Tuple2.of(this, currentConfig);
        }

        @Override
        public void recommendCache(Object outputObject, Integer slot, StorageLevel storageLevel, Integer refCount) {
            System.out.println("cache");
        }
    }

    private SparkJobNode createNode(String id, Class targetClass, Map<String, Object> config) {
        SparkJobNode sparkJobNode = new SparkJobNode();
        sparkJobNode.setId(id);
        sparkJobNode.setEntry(targetClass.getName());
        sparkJobNode.setConfig(config);
        return sparkJobNode;
    }

    /**
     * expect output
     * a:b
     * c:d
     */
    @Test
    public void testMergable_merge() throws Throwable {
        Map<String, Object> m1Config = new HashMap<>();
        Map<String, Object> m2Config = new HashMap<>();
        m1Config.put("a", "b");
        m2Config.put("c", "d");
        SparkJobDAGBuilder sparkJobDAGBuilder =
                new SparkJobDAGBuilder(null);
        SparkJobNode source = createNode("1", SparkTestSourceJob.class, null);
        SparkJobNode m1 = createNode("2", SparkTestMergeableJob.class, m1Config);
        SparkJobNode m2 = createNode("3", SparkTestMergeableJob.class, m2Config);
        SparkJobNode sink = createNode("4", SparkTestSinkJob.class, null);
        sparkJobDAGBuilder.putEdge(source, m1);
        sparkJobDAGBuilder.putEdge(m1, m2);
        sparkJobDAGBuilder.putEdge(m2, sink);
        sparkJobDAGBuilder.build();
    }

    @Test
    public void testNode() throws JobBuildException {
        SparkJobNode node = new SparkJobNode();
        node.setEntry("python.spark.ripple.Ripple");
        Map<String,Object> map = new HashMap<>();
        map.put("upper_constant",10);
        map.put("lower_constant",1);
        node.setConfig(map);
        node.setId("1");
        SparkJobDAGBuilder sparkJobDAGBuilder =
                new SparkJobDAGBuilder(null);
        sparkJobDAGBuilder.rewriteNode(node);
        Assert.assertEquals(10,node.getConfig().get("upper_constant"));
        Assert.assertEquals(1,node.getConfig().get("lower_constant"));
        Assert.assertEquals("com.eoi.jax.core.spark.python.BridgeJob",node.getEntry());
    }

    /**
     * expect output
     * a:b
     * no_merge:1
     *
     * c:d
     */
    @Test
    public void testMergable_no_merge_1() throws Throwable {
        Map<String, Object> m1Config = new HashMap<>();
        Map<String, Object> m2Config = new HashMap<>();
        m1Config.put("a", "b");
        m1Config.put("no_merge", 1);
        m2Config.put("c", "d");
        SparkJobDAGBuilder sparkJobDAGBuilder =
                new SparkJobDAGBuilder(null);
        SparkJobNode source = createNode("1", SparkTestSourceJob.class, null);
        SparkJobNode m1 = createNode("2", SparkTestMergeableJob.class, m1Config);
        SparkJobNode m2 = createNode("3", SparkTestMergeableJob.class, m2Config);
        SparkJobNode sink = createNode("4", SparkTestSinkJob.class, null);
        sparkJobDAGBuilder.putEdge(source, m1);
        sparkJobDAGBuilder.putEdge(m1, m2);
        sparkJobDAGBuilder.putEdge(m2, sink);
        sparkJobDAGBuilder.build();
    }

    /**
     * expect output
     * a:b
     *
     * cache
     * c:d
     *
     */
    @Test
    public void testMergable_no_merge_2() throws Throwable {
        Map<String, Object> m1Config = new HashMap<>();
        Map<String, Object> m2Config = new HashMap<>();
        m1Config.put("a", "b");
        m2Config.put("c", "d");
        SparkJobDAGBuilder sparkJobDAGBuilder =
                new SparkJobDAGBuilder(null);
        SparkJobNode source = createNode("1", SparkTestSourceJob.class, null);
        SparkJobNode m1 = createNode("2", SparkTestMergeableJob.class, m1Config);
        SparkJobNode m2 = createNode("3", SparkTestMergeableJob.class, m2Config);
        SparkJobNode sink = createNode("4", SparkTestSinkJob.class, null);
        SparkJobNode sink2 = createNode("5", SparkTestSinkJob.class, null);
        sparkJobDAGBuilder.putEdge(source, m1);
        sparkJobDAGBuilder.putEdge(m1, m2);
        sparkJobDAGBuilder.putEdge(m2, sink);
        sparkJobDAGBuilder.putEdge(m1, sink2);
        sparkJobDAGBuilder.build();
    }

    /**
     * expect output
     * a:b
     *
     * c:d
     *
     * cache
     *
     */
    @Test
    public void testMergable_no_merge_3() throws Throwable {
        Map<String, Object> m1Config = new HashMap<>();
        Map<String, Object> m2Config = new HashMap<>();
        m1Config.put("a", "b");
        m2Config.put("c", "d");
        SparkJobDAGBuilder sparkJobDAGBuilder =
                new SparkJobDAGBuilder(null);
        SparkJobNode source = createNode("1", SparkTestSourceJob.class, null);
        SparkJobNode m1 = createNode("2", SparkTestMergeableJob.class, m1Config);
        SparkJobNode m2 = createNode("3", SparkTestMergeableJob.class, m2Config);
        SparkJobNode sink = createNode("4", SparkTestSinkJob.class, null);
        SparkJobNode sink2 = createNode("5", SparkTestSinkJob.class, null);
        sparkJobDAGBuilder.putEdge(source, m1);
        sparkJobDAGBuilder.putEdge(m1, m2);
        sparkJobDAGBuilder.putEdge(m2, sink);
        sparkJobDAGBuilder.putEdge(m2, sink2);
        sparkJobDAGBuilder.build();
    }

    /**
     * expect output
     * a:b
     * c:d
     *
     */
    @Test
    public void testMergable_merge_4() throws Throwable {
        Map<String, Object> m1Config = new HashMap<>();
        Map<String, Object> m2Config = new HashMap<>();
        m1Config.put("a", "b");
        m2Config.put("c", "d");
        SparkJobDAGBuilder sparkJobDAGBuilder =
                new SparkJobDAGBuilder(null);
        SparkJobNode source = createNode("1", SparkTestSourceJob.class, null);
        SparkJobNode m1 = createNode("2", SparkTestMergeableJob.class, m1Config);
        SparkJobNode m2 = createNode("3", SparkTestMergeableJob.class, m2Config);
        SparkJobNode m3 = createNode("5", SparkTestNoMergeableJob.class, null);
        SparkJobNode sink = createNode("4", SparkTestSinkJob.class, null);
        sparkJobDAGBuilder.putEdge(source, m1);
        sparkJobDAGBuilder.putEdge(m1, m2);
        sparkJobDAGBuilder.putEdge(m2, m3);
        sparkJobDAGBuilder.putEdge(m3, sink);
        sparkJobDAGBuilder.build();
    }

    /**
     * expect output
     * a:b
     *
     */
    @Test
    public void testMergable_no_merge_5() throws Throwable {
        Map<String, Object> m1Config = new HashMap<>();
        m1Config.put("a", "b");
        SparkJobDAGBuilder sparkJobDAGBuilder =
                new SparkJobDAGBuilder(null);
        SparkJobNode source = createNode("1", SparkTestSourceJob.class, null);
        SparkJobNode m1 = createNode("2", SparkTestMergeableJob.class, m1Config);
        SparkJobNode m3 = createNode("5", SparkTestNoMergeableJob.class, null);
        SparkJobNode sink = createNode("4", SparkTestSinkJob.class, null);
        sparkJobDAGBuilder.putEdge(source, m1);
        sparkJobDAGBuilder.putEdge(m1, m3);
        sparkJobDAGBuilder.putEdge(m3, sink);
        sparkJobDAGBuilder.build();
    }
}
