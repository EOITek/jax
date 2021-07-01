import { JobShareApi } from '@/apis';
import { Component, Prop, VueComponentBase } from '@/common/VueComponentBase';
import type { JobConfig } from '@/models';
import { $dialog, $message } from '@/services';

@Component()
export default class SaveJobShareModal extends VueComponentBase {
  @Prop() readonly jobConfig: JobConfig;
  @Prop() readonly jobName: string;

  jobShareName = '';

  async onOk() {
    if (!this.jobShareName) {
      $message.warning('请输入分享名称');
      return Promise.reject();
    }
    const { data: { entity } } = await JobShareApi.exist(this.jobShareName);
    if (!entity) {
      this.save();
      return;
    }

    await $dialog.confirm(`${this.jobShareName}已存在,是否覆盖当前分享？`);
    this.save(false);
  }

  async save(isCreate = true) {
    const { data: { retMsg } } = await JobShareApi.save({
      shareName: this.jobShareName,
      jobConfig: this.jobConfig,
      jobName: this.jobName,
    }, isCreate);
    $message.success(retMsg);
  }
}
