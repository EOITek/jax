import { Table } from 'element-ui';

import { PipelineApi } from '@/apis';
import { Component, Prop, Ref, VueComponentBase } from '@/common/VueComponentBase';
import { PaginationConfig } from '@/helpers';
import type { PipelineLogModel, PipelineModel } from '@/models';

@Component()
export default class JobRepoJarsDetail extends VueComponentBase {
  @Prop() readonly model: PipelineModel;
  @Prop() readonly isHistory = false;

  @Ref() Table: Table;

  data: PipelineLogModel[] = [];
  pageConf = new PaginationConfig();
  loading = 0;
  currentRow: PipelineLogModel = null;

  async mounted() {
    const { data: { entity } } = await PipelineApi.getLog(this.model.pipelineName);
    entity.sort((a, b) => b.createTime - a.createTime);
    this.data = entity;
    this.pageConf.totalItems = entity.length;
    await this.$nextTick();
    this.selectFirstRow();
  }

  selectFirstRow() {
    this.Table.setCurrentRow(this.tableData[0]);
  }

  get tableData() {
    return this.pageConf.filterData(this.data);
  }
}
