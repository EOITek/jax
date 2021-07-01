import { cloneDeep, isEqual, isObject } from 'lodash-es';
import { Graph } from '@antv/g6';
import type { Item } from '@antv/g6/lib/types';
import type { INode } from '@antv/g6/lib/interface/item';
import type { Point } from '@antv/g-math/lib/types';

import type { JobDagreGetGraphData } from '@/models';

import { setNewId, AnchorTypes } from './utils';
import { getEdgeItemConfig, getNodeItemConfig } from './g6-config';

/**
 * 高亮可连接节点
 *
 * @param source 起始元素
 * @param sourceAnchor 起始锚点
 */
export const highlightAnchor = function(source, sourceAnchor, graph: Graph) {
  graph.setAutoPaint(false);
  graph.getNodes().filter(node => node !== source)
    .forEach(node => {
      let children = node.get('group').get('children');
      children = children.filter(childrenNode => [
        childrenNode.get('name').search('node-anchor') !== -1,
        childrenNode.get('anchorType') === AnchorTypes.input,
        isEqual(childrenNode.get('model'), sourceAnchor.get('model')),
        !node.get('edges').find(edge => edge.get('targetAnchorIndex') === childrenNode.get('anchorPointsId')
          && edge.get('target') === node),
      ].every(x => !!x));

      children.forEach(childrenNode => {
        childrenNode.show();
        graph.setItemState(node, 'anchorHover', childrenNode.get('anchorPointsId'));
      });
    });
  graph.setAutoPaint(true);
  graph.paint();
};

/**
 * 给锚点添加移入效果并高亮相邻连接线
 *
 * @param param0
 * @param type
 */
export const anchorMouseEvent = ({ item, target }, type: 'mouseout' | 'mouseenter', graph: Graph) => {
  item.get('edges')
    .filter(edge => edge.get('sourceAnchorIndex') === target.get('anchorPointsId')
      && edge.get('source').get('id') === item.get('id'))
    .forEach(edge => {
      if (edge.get('target')?.getContainer) {
        graph.setItemState(edge, 'selected', type === 'mouseenter');
      }
    });
};

/**
 * 判断当前锚点是否可以连入新线
 *
 * @param item  当前节点
 * @param target 当前元素
 */
export const isExistCurrentEdge = function(item, target) {
  return [
    target.get('name').startsWith('node-anchor'),
    item.get('edges').find(edge => edge.get('model').target === item.get('id')
      && edge.get('model').targetAnchor === target.get('anchorPointsId')),
  ].every(x => !!x);
};

/**
 * 复制节点
 */
export const copyNodes = function(selectNodeList: Item[], pipelineUi, graph: Graph) {
  // 存放新旧node的id对应关系
  const copyNodeIdObj = {};
  // 存放复制出来的元素
  const copyNodeList: INode[] = [];
  // 存放复制出来的pipelineUi信息
  const copyPipelineUi = {};

  selectNodeList.forEach(node => {
    const model = cloneDeep(node.getModel());
    const copyNodeId = setNewId(model.name as string);
    const copyNodeConfig = {
      id: copyNodeId,
      x: model.x + 120,
      y: model.y + 50,
    };

    graph.addItem('node', {
      ...model,
      ...copyNodeConfig,
      conf: Object.assign(model.conf, copyNodeConfig, { jobId: copyNodeId }),
    });

    const copyNode = graph.findById(copyNodeId) as INode;

    copyNodeIdObj[model.id] = copyNodeId;
    graph.setItemState(copyNodeId, 'selected', true);
    copyNodeList.push(copyNode);
    copyPipelineUi[copyNodeId] = pipelineUi[model.id];
  });

  return {
    copyNodeIdObj,
    copyNodeList,
    copyPipelineUi,
  };
};

/**
 * 复制边
 *
 * @param copyNodeIdObj 新旧节点id的对应对象
 */
