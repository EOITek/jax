import { Component, Prop, Ref, VueComponentBase, Watch } from '@/common/VueComponentBase';
import { VueAceEditor } from '@/components';

@Component()
export default class InputJson extends VueComponentBase {
  @Prop({ type: [Array, Object, String] }) readonly value: Record<string, unknown> | any[] | string;
  @Prop({ default: false }) readonly isReadonly: boolean;
  @Prop() readonly options: Record<string, unknown>;
  @Prop() height = '100%';

  @Ref() readonly aceEditor: VueAceEditor;

  tempJson = null;

  get jsonStr() {
    return this.value && JSON.stringify(this.value, null, 2);
  }

  get jsonValue() {
    return this.tempJson ?? this.jsonStr;
  }

  @Watch('jsonStr', { immediate: true })
  jsonStrChange(newV) {
    this.tempJson = newV;
  }

  onBlur() {
    try {
      this.$emit('input', this.tempJson ? JSON.parse(this.tempJson) : '');
      this.tempJson = null;
    } catch {
      this.aceEditor.focus();
    }
  }
}
