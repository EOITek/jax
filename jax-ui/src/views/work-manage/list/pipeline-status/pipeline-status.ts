import { Component, Inreactive, Prop, VueComponentBase } from '@/common/VueComponentBase';
import { PipelineStatus } from '@/constants';

@Component()
export default class PipelineStatusComponent extends VueComponentBase {
  @Prop() value: string;

  @Inreactive PipelineStatus = PipelineStatus;
}
