import G6 from '@antv/g6';
import type { IG6GraphEvent } from '@antv/g6/lib/types';

import { $message } from '@/services';
import type { JobDagreGetEdgeItemConfig, JobDagreGetNodeItemConfig } from '@/models';

import { getNodeName, setNewId, AnchorTypes } from './utils';
import { isExistCurrentEdge } from './g6-utils';
import './registerNode';
import './registerEdge';

// 记录连线的起始节点id
export let sourceNodeId: string = null;
// 记录连线的结束节点id
export let targetNodeId: string = null;

// 记录连线的起始锚点和结束锚点，防止连线时自动逼近导致连接点不固定
export let edgeConfig = {
  targetAnchor: 0,
  sourceAnchor: 0,
  id: setNewId('edge'),
  itemType: 'edge',
};

export let Minimap = null;

/**
 * 初始化所有值
 */
export const initG6ConfigValue = function() {
  sourceNodeId = null;
  targetNodeId = null;
  edgeConfig = {
    targetAnchor: 0,
    sourceAnchor: 0,
    id: setNewId('edge'),
    itemType: 'edge',
  };
  Minimap = null;
};

/**
 * 获取ImageMinimap 配置
 */
export const getImageMinimap = () => {
  Minimap = new G6.Minimap({
    width: 200,
    container: document.getElementById('ImageMinimap') as HTMLDivElement,
  });
};

/**
 * 获取创建边的交互操作配置项
 */
const getCreateEdgeBehaviorConfig = function() {
  return {
    trigger: 'drag',
    edgeConfig,
    shouldBegin({ target, item }) {
      // 只有点击输出锚点时才可以连线
      if (!target.get('name').startsWith('node-anchor')
        || target.cfg.anchorType !== AnchorTypes.output) return;

      sourceNodeId = item.get('id');
      edgeConfig.sourceAnchor = target.get('anchorPointsId');
      edgeConfig.id = setNewId('edge');

      return true;
    },
    shouldEnd({ target, item }) {
      if (!target || !item) return;

      // 只有连接到输入锚点时才可以完成创建连接
      if (target.cfg.anchorType !== AnchorTypes.input) return;

      // 一个输入锚点只允许创建一条连线
      if (isExistCurrentEdge(item, target)) {
        $message.warning('一个锚点只允许有一个输入');
        return;
      };

      targetNodeId = item.get('id');
      edgeConfig.targetAnchor = target.get('anchorPointsId');

      return true;
    },
  };
};

/**
 * 获取画布的操作模式
 *
 * @param modelTypeList 操作模式列表，不传时获取全部
 */
const getGraphModel = function(modelTypeList?: string[]) {
  const allModel: any = [
    'drag-canvas',
    {
      type: 'brush-select',
    },
    {
      type: 'click-select',
      shouldBegin({ target }: IG6GraphEvent) {
        const nameList = ['node-rect-box', 'image-debug'];
        return !nameList.includes(target.get('name'));
      },
      shouldUpdate({ item }: IG6GraphEvent) {
        return !item.hasState('selected');
      },
    },
    {
      type: 'drag-node',
      shouldBegin({ target, item }: IG6GraphEvent) {
        if (item.get('model').anchorPoints.length) {
          return !target.cfg.name.startsWith('node-anchor');
        }
        return true;
      },
    },
    {
      type: 'create-edge',
      ...getCreateEdgeBehaviorConfig(),
    },
  ];
  return modelTypeList ? allModel.filter(model => modelTypeList.includes(model.type || model)) : allModel;
};

/**
 * 清除保存的开始结束节点Id
 */
export const clearNodeId = function() {
  targetNodeId = null;
  sourceNodeId = null;
};

/**
 * 获取节点的配置项
 *
 * @param nodeItemData 节点数据
 * @param jobUi 节点的UI信息
 */
export const getNodeItemConfig = function({ nodeItemData, jobUi, isDebugModel }: JobDagreGetNodeItemConfig ) {
  const { inTypes, outTypes } = nodeItemData.jobViewData;
  const { x, y, display } = jobUi;
  return {
    id: nodeItemData.jobId,
    name: getNodeName(nodeItemData.jobName),
    type: 'job',
    x,
    y,
    conf: nodeItemData,
    itemType: 'node',
    display,
    inTypesNum: inTypes.length,
    isDebugModel,
    anchorPointsData: [
      ...inTypes.map((ele, index) => ({ x: (index + 1) / (inTypes.length + 1), y: 0, model: ele })),
      ...outTypes.map((ele, index) => ({ x: (index + 1) / (outTypes.length + 1), y: 1, model: ele })),
    ],
    anchorPoints: [
      ...inTypes.map((ele, index) => [(index + 1) / (inTypes.length + 1), 0]),
      ...outTypes.map((ele, index) => [(index + 1) / (outTypes.length + 1), 1]),
    ],
  };
};

/**
 * 获取连接线的配置项
 *
 * @param edgeItemData 连接线的数据
 */
export const getEdgeItemConfig = function({ edgeItemData, nodes, isDebugModel, isStreaming }: JobDagreGetEdgeItemConfig) {
  const { from, to, edgeId, label, fromSlot, toSlot } = edgeItemData;
  const inTypesNum = nodes.find(node => node.jobId === from).jobViewData.inTypes.length;
  return {
    id: edgeId || setNewId(),
    type: 'circle-running',
    conf: edgeItemData,
    itemType: 'edge',
    isDebugModel,
    source: from,
    target: to,
    label,
    isStreaming,
    sourceAnchor: fromSlot ? fromSlot + inTypesNum : inTypesNum,
    targetAnchor: toSlot || 0,
    labelCfg: {
      autoRotate: true,
      refY: 10,
    },
  };
};

/**
 * 获取g6配置
 */
export const getG6Graph = function() {
  initG6ConfigValue();
  const JobDagre = document.getElementById('JobDagre') as HTMLDivElement;
  getImageMinimap();
  return new G6.Graph({
    container: JobDagre,
    width: JobDagre.scrollWidth,
    height: JobDagre.scrollHeight,
    minZoom: 0.1,
    fitCenter: true,
    enabledStack: true,
    layout: {
      type: 'dagre',
      preventOverlap: true, // 防止节点重叠
      rankdir: 'TB',
      nodesep: 80,
    },
    defaultEdge: {
      type: 'circle-running',
      style: {
        stroke: '#636363',
        lineWidth: 1,
        lineAppendWidth: 5,
        endArrow: {
          path: 'M 0,0 L 8,4 L 8,-4 Z',
          fill: '#636363',
        },
        cursor: 'move',
      },
    },
    modes: {
      default: getGraphModel(),
      debug: getGraphModel(['click-select', 'drag-canvas']),
    },
    plugins: [Minimap],
  });
};
