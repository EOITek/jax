import { BaseModel } from './base';

export interface PipelineModel extends BaseModel {
  pipelineName: string;
  pipelineType: string;
  pipelineConfig: PipelineConfig;
  pipelineUi?: any;
  pipelineStatus: string;
  internalStatus: string;
  pipeDescription?: string;
  flinkJobId?: string;
  flinkSavePoint?: any;
  flinkCheckPoint?: any;
  sparkSubmissionId?: any;
  sparkRestUrl?: string;
  sparkAppId?: string;
  yarnAppId: string;
  dashboardUrl?: string;
  trackUrl?: string;
  dashboard?: Record<string, any>;
}

export interface JobDetailModel {
  id: string;
  type: string;
  name: string;
  conf: PipelineJob;
  shape: string;
  style: Record<string, any>;
  x: number;
  y: number;
  anchorPoints: number[][];
  display?: string;
}

interface PipelineConfig {
  jobs: PipelineJob[];
  edges: PipelineEdge[];
  opts?: any;
}

export interface PipelineEdge {
  from: string;
  to: string;
  edgeId?: string;
  fromSlot?: number;
  toSlot?: number;
  label?: string;
  enableMock?: boolean | null;
  mockId?: string | null;
}

export interface PipelineJob {
  jobId: string;
  jobName: string;
  jobConfig: JobConfig;
  jobOpts: any;
  jobViewData?: any;
  // parameters?: PipelineParameterModel[];
}

export interface JobConfig {
  nodes?: string;
  index?: string;
  timestampField?: string;
  timestampFormat?: string;
  valueField?: string;
  timestampFields?: string[];
  timestampFormats?: string[];
  toFormats?: string[];
  type?: string;
  saveMode?: string;
  servers?: string;
  topic?: string;
  includes?: string[];
  timestampField0?: string;
  timestampField1?: string;
  aggFields?: string[];
  aggMethods?: string[];
  groupByFields?: string[];
  taMainMetricColumn?: string;
  taMainMetricABNColumn?: string;
  taMetricsColumns?: string[];
  taMetricsABNColumns?: string[];
  taTopK?: number;
  topics?: string[];
  'bootstrap.servers'?: string;
  'group.id'?: string;
  offsetMode?: string;
  rules?: string;
}

export interface PipelineLogModel {
  pipelineName: string;
  logType: string;
  pipelineLog: string;
  createTime: number;
}

export interface FilterOptionsItem {
  text: string;
  value: string;
}

export interface FilterOptions {
  pipelineType: FilterOptionsItem[];
  pipelineStatus: FilterOptionsItem[];
  internalStatus: FilterOptionsItem[];
}

export interface PipelineSearchParams {
  inStatus?: string[];
  order?: string;
  pageIndex?: number;
  pageSize?: number;
  search?: string;
  sortBy?: string;
  status?: string[];
  type?: string;
  triggerBy?: string;
  triggered?: boolean;
}

export interface PipelineConsoleModel {
  pipelineName: string;
  opType: string;
  opTime: number;
  logContent: string;
  createTime: number;
}

export interface PipelineImport {
  arity: number;
  f0: string;
  f1: string;
}
