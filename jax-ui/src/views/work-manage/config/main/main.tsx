import { cloneDeep, isEqual, pick } from 'lodash-es';
import type { Drawer } from 'element-ui';

import { JobApi, PipelineApi } from '@/apis';
import { Component, Inreactive, Prop, Ref, VueComponentBase, Watch } from '@/common/VueComponentBase';
import { BatchJson, StreamingJson, PipelineStatus } from '@/constants';
import type { JobConfig, JobModel, PipelineModel } from '@/models';
import { $modal, $message, $router, $root } from '@/services';

import { JobDagre, JobList, JsonModal, JobConfigDrawer, JobDebugDrawer, AdvancedConfigModal } from '../componemts';
import { setStateAll } from '../componemts/job-dagre/config/g6-utils';
import { CREATE_IDS,
  disposeJobConfigDrawerHeight,
  disposeJobList,
  disposePipelineData,
  getPipelineData,
  getPipelineDataNoJobViewData } from './utils';

@Component({
  components: {
    JobDagre,
    JobList,
    JobDebugDrawer,
    JobConfigDrawer,
  },
  beforeRouteLeave(to, from, next){
    this.disposePipelineUi();
    if (isEqual(this.pipelineData, this.defaultPipelineData)) {
      next();
      return;
    }

    this.$confirm('请确认您是否已保存工作区的变更', '要离开此网站么？', {
      distinguishCancelAndClose: true,
      confirmButtonText: '确定退出',
      cancelButtonText: '取消',
    }).then(() => next());
  },
})
export default class WorkManageConfig extends VueComponentBase {
  @Prop() readonly id: string;
  @Prop() readonly jobType: string;
  @Prop() readonly clone: boolean;

  @Ref() readonly JobDagre: JobDagre;
  @Ref() readonly JobConfigDrawer: JobConfigDrawer;
  @Ref() readonly JobDebugDrawer: JobDebugDrawer;
  @Ref() readonly ElDrawer: Drawer;

  pipelineData: any = null;
  jobList: JobModel[] = [];
  // debug抽屉的高度
  debugDrawerHeight = 0;
  // 是否进入debug模式
  isDebugModel = false;
  isStartDebug = false;
  showJobConfigDrawer = false;
  currentJobId = '';
  currentJobName = '';
  jobConfig: JobConfig = {};
  webUrl = '';

  @Inreactive PipelineStatus = PipelineStatus;
  @Inreactive beforeJobId: string = null;
  @Inreactive defaultPipelineData: PipelineModel[] = [];

  /**
   * 是否为流作业
   */
  get isStreaming() {
    return this.jobType === 'streaming';
  }

  get isNew() {
    return CREATE_IDS.includes(this.id);
  }

  get isCreate() {
    return this.isNew || this.clone;
  }

  get parameters() {
    if (!this.currentJobName) return {};
    return this.jobList.find(x => x.jobName === this.currentJobName).jobMeta.parameters.parameters;
  }

  get isEditClusterName() {
    return this.isCreate || this.pipelineData.pipelineStatus === 'DRAFT';
  }

  @Watch('showJobConfigDrawer')
  changeShowJobConfigDrawer(newV) {
    if(!newV) this.beforeJobId = '';
  }

  async mounted() {
    window.addEventListener('beforeunload', this.checkLeave);
    if (this.isNew) {
      const { data: { entity } } = await JobApi.getAll();
      this.jobList = disposeJobList(entity, this.jobType);
      this.pipelineData = this.id.includes('streaming') ? StreamingJson() : BatchJson();
    } else {
      const [{ data: { entity: pipelineData } }, { data: { entity: JobData } }] =
        await Promise.all([PipelineApi.getItem(this.id), JobApi.getAll()]);

      this.jobList = disposeJobList(JobData, this.jobType);
      this.pipelineData = disposePipelineData(pipelineData, this.jobList);
    }
    this.defaultPipelineData = cloneDeep(this.pipelineData);
    if (this.clone) this.pipelineData.pipelineName += '_clone';
  }

  checkLeave(event) {
    event.returnValue = false;
  }

  beforeDestroy() {
    window.removeEventListener('beforeunload', this.checkLeave);
    $root.drawers = [];
  }

  updateEntity(data: PipelineModel) {
    this.pipelineData = data;
  }

