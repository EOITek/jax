import { Component, Prop, VueComponentBase } from '@/common/VueComponentBase';
import { $message } from '@/services';

import { DataShow } from '../data-show';

@Component({
  components: { DataShow },
})
export default class SimulateInject extends VueComponentBase {
  @Prop() readonly mockId: string;
  @Prop() readonly isStartDebug: boolean;

  dataStr = '';
  wsData = null;
  currentData = [];

  get dataArr() {
    return this.dataStr.split('\n');
  }

  getWsData() {
    this.wsData = [];
    // eslint-disable-next-line @typescript-eslint/prefer-for-of
    for (let i = 0; i < this.dataArr.length; i++) {
      try {
        const obj = JSON.parse(this.dataArr[i]);
        this.wsData.push(obj);
      } catch(e) {
        this.wsData = [];
        break;
      }
    }
  }

  send() {
    if (!this.isStartDebug) {
      $message.warning('请开始调试后再注入数据');
      return;
    }
    this.getWsData();
    if (!this.wsData.length) {
      $message.error('请输入合法的JSON数据');
      return;
    }
    this.wsData.forEach(ele=> {
      this.$emit('sendMessage', {code: '1002', job_id: this.mockId, message: ele});
    });
    this.currentData.push(...this.wsData);
    if (this.currentData.length > 100) {
      this.currentData.shift();
    }
    $message.success('发送成功');
  }
}
