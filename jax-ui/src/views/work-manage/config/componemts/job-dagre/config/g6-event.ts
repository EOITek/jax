import Vue from 'vue';
import { Graph } from '@antv/g6';
import { cloneDeep } from 'lodash-es';
import type { IEdge } from '@antv/g6/lib/interface/item';
import type { IG6GraphEvent } from '@antv/g6/lib/types';

import { parseJsonSafe } from '@/helpers';
import type { JobDagreInitG6Event, JobModel, PipelineJob } from '@/models';

import { clearNodeId, getNodeItemConfig, targetNodeId, edgeConfig } from './g6-config';
import { anchorMouseEvent, getEdgeConf, highlightAnchor, isExistCurrentEdge } from './g6-utils';
import { getNodeName, setNewId, AnchorTypes } from './utils';

// 正在操作的当前边
export let currentEdge: IEdge | null = null;

export const initCurrentEdge = function() {
  currentEdge = null;
};

// 线移动时的初始信息, 用来在空白处放开时复位线
let edgeDragstartTargetId = null;

/**
 * 隐藏所有锚点
 */
export const hideAnchor = function(graph: Graph) {
  graph.setAutoPaint(false);
  graph.getNodes().forEach(node => {
    node.get('group').get('children').forEach(childrenNode => {
      if (childrenNode.get('name').search('node-anchor') !== -1) childrenNode.hide();
    });
  });
  graph.setAutoPaint(true);
  graph.paint();
};

/**
 * 清除拖拽产生的副作用
 */
export const clearDragEffect = function(graph: Graph) {
  hideAnchor(graph);

  if (!currentEdge) return;

  // 如果起始点存在的话把线进行复位
  if (edgeDragstartTargetId) {
    graph.updateItem(currentEdge, {
      target: edgeDragstartTargetId,
    }, false);
    edgeDragstartTargetId = null;
  }

  currentEdge = null;
};

