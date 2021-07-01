export interface WebSocketData {
  code: string;
  job_id?: string;
  slot?: string;
  message: any;
}

export interface WebSocketConfig {
  successCallback?: any;
  closeCallback?: any;
  errorCallback?: any;
  onMessageCallback: any;
}
