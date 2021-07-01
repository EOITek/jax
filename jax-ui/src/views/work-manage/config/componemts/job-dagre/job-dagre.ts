import { Graph } from '@antv/g6';
import ResizeObserver from 'resize-observer-polyfill';
import { cloneDeep } from 'lodash-es';
import type { IG6GraphEvent } from '@antv/g6/lib/types';

import { Component, Inreactive, Prop, Ref, VueComponentBase, Watch } from '@/common/VueComponentBase';
import { $message } from '@/services';
import type { PipelineEdge, PipelineJob, PipelineModel } from '@/models';
import { truncateText } from '@/helpers';

import type { GraphSelectNodeList } from '../../interface';
import { getG6Graph } from './config/g6-config';
import { initG6Event } from './config/g6-event';
import { copyEdges, copyNodes, getGraphData, setStateAll, updateStackData } from './config/g6-utils';

@Component()
export default class JobDagre extends VueComponentBase {
  @Prop() readonly pipelineData: PipelineModel;
  @Prop() readonly isDebugModel: boolean;
  @Prop() readonly isStartDebug: boolean;
  @Prop() readonly isStreaming: boolean;

  @Ref() readonly Div: HTMLElement;
  @Ref() readonly ImageMinimap: HTMLDivElement;

  @Inreactive graph: Graph;
  @Inreactive observer: ResizeObserver;

  nodes: PipelineJob[] = [];
  edges: PipelineEdge[] = [];
  pipelineUi: any[] = [];
  selectNodeList: GraphSelectNodeList = {
    nodes: [],
    edges: [],
  };
  copyNodeList: GraphSelectNodeList = {
    nodes: [],
    edges: [],
  };

  /** 判断是否选中了元素 */
  get isSelectItem() {
    return !!(this.selectNodeList.nodes.length || this.selectNodeList.edges.length) && !this.isDebugModel;
  }

  /** 是否选中了节点 */
  get isSelectNode() {
    return !!this.selectNodeList.nodes.length && !this.isDebugModel;
  }

  /** 是否复制了节点 */
  get isCopytNode() {
    return !!this.copyNodeList.nodes.length && !this.isDebugModel;
  }

  @Watch('isStartDebug')
  changeIsStartDebug(newV) {
    if (newV) {
      this.graph.off();
      this.graph.setMode('debug');
      this.initNodeselectchange();
    } else {
      this.initGraphEvent();
    }
  }

  @Watch('isDebugModel')
  changeIsDebugModel(newV) {
    if (newV) {
      this.graph.setMode('debug');
    } else {
      this.graph.setMode('default');
    }
  }

  mounted() {
    this.graph = getG6Graph();

    this.initGraphEvent();
    this.graphRead();
    this.initObserver();
  };

  beforeDestroy() {
    this.graph.destroy();
    this.graph = null;
    this.observer.disconnect();
    this.observer = null;
  }

  /**
   * 初始化Observer
   */
  initObserver() {
    this.observer = new ResizeObserver(([entry]) => {
      this.graph.changeSize(entry.contentRect.width, entry.contentRect.height);
    });
    this.observer.observe(this.Div);
  }

  initNodeselectchange() {
    /**
     * 当 brush-select 选中的元素集合发生变化时记录选中元素
     */
    this.graph.on('nodeselectchange', ({ selectedItems }: any) => {
      selectedItems.edges ||= [];
      // 选中元素只有一个节点时打开配置区域
      if (selectedItems.nodes.length === 1) {
        const node = selectedItems.nodes[0];
        this.$emit('openJobConfigDrawer', node.getModel()?.conf?.jobName, node.get('id'));
      }

      this.selectNodeList = selectedItems;
    });
  }

