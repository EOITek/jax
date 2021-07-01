import { BaseModel } from './base';
import { JobModel } from './job';

export interface JarModel extends BaseModel {
  jarName: string;
  jarPath?: string;
  jarVersion: string;
  jarFile: string;
  jarDescription: string;
  clusterName: string;
  jobList?: JobModel[];
}
