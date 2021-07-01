import type { Select } from 'element-ui';

import { Component, Prop, Ref, VueComponentBase } from '@/common/VueComponentBase';

import type { SharpSelectorNextDataValModel, SharpSelectorNextSubOptionModel } from '../../../../models';

@Component()
export default class VSelect extends VueComponentBase {
  @Prop() readonly item: SharpSelectorNextDataValModel;
  @Prop() readonly options: SharpSelectorNextSubOptionModel[];
  @Prop() readonly size: string;

  @Ref() readonly Select: Select;

  get displayText() {
    const option = this.options.find(x => x.value === this.item.value);
    return option ? option.label : '--';
  }

  editing = false;

  mounted() {
    if (!this.item.value) this.autofocus();
  }

  visibleChange(val: boolean) {
    if (!val) this.editing = false;
  }

  change() {
    this.editing = false;
    this.$emit('call');
  }

  async autofocus() {
    this.editing = true;
    await this.$nextTick();
    this.Select.focus();
  }
}
