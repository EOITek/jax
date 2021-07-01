import axios from 'axios';

import type { ClusterModel, ItoaResponse } from '@/models';
import { V1ApiPrefix } from '@/constants';

const url = `${V1ApiPrefix}/cluster`;

export const ClusterApi = {
  getAll(isResource = false) {
    return axios.get<ItoaResponse<ClusterModel[]>>(`${url}${isResource ? '-resource' : ''}`);
  },
  save(body, isCreated = true) {
    return axios[isCreated ? 'post' : 'put']<ItoaResponse<any[]>>(`${url}/${body.clusterName}`, body);
  },
  delete(clusterName: string) {
    return axios.delete<ItoaResponse<any[]>>(`${url}/${clusterName}`);
  },
};
