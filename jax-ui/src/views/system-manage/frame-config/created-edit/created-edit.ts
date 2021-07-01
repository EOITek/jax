import type { Form } from 'element-ui';
import { cloneDeep } from 'lodash-es';

import { OptsApi } from '@/apis';
import { Component, Inreactive, Prop, Ref, VueComponentBase } from '@/common/VueComponentBase';
import { FrameTypeList, FrameTypeValue } from '@/constants';
import type { AllOptsList } from '@/models';
import { $loading, $message } from '@/services';

import { CeatedEditConfig } from './components';

@Component({
  components: { CeatedEditConfig },
})
export default class FrameConfigCreatedEdit extends VueComponentBase {
  @Prop({ type: String }) readonly name: string;
  @Prop({ type: String }) readonly type: string;
  @Prop({ type: Number }) readonly view: number;
  @Prop({ type: Number }) readonly clone: number;

  @Ref() readonly Form: Form;
  @Ref() readonly CeatedEditConfig: CeatedEditConfig;

  frameData: any = {
    optsName: '',
    optsType: '',
    version: '',
    optsList: [],
  };
  frameConfigList: AllOptsList = null;

  @Inreactive readonly FrameTypeList = FrameTypeList;

  created() {
    if (this.clone || !this.name) this.getFrameConfig();
    if (this.name) this.fetchData();
  }

  async getFrameConfig() {
    const { data: { entity } } = await OptsApi.getFrameConfig();
    this.frameConfigList = entity;
  }

  async fetchData() {
    const { data: { entity } } = await OptsApi.getItem({
      optsName: this.name,
      optsType: this.type,
    });
    if (this.clone) entity.optsName = '';
    this.frameData = entity;
  }

  get actionType() {
    if (this.name) {
      if (this.clone) return '克隆';
      if (this.view) return '查看';
      return '编辑';
    } else {
      return '新建';
    }
  }

  optsTypeChange(value: string) {
    this.frameData.optsList = value === FrameTypeValue.flink
      ? this.frameConfigList.flinkOptsList
      : this.frameConfigList.sparkOptsList;
  }

  async save() {
    await this.Form.validate();
    await this.CeatedEditConfig.validate();

    const frameData = cloneDeep(this.frameData);
    frameData.optsList.forEach(ele => {
      if (ele.type === 'LIST' && !ele.value) ele.value = [];
    });

    $loading.increase();
    const Api = this.name && !this.clone ? OptsApi.update : OptsApi.created;
    const { data: { retMsg } } = await Api(frameData).finally($loading.decrease);
    $message.success(retMsg);
    this.$router.push({ name: 'frameConfig' });
  }
}
