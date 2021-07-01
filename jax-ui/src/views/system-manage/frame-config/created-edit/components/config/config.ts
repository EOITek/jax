import type { Form } from 'element-ui';

import { Component, Prop, Ref, VueComponentBase } from '@/common/VueComponentBase';
import type { OptsListItem } from '@/models';

@Component()
export default class CeatedEditConfig extends VueComponentBase {
  @Prop() readonly configData: OptsListItem[];
  @Prop({ default: false }) readonly disabled: boolean;

  @Ref() readonly Form: Form;

  async validate() {
    await this.Form.validate();
  }

  get configFormData() {
    return Object.fromEntries(this.configData.map(x => [x.name, x.value]));
  }
}
