import { Component, Prop, Ref, VueComponentBase, Watch } from '@/common/VueComponentBase';

import { getElTextWidth } from '../../../../helpers';
import type { SharpSelectorNextDataValModel } from '../../../../models';

@Component()
export default class VInput extends VueComponentBase {
  @Prop() readonly item: SharpSelectorNextDataValModel<string>;

  @Ref() readonly Input: HTMLInputElement;

  editing = false;

  @Watch('item.value')
  valueChange(val: string) {
    this.Input.style.width = `${getElTextWidth(val, getComputedStyle(this.Input).font) + 10}px`;
  }

  mounted() {
    if (!this.item.value) this.focus();

    this.Input.style.width = `${getElTextWidth(this.item.value, getComputedStyle(this.Input).font) + 10}px`;
  }

  focus() {
    this.editing = true;
    this.Input.focus();
  }

  blur() {
    this.Input.blur();
    this.editing = false;
    this.$emit('call');
  }
}
