import { cloneDeep } from 'lodash-es';

import { Component, Prop, VueComponentBase, Watch } from '@/common/VueComponentBase';
import type { PipelineParameterModel } from '@/models';

import { disposeParameters } from '../../utils';
import { MapList, InputString } from './components';

@Component({
  name: 'JobParameter',
  components: { MapList, InputString },
})
export default class JobParameter extends VueComponentBase {
  @Prop() readonly parameter: PipelineParameterModel;
  @Prop() readonly prevProp: string = '';
  @Prop() readonly formProp: string = '';
  @Prop() readonly isDebugModel: boolean;
  @Prop() readonly label: string;
  @Prop() readonly isShowLabelInfo: boolean = true;

  isSpread = false;

  get parameterDesc() {
    return [
      `字段名称：${this.parameter.label}`,
      `name: ${this.parameter.name}`,
      `字段类型：${this.parameter.type[0]}`,
      `默认值：${this.parameter.defaultValue}`,
      `是否必填：${!this.parameter.optional ? '是' : '否'}`,
      `描述：${this.parameter.description}`,
    ].join('\n');
  }

  get prop() {
    if (this.formProp) {
      return `${this.parameter.prevProp}${this.parameter.prevProp ? '.' : ''}${this.formProp}`;
    } else {
      return this.parameter.prevProp;
    }
  }

  get isList() {
    return this.parameter.type[0] === 'LIST';
  }

  get isObject() {
    return this.parameter.type[0] === 'OBJECT';
  }

  get isMap() {
    return this.parameter.type[0] === 'MAP';
  }

  get isNumber() {
    return ['FLOAT','DOUBLE','INT','LONG'].includes(this.parameter.type[0]);
  }

  get isComplexType() {
    return this.isList || this.isObject || this.isMap;
  }

  get parameterLabel() {
    return (this.isComplexType && this.label) || this.parameter.label;
  }

  @Watch('prevProp', {
    immediate: true,
  })
  changePrevProp(newV) {
    this.parameter.prevProp = newV;
    this.parameter.formProp = this.formProp;
  }

  handlerAddListItem() {
    const { viewDataList, prop, listParameter, tier } = this.parameter;
    const copyListParameter = cloneDeep(viewDataList[viewDataList.length - 1] || listParameter);
    copyListParameter.prop = `${prop}[${viewDataList.length}]`;
    switch (copyListParameter.type[0]) {
      case 'OBJECT':
        copyListParameter.tier ||= tier + 1;
        copyListParameter.objectParameters =
          disposeParameters(copyListParameter.objectParameters, {}, copyListParameter.prop, copyListParameter.tier);
        break;
      case 'MAP':
        copyListParameter.viewDataMap =
          Object.entries(copyListParameter.value || {}).map(x => ({ key: x[0], value: x[1] }));
        break;
      default:
        copyListParameter.value = null;
        break;
    }
    this.parameter.viewDataList.push(copyListParameter);
  }

  handlerSubListItem(index) {
    this.parameter.viewDataList.splice(index, 1);
  }
}
