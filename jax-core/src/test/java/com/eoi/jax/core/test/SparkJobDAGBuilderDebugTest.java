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
import com.eoi.jax.api.SparkEnvironment;
import com.eoi.jax.api.SparkProcessJobBuilder;
import com.eoi.jax.api.SparkSinkJobBuilder;
import com.eoi.jax.api.SparkSourceJobBuilder;
import com.eoi.jax.common.JsonUtil;
import com.eoi.jax.core.SparkJobDAGBuilder;
import com.eoi.jax.core.SparkJobNode;
import com.eoi.jax.core.SparkJobOpts;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SparkJobDAGBuilderDebugTest {

    public static class Data {
        private long ts;
        private String name;
        private int value;

        public long getTs() {
            return ts;
        }

        public Data setTs(long ts) {
            this.ts = ts;
            return this;
        }

        public String getName() {
            return name;
        }

        public Data setName(String name) {
            this.name = name;
            return this;
        }

        public int getValue() {
            return value;
        }

        public Data setValue(int value) {
            this.value = value;
            return this;
        }
    }

    public static class SparkTestSourceDFJob implements SparkSourceJobBuilder<Dataset<Row>, EmptyConfig> {

        @Override
        public Dataset<Row> build(SparkEnvironment context, EmptyConfig config) throws Throwable {
            List<Data> list = new ArrayList<>();
            list.add(new Data().setName("n1").setValue(1).setTs(1));
            return context.sparkSession.createDataFrame(list, Data.class);
        }

        @Override
        public EmptyConfig configure(Map<String, Object> mapConfig) throws Throwable {
            return new EmptyConfig();
        }
    }

    public static class SparkTestSourceRddJob implements SparkSourceJobBuilder<RDD<Map<String, Object>>, EmptyConfig> {

        @Override
        public RDD<Map<String, Object>> build(SparkEnvironment context, EmptyConfig config) throws Throwable {
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(JsonUtil.decode2Map("{\"name\":\"n1\",\"value\":1,\"ts\":1}"));
            JavaSparkContext ctx = new JavaSparkContext(context.sparkContext);
            return ctx.parallelize(list, 1).rdd();
        }

        @Override
        public EmptyConfig configure(Map<String, Object> mapConfig) throws Throwable {
            return new EmptyConfig();
        }
    }

    public static class SparkTestProcessDFJob implements SparkProcessJobBuilder<Dataset<Row>, Dataset<Row>, EmptyConfig> {

        @Override
        public Dataset<Row> build(SparkEnvironment context, Dataset<Row> o, EmptyConfig config) throws Throwable {
            return o;
        }

        @Override
        public EmptyConfig configure(Map<String, Object> mapConfig) throws Throwable {
            return new EmptyConfig();
        }
    }

    public static class SparkTestProcessRddJob implements SparkProcessJobBuilder<RDD<Map<String, Object>>, RDD<Map<String, Object>>, EmptyConfig> {

        @Override
        public RDD<Map<String, Object>> build(SparkEnvironment context, RDD<Map<String, Object>> o, EmptyConfig config) throws Throwable {
            return o;
        }

        @Override
        public EmptyConfig configure(Map<String, Object> mapConfig) throws Throwable {
            return new EmptyConfig();
        }
    }

    public static class SparkTestSinkDFJob implements SparkSinkJobBuilder<Dataset<Row>, EmptyConfig> {
        @Override
        public void build(SparkEnvironment context, Dataset<Row> in, EmptyConfig config)
                throws Throwable {
        }

        @Override
        public EmptyConfig configure(Map<String, Object> mapConfig) throws Throwable {
            return new EmptyConfig();
        }
    }

    public static class SparkTestSinkRddJob implements SparkSinkJobBuilder<RDD<Map<String, Object>>, EmptyConfig> {
        @Override
        public void build(SparkEnvironment context, RDD<Map<String, Object>> in, EmptyConfig config)
                throws Throwable {
        }

        @Override
        public EmptyConfig configure(Map<String, Object> mapConfig) throws Throwable {
            return new EmptyConfig();
        }
    }

    private SparkJobNode createNode(String id, Class targetClass, Map<String, Object> config) {
        SparkJobNode sparkJobNode = new SparkJobNode();
        sparkJobNode.setId(id);
        sparkJobNode.setEntry(targetClass.getName());
        sparkJobNode.setConfig(config);
        return sparkJobNode;
    }

    @Test
    public void testDebugDf() throws Throwable {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local[*]").setAppName("testDebug");
        SparkSession sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();
        SparkEnvironment environment = new SparkEnvironment(sparkSession.sparkContext(), sparkSession);
        SparkJobDAGBuilder builder = new SparkJobDAGBuilder(environment, false);
        builder.setPipelineName("testDebug");
        SparkJobOpts opts = new SparkJobOpts();
        opts.setEnableDebug(true);
        opts.setDebugEntry("com.eoi.jax.core.test.SparkPrintDebugSinker");
        SparkJobNode src = createNode("1", SparkTestSourceDFJob.class, null);
        src.setOpts(opts);
        SparkJobNode prc = createNode("2", SparkTestProcessDFJob.class, null);
        prc.setOpts(opts);
        SparkJobNode sik = createNode("3", SparkTestSinkDFJob.class, null);
        builder.putEdge(src, prc);
        builder.putEdge(prc, sik);
        builder.build();
    }

    @Test
    public void testDebugRdd() throws Throwable {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local[*]").setAppName("testDebug");
        SparkSession sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();
        SparkEnvironment environment = new SparkEnvironment(sparkSession.sparkContext(), sparkSession);
        SparkJobDAGBuilder builder = new SparkJobDAGBuilder(environment, false);
        builder.setPipelineName("testDebug");
        SparkJobOpts opts = new SparkJobOpts();
        opts.setEnableDebug(true);
        opts.setDebugEntry("com.eoi.jax.core.test.SparkPrintDebugSinker");
        SparkJobNode src = createNode("1", SparkTestSourceRddJob.class, null);
        src.setOpts(opts);
        SparkJobNode prc = createNode("2", SparkTestProcessRddJob.class, null);
        prc.setOpts(opts);
        SparkJobNode sik = createNode("3", SparkTestSinkRddJob.class, null);
        builder.putEdge(src, prc);
        builder.putEdge(prc, sik);
        builder.build();
    }
}
