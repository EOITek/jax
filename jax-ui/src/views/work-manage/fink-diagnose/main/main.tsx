import type { Card } from 'element-ui';
import ResizeObserver from 'resize-observer-polyfill';

import { Diagnosis } from '@/apis';
import { Component, Prop, Ref, VueComponentBase, Inreactive } from '@/common/VueComponentBase';
import { FinkStatus, FinkDiagnoseTypeList, FinkDiagnoseTypeValue } from '@/constants';
import { $modal } from '@/services';

@Component()
export default class FinkDiagnose extends VueComponentBase {
  @Prop() readonly flinkJobId: string;
  @Prop() readonly jobType: string;
  @Prop() readonly trackUrl: string;

  @Ref() readonly Card: Card;

  FinkDiagnoseTypeList = FinkDiagnoseTypeList;
  logDiagnose = '';
  timer = null;
  observer: ResizeObserver = null;

  @Inreactive FinkStatus = FinkStatus;
  @Inreactive FinkDiagnoseTypeValue = FinkDiagnoseTypeValue;

  isLog(FinkItem) {
    return FinkItem.value === FinkDiagnoseTypeValue.log;
  }

  mounted(){
    this.fetchData();
    this.observer = new ResizeObserver(([entry]) => {
      const cardElement: HTMLElement = document.querySelector('.log-diagnose');
      cardElement.style.height = entry.contentRect.height + 'px';
    });
    this.observer.observe(this.Card[4]);
  }

  beforeDestroy() {
    this.observer.disconnect();
    this.observer = null;
    clearTimeout(this.timer);
    this.timer = null;
  }

  fetchData() {
    clearTimeout(this.timer);
    this.FinkDiagnoseTypeList.forEach(item => {
      if (this.isLog(item) && item.data.length) return;
      this.getFinkDiagnoseData(item);
    });
    this.timer = setTimeout(() => this.fetchData(), 5 * 1000);
  }

  async getFinkDiagnoseData(item) {
    if (this.isLog(item) && !item.data.length) item.loading = true;

    const { data: { entity } } = await Diagnosis.getItem(item.value, {
      flinkJobId: this.flinkJobId,
      trackingUrl: this.trackUrl,
    }).finally(() => item.loading = false);
    item.data = entity.detects;
  }

  openErrorMessageModal(errorMessage: string) {
    if(!errorMessage) return;

    $modal({
      component: {
        render() {
          return <pre class="text-wrap error-message">{errorMessage}</pre>;
        },
      },
      props: {
        title: '错误信息',
        okText: null,
        cancelText: null,
      },
    });
  }
}
