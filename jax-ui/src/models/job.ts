import { JarModel } from './jar';
import { InType, PipelineParameterModel } from './base';
import { JobConfig } from './pipeline';

export interface JobModel {
  jobName: string;
  jobType: string;
  jobMeta: JobMeta;
  jarName: string;
  jobRole: string;
  internal: boolean | null;
  jar: JarModel;
  iconUrl: string | null;
  docUrl: string | null;
  hasDoc: boolean;
  jobConfig?: JobConfig;
}

interface JobMeta {
  inTypes?: InType[];
  outTypes?: InType[];
  jobInfo: JobInfo;
  parameters: {
    parameters: PipelineParameterModel[];
  };
}

interface JobInfo {
  name: string;
  type: string;
  display: string;
  description: string;
  apiVersion: number;
}
