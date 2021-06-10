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

import com.eoi.jax.core.AbstractDAGNode;
import com.eoi.jax.core.AbstractDAGVisitor;
import com.eoi.jax.core.DAGVisitException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class AbstractDAGVisitorMultiSlotTest {

    /**
     * echo '[1]->{label:"0:0";}[2][1]->{label:"1:0"}[3]' | graph-easy
     * +------+  0:0   +---+
     * |  1   | -----> | 2 |
     * +------+        +---+
     *   |
     *   | 1:0
     *   v
     * +------+
     * |  3   |
     * +------+
     */
    @Test
    public void test_AbstractDAGVisitorMultiSlotTest_1() throws Throwable {
        MultiSlotDAGNode node1 = new MultiSlotDAGNode("1", p -> Arrays.asList("0", "1"));
        MultiSlotDAGNode node2 = new MultiSlotDAGNode("2", p -> Arrays.asList(p.get(0).toString() + "2"));
        MultiSlotDAGNode node3 = new MultiSlotDAGNode("3", p -> Arrays.asList(p.get(0).toString() + "3"));

        MultiSlotDAGVisitor visitor = new MultiSlotDAGVisitor();
        visitor.putEdge(node1, node3, 1, 0);
        visitor.putEdge(node1, node2, 0, 0);
        List results = visitor.getChain();
        Assert.assertEquals("13", ((List)results.get(0)).get(0));
        Assert.assertEquals("02", ((List)results.get(1)).get(0));
    }

    /**
     * echo '[1]->{label:"0:0";}[2][1]->{label:"1:0"}[3][4]->{label:"0:1";}[3][2]->{label:"1:0"}[5][3]->{label:"0:1"}[5]' | graph-easy
     * +---+  0:0   +---+  1:0   +---+
     * | 1 | -----> | 2 | -----> | 5 |
     * +---+        +---+        +---+
     *   |   1:0                   ^
     *   +------------+            |
     *                v            |
     * +---+  0:1   +---+  0:1     |
     * | 4 | -----> | 3 | ---------+
     * +---+        +---+
     */
    @Test
    public void test_AbstractDAGVisitorMultiSlotTest_2() throws Throwable {
        // node1分别在slot0和slot1上输出"0"和"1"
        MultiSlotDAGNode node1 = new MultiSlotDAGNode("1", p -> Arrays.asList("0", "1"));
        // node2分别在slot0和slot1上输出null和input(0)+"2"
        MultiSlotDAGNode node2 = new MultiSlotDAGNode("2", p -> Arrays.asList(null, p.get(0).toString() + "2"));
        // node3在slot0上输出input(0)+input(1)+"3"
        MultiSlotDAGNode node3 = new MultiSlotDAGNode("3", p -> Arrays.asList(p.get(0).toString() + p.get(1).toString() + "3"));
        // node4在slot0上输出"4"
        MultiSlotDAGNode node4 = new MultiSlotDAGNode("4", p -> Arrays.asList("4"));
        // node在slot0上输出input(0)+input(1)+"5"
        MultiSlotDAGNode node5 = new MultiSlotDAGNode("5", p -> Arrays.asList(p.get(0).toString() + p.get(1).toString() + "5"));
        MultiSlotDAGVisitor visitor = new MultiSlotDAGVisitor();
        visitor.putEdge(node1, node3, 1, 0);
        visitor.putEdge(node1, node2, 0, 0);
        visitor.putEdge(node4, node3, 0, 1);
        visitor.putEdge(node2, node5, 1, 0);
        visitor.putEdge(node3, node5, 0, 1);
        List results = visitor.getChain();
        Assert.assertEquals("021435", ((List)results.get(0)).get(0));
    }

    @Test
    public void test_AbstractDAGVisitorMultiSlotTest_2_0() throws Throwable {
        // node1分别在slot0和slot1上输出"0"和"1"
        MultiSlotDAGNode node1 = new MultiSlotDAGNode("1", p -> Arrays.asList("0", "1"));
        // node2分别在slot0和slot1上输出null和input(0)+"2"
        MultiSlotDAGNode node2 = new MultiSlotDAGNode("2", p -> Arrays.asList(null, p.get(0).toString() + "2"));
        // node3在slot0上输出input(0)+input(1)+"3"
        MultiSlotDAGNode node3 = new MultiSlotDAGNode("3", p -> Arrays.asList(p.get(0).toString() + p.get(1).toString() + "3"));
        // node4在slot0上输出"4"
        MultiSlotDAGNode node4 = new MultiSlotDAGNode("4", p -> Arrays.asList("4"));
        // node在slot0上输出input(0)+input(1)+"5"
        MultiSlotDAGNode node5 = new MultiSlotDAGNode("5", p -> Arrays.asList(p.get(0).toString() + p.get(1).toString() + "5"));
        MultiSlotDAGVisitor visitor = new MultiSlotDAGVisitor();
        visitor.putEdge(node1, node2, 0, 0);
        visitor.putEdge(node1, node3, 1, 0);
        visitor.putEdge(node4, node3, 0, 1);
        visitor.putEdge(node2, node5, 1, 0);
        visitor.putEdge(node3, node5, 0, 1);
        List results = visitor.getChain();
        Assert.assertEquals("021435", ((List) results.get(0)).get(0));
    }

    /**
     * echo '[1]->{label:"0:0";}[2][1]->{label:"0:0"}[3][4]->{label:"0:1";}[3][2]->{label:"1:0"}[5][3]->{label:"0:1"}[5]' | graph-easy
     * +---+  0:0   +---+  1:0   +---+
     * | 1 | -----> | 2 | -----> | 5 |
     * +---+        +---+        +---+
     *   |   0:0                   ^
     *   +------------+            |
     *                v            |
     * +---+  0:1   +---+  0:1     |
     * | 4 | -----> | 3 | ---------+
     * +---+        +---+
     */
    @Test
    public void test_AbstractDAGVisitorMultiSlotTest_3() throws Throwable {
        // node1分别在slot0和slot1上输出"0"和"1"
        MultiSlotDAGNode node1 = new MultiSlotDAGNode("1", p -> Arrays.asList("0", "1"));
        // node2分别在slot0和slot1上输出null和input(0)+"2"
        MultiSlotDAGNode node2 = new MultiSlotDAGNode("2", p -> Arrays.asList(null, p.get(0).toString() + "2"));
        // node3在slot0上输出input(0)+input(1)+"3"
        MultiSlotDAGNode node3 = new MultiSlotDAGNode("3", p -> Arrays.asList(p.get(0).toString() + p.get(1).toString() + "3"));
        // node4在slot0上输出"4"
        MultiSlotDAGNode node4 = new MultiSlotDAGNode("4", p -> Arrays.asList("4"));
        // node在slot0上输出input(0)+input(1)+"5"
        MultiSlotDAGNode node5 = new MultiSlotDAGNode("5", p -> Arrays.asList(p.get(0).toString() + p.get(1).toString() + "5"));
        MultiSlotDAGVisitor visitor = new MultiSlotDAGVisitor();
        visitor.putEdge(node1, node2, 0, 0);
        visitor.putEdge(node4, node3, 0, 1);
        visitor.putEdge(node1, node3, 0, 0);
        visitor.putEdge(node2, node5, 1, 0);
        visitor.putEdge(node3, node5, 0, 1);
        List results = visitor.getChain();
        Assert.assertEquals("020435", ((List) results.get(0)).get(0));
    }

    static class MultiSlotDAGNode extends AbstractDAGNode {

        private String id;
        private Function<List, List> visit;

        public MultiSlotDAGNode(String i, Function<List, List> visit) {
            this.id = i;
            this.visit = visit;
        }

        @Override
        public String getId() {
            return id;
        }

        public List visit(List parents) {
            return this.visit.apply(parents);
        }
    }

    static class MultiSlotDAGVisitor extends AbstractDAGVisitor<MultiSlotDAGNode> {
        @Override
        protected List visit(MultiSlotDAGNode node, List parents) throws DAGVisitException {
            return node.visit(parents);
        }
    }
}
