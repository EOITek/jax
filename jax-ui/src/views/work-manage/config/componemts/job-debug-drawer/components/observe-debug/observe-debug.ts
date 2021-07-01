import { groupBy, isEqual } from 'lodash-es';

import { Component, Inreactive, Prop, Ref, VueComponentBase } from '@/common/VueComponentBase';
import { ObserveDebugList } from '@/models';

import { DataShow } from '../data-show';

@Component({
  components: { DataShow },
})
export default class ObserveDebug extends VueComponentBase {
  @Prop() readonly observeDebugList: [];

  @Ref() readonly JobListBox: HTMLUListElement;
  @Ref() readonly JobListWrap: HTMLDivElement;

  @Inreactive translateX = 0;

  currentJobId: string = null;
  observeDebugObjKeyList = [];


  get observeDebugObj() {
    const observeDebugObj = groupBy(this.observeDebugList, (ele: ObserveDebugList) => ele.job_id + ele.slot);
    const obj = {};
    Object.keys(observeDebugObj).forEach(key => {
      if (!this.observeDebugObjKeyList.find(ele => ele === key)) {
        this.observeDebugObjKeyList.push(key);
      }
      if (observeDebugObj[key].length > 100) {
        const item = observeDebugObj[key].shift();
        const index = this.observeDebugList.findIndex(ele => isEqual(item, ele));
        this.observeDebugList.splice(index, 1);
      }
    });
    this.observeDebugObjKeyList.forEach(key => {
      if (observeDebugObj[key]) obj[key] = observeDebugObj[key];
    });
    return obj;
  }

  get currentData() {
    const data = this.observeDebugObj[this.currentJobId] || [];
    return data.map(ele => ele.message);
  }

  /**
   * 处理列表平移
   *
   * @param direction 平移的方向
   */
  handlerjobListBoxMove(direction: 'l' | 'r') {
    const step = this.JobListWrap.clientWidth / 2;
    const stopTranslateX = this.JobListBox.clientWidth - this.JobListWrap.clientWidth;
    direction === 'l' ? this.translateX -= step : this.translateX += step;

    // 处理边界
    if (this.translateX <= 0) this.translateX = 0;
    if (this.translateX >= stopTranslateX) this.translateX = stopTranslateX;

    this.JobListBox.style.transform = `translateX(-${this.translateX}px)`;
  }
}
