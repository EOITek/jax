import { Component, Prop, VueComponentBase } from '@/common/VueComponentBase';
import type { PipelineViewDataListModel } from '@/models';

@Component()
export default class MapList extends VueComponentBase {
  @Prop() readonly viewDataMap: PipelineViewDataListModel[];
}
