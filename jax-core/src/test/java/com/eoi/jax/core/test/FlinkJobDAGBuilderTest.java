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

import com.eoi.jax.api.FlinkEnvironment;
import com.eoi.jax.core.FlinkJobDAGBuilder;
import com.eoi.jax.core.FlinkJobNode;
import com.eoi.jax.core.FlinkJobOpts;
import com.eoi.jax.core.JobBuildException;
import com.eoi.jax.core.flink.python.PythonWrapperJob;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Assert;
import org.junit.Test;

public class FlinkJobDAGBuilderTest {

    public static FlinkEnvironment basicContext() {
        StreamExecutionEnvironment streamEnv;
        streamEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        return new FlinkEnvironment(streamEnv, null);
    }

    /**
     * [1.TestSourceJob]->[2.TestProcessJob1]->[4.TestSinkJob][1.TestSourceJob]->[3.TestProcessJob2]->[5.TestSinkJob]
     * +-------------------+     +-------------------+     +----------------+
     * |  1.TestSourceJob  | --> | 2.TestProcessJob1 | --> | 4.TestSinkJob  |
     * +-------------------+     +-------------------+     +----------------+
     *   |
     *   |
     *   v
     * +-------------------+     +-------------------+
     * | 3.TestProcessJob2 | --> |   5.TestSinkJob   |
     * +-------------------+     +-------------------+
     */
    @Test
    public void flinkJobBuilderTest_1() throws Throwable {
        FlinkEnvironment context = basicContext();

        FlinkJobDAGBuilder flinkJobDAGBuilder = new FlinkJobDAGBuilder(context);
        FlinkJobNode sourceNode = FlinkJobNode.newFlinkJobNode("1", TestSourceJob.class.getName());
        FlinkJobNode processNode1 = FlinkJobNode.newFlinkJobNode("2", TestProcessJob1.class.getName());
        FlinkJobNode processNode2 = FlinkJobNode.newFlinkJobNode("3", TestProcessJob2.class.getName());
        FlinkJobNode sinkNode1 = FlinkJobNode.newFlinkJobNode("4", TestSinkJob.class.getName());
        FlinkJobNode sinkNode2 = FlinkJobNode.newFlinkJobNode("5", TestSinkJob.class.getName());
        // put edge
        flinkJobDAGBuilder.putEdge(sourceNode, processNode1);
        flinkJobDAGBuilder.putEdge(sourceNode, processNode2);
        flinkJobDAGBuilder.putEdge(processNode1, sinkNode1);
        flinkJobDAGBuilder.putEdge(processNode2, sinkNode2);

        // start to visit and build
        flinkJobDAGBuilder.getChain();
        context.streamEnv.execute();
    }

    @Test(expected = JobBuildException.class)
    public void flinkJobBuilderTest_2() throws Throwable {
        FlinkEnvironment context = basicContext();
        FlinkJobDAGBuilder flinkJobDAGBuilder = new FlinkJobDAGBuilder(context);

        FlinkJobNode sourceNode = new FlinkJobNode();
        sourceNode.setId("1").setEntry(TestSourceJob.class.getName());
        FlinkJobNode processNode1 = new FlinkJobNode();
        processNode1.setId("2").setEntry(TestProcessJob.class.getName());

        flinkJobDAGBuilder.putEdge(sourceNode, processNode1);
        try {
            flinkJobDAGBuilder.getChain();
        } catch (Exception ex) {
            System.out.println(ex.toString());
            throw ex;
        }
    }

    @Test
    public void flinkJobBuilderTest_3() throws Throwable {
        FlinkEnvironment context = basicContext();
        FlinkJobDAGBuilder flinkJobDAGBuilder = new FlinkJobDAGBuilder(context);

        FlinkJobNode sourceNode = new FlinkJobNode();
        sourceNode.setId("1").setEntry(TestSourceJob.class.getName());
        FlinkJobNode processNode1 = new FlinkJobNode();
        processNode1.setId("2").setEntry(TestProcessJob0.class.getName());
        FlinkJobNode processNode2 = new FlinkJobNode();
        processNode2.setId("3").setEntry(TestProcessJob.class.getName());

        flinkJobDAGBuilder.putEdge(sourceNode, processNode1);
        flinkJobDAGBuilder.putEdge(processNode1, processNode2);
        flinkJobDAGBuilder.getChain();
    }

    @Test
    public void flinkJobBuilderTest_debug() throws Throwable {
        FlinkEnvironment context = basicContext();
        FlinkJobDAGBuilder flinkJobDAGBuilder = new FlinkJobDAGBuilder(context);
        FlinkJobOpts opts = new FlinkJobOpts();
        opts.setEnableDebug(true);
        opts.setDebugEntry("com.eoi.jax.core.test.PrintDebugSinker");
        FlinkJobNode sourceNode = new FlinkJobNode();
        sourceNode.setId("TestSourceJob").setEntry(TestSourceJob.class.getName());
        sourceNode.setOpts(opts);
        FlinkJobNode processNode1 = new FlinkJobNode();
        processNode1.setId("TestProcessJob0").setEntry(TestProcessJob0.class.getName());
        processNode1.setOpts(opts);
        flinkJobDAGBuilder.putEdge(sourceNode, processNode1);
        flinkJobDAGBuilder.build();

        context.streamEnv.execute();

        // should see print things
        // TestSourceJob[0]: 72
        // TestSourceJob[0]: 103
        // TestProcessJob0[0]: {"index":3}
        // TestSourceJob[0]: 76
        // TestProcessJob0[0]: {"index":2}
        // TestSourceJob[0]: 80
        // TestProcessJob0[0]: {"index":7}
        // TestSourceJob[0]: 77
        // TestSourceJob[0]: 84
    }

    @Test
    public void rewrite_test() throws Throwable {
        FlinkEnvironment context = basicContext();
        FlinkJobDAGBuilder flinkJobDAGBuilder = new FlinkJobDAGBuilder(context);
        FlinkJobNode source = new FlinkJobNode();
        source.setId("1").setEntry(TestSourceMapJob.class.getName());
        FlinkJobNode pythonNode = new FlinkJobNode();
        pythonNode.setId("2").setEntry("python.flink.sample_moving_avg.MovingAvg.MovingAvg");
        flinkJobDAGBuilder.putEdge(source, pythonNode);
        flinkJobDAGBuilder.build();
        Assert.assertEquals(pythonNode.getEntry(), PythonWrapperJob.class.getCanonicalName());
        Assert.assertEquals(source.getEntry(), TestSourceMapJob.class.getCanonicalName());
        Assert.assertEquals("sample_moving_avg.MovingAvg", pythonNode.getConfig().get("pythonModule"));
        Assert.assertEquals("MovingAvg", pythonNode.getConfig().get("pythonClass"));
    }
}
