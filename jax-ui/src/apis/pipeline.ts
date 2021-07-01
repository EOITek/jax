import axios from 'axios';

import type { ItoaResponse,
  PipelineConsoleModel,
  PipelineImport,
  PipelineLogModel,
  PipelineModel,
  PipelineSearchParams } from '@/models';
import { downloadFile } from '@/helpers';
import { V1ApiPrefix } from '@/constants';

const url = `${V1ApiPrefix}/pipeline`;

export const PipelineApi = {
  release(data: PipelineModel, isCreate = false) {
    if (isCreate) {
      return axios.post<ItoaResponse<any>>(`${url}/${data.pipelineName}`, data);
    }
    return axios.put<ItoaResponse<any>>(`${url}/${data.pipelineName}`, data);
  },
  save(data: PipelineModel, isCreate = false) {
    if (isCreate) {
      return axios.post<ItoaResponse<any>>(`${url}/${data.pipelineName}/stage`, data);
    }
    return axios.put<ItoaResponse<any>>(`${url}/${data.pipelineName}/stage`, data);
  },
  getAll(params: PipelineSearchParams) {
    return axios.get<ItoaResponse<PipelineModel[]>>(url, { params });
  },
  getItem(name: string) {
    return axios.get<ItoaResponse<PipelineModel>>(`${url}/${name}`);
  },
  updateStatus(name: string, status: 'start' | 'stop', body = {}) {
    return axios.put<ItoaResponse<any>>(`${url}/${name}/${status}`, null, { params: body });
  },
  delete(name: string) {
    return axios.delete<ItoaResponse<any>>(`${url}/${name}`);
  },
  getLog(name: string) {
    return axios.get<ItoaResponse<PipelineLogModel[]>>(`${url}/${name}/log`);
  },
  getConsole(params) {
    return axios.get<ItoaResponse<PipelineConsoleModel[]>>(`${url}/${params.pipelineName}/console`, { params });
  },
  export(body) {
    downloadFile(`${url}-export`, body, 'post');
  },
  import(body) {
    const form = new FormData();
    Object.keys(body).forEach(key => {
      form.append(key, body[key]);
    });
    return axios.post<ItoaResponse<PipelineImport[]>>(`${url}-import`, form);
  },
};
