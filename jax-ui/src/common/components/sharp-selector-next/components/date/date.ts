import { format } from 'date-fns';
import { cloneDeep } from 'lodash-es';

import { Component, Prop, Ref, VueComponentBase } from '@/common/VueComponentBase';

import type { SharpSelectorNextDataValModel, SharpSelectorNextSubOptionModel } from '../../../../models';

@Component()
export default class VDate extends VueComponentBase {
  @Prop() readonly item: SharpSelectorNextDataValModel<{ range: string; timestamp: number }>;
  @Prop() readonly options: SharpSelectorNextSubOptionModel[];
  @Prop() readonly size: string;

  @Ref() readonly Input: HTMLInputElement;

  visible = false;
  cacheValue = {
    range: '', // '>' '=' '<'
    timestamp: null,
  }; // 缓存选中值

  get displayText() {
    if (this.item.value.timestamp) {
      return this.item.value.range + format(this.item.value.timestamp, 'yyyy-MM-dd HH:mm:ss');
    }
    return '';
  }

  created() {
    if (!this.item.value) {
      this.item.value = {
        range: '=',
        timestamp: null,
      };
    }
  }

  mounted() {
    if (!this.item.value.timestamp) {
      this.edit();
    } else {
      this.cacheValue = cloneDeep(this.item.value);
    }
  }

  edit() {
    this.Input.focus();
    this.visible = true;
  }

  save() {
    this.cacheValue = cloneDeep(this.item.value);
    this.visible = false;
    this.$emit('call');
  }

  cancel() {
    if (this.cacheValue.timestamp) {
      this.item.value = cloneDeep(this.cacheValue);
    }
    this.visible = false;
  }
}
