import type { DatePicker } from 'element-ui';

import { Component, Prop, Ref, VueComponentBase } from '@/common/VueComponentBase';

import type { SharpSelectorNextDataValModel } from '../../../../models';

@Component()
export default class DateRange extends VueComponentBase {
  @Prop() readonly item: SharpSelectorNextDataValModel<number[]>;
  @Prop() readonly size: string;

  @Ref() readonly Date: DatePicker;

  editing = false;

  mounted() {
    if (!this.item.value) this.edit();
  }

  change() {
    this.editing = false;
    this.$emit('call');
  }

  async edit() {
    this.editing = true;
    await this.$nextTick();
    this.Date.focus();
  }

  blur() {
    this.editing = false;
  }
}