  /**
   * 初始化G6事件绑定
   */
  initGraphEvent() {
    initG6Event({
      graph: this.graph,
      pipelineData: this.pipelineData,
      isDebugModel: this.isDebugModel,
      isStreaming: this.isStreaming,
    });

    this.initNodeselectchange();

    /**
     * 点击选中边
     */
    this.graph.on('edge:click', ({ item, target }: IG6GraphEvent) => {
      if (target.get('name') === 'image-straw') {
        const hasSelected = item.hasState('strawActive');
        setStateAll('edge', 'strawActive', false, this.graph);
        this.graph.setItemState(item, 'strawActive', !hasSelected);
        this.edges.forEach(edge => {
          if (edge.edgeId === item.get('id')) {
            const mockId = !hasSelected ? +new Date() + '' : null;
            edge.enableMock = !hasSelected;
            edge.mockId = mockId;
          } else {
            edge.enableMock = null;
            edge.mockId = null;
          }
        });
      } else {
        const hasSelected = item.hasState('selected');
        this.clearAllState('selected');
        Object.assign(this.selectNodeList, { edges: hasSelected ? [] : [item] });

        !hasSelected && this.graph.setItemState(item, 'selected', true);
      }
    });

    this.graph.on('node:click', ({ item, target }: IG6GraphEvent) => {
      if (target.get('name') !== 'image-debug') return;
      // 点击debug图标时
      const jobOpts = this.pipelineData.pipelineConfig.jobs.find(job => job.jobId === item.get('id')).jobOpts;
      jobOpts.enableDebug = !jobOpts.enableDebug;
      this.graph.setItemState(item, 'debugActive', jobOpts.enableDebug);
      this.graph.refreshItem(item);
    });
  }

  /**
   * G6渲染数据
   */
  graphRead() {
    this.nodes = this.pipelineData.pipelineConfig.jobs;
    this.edges = this.pipelineData.pipelineConfig.edges;
    this.pipelineUi = this.pipelineData.pipelineUi;
    this.graph.read(getGraphData(this));
  }

  resetGraph() {
    const zoomNum = this.graph.getZoom();
    this.graph.clear();
    this.graphRead();
    setStateAll('node', 'debugActive', true, this.graph);
    this.graph.zoomTo(zoomNum);
    this.emptySelectNodeList();
  }

  /**
   * 控制画布的缩放比例
   *
   * @param type 操作类型
   */
  handlerGraphZoom(type: 'zoomIn' | 'zoomOut') {
    const point = {
      x: this.graph.getWidth() / 2,
      y: this.graph.getHeight() / 2,
    };
    this.graph.zoom(type === 'zoomIn' ? 1.1 : 0.9, point);
  }

  /**
   * 删除元素
   */
  handlerDeleteNodes() {
    if (!this.isSelectItem) return;

    this.selectNodeList.edges.forEach(edge => {
      const index = this.edges.findIndex(ele => ele.edgeId === edge.get('id'));
      this.edges.splice(index, 1);
      this.graph.removeItem(edge);
    });
    this.selectNodeList.nodes.forEach(node => {
      const index = this.nodes.findIndex(ele => ele.jobId === node.get('id'));
      this.nodes.splice(index, 1);

      // 移除节点时移除节点关联线的数据
      const edges = node.get('edges');
      if (edges.length) {
        edges.forEach(edge => {
          const edgeIndex = this.edges.findIndex(ele => ele.edgeId === edge.get('id'));
          this.edges.splice(edgeIndex, 1);
        });
      }
      this.graph.removeItem(node);
    });

    this.$emit('deleteNode');
    this.emptySelectNodeList();
  }

  /**
   * 复制
   */
  handlerIsOpenCopyToggle() {
    if (!this.isSelectNode) return;
    this.copyNodeList = cloneDeep(this.selectNodeList);
    $message.success('复制成功');
  }

  /**
   * 进行粘贴操作
   */
  handlerPaste() {
    if (!this.isCopytNode) return;
    this.clearAllState('selected', false);
    const { copyNodeIdObj, copyNodeList, copyPipelineUi }
      = copyNodes(this.copyNodeList.nodes, this.pipelineData.pipelineUi, this.graph);
    const copyEdgeList = copyEdges(copyNodeIdObj, this.copyNodeList.edges, this.graph);

    this.nodes.push(...copyNodeList.map(node => node.getModel().conf as any));
    Object.assign(this.pipelineData.pipelineUi, copyPipelineUi);

    this.edges.push(...copyEdgeList.map(edge => edge.getModel().conf as any));

    Object.assign(this.selectNodeList, {
      nodes: copyNodeList,
      edges: copyEdgeList,
    });
    this.copyNodeList = cloneDeep(this.selectNodeList);
  }

  /**
   * 清除所有状态
   *
   * @param stateName 要清除的状态名
   */
  clearAllState(stateName: string, isEmptySelectNodeList = true) {
    this.graph.findAllByState('node', stateName).forEach(node => {
      this.graph.clearItemStates(node, stateName);
    });
    this.graph.findAllByState('edge', stateName).forEach(edge => {
      this.graph.clearItemStates(edge, stateName);
    });

    // 清空选中的数据
    isEmptySelectNodeList && this.emptySelectNodeList();
  }

