import axios from 'axios';

import { ItoaResponse, JobModel, PipelineModel } from '@/models';
import { V1ApiPrefix } from '@/constants';

const url = `${V1ApiPrefix}/job`;

export const JobApi = {
  getAll() {
    return axios.get<ItoaResponse<JobModel[]>>(url);
  },
  getPipelines(jobName: string) {
    return axios.get<ItoaResponse<PipelineModel[]>>(`${url}/${jobName}/pipeline`);
  },
  // eslint-disable-next-line no-shadow
  getMartDown(url) {
    return axios.get<ItoaResponse<any>>(url);
  },
};
