import { Component, Prop, VueComponentBase } from '@/common/VueComponentBase';
import type { PipelineModel } from '@/models';
import { $message } from '@/services';

@Component()
export default class JsonModal extends VueComponentBase {
  @Prop({ type: [String, Object] }) readonly entity: PipelineModel | string;
  @Prop() readonly options: Record<string, unknown>;
  @Prop({ default: false }) readonly isReadonly: boolean;
  @Prop() readonly height = '500px';
  @Prop() readonly lang = 'json';

  json = '';

  get isString() {
    return typeof this.entity === 'string';
  }

  created() {
    this.json = this.isString ? this.entity as string : JSON.stringify(this.entity, null, 2);
  }

  onOk() {
    try {
      return  this.isString ? this.json : JSON.parse(this.json);
    } catch(e) {
      $message.error('非法JSON，请检查后重新提交');
      return Promise.reject();
    }
  }
}
