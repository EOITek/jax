import { ClusterApi } from '@/apis';
import { Component, Inreactive, Prop, VueComponentBase } from '@/common/VueComponentBase';
import { ClusterTypeValueObj, ClusterTypeValue } from '@/constants';
import type { ClusterModel, ResourcePoolFlink, ResourcePoolYarn, ResourcePoolSpark } from '@/models';
import { $loading, $message, $modal } from '@/services';

import { CreatedEditModal } from '../created-edit-modal';

const disposeFlinkUtilization = function(data: ResourcePoolFlink, loading = true) {
  return [
    {
      label: 'SLOT',
      used: data?.slotsTotal - data?.slotsAvailable,
      total: data?.slotsTotal,
      loading,
    },
  ];
};

const disposeYarnUtilization = function(data: ResourcePoolYarn, loading = true) {
  return [
    {
      label: 'CPU',
      used: data?.totalVirtualCores - data?.availableVirtualCores,
      total: data?.totalVirtualCores,
      loading,
    },
    {
      label: 'Memory',
      used: data?.totalMB - data?.availableMB,
      total: data?.totalMB,
      loading,
    },
  ];
};

const disposeSparkUtilization = function(data: ResourcePoolSpark, loading = true) {
  return [
    {
      label: 'CPU',
      used: data?.coresUsed,
      total: data?.cores,
      loading,
    },
    {
      label: 'Memory',
      used: data?.memoryUsed,
      total: data?.memory,
      loading,
    },
  ];
};

@Component()
export default class ClusterCard extends VueComponentBase {
  @Prop() readonly clusterDetail: ClusterModel;

  @Inreactive ClusterTypeValue = ClusterTypeValue;

  customColors = [
    {color: '#67C23A', percentage: 25},
    {color: '#409eff', percentage: 60},
    {color: '#e6a23c', percentage: 80},
    {color: '#F56C6C', percentage: 100},
  ];

  get utilizationList() {
    const resourcePool = this.clusterDetail?.resourcePool;
    let list = [];
    switch (this.clusterDetail.clusterType) {
      case ClusterTypeValue.flinkStandalone:
        list = disposeFlinkUtilization(resourcePool?.flink, !resourcePool);
        break;
      case ClusterTypeValue.sparkStandalone:
        list = disposeSparkUtilization(resourcePool?.spark, !resourcePool);
        break;
      case ClusterTypeValue.yarn:
        list = disposeYarnUtilization(resourcePool?.yarn, !resourcePool);
        break;
    }
    return list.map(ele => {
      ele.utilization = Math.round((ele.used / ele.total) * 100);
      return ele;
    });
  }

  getClusterDetailValue(key: string) {
    return this.clusterDetail[ClusterTypeValueObj[this.clusterDetail.clusterType][key]];
  }

  getClusterTypeValue(key: string) {
    return ClusterTypeValueObj[this.clusterDetail.clusterType][key];
  }

  openCreatedEditModal() {
    $modal({
      props: {
        title: `编辑${this.clusterDetail.clusterName}集群`,
        width: 579,
      },
      data: {
        clusterDetail: this.clusterDetail,
      },
      component: CreatedEditModal,
    }).then(() => {
      this.$emit('fetchData');
    });
  }

  deleteCluster() {
    const clusterName = this.clusterDetail.clusterName;
    $modal({
      props: {
        title: '提示',
        width: 579,
      },
      component: {
        render() {
          return <p>是否确认删除{clusterName}集群</p>;
        },
        methods: {
          async onOk() {
            $loading.increase();
            const { data: { retMsg } } = await ClusterApi.delete(clusterName)
              .finally($loading.decrease);
            $message.success(retMsg);
          },
        },
      },
    }).then(() => {
      this.$emit('fetchData');
    });
  }
}
