import type { Table } from 'element-ui';
import { cloneDeep, sortBy, uniqBy } from 'lodash-es';

import { JobApi, PipelineApi } from '@/apis';
import { Component, Inreactive, Prop, Ref, VueComponentBase, Watch } from '@/common/VueComponentBase';
import { InternalStatus, PipelineStatus as PipelineStatusMap, PipelineType } from '@/constants';
import { PaginationConfig } from '@/helpers';
import { FilterOptions, JobModel, PipelineModel } from '@/models';
import { $dialog, $message, $modal, $router } from '@/services';

import { OperationLog } from '../operation-log';
import { PipelineStatus } from '../pipeline-status';
import { ImportErrorModal } from '../components';
@Component({
  components: { PipelineStatus },
})
export default class WorkManageList extends VueComponentBase {
  @Prop() readonly jobName: string;
  @Prop() readonly pipelineType: 'streaming' | 'batch';
  @Prop() readonly isHistory = false;

  @Ref() Table: Table;

  data: PipelineModel[] = [];
  entity: PipelineModel[] = [];
  jobs: Record<string, JobModel[]> = {};
  pageConf = new PaginationConfig(100);
  loading = 0;
  selection: PipelineModel[] = [];
  filtersOpts: Record<string, any[]> = {};
  summary = {
    RUNNING: 0,
    STOPPED: 0,
    START_FAILED: 0,
    STOP_FAILED: 0,
  };
  search = '';
  defaultJobName = '';
  filterOptions: FilterOptions = {
    pipelineType: [],
    pipelineStatus: [],
    internalStatus: [],
  };
  isForceStop = false;
  fetchWorks = [];
  triggerBy = '';

  @Inreactive PipelineType = PipelineType;
  @Inreactive PipelineStatus = PipelineStatusMap;
  @Inreactive InternalStatus = InternalStatus;

  mounted() {
    this.doSearch();
    if (this.isHistory) this.getFetchWorks();
  }

  get tableData() {
    return this.pageConf.filterData(this.data);
  }

  get isStreaming() {
    return this.pipelineType === 'streaming';
  }

  @Watch('jobName')
  doSearch() {
    this.defaultJobName = this.jobName;
    this.fetchData();
  }

  @Watch('$route')
  routeChanged() {
    this.fetchData();
  }

  async getFetchWorks() {
    const { data: { entity } } = await PipelineApi.getAll({
      search: '',
      type: 'batch',
    });
    this.fetchWorks = entity.map(x => x.pipelineName);
  }

  fetchData() {
    ++this.loading;
    this.Table.clearFilter();
    (this.defaultJobName
      ? JobApi.getPipelines(this.defaultJobName)
      : PipelineApi.getAll({
        search: this.search ? this.search : '',
        type: this.pipelineType,
        triggerBy: this.triggerBy,
        triggered: this.isHistory,
      }))
      .then(({ data: { entity } }) => {
        this.pageConf.totalItems = entity.length;
        entity.sort((a, b) => b.createTime - a.createTime);
        this.data = cloneDeep(entity);
        this.entity = cloneDeep(entity);
        entity.forEach(item => {
          const pipelineTypeTmp = this.PipelineType[item.pipelineType];
          const pipelineStatusTmp = this.PipelineStatus[item.pipelineStatus];
          const internalStatusTmp = this.InternalStatus[item.internalStatus];
          if (pipelineTypeTmp) {
            this.filterOptions.pipelineType.push({
              text: pipelineTypeTmp,
              value: item.pipelineType,
            });
            this.filterOptions.pipelineType = uniqBy(this.filterOptions.pipelineType, 'value');
            this.filterOptions.pipelineType = sortBy(this.filterOptions.pipelineType, 'text');
          }
          if (pipelineStatusTmp) {
            this.filterOptions.pipelineStatus.push({
              text: pipelineStatusTmp,
              value: item.pipelineStatus,
            });
            this.filterOptions.pipelineStatus = uniqBy(this.filterOptions.pipelineStatus, 'value');
            this.filterOptions.pipelineStatus = sortBy(this.filterOptions.pipelineStatus, 'text');
          }
          if (internalStatusTmp) {
            this.filterOptions.internalStatus.push({
              text: internalStatusTmp,
              value: item.internalStatus,
            });
            this.filterOptions.internalStatus = uniqBy(this.filterOptions.internalStatus, 'value');
            this.filterOptions.internalStatus = sortBy(this.filterOptions.internalStatus, 'text');
          }
        });
        this.selection = [];
      })
      .finally(() => --this.loading);
  }

