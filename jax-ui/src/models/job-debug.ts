import { WebSocketData } from './socket';

// eslint-disable-next-line no-shadow
export enum RunningLogType {
  success,
  error,
}

export interface JobDebugRunningLog {
  type: RunningLogType;
  message: string;
  id: number;
  display?: string;
  typeNumber?: number;
}

export interface ObserveDebugList extends WebSocketData {
  display: string;
  time: number;
}
