import axios, { CancelToken } from 'axios';

import type { ItoaResponse, JarModel } from '@/models';
import { V1ApiPrefix } from '@/constants';

const url = `${V1ApiPrefix}/jar`;

export const JarApi = {
  getAll() {
    return axios.get<ItoaResponse<JarModel[]>>(url);
  },
  save(body: JarModel, file: File, cancelToken: CancelToken, jarType: string, isNew: boolean) {
    const form = new FormData();
    form.append('file', file);
    form.append('jarVersion', body.jarVersion);
    form.append('jarFile', body.jarFile);
    form.append('jarDescription', body.jarDescription);
    form.append('clusterName', body.clusterName);
    form.append('jarType', jarType);
    return axios[isNew ? 'post' : 'put'](`${url}/${encodeURIComponent(body.jarName)}`, form, { cancelToken });
  },
  delete(jarName: string) {
    return axios.delete(`${url}/${jarName}`);
  },
};
