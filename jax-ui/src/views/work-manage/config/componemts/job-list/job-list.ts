import { groupBy } from 'lodash-es';

import { JobShareApi } from '@/apis';
import { Component, Inreactive, Prop, VueComponentBase } from '@/common/VueComponentBase';
import { JobRoleTypeValue, JobDefaultIcon } from '@/constants';
import type { JobModel } from '@/models';
import { $dialog, $message } from '@/services';

import { disposeJobList } from '../../main/utils';

@Component()
export default class JobList extends VueComponentBase {
  @Prop() readonly jobList: JobModel[];
  @Prop() readonly jobType: string;

  searchParam = '';
  showV1Job = true;
  jobshareList: JobModel[] = [];

  @Inreactive JobDefaultIcon = JobDefaultIcon;
  @Inreactive JobRoleTypeValue = JobRoleTypeValue;

  get filteredGroupObj() {
    const emptyGroupObj = {
      [JobRoleTypeValue.source]: [],
      [JobRoleTypeValue.process]: [],
      [JobRoleTypeValue.sink]: [],
      [JobRoleTypeValue.share]: this.jobshareList,
    };
    const filteredList: JobModel[] = this.getFilteredList(this.jobList);

    if (!filteredList.length) return emptyGroupObj;

    const groupObj = groupBy(filteredList, 'jobRole');
    return Object.assign(emptyGroupObj, groupObj);
  }

  getFilteredList(jobList: JobModel[]) {
    const filterText = this.searchParam.toLowerCase();
    let filteredList: JobModel[] = jobList;

    if (filterText) {
      filteredList = jobList.filter(item => {
        const { name, display, description } = item.jobMeta.jobInfo;
        return name.toLowerCase().includes(filterText)
          || display.toLowerCase().includes(filterText)
          || description.toLowerCase().includes(filterText);
      });
    };

    if (!this.showV1Job) {
      filteredList = filteredList.filter(item => item.jobMeta.jobInfo.apiVersion === 2);
    }

    return filteredList;
  }

  async fetchJobshareList() {
    const { data: { entity } } = await JobShareApi.getAll();
    this.jobshareList = disposeJobList(entity, this.jobType) ;
  }

  created() {
    window.addEventListener('keydown', this.handleKeydown);
    this.fetchJobshareList();
  }

  beforeDestroy() {
    window.removeEventListener('keydown', this.handleKeydown);
  }

  handlerDragstart(event: DragEvent, model: JobModel) {
    event.dataTransfer.setData('x-json/job-model', JSON.stringify(model));
  }

  formatTootip({ name, display, description }) {
    return [`名称：${name}`, `显示：${display}`, `描述：${description}`].join('\n');
  }

  handleKeydown(e) {
    const keyCode = e.keyCode || e.which || e.charCode;
    const ctrlKey = e.ctrlKey || e.metaKey;

    // 监听ctrl + H 组合按键
    if (ctrlKey && keyCode === 72) {
      e.preventDefault();
      this.showV1Job = !this.showV1Job;
    }
  }

  isShare(groupKey: string) {
    return groupKey === JobRoleTypeValue.share;
  }

  async handleRemoveJobShare(shareName: string) {
    await $dialog.confirm(`是否确认删除 ${shareName}？`);

    const { data: { retMsg } } = await JobShareApi.delete(shareName);
    $message.success(retMsg);
    this.fetchJobshareList();
  }
}
