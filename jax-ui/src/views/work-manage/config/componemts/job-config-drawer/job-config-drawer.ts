import { cloneDeep } from 'lodash-es';
import { Form, Input } from 'element-ui-eoi';

import { JobApi } from '@/apis';
import { Component, Prop, Ref, VueComponentBase, Watch } from '@/common/VueComponentBase';
import type { JobConfig, PipelineParameterModel } from '@/models';
import { $modal } from '@/services';
import { MarkdownHtml } from '@/views/job-repo/list/markdown-html';

import { JobParameter, SaveJobShareModal } from './components';
import { disposeParameters, getJobParametersFormData } from './utils';

@Component({
  components: { JobParameter },
})
export default class JobConfigDrawer extends VueComponentBase {
  @Prop() readonly jobConfig: JobConfig;
  @Prop() readonly jobUiData: any;
  @Prop() readonly parameters: PipelineParameterModel[];
  @Prop() readonly isDebugModel: boolean;
  @Prop() readonly jobName: string;

  @Ref() readonly Form: Form;
  @Ref() readonly jobParametersForm: Form;
  @Ref() readonly Display: Input;

  viewParameters: PipelineParameterModel[] = [];
  jobParametersFormData = {};
  jobShareName = '';

  @Watch('jobConfig', {
    immediate: true,
    deep: true,
  })
  async changeParameters() {
    await this.$nextTick();
    this.viewParameters = disposeParameters(cloneDeep(this.parameters), cloneDeep(this.jobConfig));
    console.log('this.viewParameters ---------',this.viewParameters);
    this.Display?.focus();
  }

  @Watch('viewParameters', {
    deep: true,
  })
  changeViewParameters(newV) {
    this.jobParametersFormData = getJobParametersFormData(newV, this.jobConfig, false, 'formProp');
  }

  /**
   * 打开配置帮助文档
   */
  async handlerOpenConfigHelpModal() {
    const { data } = await JobApi.getMartDown(this.jobUiData.docUrl);
    $modal({
      component: MarkdownHtml,
      props: {
        title: '配置帮助',
        width: 1000,
        okText: null,
        cancelText: null,
      },
      data: {
        data,
      },
    });
  }

  getFormData() {
    return getJobParametersFormData(this.viewParameters, this.jobConfig, true);
  }

  handleJobShare() {
    this.jobShareName = '';
    $modal({
      component: SaveJobShareModal,
      props: {
        title: '分享job',
        width: 300,
      },
      data: {
        jobConfig: this.jobConfig,
        jobName: this.jobName,
      },
    }).then(() => {
      this.$emit('updateJobShareList');
    });
  }


  async onOk() {
    await this.Form.validate();
    await this.jobParametersForm.validate();
    const FormData = this.getFormData();
    return FormData;
  }
}
