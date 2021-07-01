import axios from 'axios';

import type { DiagnosisModel, ItoaResponse } from '@/models';
import { V1ApiPrefix } from '@/constants';

const url = `${V1ApiPrefix}/diagnosis`;

export const Diagnosis = {
  getItem(type: string, params) {
    return axios.get<ItoaResponse<DiagnosisModel>>(`${url}/${type}`, { params });
  },
};
