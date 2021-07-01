import type { Graph } from '@antv/g6';

import type { PipelineEdge, PipelineJob, PipelineModel } from './pipeline';

interface BaseField {
  isDebugModel: boolean;
  isStreaming?: boolean;
}

export interface JobDagreInitG6Event extends BaseField {
  graph: Graph;
  pipelineData: PipelineModel;
}

export interface JobDagreGetGraphData extends BaseField {
  nodes: PipelineJob[];
  edges: PipelineEdge[];
  pipelineUi: Record<string, any>;
}

export interface JobDagreGetNodeItemConfig extends BaseField {
  nodeItemData: PipelineJob;
  jobUi: Record<string, any>;
}

export interface JobDagreGetEdgeItemConfig extends BaseField {
  edgeItemData: PipelineEdge;
  nodes: PipelineJob[];
}
