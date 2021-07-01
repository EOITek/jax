import { groupBy, throttle } from 'lodash-es';

import { Component, Prop, Ref, VueComponentBase } from '@/common/VueComponentBase';
import { SocketApi } from '@/helpers';
import type { JobDebugRunningLog,
  ObserveDebugList,
  PipelineModel,
  WebSocketData } from '@/models';
import { RunningLogType } from '@/models';
import { $message } from '@/services';

import { getPipelineDataNoJobViewData } from '../../main/utils';
import { RunningLog, ObserveDebug, SimulateInject } from './components';
import { getAnimationGears } from './utils';

@Component({
  components: { RunningLog, ObserveDebug, SimulateInject },
})
export default class JobDebugDrawer extends VueComponentBase {
  @Prop() readonly pipelineData: PipelineModel;
  @Prop() readonly isStartDebug: boolean;
  @Prop() readonly isStreaming: boolean;
  @Prop() readonly webUrl: string;

  @Ref() readonly ObserveDebug: ObserveDebug;

  ws: SocketApi = null;
  currentModel = 2;
  // 存放运行日志的数据列表
  runningLogList: JobDebugRunningLog[] = [];
  // 存放调试观测的数据列表
  observeDebugList: ObserveDebugList[] = [];
  modelList = [
    {
      id: 0,
      label: '模拟注入',
    },
    {
      id: 1,
      label: '调试观测',
    },
    {
      id: 2,
      label: '运行日志',
    },
  ];
  updateEdgeRunningState = throttle(() => {
    this.$emit('updateEdgeRunningState', this.runningStateData);
  }, 4500);

  get mockId() {
    return this.pipelineData.pipelineConfig.edges.find(ele => ele.mockId)?.mockId || null;
  }

  /** running状态下的动画信息 */
  get runningStateData() {
    const list = this.observeDebugList.filter(ele => +new Date() - ele.time <= 5000);
    const obj = groupBy(list, ele => ele.job_id + ele.slot);
    const runningStateData = [];
    Object.values(obj).forEach(value => {
      runningStateData.push({
        job_id: value[0].job_id,
        slot: value[0].slot,
        animationGears: getAnimationGears(value.length),
      });
    });
    return runningStateData;
  }

  created() {
    // 不是流作业时不展示模拟注入功能
    if (!this.isStreaming) this.modelList.shift();
  }

  beforeDestroy() {
    this.closeWs();
  }

  init() {
    this.runningLogList = [];
    this.observeDebugList = [];
  }

  closeWs() {
    this.ws?.close();
  }

  startDebug() {
    this.init();
    const data = { code: '1001', message: getPipelineDataNoJobViewData(this.pipelineData)};
    this.ws = new SocketApi('/ws/pipeline/ping', data, {
      onMessageCallback: (wsData: WebSocketData) => this.disposeWsData(wsData),
      closeCallback: () => {
        const errorInfo = this.isStreaming ? '调试异常,请重新开始!' : '调试结束;';
        $message[this.isStreaming ? 'error' : 'success'](errorInfo);
        this.getRunningLogList(errorInfo, RunningLogType.error);
        this.$emit('stopDebug');
      },
    });
  }

  sendMessage(data: WebSocketData) {
    this.ws?.send(data);
  }

  /**
   * 处理websocket接受的数据
   *
   * @param wsData
   */
  disposeWsData(wsData: WebSocketData) {
    switch (wsData.code) {
      case '1003':
        this.getObserveDebugList(wsData);
        break;
      case '1004':
        this.getRunningLogList(wsData.message, RunningLogType.success);
        break;
      case '9999':
        this.getRunningLogList(wsData.message, RunningLogType.error);
        break;
      case '1005':
        this.$emit('update:webUrl', wsData.message);
      default:
        break;
    }
  }

  /**
   * 得到运行日志的数据
   *
   * @param message
   * @param type
   */
  getRunningLogList(message, type: RunningLogType) {
    const length = this.runningLogList.length;
    const id = length ? this.runningLogList[length - 1].id + 1 : 0;
    const data: JobDebugRunningLog  = { type, message, id };
    const typeNumber = this.runningLogList[length - 1]?.typeNumber ?? 0;
    data.typeNumber = this.runningLogList[length - 1]?.type !== type ? typeNumber + 1 : typeNumber;
    this.runningLogList.push(data);
  }

  /**
   * 得到调试观测的数据
   *
   * @param wsData
   */
  getObserveDebugList(wsData: WebSocketData) {
    const display = this.pipelineData.pipelineUi[wsData.job_id].display;
    delete wsData.code;
    wsData.message = JSON.parse(wsData.message);
    this.observeDebugList.push({ display, time: +new Date(), ...wsData } as unknown as ObserveDebugList);
    this.ObserveDebug.currentJobId ??= wsData.job_id + wsData.slot;
    this.updateEdgeRunningState();
  }
}