  handlerOpenJsonModel() {
    const pipelineData = getPipelineDataNoJobViewData(this.pipelineData, true);
    $modal({
      component: JsonModal,
      props: {
        title: pipelineData.pipelineName + ' 详细信息',
        width: 800,
      },
      data: {
        entity: pipelineData,
        options: { maxLines: 30 },
      },
    }).then(pipelineJsonData => {
      this.pipelineData = disposePipelineData(getPipelineData(pipelineJsonData, this.pipelineData), this.jobList);
      this.JobDagre.graphRead();
    });
  }

  getJobConfig(jobId: string) {
    return this.pipelineData.pipelineConfig.jobs.find(x => x.jobId === jobId);
  }

  async handlerOpenJobConfigDrawer(jobName: string, jobId: string) {
    this.showJobConfigDrawer = true;
    this.currentJobId = jobId;
    this.currentJobName = jobName;
    this.jobConfig = this.getJobConfig(this.currentJobId).jobConfig;
    await this.openJobConfigDrawerBefore();
    this.beforeJobId = jobId;
    this.$nextTick(() => {
      disposeJobConfigDrawerHeight(this.debugDrawerHeight, true);
    });
  }

  async handleJobConfigDrawerAffirm() {
    const result = await this.JobConfigDrawer.onOk();
    this.showJobConfigDrawer = false;
    this.updateJobConfig(this.currentJobId, result);
  }

  /**
   * 更新对应 jobId 的数据
   *
   * @param jobId
   */
  updateJobConfig(jobId, formData) {
    const jobData = this.getJobConfig(jobId);
    if (formData) {
      jobData.jobConfig = formData;
    } else {
      jobData.jobConfig = this.JobConfigDrawer.getFormData();
    }
    this.JobDagre.updateNode(jobId, jobData.jobConfig);
  }

  /**
   * 切换节点配置时提示用户是否保存之前的更改
   */
  openJobConfigDrawerBefore(jobId = this.beforeJobId) {
    const jobConfig = this.getJobConfig(jobId)?.jobConfig;
    if (!(jobConfig && this.showJobConfigDrawer)) return;

    // 数据比对，判断用户是否修改过内容
    const formData = this.JobConfigDrawer.getFormData();
    if (isEqual(formData, jobConfig)) return;

    console.log('formData, jobData.jobConfig ---------',formData, jobConfig);

    return this.$confirm('检测到未保存的内容，是否在离开前保存修改？', '确认信息', {
      distinguishCancelAndClose: true,
      confirmButtonText: '保存',
      cancelButtonText: '放弃修改',
    }).then(() => {
      this.updateJobConfig(jobId, formData);
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    }).catch(() =>{});
  }

  /**
   * 作业提交前的校验
   */
  pipelineDataValidate() {
    const { pipelineName, pipelineType } = this.pipelineData;
    if (pipelineName === 'null' || pipelineName === '') {
      $message.error('参数 {pipelineName} 填写不正确');
      return;
    }
    if (this.id.includes('streaming') && pipelineType !== 'streaming') {
      $message.error('流作业创建 参数{pipelineType} 必须为 "streaming"');
      return;
    }
    if (this.id.includes('batch') && pipelineType !== 'batch') {
      $message.error('批作业创建 参数{pipelineType} 必须为 "batch"');
      return;
    }
    return true;
  }

  /**
   * 提取PipelineU所需数据
   * 发布的时候清除无用的节点UI数据
   *
   * @param type 操作类型
   */
  disposePipelineUi(type: 'stage' | 'release' = 'stage' ) {
    this.JobDagre.graph.paint();
    const { nodes }: any = this.JobDagre.graph.save();
    Object.keys(this.pipelineData.pipelineUi).forEach(pipelineUiKey => {
      const node = nodes.find(nodeItem => nodeItem.id === pipelineUiKey);
      if (node) {
        Object.assign(this.pipelineData.pipelineUi[node.id], pick(node, 'x', 'y'));
      } else {
        if (type === 'release') delete this.pipelineData.pipelineUi[pipelineUiKey];
      }
    });
  }

  /**
   * 打开调试面板
   */
  async handlerOpenDebugDrawer() {
    await this.openJobConfigDrawerBefore(this.currentJobId);
    this.disposePipelineUi();
    this.debugDrawerHeight = 260;
    this.isDebugModel = true;
    this.isStartDebug = false;
    await this.$nextTick();
    disposeJobConfigDrawerHeight(this.debugDrawerHeight);
    this.disposePipelineDataOpenDebug();
    this.JobDagre.resetGraph();
    this.showJobConfigDrawer = false;
  }

