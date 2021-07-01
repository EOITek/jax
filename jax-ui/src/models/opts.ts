export interface OptsModel {
  sparkOptsName?: string;
  flinkOptsName?: string;
  optsName: string;
  optsDescription: string;
  entryJar: string;
  entryClass: string;
  jobLib: string;
  home: string;
  version: string;
  yarnQueue?: any;
  driverMemory: string;
  executorMemory: string;
  driverCores: number;
  executorCores: number;
  numExecutors: number;
  optsList: OptsListItem[];
  otherStartArgs: any[];
  createTime: number;
  createBy?: any;
  updateTime: number;
  updateBy?: any;
  optsType: string;
}

export interface AllOptsList {
  flinkOptsList: OptsListItem[];
  sparkOptsList: OptsListItem[];
}

export interface OptsListItem {
  name: string;
  type: string;
  value?: string[] | any[] | number | string;
  description: string;
  required: boolean;
}
