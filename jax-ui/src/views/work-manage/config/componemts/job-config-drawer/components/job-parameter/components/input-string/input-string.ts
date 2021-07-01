import { Component, Prop, VueComponentBase } from '@/common/VueComponentBase';
import type { PipelineParameterModel } from '@/models';
import { $modal } from '@/services';
import { JsonModal } from '@/views/work-manage/config/componemts';

@Component()
export default class InputString extends VueComponentBase {
  @Prop() readonly parameter: PipelineParameterModel;
  @Prop() readonly isDebugModel: boolean;

  get isShowSelect() {
    return this.parameter.candidates && this.parameter.candidates.length;
  }

  get lang() {
    const langList = {
      SQL: 'sql',
      SPL: 'sql',
      JAVASCRIPT: 'javascript',
    };
    return langList[this.parameter.inputType] || 'json';
  }

  openInputJson() {
    $modal({
      component: JsonModal,
      props: {
        title: '编辑参数',
      },
      data: {
        options: { maxLines: 30 },
        isReadonly: this.isDebugModel,
        entity: this.parameter.value || '',
        lang: this.lang,
      },
    }).then(json => {
      this.parameter.value = json;
    });
  };
};
