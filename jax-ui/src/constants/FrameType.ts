const FrameType = Object.freeze({
  flink: { label: 'flink', value: 'flink' },
  spark: { label: 'spark', value: 'spark' },
});

const FrameTypeList = Object.values(FrameType);

const FrameTypeValue: {
  [key in keyof typeof FrameType]: string;
} = Object.fromEntries(Object.entries(FrameType).map(x => [x[0], x[1].value])) as any;

export {
  FrameType,
  FrameTypeList,
  FrameTypeValue,
};