export const copyEdges = function(copyNodeIdObj: { [k: string]: string }, selectEdgeList: Item[], graph: Graph) {
  // 存放复制出来的边
  const copyEdgeList: Item[] = [];

  selectEdgeList.forEach(edge => {
    const model = cloneDeep(edge.getModel());
    const edgeId = setNewId();
    const from = copyNodeIdObj[model.source as string];
    const to = copyNodeIdObj[model.target as string];

    graph.addItem('edge', {
      ...model,
      id: edgeId,
      conf: Object.assign(model.conf, { edgeId, from, to }),
      source: from,
      target: to,
    });

    const copyEdge = graph.findById(edgeId);
    graph.setItemState(edgeId, 'selected', true);
    copyEdgeList.push(copyEdge);
  });

  return copyEdgeList;
};

/**
 * 获取画布所需的数据
 *
 * @param nodes 节点数据
 * @param edges 线数据
 * @param pipelineUi 画布的ui数据
 * @param isDebugModel 是否处于debug模式
 * @param isStreaming 是否为流作业
 */

export const getGraphData = function({ nodes, edges, pipelineUi, isDebugModel, isStreaming }: JobDagreGetGraphData) {
  return {
    nodes: nodes.map(item => getNodeItemConfig({
      nodeItemData: item,
      jobUi: pipelineUi[item.jobId],
      isDebugModel,
    })),
    edges: edges.map(item => getEdgeItemConfig({ edgeItemData: item, nodes, isDebugModel, isStreaming })),
  };
};

/**
 * 设置全部元素的状态
 *
 * @param type 元素的类型
 * @param stateName 状态名称
 * @param stateValue 状态值
 * @param graph 画布
 */
export const setStateAll = function(type: 'node' | 'edge', stateName: string, stateValue, graph: Graph) {
  graph.setAutoPaint(false);

  graph[type === 'node' ? 'getNodes' : 'getEdges']().forEach(node => {
    graph.setItemState(node, stateName, stateValue);
  });

  graph.setAutoPaint(true);
  graph.paint();
};

/**
 * 拖拽元素时自动扩充画布大小
 * TODO 这个逻辑暂时先去掉
 *
 * @param param0 拖拽源的视口坐标
 * @param graph
 */
export const translateGraph = function({ x, y }, graph) {
  const topLeft: Point = graph.getPointByCanvas(0, 0);
  const bottomRight: Point = graph.getPointByCanvas(graph.getWidth(), graph.getHeight());
  const translateX = x <= topLeft.x + 150 ? 150 : (x >= bottomRight.x - 150 ? -150 : 0);
  const translateY = y <= topLeft.y + 100 ? 100 : (y >= bottomRight.y - 100 ? -100 : 0);
  graph.translate(translateX, translateY);
};

/**
 * 获取线的conf配置
 *
 * @param param0
 * @param graph
 */
export const getEdgeConf = function({ id, source, sourceAnchor, target, targetAnchor }, graph) {
  const inTypesNum = graph.findById(source).getModel().inTypesNum;
  const conf = {
    edgeId: id,
    from: source,
    fromSlot: sourceAnchor - inTypesNum,
    label: null,
    to: target,
    toSlot: targetAnchor,
  };
  return conf;
};

/**
 * 更新操作栈内的节点数据
 * 由于数据层面的更改不入操作栈，所以每次更改完数据后需要手动更新对应元素的数据
 *
 * @param stackData 操作栈数据
 * @param itemId 节点id
 * @param nodeConfig 节点配置
 */
export const updateStackData = function(stackData, nodeId, nodeConfig, graph) {
  const keyList = ['combos', 'edges', 'nodes'];
  Object.keys(stackData).forEach(stackKey => {
    const stackItem = stackData[stackKey];
    const isArray = Array.isArray(stackItem);

    if (!(isArray || isObject(stackItem))) return;

    if (isArray && keyList.includes(stackKey)) {
      stackItem.forEach(item => {
        if (item.id === nodeId) item.conf = nodeConfig;
      });
    } else {
      updateStackData(stackItem, nodeId, nodeConfig, graph);
    }
  });
};
