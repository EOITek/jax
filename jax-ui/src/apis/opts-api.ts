import axios from 'axios';

import type { AllOptsList, ItoaResponse, OptsModel } from '@/models';
import { V1ApiPrefix } from '@/constants';

const url = `${V1ApiPrefix}/opts`;

interface Opts {
  optsName: string;
  optsType: string;
}

export const OptsApi = {
  getAll() {
    return axios.get<ItoaResponse<OptsModel[]>>(url);
  },
  getFrameConfig() {
    return axios.get<ItoaResponse<AllOptsList>>(`${url}-options`);
  },
  getItem({ optsName, optsType }: Opts) {
    return axios.get<ItoaResponse<OptsModel>>(`${url}/${optsType}/${optsName}`);
  },
  update(body) {
    return axios.put<ItoaResponse<any>>(`${url}/${body.optsType}/${body.optsName}`, body);
  },
  created(body) {
    return axios.post<ItoaResponse<any>>(`${url}/${body.optsType}/${body.optsName}`, body);
  },
  delete({ optsName, optsType }: Opts) {
    return axios.delete<ItoaResponse<any>>(`${url}/${optsType}/${optsName}`);
  },
};
