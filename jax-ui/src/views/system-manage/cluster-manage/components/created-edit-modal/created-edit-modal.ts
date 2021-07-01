import type { Form } from 'element-ui';
import { cloneDeep } from 'lodash-es';

import { ClusterApi, OptsApi } from '@/apis';
import { Component, Inreactive, Prop, Ref, VueComponentBase } from '@/common/VueComponentBase';
import { ClusterTypeList, ClusterTypeValue } from '@/constants';
import type { ClusterModel, OptsModel } from '@/models';
import { $message } from '@/services';

@Component()
export default class CreatedEditModal extends VueComponentBase {
  @Prop() readonly clusterDetail: ClusterModel;

  @Ref() readonly Form: Form;

  clusterData: any = {
    clusterName: '',
    clusterType: '',
    clusterDescription: '',
    hadoopHome: '',
    hdfsServer: '',
    yarnWebUrl: '',
    flinkServer: '',
    flinkWebUrl: '',
    sparkServer: '',
    sparkWebUrl: '',
    pythonEnv: '',
    principal: '',
    keytab: '',
    defaultFlinkCluster: false,
    defaultSparkCluster: false,
    timeoutMs: 0,
    flinkOptsName: '',
    sparkOptsName: '',
  };
  flinkFrameOptsList: OptsModel[] = [];
  sparkFrameOptsList: OptsModel[] = [];

  @Inreactive readonly ClusterTypeList = ClusterTypeList;
  @Inreactive readonly ClusterTypeValue = ClusterTypeValue;

  created() {
    if (this.clusterDetail) this.clusterData = cloneDeep(this.clusterDetail);
    this.clusterData.timeoutMs /= 1000;
    this.getFrameOptsList();
  }

  async getFrameOptsList() {
    const { data: { entity } } = await OptsApi.getAll();
    this.flinkFrameOptsList = entity.filter(ele => ele.optsType === 'flink');
    this.sparkFrameOptsList = entity.filter(ele => ele.optsType === 'spark');
  }

  clusterTypeChange() {
    this.Form.clearValidate();
    this.$forceUpdate();
  }

  async onOk() {
    await this.Form.validate();

    const clusterData = cloneDeep(this.clusterData);
    clusterData.timeoutMs *= 1000;

    const { data: {retMsg } } = await ClusterApi.save(clusterData, !this.clusterDetail);
    $message.success(retMsg);
  }
}