  /**
   * 处理键盘按下事件
   */
  keyDown(e) {
    const keyCode = e.keyCode || e.which || e.charCode;
    const ctrlKey = e.ctrlKey || e.metaKey;

    // 监听ctrl + c 组合按键
    if (ctrlKey && keyCode === 67 && this.isSelectNode) {
      this.handlerIsOpenCopyToggle();
    }

    // 监听ctrl + v 组合按键
    if (ctrlKey && keyCode === 86 && this.isCopytNode) {
      this.handlerPaste();
    }

    // 监听删除快捷键(兼容mac)
    if ((ctrlKey && keyCode === 8) || keyCode === 46 || keyCode === 8) {
      this.handlerDeleteNodes();
    }
  }

  emptySelectNodeList() {
    this.selectNodeList = { nodes: [], edges: [] };
  }

  /** 一键布局 */
  layout() {
    this.graph.off('afterlayout');
    this.graph.layout();
  }

  /**
   * 更新线的动画状态
   *
   * @param runningStateData running状态下的动画信息
   */
  updateEdgeRunningState(runningStateData) {
    this.graph.getEdges().forEach(edge => {
      const conf: any = edge.getModel().conf;
      const animationGears = runningStateData.find(ele => (
        ele.job_id === edge.get('sourceNode').get('id') && +ele.slot === conf.fromSlot
      ))?.animationGears || 0;

      this.graph.setItemState(edge, 'running', this.isStartDebug ? animationGears : 0);
    });
  }

  /**
   * 处理画布操作的撤销或恢复
   *
   * @param type 操作类型
   */
  disposeUndoOrRedo(type: 'undo' | 'redo' = 'undo') {
    const isUndo = type === 'undo';
    const stack = this.graph[isUndo ? 'getUndoStack' : 'getRedoStack']();
    if (!stack?.length) return;

    const currentData = stack.pop();
    if (!currentData) return;

    const { action } = currentData;
    let data;
    if (isUndo) {
      data = currentData.data[action === 'add' ? 'after' : 'before'];
    } else {
      data = currentData.data[action === 'delete' ? 'before' : 'after'];
    }
    this.graph.pushStack(action, cloneDeep(currentData.data), isUndo ? 'redo' : 'undo');

    this.disposeRedoOrUndo(data, action, isUndo);
  }

  /**
   * 处理不同类型的画布操作
   *
   * @param data 当前的元素数据
   * @param action 活动类型
   * @param isUndo 操作类型
   */
  disposeRedoOrUndo(data, action, isUndo) {
    Object.keys(data).forEach(key => {
      const array = data[key];

      if (!array) return;
      array.forEach(model => {
        switch (action) {
          case 'update':
            if (model.itemType === 'edge') {
              model.target = model.conf.to;
              model.targetAnchor = model.conf.toSlot;
            }
            this.graph.updateItem(model.id, model, false);
            break;
          case 'add':
            this[isUndo ? 'removeItemAndData' : 'addItemAndData'](model);
            break;
          case 'delete':
            this[isUndo ? 'addItemAndData' : 'removeItemAndData'](model);
            break;
          default:
            break;
        }
      });
    });
  }

  /**
   * 删除对应的元素以及数据
   *
   * @param model 元素的数据模型
   */
  removeItemAndData(model) {
    const { id, itemType } = model;
    this.graph.removeItem(id, false);
    if (itemType === 'edge') {
      this.edges.splice(this.edges.findIndex(edge => edge.edgeId === id), 1);
    } else {
      this.nodes.splice(this.nodes.findIndex(node => node.jobId === id), 1);
    }
  }

  /**
   * 添加对应的元素以及数据
   *
   * @param model 元素的数据模型
   */
  addItemAndData(model) {
    this.graph.addItem(model.itemType, model, false);
    this[model.itemType === 'edge' ? 'edges' : 'nodes'].push(model.conf);
  }

  /**
   * 更新节点数据
   *
   * @param nodeId 节点Id
   * @param nodeConfig 节点的配置
   */
  updateNode(nodeId, nodeConfig) {
    this.graph.updateItem(nodeId, {
      'node-title': {
        attrs: { text: truncateText(this.pipelineData.pipelineUi[nodeId].display, 70, 18) },
      },
    }, false);
    this.graph.refreshItem(nodeId);
    updateStackData(this.graph.getStackData(), nodeId, nodeConfig, this.graph);
  }
};
