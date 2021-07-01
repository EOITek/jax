export interface ResourcePoolFlink {
  taskManagers?: number;
  slotsTotal?: number;
  slotsAvailable: number;
  jobsRunning: number;
  jobsFinished: number;
  jobsCancelled: number;
  jobsFailed: number;
  flinkVersion: string;
  flinkCommit: string;
}

export interface ResourcePoolSpark {
  appsRunning?: number;
  driversDunning?: number;
  url: string;
  aliveWorkers?: number;
  cores?: number;
  coresUsed?: number;
  memory?: number;
  memoryUsed?: number;
  status: string;
}

export interface ResourcePoolYarn {
  appsSubmitted?: number;
  appsCompleted?: number;
  appsPending?: number;
  appsRunning?: number;
  appsKilled?: number;
  reservedMB?: number;
  allocatedMB?: number;
  availableMB?: number;
  totalMB?: number;
  reservedVirtualCores?: number;
  availableVirtualCores?: number;
  allocatedVirtualCores?: number;
  totalVirtualCores?: number;
  containersAllocated?: number;
  containersReserved?: number;
  containersPending?: number;
  totalNodes?: number;
  activeNodes?: number;
  lostNodes?: number;
  unhealthyNodes?: number;
  decommissioningNodes?: number;
  decommissionedNodes?: number;
  rebootedNodes?: number;
  shutdownNodes?: any;
}

export interface ClusterModelResourcePool {
  clusterName: string;
  clusterType: string;
  flink: ResourcePoolFlink;
  flinkGot: boolean;
  spark: ResourcePoolSpark;
  sparkGot: boolean;
  yarn: ResourcePoolYarn;
  yarnGot: boolean;
}

export interface ClusterModel {
  clusterName: string;
  clusterType: string;
  clusterDescription: string;
  hadoopHome: string;
  hdfsServer: string;
  yarnWebUrl: string;
  flinkServer: string;
  flinkWebUrl: string;
  sparkServer: string;
  sparkWebUrl: string;
  sparkHistoryServer?: any;
  pythonEnv: string;
  principal: string;
  keytab: string;
  defaultFlinkCluster: boolean;
  defaultSparkCluster: boolean;
  timeoutMs: number;
  flinkOptsName: string;
  sparkOptsName: string;
  createTime: any;
  createBy?: any;
  updateTime: any;
  updateBy?: any;
  flinkOpts?: any;
  sparkOpts?: any;
  resourcePool: ClusterModelResourcePool;
}
