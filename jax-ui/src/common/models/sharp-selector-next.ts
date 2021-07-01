export type SharpSelectorNextDataModel = Record<string, SharpSelectorNextDataValModel>;

export interface SharpSelectorNextDataValModel<T = any> {
  not: boolean;
  value: T;
}

export interface SharpSelectorNextOptionModel {
  type: 'input' | 'select' | 'date' | 'dateRange' | 'cascader' | Vue;
  key: string;
  name: string;
  options?: SharpSelectorNextSubOptionModel[];
}

export interface SharpSelectorNextSubOptionModel {
  label: string;
  value: boolean | string | number;
  children?: SharpSelectorNextSubOptionModel[];
}
