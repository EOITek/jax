import { mergeWith } from 'lodash-es';

import { ClusterApi } from '@/apis';
import { Component, VueComponentBase } from '@/common/VueComponentBase';
import { ClusterModel } from '@/models';
import { $loading, $modal } from '@/services';

import { ClusterCard, CreatedEditModal } from '../components';

@Component({
  components: { ClusterCard },
})
export default class ClusterManageMain extends VueComponentBase {
  clusterList: ClusterModel[] = [];

  created() {
    this.fetchData();
  }

  async fetchData() {
    $loading.increase();
    const { data: { entity } } = await ClusterApi.getAll().finally($loading.decrease);
    this.clusterList = entity;
    this.getAllResource();
  }

  async getAllResource() {
    const { data: { entity } } = await ClusterApi.getAll(true);
    this.clusterList = mergeWith(entity, this.clusterList, (objValue, srcValue, key) => {
      if (key === 'resourcePool') return objValue;
    });
  }

  openCreatedEditModal() {
    $modal({
      props: {
        title: '新建集群',
        width: 579,
      },
      component: CreatedEditModal,
    }).then(() => {
      this.fetchData();
    });
  }
}
