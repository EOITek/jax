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

import com.eoi.jax.api.tuple.Tuple2;
import com.eoi.jax.api.tuple.Tuple4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAGVisitor实现了遍历有向无环图的算法。 前驱节点: 父节点 后继节点: 子节点 继承类只需要实现visit方法，算法将传入当前节点及其直接的一个或多个前驱节点(父节点)的visit结果，visit方法返回一个或多个遍历结果，所以返回也是List
 * 支持基于slot的图结构，即任何节点都支持多个输入和多个输出slot，在表示图结构时，除了表达节点和节点的关系，还可以有选择性的接受节点的slot之间的对应关系
 * <p>
 * 继承类首先需要定义DAG的节点类型，DAG节点类型必须是 {@code AbstractDAGNode} 的实现类
 *
 * @param <N> 继承类需要定义的DAG的节点类型
 */
// CHECKSTYLE.OFF:
public abstract class AbstractDAGVisitor<N extends AbstractDAGNode> {
    // CHECKSTYLE.ON:

    /**
     * 保存节点间的后继关系，key为当前节点编号，value是当前节点的所有后继节点
     */
    protected Map<String, List<N>> nexts;
    /**
     * 保存节点间的前驱关系，key为当前节点编号，value是当前节点的所有前驱节点(有序) 前驱节点列表本身的顺序表达了输入的顺序 为了支持多输出，每个前驱节点的由一个Tuple表示，除了包含前驱节点对象本身，还有前驱节点的第几个slot
     */
    protected Map<String, List<Tuple2<N, Integer>>> prevs;

    /**
     * 保存所有节点编号和node
     */
    protected Map<String, N> nodes;

    /**
     * 对于已经遍历过的节点保存visit结果，复用
     */
    protected Map<String, List> cachedVisitResults;

    /**
     * 保存所有edges
     */
    protected List<Tuple4<String, String, Integer, Integer>> edges;

    public Map<String, N> getNodes() {
        return this.nodes;
    }

    public List<Tuple4<String, String, Integer, Integer>> getEdges() {
        return this.edges;
    }

    public AbstractDAGVisitor() {
        nexts = new LinkedHashMap<>();
        prevs = new LinkedHashMap<>();
        nodes = new LinkedHashMap<>();
        edges = new ArrayList<>();
        cachedVisitResults = new HashMap<>();
    }

    /**
     * 插入一条边的，按顺序增大，默认的form和to连接的都是slot0
     *
     * @param from 边的起始节点
     * @param to   边的结束节点
     */
    public void putEdge(N from, N to) {
        putEdge(from, to, -1);
    }

    /**
     * 插入一条边的，只考虑了to节点接收多个参数的情况，传入toSlot表示，连接to时对应第几个slot
     *
     * @param from   边的起始节点
     * @param to     边的结束节点
     * @param toSlot 连接to时对应第几个slot，从0开始，-1表示接入到输入节点的下一个slot
     */
    public void putEdge(N from, N to, int toSlot) {
        putEdge(from, to, 0, toSlot);
    }

    /**
     * 插入一条边，从from指向to，并且指定了from节点对应的输出slot，以及to节点的输入slot 当节点拥有多个槽位的时候，需要使用这种方式
     *
     * @param from     边的起始节点
     * @param to       边的结束节点
     * @param fromSlot 起始节点的发起slot，从0开始
     * @param toSlot   结束节点的达到slot，从0开始，-1表示接入到输入节点的下一个slot
     */
    public void putEdge(N from, N to, int fromSlot, int toSlot) {
        // 插入后继关系, 后继关系不关注顺序
        putMap(nexts, from, to, -1);
        // 此处保持to在后继关系中存在
        putMap(nexts, to, null, -1);
        // 更新前驱关系，前驱关系关注顺序
        putMap(prevs, to, Tuple2.of(from, fromSlot), toSlot);
        // 此处保持from在前驱关系中存在
        putMap(prevs, from, null, -1);

        nodes.put(from.getId(), from);
        nodes.put(to.getId(), to);
        edges.add(Tuple4.of(from.getId(), to.getId(), fromSlot, toSlot == -1 ? 0 : toSlot));
    }

    /**
     * 单独加一个孤立的节点
     */
    public void putNode(N node) {
        putMap(nexts, node, null, -1);
        putMap(prevs, node, null, -1);

        nodes.put(node.getId(), node);
    }

