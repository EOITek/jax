import { Component, Prop, Ref, VueComponentBase, Watch } from '@/common/VueComponentBase';
import { isObject } from 'lodash-es';
import { scrollToBottom } from '../../../../utils';

@Component()
export default class ObserveDebugTable extends VueComponentBase {
  @Prop() readonly tableData: [];

  @Ref() readonly Table;

  @Watch('tableData')
  async changeTableData() {
    await this.$nextTick();
    scrollToBottom(this.Table.$el.childNodes[2]);
  }

  get tableColumns() {
    let observeDebug = {};
    this.tableData?.forEach(x => {
      if (Object.keys(x).length > Object.keys(observeDebug).length) observeDebug = x;
    });
    return observeDebug;
  }

  formatter(row, column, cellValue) {
    if (Array.isArray(cellValue)) return '<Array>';
    if (isObject(cellValue)) return '<Object>';
    return cellValue;
  }
}
