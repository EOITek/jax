import axios, { AxiosError, AxiosResponse } from 'axios';

import '@/common/app/run';

import { Project } from '@/constants';

axios.interceptors.response.use((value: AxiosResponse) => {
  if (value.config.url.startsWith(Project.serviceName ? `/${Project.serviceName}/api` : '/api')) {
    if (value.data && value.data.retCode) {
      if (value.data.retCode !== '0000') {
        return Promise.reject({
          config: value.config,
          code: value.status,
          request: value.request,
          response: value,
          isAxiosError: true,
        });
      }
    }
  }
  return value;
}, (err: AxiosError) => {
  if (!navigator.onLine) {
    err.message = '网络错误，请检查网络配置!';
  }
  return Promise.reject(err);
});