  async updateStatus(rows: PipelineModel[], status: 'start' | 'stop') {
    ++this.loading;
    const body: Record<string, any> = {};
    if (status === 'stop') body.forceStop = this.isForceStop ;
    const results = await Promise.allSettled(
      rows.map(x => PipelineApi.updateStatus(x.pipelineName, status, body)),
    ).finally(() => {
      --this.loading;
      this.isForceStop = false;
    });

    if (results.length === 1) {
      if (results[0].status === 'fulfilled') {
        $message.success('更新状态完成，请稍候查看结果');
      } else {
        $message.fuck(results[0].reason);
      }
    } else {
      const msg = results.some(x => x.status === 'rejected') ? $message.warning : $message.success;
      msg(`更新状态完成，成功${
        results.filter(x => x.status === 'fulfilled').length
      }个，失败${
        results.filter(x => x.status === 'rejected').length
      }个，请稍候查看结果`);
    }
    this.fetchData();
  }

  stopAffirm(rows: PipelineModel[]) {
    const self = this;
    $modal({
      component: {
        render() {
          return <div>
            <p class='mb-10'>是否确认停止选中pipeline？</p>
            <span class='mr-10'>是否强制停止:</span>
            <el-switch vModel={self.isForceStop} />
          </div>;
        },
      },
      props: {
        title: '提示',
        width: 400,
      },
    }).then(() => {
      this.updateStatus(rows, 'stop');
    });
  }

  async remove(rows: PipelineModel[]) {
    await $dialog.confirm(`确认删除${rows.length}个任务？`);

    ++this.loading;
    const results = await Promise.allSettled(
      rows.map(x => PipelineApi.delete(x.pipelineName)),
    ).finally(() => --this.loading);

    if (results.length === 1) {
      if (results[0].status === 'fulfilled') {
        $message.success('删除任务完成，请稍候查看结果');
      } else {
        $message.fuck(results[0].reason);
      }
    } else {
      const msg = results.some(x => x.status === 'rejected') ? $message.warning : $message.success;
      msg(`删除任务完成，成功${
        results.filter(x => x.status === 'fulfilled').length
      }个，失败${
        results.filter(x => x.status === 'rejected').length
      }个，请稍候查看结果`);
    }
    this.fetchData();
  }

  async handleExport(rows) {
    PipelineApi.export({ pipelineNames: rows.map(ele => ele.pipelineName) });
  }

  async chooseFile(file) {
    const { data: { entity } } = await PipelineApi.import({file});
    if (!entity.length) {
      $message.success('上传成功');
      return;
    }

    $modal({
      component: ImportErrorModal,
      props: {
        title: '上传失败列表',
        okText: null,
        cancelText: null,
      },
      data: { entity },
    });
  }

  showOperationLog(row: PipelineModel) {
    $modal({
      component: OperationLog,
      props: {
        title: row.pipelineName + '操作日志',
        width: 1024,
        cancelText: null,
        okText: null,
      },
      data: {
        model: row,
        isHistory: this.isHistory,
      },
    });
  }

  openDashboard(url: string) {
    open(url, '_blank');
  }

  filterChange(filters: Record<string, any>) {
    const [[key, values]] = Object.entries(filters);
    this.filtersOpts[key] = values;
    let data = this.entity;

    Object.entries(this.filtersOpts).forEach(([columnKey, vals]) => {
      if (vals.length === 0) return;
      data = data.filter(item => vals.includes(item[columnKey]));
    });
    this.data = cloneDeep(data);
    this.pageConf.totalItems = this.data.length;
  }

  openTrack(row: PipelineModel) {
    open(row.trackUrl, '_blank');
  }

  clonePipeline({ pipelineName, pipelineType }: PipelineModel) {
    $router.push({
      name: 'work-manage/config',
      params: { id: pipelineName },
      query: { jobType: pipelineType, clone: '1' },
    });
  }

  isShowDashboardUrl(row: PipelineModel, field: string) {
    return row.dashboard && row.dashboard[field] && row.pipelineStatus !== 'DRAFT';
  }

  openFinkDiagnose(row: PipelineModel) {
    open($router.resolve({
      name: 'WorkManageFinkDiagnose',
      query: {
        flinkJobId: row.flinkJobId,
        trackUrl: row.trackUrl,
        jobType: this.pipelineType,
        detailUrl: row.dashboard.detailUrl,
        summaryUrl: row.dashboard.summaryUrl,
      },
    }).href);
  }
}
