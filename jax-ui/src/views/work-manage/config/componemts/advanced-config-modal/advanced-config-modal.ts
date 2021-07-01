import type { Form } from 'element-ui-eoi';
import { cloneDeep } from 'lodash-es';

import { Component, Prop, Ref, VueComponentBase } from '@/common/VueComponentBase';
import { ClusterApi, OptsApi } from '@/apis';
import { $message } from '@/services';

@Component()
export default class AdvancedConfigModal extends VueComponentBase {
  @Prop() readonly opts: Record<string, any>;
  @Prop() readonly isStreaming: boolean;
  @Prop() readonly defaultClusterName: string;
  @Prop() readonly isEditClusterName: boolean;

  @Ref() readonly Form: Form;

  optsDataList = [];
  allOptsDataList = [];
  fieldName = '';
  clusterName = '';
  clusterList: string[] = [];

  get keyList() {
    return this.allOptsDataList.filter(ele => !this.optsDataList.find(opts => opts.name === ele.name));
  }

  get optsFormData() {
    return Object.fromEntries(this.optsDataList.map(opts => [opts.name, opts.value]));
  }

  get fieldData() {
    return this.allOptsDataList.find(ele => ele.name === this.fieldName);
  }

  created() {
    this.clusterName = this.defaultClusterName;
    this.fetchData();
    this.fetchclusterList();
  }

  async fetchclusterList() {
    const { data: { entity } } = await ClusterApi.getAll();
    this.clusterList = entity.map(ele => ele.clusterName);
  }

  async fetchData() {
    const { data: { entity } } = await OptsApi.getFrameConfig();
    this.allOptsDataList = this.isStreaming ? entity.flinkOptsList : entity.sparkOptsList;
    const optsKeyList = Object.keys(this.opts);
    if (!optsKeyList.length) {
      return;
    }

    optsKeyList.forEach(key => {
      const obj = cloneDeep(this.allOptsDataList.find(ele => ele.name === key));
      if (obj) {
        obj.value = this.opts[key];
        this.optsDataList.push(obj);
      }
    });
  }

  addoptsDataItem() {
    if (!this.fieldData) {
      $message.warning('请选择要添加的字段');
      return;
    }
    this.optsDataList.push(cloneDeep(this.fieldData));
    this.fieldName = this.keyList[0]?.name;
  }

  async onOk() {
    await this.Form.validate();
    return { optsFormData: this.optsFormData, clusterName: this.clusterName };
  }
}
