import { Component, Prop, Ref, VueComponentBase, Watch } from '@/common/VueComponentBase';
import { IconType } from '../../constants';
import { scrollToBottom } from '../../utils';

import { ObserveDebugTable, DataShowType } from './components';

@Component({
  components: { ObserveDebugTable, DataShowType },
})
export default class DataShow extends VueComponentBase {
  @Prop() readonly currentData: [] = [];

  @Ref() readonly JsonData: HTMLPreElement;

  currentDataType: number = IconType.table;
  IconType = IconType;

  get tableData() {
    return this.currentData.map((ele: any) => ele);
  }

  get jsonData() {
    return this.currentData.map((ele: any) => JSON.stringify(ele)).join('\n');
  }

  @Watch('jsonData')
  async changeTableData() {
    await this.$nextTick();
    scrollToBottom(this.JsonData);
  }

  handlerDataTypeChange(type) {
    this.currentDataType = type;
  }
}
