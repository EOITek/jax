import { cloneDeep, isObject, mergeWith } from 'lodash-es';

import { JobRoleTypeValue, Project } from '@/constants';
import type { JobModel, PipelineEdge, PipelineModel } from '@/models';

import { setNewId } from '../componemts/job-dagre/config/utils';

export const CREATE_IDS = ['create_streaming', 'create_batch'];
/**
 * 处理算子配置抽屉的高度和阴影
 *
 * @param height 需要减去的高度
 * @param isBoxShadow 是否需要处理阴影
 */
export const disposeJobConfigDrawerHeight = function(height: number, isDisposeBoxShadow = false) {
  const drawer: HTMLDivElement = document.querySelector('.job-config-drawer');
  if (!drawer) return;
  drawer.style.height = `calc(100% - ${height}px)`;
  if (isDisposeBoxShadow) drawer.style.boxShadow = '-5px 0 5px rgba(0, 0, 0, 0.1)';
};


/**
 * 处理后台给的边数据
 */
export const disposeEdgesData = function(edges: PipelineEdge[]) {
  const reg = /\[(.+)\]/;
  return edges.map(edge => {
    const edgeFrom = edge.from.match(reg);
    const edgeTo = edge.to.match(reg);
    edge.edgeId ||= setNewId();
    if (edgeFrom) {
      edge.from = edge.from.slice(0, edgeFrom.index);
      edge.fromSlot = +edgeFrom[1];
    }
    if (edgeTo) {
      edge.to = edge.to.slice(0, edgeTo.index);
      edge.toSlot = +edgeTo[1];
    }
  }) as unknown as PipelineEdge[];
};

/**
 * 处理JobConfig数据，过滤无用null值
 *
 * @param jobConfig
 */
const disposeJobConfig = function(jobConfig) {
  Object.keys(jobConfig).forEach(key => {
    if ([null, ''].includes(jobConfig[key])) delete jobConfig[key];
    if (isObject(jobConfig[key])) disposeJobConfig(jobConfig[key]);
    if (Array.isArray(jobConfig[key])) {
      jobConfig[key].forEach(ele => {
        if (isObject(ele)) disposeJobConfig(ele);
      });
    }
  });
};

/**
 * 处理得到拓扑图所需的job数据
 *
 * @param pipelineData
 * @param jobList
 */
export const disposeJobsData = function(pipelineData: PipelineModel, jobList: JobModel[]) {
  const { pipelineConfig: { jobs } } = pipelineData;
  const pipelineUi = pipelineData.pipelineUi ||= {};
  jobs.forEach(job => {
    const { jobName, jobId, jobConfig } = job;
    const { jobRole, iconUrl, docUrl, hasDoc, jobMeta: { inTypes, outTypes, jobInfo: { description, display } } }
      = jobList.find(x => x.jobName === jobName);

    job.jobViewData = { inTypes, outTypes, jobRole, iconUrl };
    disposeJobConfig(jobConfig);

    pipelineUi[jobId] ||= {};
    mergeWith(pipelineUi[jobId], { docUrl, hasDoc, description, display }, (objValue, srcValue, key) => {
      // 一些个别字段合并策略：原来有值就采用原来的值，没有的时候采用后来的值
      if (!['description', 'display'].includes(key)) return;

      if (objValue) return objValue;
    });
  });

  return {
    pipelineUi,
    jobs,
  };
};

/**
 * 处理后台返回的PipelineData数据
 *
 * @param pipelineData
 */
export const disposePipelineData = function(pipelineData: PipelineModel, jobList: JobModel[]) {
  disposeJobsData(pipelineData, jobList);
  disposeEdgesData(pipelineData.pipelineConfig.edges);
  return pipelineData;
};

/**
 * 处理后台返回的Joblist数据
 *
 * @param entity 后台返回的作业数据
 */
export const disposeJobList = function(JobList: JobModel[], jobType: string) {
  return JobList
    .map(job => {
      if (!job.jobMeta.inTypes ) job.jobMeta.inTypes = job.jobRole === JobRoleTypeValue.source ? [] : [null];
      if (!job.jobMeta.outTypes ) job.jobMeta.outTypes = job.jobRole === JobRoleTypeValue.sink ? [] : [null];
      if (job.iconUrl && Project.serviceName) job.iconUrl = `/${Project.serviceName}${job.iconUrl}`;
      return job;
    })
    .filter(job => job.jobType === jobType && !!job.jobRole && !job.internal)
    .sort((a, b) => a.jobMeta.jobInfo?.display.localeCompare(b.jobMeta.jobInfo?.display));
};

/**
 * 获取后台需要的数据，
 *
 * @param pipelineData 需要处理的数据
 * @param isDeletePipelineUi 是否删除 PipelineUi
 */
export const getPipelineDataNoJobViewData = function(pipelineData, isDeletePipelineUi = false) {
  const newPipelineData = cloneDeep(pipelineData);
  isDeletePipelineUi && delete newPipelineData.pipelineUi;
  newPipelineData.pipelineConfig.jobs.forEach(job => {
    delete job.jobViewData;
  });
  return newPipelineData;
};

export const getPipelineData = function(pipelineDataJson, pipelineData) {
  pipelineDataJson.pipelineUi = pipelineData.pipelineUi;
  pipelineDataJson.pipelineConfig.jobs.forEach(job => {
    job.jobViewData = cloneDeep(pipelineData.pipelineConfig.jobs.find(ele => job.jobId === ele.jobId).jobViewData);
  });
  return pipelineDataJson;
};
