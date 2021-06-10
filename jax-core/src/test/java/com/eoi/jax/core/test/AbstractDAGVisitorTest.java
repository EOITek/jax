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

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class AbstractDAGVisitorTest {

    /**
     * +---+     +---+     +---+
     * | 1 | --> | 2 | --> | 3 |
     * +---+     +---+     +---+
     */
    @Test
    public void test_DAGVisitorTest_1() throws Throwable {
        TestAbstractDAGVisitor visitor = new TestAbstractDAGVisitor();
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("2", "2"));
        visitor.putEdge(new TestAbstractDAGNode("2", "2"), new TestAbstractDAGNode("3", "3"));
        List chain = visitor.getChain();
        Assert.assertEquals(1, chain.size());
        Assert.assertTrue(chain.get(0) instanceof List);
        Assert.assertTrue(((List)chain.get(0)).get(0) instanceof TestDAGChain);
        TestDAGChain dagChain = (TestDAGChain)((List)chain.get(0)).get(0);
        Assert.assertEquals("3", dagChain.id);
        Assert.assertEquals("2", dagChain.parents.get(0).id);
        Assert.assertEquals("1", dagChain.parents.get(0).parents.get(0).id);
    }

    /**
     * +---+     +---+     +---+     +---+
     * | 1 | --> | 2 | --> | 4 | --> | 5 |
     * +---+     +---+     +---+     +---+
     *   |                             ^
     *   |                             |
     *   v                             |
     * +---+                           |
     * | 3 | --------------------------+
     * +---+
     */
    @Test
    public void test_DAGVisitorTest_2() throws Throwable {
        TestAbstractDAGVisitor visitor = new TestAbstractDAGVisitor();
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("2", "2"));
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("3", "3"));
        visitor.putEdge(new TestAbstractDAGNode("2", "2"), new TestAbstractDAGNode("4", "4"));
        visitor.putEdge(new TestAbstractDAGNode("4", "4"), new TestAbstractDAGNode("5", "5"));
        visitor.putEdge(new TestAbstractDAGNode("3", "3"), new TestAbstractDAGNode("5", "5"));
        List chain = visitor.getChain();
        Assert.assertEquals(1, chain.size());
        TestDAGChain dagChain = (TestDAGChain) ((List)chain.get(0)).get(0);
        Assert.assertEquals("5", dagChain.id);
        Assert.assertEquals("4", dagChain.parents.get(0).id);
        Assert.assertEquals("2", dagChain.parents.get(0).parents.get(0).id);
        Assert.assertEquals("1", dagChain.parents.get(0).parents.get(0).parents.get(0).id);
        Assert.assertEquals("3", dagChain.parents.get(1).id);
        Assert.assertEquals("1", dagChain.parents.get(1).parents.get(0).id);

        // check cached feature
        Assert.assertSame(dagChain.parents.get(1).parents.get(0), dagChain.parents.get(0).parents.get(0).parents.get(0));
    }

    /**
     *                     +---+
     *                     | 7 |
     *                     +---+
     *                       ^
     *                       |
     *                       |
     * +---+     +---+     +---+     +---+
     * | 1 | --> | 4 | --> | 5 | --> | 6 |
     * +---+     +---+     +---+     +---+
     *   |
     *   +---------+
     *             v
     * +---+     +---+     +---+
     * | 3 | --> | 2 | --> | 8 |
     * +---+     +---+     +---+
     */
    @Test
    public void test_DAGVisitorTest_3() throws Throwable {
        TestAbstractDAGVisitor visitor = new TestAbstractDAGVisitor();
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("2", "2"));
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("4", "4"));
        visitor.putEdge(new TestAbstractDAGNode("3", "3"), new TestAbstractDAGNode("2", "2"), 0);
        visitor.putEdge(new TestAbstractDAGNode("2", "2"), new TestAbstractDAGNode("8", "8"));
        visitor.putEdge(new TestAbstractDAGNode("4", "4"), new TestAbstractDAGNode("5", "5"));
        visitor.putEdge(new TestAbstractDAGNode("5", "5"), new TestAbstractDAGNode("6", "6"));
        visitor.putEdge(new TestAbstractDAGNode("5", "5"), new TestAbstractDAGNode("7", "7"));
        List chain = visitor.getChain();
        Assert.assertEquals(3, chain.size());
        TestDAGChain dagChain8 = (TestDAGChain) ((List)chain.get(0)).get(0);
        Assert.assertEquals("8", dagChain8.id);
        Assert.assertEquals("2", dagChain8.parents.get(0).id);
        Assert.assertEquals("3", dagChain8.parents.get(0).parents.get(0).id);
        Assert.assertEquals("1", dagChain8.parents.get(0).parents.get(1).id);

        TestDAGChain dagChain6 = (TestDAGChain) ((List)chain.get(1)).get(0);
        Assert.assertEquals("6", dagChain6.id);
        Assert.assertEquals("5", dagChain6.parents.get(0).id);
        Assert.assertEquals("4", dagChain6.parents.get(0).parents.get(0).id);
        Assert.assertEquals("1", dagChain6.parents.get(0).parents.get(0).parents.get(0).id);

        TestDAGChain dagChain7 = (TestDAGChain) ((List)chain.get(2)).get(0);
        Assert.assertEquals("7", dagChain7.id);
        Assert.assertEquals("5", dagChain7.parents.get(0).id);

        Assert.assertSame(dagChain6.parents.get(0), dagChain7.parents.get(0));
        Assert.assertSame(dagChain8.parents.get(0).parents.get(1), dagChain6.parents.get(0).parents.get(0).parents.get(0));
    }


    /**
     * +---+     +---+     +---+     +---+     +---+     +----+
     * | 4 | <-- | 1 | --> | 2 | --> | 5 | --> | 8 | --> | 10 |
     * +---+     +---+     +---+     +---+     +---+     +----+
     *   |         |                   |                   ^
     *   |         |                   |                   |
     *   v         v                   v                   |
     * +---+     +---+     +---+     +---+                 |
     * | 7 |     | 3 | --> | 6 |     | 9 | ----------------+
     * +---+     +---+     +---+     +---+
     */
    @Test
    public void test_DAGVisitorTest_4() throws Throwable {
        TestAbstractDAGVisitor visitor = new TestAbstractDAGVisitor();
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("2", "2"));
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("3", "3"));
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("4", "4"));
        visitor.putEdge(new TestAbstractDAGNode("2", "2"), new TestAbstractDAGNode("5", "5"));
        visitor.putEdge(new TestAbstractDAGNode("3", "3"), new TestAbstractDAGNode("6", "6"));
        visitor.putEdge(new TestAbstractDAGNode("4", "4"), new TestAbstractDAGNode("7", "7"));
        visitor.putEdge(new TestAbstractDAGNode("5", "5"), new TestAbstractDAGNode("8", "8"));
        visitor.putEdge(new TestAbstractDAGNode("5", "5"), new TestAbstractDAGNode("9", "9"));
        visitor.putEdge(new TestAbstractDAGNode("8", "8"), new TestAbstractDAGNode("10", "10"));
        visitor.putEdge(new TestAbstractDAGNode("9", "9"), new TestAbstractDAGNode("10", "10"));
        visitor.putEdge(new TestAbstractDAGNode("7", "7"), new TestAbstractDAGNode("10", "10"));
        List chain = visitor.getChain();
        Assert.assertEquals(2, chain.size());
        TestDAGChain dagChain6 = (TestDAGChain) ((List)chain.get(0)).get(0);
        Assert.assertEquals("6", dagChain6.id);
        Assert.assertEquals("3", dagChain6.parents.get(0).id);
        Assert.assertEquals("1", dagChain6.parents.get(0).parents.get(0).id);

        TestDAGChain dagChain10 = (TestDAGChain) ((List)chain.get(1)).get(0);
        Assert.assertEquals("10", dagChain10.id);
        Assert.assertEquals("8", dagChain10.parents.get(0).id);
        Assert.assertEquals("9", dagChain10.parents.get(1).id);
        Assert.assertEquals("7", dagChain10.parents.get(2).id);
    }


    /**
     * +---+     +---+     +---+
     * | 1 | --> | 2 | --> | 3 |
     * +---+     +---+     +---+
     *   |                   ^
     *   |                   |
     *   v                   |
     * +---+                 |
     * | 4 | ----------------+
     * +---+
     */
    @Test
    public void test_DAGVisitor_checkCircle_1() throws Exception {
        TestAbstractDAGVisitor visitor = new TestAbstractDAGVisitor();
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("2", "2"));
        visitor.putEdge(new TestAbstractDAGNode("2", "2"), new TestAbstractDAGNode("3", "3"));
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("4", "4"));
        visitor.putEdge(new TestAbstractDAGNode("4", "4"), new TestAbstractDAGNode("3", "3"));
        Assert.assertFalse(visitor.hasCircle());
    }

    /**
     *   +-------------------+
     *   v                   |
     * +---+     +---+     +---+
     * | 1 | --> | 2 | --> | 3 |
     * +---+     +---+     +---+
     */
    @Test
    public void test_DAGVisitor_checkCircle_2() throws Exception {
        TestAbstractDAGVisitor visitor = new TestAbstractDAGVisitor();
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("2", "2"));
        visitor.putEdge(new TestAbstractDAGNode("2", "2"), new TestAbstractDAGNode("3", "3"));
        visitor.putEdge(new TestAbstractDAGNode("3", "3"), new TestAbstractDAGNode("1", "1"));
        Assert.assertTrue(visitor.hasCircle());
    }

    /**
     * +---+     +---+     +---+
     * | 1 | --> | 2 | --> | 3 |
     * +---+     +---+     +---+
     *
     *   +---------+
     *   v         |
     * +---+     +---+
     * | 4 | --> | 5 |
     * +---+     +---+
     */
    @Test
    public void test_DAGVisitor_checkCircle_3() throws Exception {
        TestAbstractDAGVisitor visitor = new TestAbstractDAGVisitor();
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("2", "2"));
        visitor.putEdge(new TestAbstractDAGNode("2", "2"), new TestAbstractDAGNode("3", "3"));
        visitor.putEdge(new TestAbstractDAGNode("4", "4"), new TestAbstractDAGNode("5", "5"));
        visitor.putEdge(new TestAbstractDAGNode("5", "5"), new TestAbstractDAGNode("4", "4"));
        Assert.assertTrue(visitor.hasCircle());
    }

    /**
     * [1]->[4]->[7][1]->[3]->[6][1]->[2]->[5]->[9]->[10][5]->[8]->[10][10]->[2]
     * +---+     +---+     +----+     +---+
     * | 4 | <-- | 1 | --> | 3  | --> | 6 |
     * +---+     +---+     +----+     +---+
     *   |         |
     *   |         |         +---------------+
     *   v         v         |               v
     * +---+     +---+     +----+          +---+
     * | 7 |     | 2 | --> | 5  | -+       | 8 |
     * +---+     +---+     +----+  |       +---+
     *             ^               |         |
     *             +---------+     |         |
     *                       |     |         |
     *           +---+     +----+  |         |
     *           | 9 | --> | 10 | <+---------+
     *           +---+     +----+  |
     *             ^               |
     *             +---------------+
     */
    @Test
    public void test_DAGVisitor_checkCircle_5() throws Exception {
        TestAbstractDAGVisitor visitor = new TestAbstractDAGVisitor();
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("2", "2"));
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("3", "3"));
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("4", "4"));
        visitor.putEdge(new TestAbstractDAGNode("2", "2"), new TestAbstractDAGNode("5", "5"));
        visitor.putEdge(new TestAbstractDAGNode("3", "3"), new TestAbstractDAGNode("6", "6"));
        visitor.putEdge(new TestAbstractDAGNode("4", "4"), new TestAbstractDAGNode("7", "7"));
        visitor.putEdge(new TestAbstractDAGNode("5", "5"), new TestAbstractDAGNode("8", "8"));
        visitor.putEdge(new TestAbstractDAGNode("5", "5"), new TestAbstractDAGNode("9", "9"));
        visitor.putEdge(new TestAbstractDAGNode("8", "8"), new TestAbstractDAGNode("10", "10"));
        visitor.putEdge(new TestAbstractDAGNode("9", "9"), new TestAbstractDAGNode("10", "10"));
        visitor.putEdge(new TestAbstractDAGNode("7", "7"), new TestAbstractDAGNode("10", "10"));
        visitor.putEdge(new TestAbstractDAGNode("10", "10"), new TestAbstractDAGNode("2", "2"));
        Assert.assertTrue(visitor.hasCircle());
    }


    @Test
    public void test_DAGVisitor_single_node() throws Throwable {
        TestAbstractDAGVisitor visitor = new TestAbstractDAGVisitor();
        visitor.putNode(new TestAbstractDAGNode("s1", "s1"));
        visitor.putNode(new TestAbstractDAGNode("s2", "s2"));
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("2", "2"));
        visitor.putEdge(new TestAbstractDAGNode("1", "1"), new TestAbstractDAGNode("3", "3"));
        List chain = visitor.getChain();
        Assert.assertEquals(4, chain.size());
    }
}
