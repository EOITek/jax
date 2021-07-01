const FinkDetectIdType = Object.freeze({
  jobManagerStatus:       { value: 'jobManagerStatus', label: '是否可访问Jobmanager' },
  clusterResource:        { value: 'clusterResource', label: '是否有可用taskmanager资源' },
  rootEx:                 { value: 'rootEx', label: '根因异常' },
  historyEx:              { value: 'historyEx', label: '历史异常' },
  jobStatus:              { value: 'jobStatus', label: '任务状态' },
  allVertixStatus:        { value: 'allVertixStatus', label: '任务节点状态' },
  hasSourceData:          { value: 'hasSourceData', label: '输入节点是否有数据接入' },
  dataLoss:               { value: 'dataLoss', label: '是否有数据丢失' },
  backpressureLevel:      { value: 'backpressureLevel', label: '反压状态' },
  allTaskStatus:          { value: 'allTaskStatus', label: '子任务状态' },
  attempt:                { value: 'attempt', label: '重启次数' },
  dataSkew:               { value: 'dataSkew', label: '是否有数据倾斜' },
  hasWatermark:           { value: 'hasWatermark', label: '是否有watermark' },
  hasCheckpoint:          { value: 'hasCheckpoint', label: '是否启用checkpoint' },
  checkpointFailCount:    { value: 'checkpointFailCount', label: '历史checkpoint失败次数' },
  lastCheckpointStatus:   { value: 'lastCheckpointStatus', label: '最近一次checkpoint状态' },
  lastCheckpointDuration: { value: 'lastCheckpointDuration', label: '最近一次checkpoint时间' },
  gcTime:                 { value: 'gcTime', label: 'full gc时间' },
  memoryUsed:             { value: 'memoryUsed', label: '内存使用比率' },
});

export {
  FinkDetectIdType,
};
