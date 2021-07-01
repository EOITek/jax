export const StreamingJson = () => ({
  pipelineName: `pipeline_${+new Date()}`,
  pipelineType: 'streaming',
  pipelineConfig: {
    jobs: [],
    edges: [],
    opts: {},
  },
  pipeDescription: null,
  clusterName: null,
  pipelineUi: {},
});

export const BatchJson = () => ({
  pipelineName: `pipeline_${+new Date()}`,
  pipelineType: 'batch',
  pipelineConfig: {
    jobs: [],
    edges: [],
    opts: {},
  },
  pipeDescription: null,
  clusterName: null,
  pipelineUi: {},
});
