import Vue from 'vue';

import '@/common/app/filters';
import * as Constants from '@/constants';

const Obj: Record<string, any> = Object.assign({}, ...[
  'ClusterType',
  'JobRoleType',
  'FinkDetectIdType',
].map(x => {
  const arr = Object.keys(Constants[x]).map(item => ({ ...Constants[x][item] }));
  return { [x]: Object.fromEntries(arr.map(({ value, label }) => [value, label])) };
}) as any);

const filters = {
  ClusterType(value: number) {
    return Obj.ClusterType[value] || '-';
  },
  JobRoleType(value: string) {
    return Obj.JobRoleType[value] || '-';
  },
  FinkDetectIdType(value: string) {
    return Obj.FinkDetectIdType[value] || '-';
  },
};

Object.entries(filters).forEach(([key, value]) => Vue.filter(key, value as any));
