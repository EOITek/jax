import { JobApi } from '@/apis';
import { Component, VueComponentBase } from '@/common/VueComponentBase';
import { PaginationConfig } from '@/helpers';
import { JobModel } from '@/models';
import { $modal } from '@/services';

import { MarkdownHtml } from './markdown-html';

@Component()
export default class JobRepoList extends VueComponentBase {
  data: JobModel[] = [];
  pageConf = new PaginationConfig(100);
  loading = 0;
  search = '';

  mounted() {
    this.fetchData();
  }

  onExampleClick(row) {
    ++this.loading;
    JobApi.getMartDown(row.docUrl).then(({ data }) => {
      --this.loading;
      $modal({
        component: MarkdownHtml,
        props: {
          title: '示例文档',
          width: '1000px',
          okText: null,
          cancelText: null,
        },
        data: {
          data,
        },
      });
    });
  }

  fetchData() {
    ++this.loading;

    JobApi.getAll().then(({ data: { entity } }) => {
      entity.sort((a, b) => a.jobMeta.jobInfo.name.localeCompare(b.jobMeta.jobInfo.name));
      if(this.search) {
        this.data = entity.filter(x => {
          const { description, display, name, type } = x.jobMeta.jobInfo;
          return [description, display, name, type].some(dataItem => dataItem.indexOf(this.search) >= 0);
        });
      } else {
        this.data = entity;
      }
      this.pageConf.totalItems = this.data.length;
    }).finally(() => {
      --this.loading;
    });
  }
  get tableData() {
    return this.pageConf.filterData(this.data);
  }
}
