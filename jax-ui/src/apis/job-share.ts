import axios from 'axios';

import { ItoaResponse, JobModel, JobConfig } from '@/models';
import { V1ApiPrefix } from '@/constants';

interface JobShareApiBody {
  jobConfig: JobConfig;
  jobName: string;
  shareName: string;
}

const url = `${V1ApiPrefix}/jobshare`;

export const JobShareApi = {
  getAll() {
    return axios.get<ItoaResponse<JobModel[]>>(url);
  },
  save(body: JobShareApiBody, isCreated = true) {
    return axios[isCreated ? 'post' : 'put']<ItoaResponse<any>>(`${url}/${body.shareName}`, body);
  },
  delete(shareName: string) {
    return axios.delete<ItoaResponse<any>>(`${url}/${shareName}`);
  },
  exist(shareName: string) {
    return axios.get<ItoaResponse<any>>(`${url}/${shareName}/exist`);
  },
};
