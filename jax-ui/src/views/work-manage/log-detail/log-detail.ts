import { groupBy } from 'lodash-es';

import { Component, Prop, VueComponentBase } from '@/common/VueComponentBase';
import { PipelineApi } from '@/apis';
import type { PipelineConsoleModel } from '@/models';

@Component()
export default class WorkManageLogDetail extends VueComponentBase {
  @Prop() readonly name: string;
  @Prop() readonly jobType: string;
  @Prop() readonly isHistory: boolean;

  allLogData: PipelineConsoleModel[] = [];
  pageParams = {
    pageIndex: 0,
    pageSize: 100,
  };
  total = 1;
  activeNames: string[] = [];

  get fromRouter() {
    if (this.isHistory) {
      return { name: 'work-manage/timing-work', query: { id: '运行历史' } };
    }
    return { name: 'work-manage/list', params: { pipelineType: this.jobType } };
  }

  async fetchData() {
    if (this.total <= this.allLogData.length) return;

    const { data: { entity, total } } = await PipelineApi.getConsole({
      pipelineName: this.name,
      order: 'asc',
      ...this.pageParams,
    });

    // 新加载的数据默认展开
    entity.forEach(ele => {
      const key = [ele.opTime, ele.opType].join('-');
      if (!this.activeNames.includes(key)) this.activeNames.push(key);
    });
    this.allLogData.push(...entity);
    this.pageParams.pageIndex++;
    this.total = total;
  }

  get LogGroupData() {
    const logGroupData: any = groupBy(this.allLogData, ele => [ele.opTime, ele.opType].join('-'));
    Object.keys(logGroupData).forEach(key => {
      logGroupData[key] = {
        opType: logGroupData[key][0].opType,
        opTime: logGroupData[key][0].opTime,
        displayText: logGroupData[key].map(ele => ele.logContent).join('\n').replace(/</, ' &lt'),
      };
    });
    return logGroupData;
  }
}
