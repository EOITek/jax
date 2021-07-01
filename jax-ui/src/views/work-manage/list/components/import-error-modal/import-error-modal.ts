import { Component, Prop, VueComponentBase } from '@/common/VueComponentBase';
import { PipelineImport } from '@/models';

@Component()
export default class ImportErrorModal extends VueComponentBase {
  @Prop() readonly entity: PipelineImport[];
}
