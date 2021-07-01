import axios, { CancelTokenSource } from 'axios';
import { Form } from 'element-ui';

import { ClusterApi, JarApi } from '@/apis';
import { Component, Inreactive, Prop, Ref, VueComponentBase } from '@/common/VueComponentBase';
import { SharpModal } from '@/components';
import type { JarModel } from '@/models';

@Component()
export default class JobRepoJarsDetail extends VueComponentBase {
  @Ref() Form: Form;
  @Prop() readonly model: JarModel;
  @Prop() readonly isNew: boolean;
  @Prop() readonly modalInstance: SharpModal;
  @Prop() readonly jarType: string;

  allowType = '';
  clusterTypeList = [];

  @Inreactive cancelTokenSource: CancelTokenSource = null;

  file: File = null;

  created() {
    if (!this.model.jarFile) this.$set(this.model, 'jarFile', '');
    if (!this.model.jarVersion) this.$set(this.model, 'jarVersion', '');
    if (!this.model.clusterName) this.$set(this.model, 'clusterName', '');
  }
  mounted() {
    this.allowType = this.jarType === 'python' ? '.zip' : '.jar';
    ClusterApi.getAll().then(({ data: { entity } }) => {
      this.clusterTypeList = entity.filter(x => x.clusterType === 'yarn').map(y => y.clusterName);
    });
  }

  chooseFile(file?: File) {
    this.file = file;
    const name = this.file?.name ?? '';
    this.model.jarFile = name.slice(0, name.lastIndexOf('.'));
    this.model.jarVersion = name.match(/\d+\.\d+\.\d+/)?.[0] ?? '';
  }

  async onOk() {
    if (!this.file) this.model.jarFile = '';
    await this.Form.validate();

    this.cancelTokenSource = axios.CancelToken.source();
    await JarApi.save(this.model, this.file, this.cancelTokenSource.token, this.jarType, this.isNew);
    this.cancelTokenSource = null;
  }

  onCancel() {
    if (this.cancelTokenSource) this.cancelTokenSource.cancel();
    this.modalInstance.close('canceled', false);
  }
}
