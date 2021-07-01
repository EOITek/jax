const ClusterType = Object.freeze({
  yarn: {
    value: 'yarn',
    label: 'YARN',
    bgColor: '#EC980E',
    webUrlField: 'yarnWebUrl',
    webUrlLabel: 'Yarn WebUrl',
    serverField: 'hdfsServer',
    serverLabel: 'Hdfs Server',
  },
  sparkStandalone: {
    value: 'spark_standalone',
    label: 'Spark Standalone',
    bgColor: '#4A90E2',
    webUrlField: 'sparkWebUrl',
    webUrlLabel: 'Spark WebUrl',
    serverField: 'sparkServer',
    serverLabel: 'Spark Server',
  },
  flinkStandalone: {
    value: 'flink_standalone',
    label: 'Flink Standalone',
    bgColor: '#694AE2',
    webUrlField: 'flinkWebUrl',
    webUrlLabel: 'Flink WebUrl',
    serverField: 'flinkServer',
    serverLabel: 'Flink Server',
  },
});

const ClusterTypeValue: {
  [key in keyof typeof ClusterType]: string;
} = Object.fromEntries(Object.entries(ClusterType).map(x => [x[0], x[1].value])) as any;

const ClusterTypeValueObj = Object.fromEntries(Object.entries(ClusterType).map(x => [x[1].value, x[1]]));

const ClusterTypeList = Object.values(ClusterType);

export {
  ClusterType,
  ClusterTypeValue,
  ClusterTypeValueObj,
  ClusterTypeList,
};
