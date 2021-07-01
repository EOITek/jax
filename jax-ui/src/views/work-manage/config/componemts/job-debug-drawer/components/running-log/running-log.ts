import { Component, Prop, Ref, VueComponentBase, Watch } from '@/common/VueComponentBase';
import { JobDebugRunningLog, RunningLogType } from '@/models';
import { groupBy } from 'lodash-es';
import { scrollToBottom } from '../../utils';

@Component()
export default class RunningLog extends VueComponentBase {
  @Prop() readonly runningLogList: JobDebugRunningLog[];

  @Ref() readonly logBox: HTMLDivElement;

  RunningLogType = RunningLogType;

  get runningLogObj() {
    const runningLogObj = groupBy(this.runningLogList, item => `${item.type}-${item.typeNumber}`);
    const obj = {};
    Object.keys(runningLogObj).forEach(key => {
      obj[key] = {
        message: runningLogObj[key].map(ele => ele.message).join('\n'),
        type: runningLogObj[key][0].type,
      };
    });
    return obj;
  }

  @Watch('runningLogList')
  async changeRunningLogList() {
    await this.$nextTick();
    scrollToBottom(this.logBox);
  }
}