export const initG6Event = function({ graph, pipelineData, isDebugModel }: JobDagreInitG6Event) {
  const { edges, jobs: nodes } = pipelineData.pipelineConfig;
  const pipelineUi = pipelineData.pipelineUi;
  initCurrentEdge();
  /**
   * 布局完成后更新所有节点的位置
   */
  graph.on('afterlayout', () => {
    graph.setAutoPaint(false);
    graph.getNodes().forEach(node => {
      const { x, y } = pipelineUi[node.get('id')];
      graph.updateItem(node, {
        x,
        y,
      }, false);
    });
    graph.setAutoPaint(true);
    graph.paint();
  });

  /**
   * 鼠标移入的时候显示可以操作的锚点
   */
  graph.on('mouseenter', ({ item, target }: IG6GraphEvent) => {
    if (!item || item.get('type') !== 'node') return;
    const { cfg: { children } } = item.getContainer();

    const anchorNodes = children.filter(node => node.get('name').startsWith('node-anchor')
      && node.get('anchorType') === AnchorTypes.output);


    // 当前节点不是连接结束节点时显示输出锚点
    if (item.get('id') === targetNodeId) return;
    anchorNodes.forEach(node => node.show());
    graph.paint();

    // 移入锚点高亮关联线
    if (target.get('name').startsWith('node-anchor')) {
      anchorMouseEvent({ item, target }, 'mouseenter', graph);
    }
    graph.setItemState(item, 'hover', true);
  });

  /**
   * 鼠标移出隐藏所有锚点
   */
  graph.on('mouseout', ({ item, target }: IG6GraphEvent) => {
    if (!item || item.get('type') !== 'node') return;

    const { cfg: { children } } = item.getContainer();
    graph.setItemState(item, 'hover', false);

    // 移出锚点时取消关联线高亮
    if (target.get('name').startsWith('node-anchor')) {
      anchorMouseEvent({ item, target }, 'mouseout', graph);
    }

    children.forEach(node => {
      if (node.get('name').search('node-anchor') !== -1) node.hide();
    });
  });

  /**
   * 开始连线时显示所有可连接的点
   */
  graph.on('node:dragstart', ({ item, target }: IG6GraphEvent) => highlightAnchor(item, target, graph));

  /**
   * 拖拽线条在锚点上释放时自动吸附到对应节点，并清除连接的副作用
   */
  graph.on('node:drop', (e: IG6GraphEvent) => {
    const { target, item } = e;
    if (isExistCurrentEdge(item, target) || !target.get('name').startsWith('node-anchor')) {
      clearDragEffect(graph);
      return;
    }
    edgeDragstartTargetId = null;
    const targetId = item.get('id');
    const targetAnchor = target.get('anchorPointsId');
    const edgeConfData = { to: targetId, toSlot: targetAnchor };
    if (currentEdge) {
      const edgeConf: any = {
        target: targetId,
        targetAnchor,
        conf: Object.assign(cloneDeep(currentEdge.getModel().conf), edgeConfData),
      };
      graph.updateItem(currentEdge, edgeConf);
      // 更新对应线条的数据
      const currentEdgeData = edges.find(edge => edge.edgeId === currentEdge.getModel().id);
      Object.assign(currentEdgeData, edgeConfData);
    }
    clearDragEffect(graph);
  });

  /**
   * 开始拖拽线时高亮可连接节点
   */
  graph.on('dragstart', ({ item, x, y }) => {
    if (item?.get('type') !== 'edge') return;
    edgeDragstartTargetId ||= item.getModel().target;
    currentEdge = item as IEdge;
    graph.updateItem(item, {
      target: { x, y },
    }, false);

    const source = item.get('source');
    const sourceAnchor = source.get('group').get('children').find(node => (
      node.get('anchorPointsId') === item.get('sourceAnchorIndex')
    ));

    highlightAnchor(source, sourceAnchor, graph);
  });

  /**
   * 监听线的拖拽，实时更新线的结束点位置
   */
  graph.on('drag', ({ item, x, y }: IG6GraphEvent) => {
    if (item?.get('type') !== 'edge') return;

    graph.updateItem(item, {
      target: { x, y },
    }, false);
  });

  /**
   * 连接线绘制后的回调
   */
  graph.on('aftercreateedge', ({ edge }: any) => {
    // 置空起始结束点信息
    clearNodeId();

    const { sourceAnchor, targetAnchor, id } = edgeConfig;
    const { source, target } = edge.get('model');

    const conf = getEdgeConf({ sourceAnchor, targetAnchor, id, source, target }, graph);

    edges.push(conf);

    graph.updateItem(edge, { id, conf, sourceAnchor, targetAnchor }, false);
    edge.set('id', id);
    // 手动出栈
    graph.getUndoStack().pop();
    // 手动推入操作栈
    graph.pushStack('add', {
      before: {},
      after: { edges: [cloneDeep(edge.getModel())] },
    });

    clearDragEffect(graph);
  });

  /**
   * 在线上和画布处释放是让线复原
   */
  graph.on('drop', ({ item }) => {
    if (['edge', undefined].includes(item?.get('type'))) {
      clearDragEffect(graph);
    }
  });

  /**
   * 监听元素在画布上完成拖拽事件
   */
  graph.on('canvas:drop', (e: IG6GraphEvent) => {
    const { dataTransfer } = (e.originalEvent as DragEvent);
    if (!dataTransfer) return;
    const job = parseJsonSafe(dataTransfer.getData('x-json/job-model')) as JobModel;

    if (!job) return;

    const { jobName, jobRole, iconUrl, hasDoc, docUrl, jobConfig,  jobMeta: { inTypes, outTypes, jobInfo: { display, description } } } = job;
    const jobId = setNewId(getNodeName(jobName));
    const config: PipelineJob = {
      jobId,
      jobName,
      jobConfig: jobConfig || {},
      jobViewData: { inTypes, outTypes, jobRole, iconUrl },
      jobOpts: null,
    };
    nodes.push(config);

    Vue.set(pipelineUi, jobId, { display, description, x: e.x - 130, y: e.y - 30, hasDoc, docUrl });

    graph.addItem('node', {
      ...getNodeItemConfig({
        nodeItemData: config,
        jobUi: pipelineUi[jobId],
        isDebugModel,
      }),
    });
  });
};
