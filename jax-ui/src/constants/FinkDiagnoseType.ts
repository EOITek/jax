const FinkDiagnoseType = Object.freeze({
  cluster:    { value: 'cluster', type: '集群诊断', label: '运行环境', data: [], url: null, urlLabel: '打开概览仪表盘', loading: true },
  exception:  { value: 'exception', type: '异常诊断', label: '任务状态', data: [], url: null, urlLabel: '打开详情仪表盘', loading: true },
  checkpoint: { value: 'checkpoint', type: 'checkpoint诊断', label: 'Checkpoint', data: [], loading: true },
  tm:         { value: 'tm', type: 'taskmanager诊断', label: 'TaskManager', data: [], loading: true },
  task:       { value: 'task', type: '任务诊断', label: '数据处理', data: [], loading: true },
  log:        { value: 'log', type: '日志诊断', label: '日志诊断', data: [], loading: true },
});

const FinkDiagnoseTypeList = Object.values(FinkDiagnoseType);

const FinkDiagnoseTypeValue: {
  [key in keyof typeof FinkDiagnoseType]: string;
} = Object.fromEntries(Object.entries(FinkDiagnoseType).map(x => [x[0], x[1].value])) as any;

export {
  FinkDiagnoseType,
  FinkDiagnoseTypeList,
  FinkDiagnoseTypeValue,
};