  /**
   * 处理调试所需要的PipelineData
   *
   */
  disposePipelineDataOpenDebug(type: 'add' | 'delete' = 'add') {
    const { jobs, edges } = this.pipelineData.pipelineConfig;
    jobs.forEach(job => {
      if (job.jobViewData.jobRole === 'sink') return;
      job.jobOpts ||= {};
      if (type === 'add') {
        job.jobOpts.enableDebug = true;
      } else {
        delete job.jobOpts.enableDebug;
      }
    });
    edges.forEach(edge => {
      edge.enableMock = null;
      edge.mockId = null;
    });
  }

  handlerStartDebug() {
    this.JobDebugDrawer.startDebug();
    this.isStartDebug = true;
  }

  /**
   * 停止或关闭debug模式
   *
   * @param type
   */
  async handlerStopDebug(type: 'stop' | 'close') {
    this.JobDebugDrawer.closeWs();
    this.isStartDebug = false;
    this.webUrl = '';
    setStateAll('edge', 'running', 0, this.JobDagre.graph);

    if (type !== 'close') return;

    this.isDebugModel = false;
    this.debugDrawerHeight = 0;
    await this.$nextTick();
    disposeJobConfigDrawerHeight(this.debugDrawerHeight);
    this.disposePipelineDataOpenDebug('delete');
    this.JobDagre.resetGraph();
  }

  /**
   * 更新线的运动状态
   */
  handleUpdateEdgeRunningState(runningStateData) {
    this.JobDagre.updateEdgeRunningState(runningStateData);
  }

  /**
   * 处理调试框尺寸变化的回调
   */
  handledrawerResize() {
    const drawer = this.ElDrawer.$refs.drawer as HTMLDivElement;
    this.debugDrawerHeight = drawer.clientHeight;
    disposeJobConfigDrawerHeight(this.debugDrawerHeight);
  }

  /**
   * 打开高级配置框
   */
  handlerOpenAdvancedConfig() {
    $modal({
      component: AdvancedConfigModal,
      props: {
        title: '高级配置',
        width: 710,
      },
      data: {
        opts: this.pipelineData.pipelineConfig.opts || {},
        isStreaming: this.isStreaming,
        defaultClusterName: this.pipelineData.clusterName,
        isEditClusterName: this.isEditClusterName,
      },
    }).then(({ optsFormData, clusterName }) => {
      this.pipelineData.pipelineConfig.opts = optsFormData;
      this.pipelineData.clusterName = clusterName;
    });
  }

  /**
   * 处理发布
   */
  async handlerRelease() {
    if (!this.pipelineDataValidate()) return;
    try {
      this.disposePipelineUi('release');
      const { data: { retMsg } } = await PipelineApi.release(getPipelineDataNoJobViewData(this.pipelineData), this.isCreate);
      $message.success(retMsg);
      this.defaultPipelineData = this.pipelineData;
      $router.push({name: 'work-manage/list', params: { pipelineType: this.jobType }});
    } catch({ response: { data } }) {
      this.disposeErrorInfo(data);
    }
  }

  disposeErrorInfo({ stackTrace, retMsg }) {
    if (stackTrace) {
      $modal({
        component: {
          render() {
            return <pre class="width-1000 text-wrap">{stackTrace}</pre>;
          },
        },
        props: {
          title: retMsg,
          width: 1050,
          okText: null,
          cancelText: null,
        },
      });
    } else {
      $message.error(retMsg);
    }
  }

  async save() {
    if (!this.pipelineDataValidate()) return;
    try {
      this.disposePipelineUi();
      const { data: { retMsg } } = await PipelineApi.save(getPipelineDataNoJobViewData(this.pipelineData), this.isCreate);
      $message.success(retMsg);
      const { name, params, query } = this.$route;
      this.defaultPipelineData = this.pipelineData;
      if (!this.isCreate) return;
      $router.replace({
        name,
        params: { ...params, id: this.pipelineData.pipelineName },
        query: { ...query, clone: '0' },
      });
    } catch({ response: { data } }) {
      this.disposeErrorInfo(data);
    }
  }
}