    private <T> void putMap(Map<String, List<T>> map, N key, T value, Integer order) {
        List<T> children = map.get(key.getId());
        if (children == null) {
            children = new ArrayList<>();
            map.put(key.getId(), children);
        }

        if (value != null) {
            if (order == -1) {
                children.add(value);
            } else {
                if (children.size() < order) {
                    // 如果children的size比order还小，说明order插入顺序不是从大到小的，这个时候需要补null
                    for (int i = 0; i < order - children.size(); i++) {
                        children.add(null);
                    }
                    children.add(order, value);
                } else if (children.size() == order) {
                    // 如果是末尾插入
                    children.add(order, value);
                } else {
                    // order < children.zie()
                    if (children.get(order) != null) {
                        children.add(order, value);
                    } else {
                        // 如果位置上是null，说明是之前补的null，直接赋值
                        children.set(order, value);
                    }
                }
            }
        }
    }

    /**
     * 为图中每个没有后继的节点，通过递归调用visit，reduce成一个Object，最终返回一个Object List
     *
     * @return Object的List
     */
    public List getChain() throws Throwable {
        if (hasCircle()) {
            throw new GraphCircleDetectedException();
        }
        List chain = new ArrayList();
        for (Map.Entry<String, List<N>> nextEntry : nexts.entrySet()) {
            List<N> children = nextEntry.getValue();
            if (children == null || children.isEmpty()) {
                // 获得这个节点的所有前驱节点
                Object visitedValue = visitNode(nextEntry.getKey());
                chain.add(visitedValue);
            }
        }
        return chain;
    }

    public boolean hasCircle() {
        Set<String> allMarked = new HashSet<>();
        Set<String> stackMarked = new HashSet<>();
        try {
            for (Map.Entry<String, N> node : nodes.entrySet()) {
                if (allMarked.contains(node.getKey())) {
                    continue;
                }
                markNode(allMarked, stackMarked, node.getKey());
            }
            return false;
        } catch (Exception ex) {
            return true;
        }
    }

    private void markNode(Set<String> allMarked, Set<String> stackMarked, String nodeId) throws Exception {
        allMarked.add(nodeId);
        if (stackMarked.contains(nodeId)) {
            throw new GraphCircleDetectedException();
        }
        stackMarked.add(nodeId);
        List<N> children = nexts.get(nodeId);
        if (children == null || children.isEmpty()) {
            stackMarked.remove(nodeId);
            return;
        }

        for (N child : children) {
            markNode(allMarked, stackMarked, child.getId());
        }
        stackMarked.remove(nodeId);
    }

    private N getNodeById(String nodeId) {
        return nodes.get(nodeId);
    }

    private List visitNode(String nodeId) throws Throwable {
        if (cachedVisitResults.containsKey(nodeId)) {
            return cachedVisitResults.get(nodeId);
        }
        List<Tuple2<N, Integer>> parents = prevs.get(nodeId);
        if (parents == null || parents.isEmpty()) {
            List r = visit(getNodeById(nodeId), null);
            cachedVisitResults.put(nodeId, r);
            return r;
        }

        List parentList = new ArrayList();
        for (int i = 0; i < parents.size(); i++) {
            Tuple2<N, Integer> parent = parents.get(i);
            List r = visitNode(parent.f0.getId());
            parentList.add(r.get(parent.f1));
        }
        List r = visit(getNodeById(nodeId), parentList);
        cachedVisitResults.put(nodeId, r);
        return r;
    }

    /**
     * 计算某个node其输出结果被后续node的引用计数 由于输出可能是多个slot，所以返回Map，Map的key是slot，value是引用计数
     *
     * @return 返回引用计数
     */
    public Map<Integer, Integer> outputRefCountOf(N node) {
        Map<Integer, Integer> outputRefCount = new HashMap<>();
        for (Map.Entry<String, List<Tuple2<N, Integer>>> n : prevs.entrySet()) {
            List<Tuple2<N, Integer>> edges = n.getValue();
            for (Tuple2<N, Integer> edge : edges) {
                if (edge.f0.getId().equals(node.getId())) {
                    outputRefCount.put(edge.f1, outputRefCount.getOrDefault(edge.f1, 0) + 1);
                }
            }
        }
        return outputRefCount;
    }

    /**
     * 继承类需要实现的方法，采用visit模式 每次访问图中的某个节点都会调用这个方法，传入当前被访问的节点对象，以及前驱节点的访问结果，需返回一个结果对象 可参考 {@link FlinkJobDAGBuilder} 相关的测试方法和测试类
     *
     * @param node    当前被访问的节点的Node对象
     * @param parents 已经遍历的前驱节点并得到结果的对象
     * @return 返回一个结果对象列表，一般来说列表只有一个元素，当有多个输出的时候，按照输出slot顺序输出
     */
    protected abstract List visit(N node, List parents) throws DAGVisitException;
}
