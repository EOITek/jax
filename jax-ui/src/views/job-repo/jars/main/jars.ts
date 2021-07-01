import { cloneDeep } from 'lodash-es';

import { JarApi } from '@/apis';
import { Component, VueComponentBase } from '@/common/VueComponentBase';
import { PaginationConfig } from '@/helpers';
import type { JarModel } from '@/models';
import { $dialog, $message, $modal } from '@/services';

import { JobRepoJarsDetail } from '../detail';

@Component()
export default class JobRepoJarsMain extends VueComponentBase {
  tableData: JarModel[] = [];
  pageConf = new PaginationConfig();
  loading = 0;

  mounted() {
    this.fetchData();
  }

  fetchData() {
    ++this.loading;
    return JarApi.getAll().then(({ data: { entity } }) => {
      this.pageConf.totalItems = entity.length;
      this.tableData = entity;
    }).finally(() => --this.loading);
  }

  upload(type) {
    $modal({
      component: JobRepoJarsDetail,
      props: {
        title: `导入${type}包`,
      },
      data: {
        model: {
          jarName: '',
          jarVersion: '',
          jarFile: '',
          jarDescription: '',
        } as JarModel,
        isNew: true,
        jarType: type === 'java' ? 'jar' : 'python',
      },
    }).then(() => {
      $message.success('导入扩展包成功');
      this.fetchData();
    });
  }

  update(row) {
    $modal({
      component: JobRepoJarsDetail,
      props: {
        title: `更新${row.jarType === 'python' ? 'python' : 'java'}包`,
      },
      data: {
        model: cloneDeep(row),
        isNew: false,
        jarType: row.jarType === 'python' ? 'python' : 'jar',
      },
    }).then(() => {
      $message.success(`更新${row.jarType === 'python' ? 'python' : 'java'}成功`);
      this.fetchData();
    });
  }

  remove(row: JarModel) {
    $dialog.confirm(
      '删除后将自动移除相关Job，请谨慎操作！',
      `确认删除jar包“${row.jarFile}”？`,
    ).then(() => (
      JarApi.delete(row.jarName)
    )).then(() => {
      $message.success('删除扩展包成功');
      this.fetchData();
    });
  }
}
