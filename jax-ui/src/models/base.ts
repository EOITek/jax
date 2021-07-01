export interface BaseModel {
  createTime: number;
  createBy?: any;
  updateTime: number;
  updateBy?: any;
}

export interface InType {
  raw: string;
  parameterizedTypes?: string[];
}

export interface PipelineParameterModel {
  viewDataMap: PipelineViewDataListModel[];
  apiVersion: number;
  name: string;
  label: string;
  type: any[];
  description: string;
  optional: boolean;
  defaultValue: string;
  placeholder?: any;
  candidates?: any;
  inputType?: any;
  range?: any;
  regex?: any;
  algorithmic: boolean;
  listParameter?: any;
  objectParameters?: any;
  prop?: string;
  prevProp?: string;
  value?: any;
  viewDataList?: PipelineParameterModel[];
  formRule?: any;
  formProp?: string;
  defaultFormValue?: any;
  tier?: number;
}

export interface PipelineViewDataListModel {
  key: string;
  value: unknown;
}
