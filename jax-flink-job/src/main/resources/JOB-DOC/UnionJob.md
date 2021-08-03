# 数据流合并

数据流合并Job, 最多支持4个数据流同时合并

- 在pipeline编排时，可以把2，3，4个数据流合并，输出一个数据合并后的数据流

例如

有以下3个SourceMapFunctionJob需要合并数据流：

```
[ {
  "jobName" : "com.eoi.jax.flink.job.test.util.SourceMapFunctionJob",
  "jobConfig" : { },
  "jobId" : "1"
}, {
  "jobName" : "com.eoi.jax.flink.job.process.UnionJob",
  "jobConfig" : { },
  "jobId" : "4"
}, {
  "jobName" : "com.eoi.jax.flink.job.test.util.SourceMapFunctionJob",
  "jobConfig" : { },
  "jobId" : "2"
}, {
  "jobName" : "com.eoi.jax.flink.job.test.util.SourceMapFunctionJob",
  "jobConfig" : { },
  "jobId" : "3"
}, {
  "jobName" : "com.eoi.jax.flink.job.test.util.JobIdCollectorSinkJob",
  "jobConfig" : { },
  "jobId" : "5"
} ]
```

编排的时候可以合并到一个job上，不同的数据流汇聚到UnionJob的不同的slot上，再输出到下游：

- 其中 slot 0 必填，slot 1，2，3为选填

```
[ {
  "fromSlot" : 0,
  "from" : "1",
  "to" : "4",
  "toSlot" : 0
}, {
  "fromSlot" : 0,
  "from" : "2",
  "to" : "4",
  "toSlot" : 1
}, {
  "fromSlot" : 0,
  "from" : "3",
  "to" : "4",
  "toSlot" : 2
}, {
  "fromSlot" : 0,
  "from" : "4",
  "to" : "5",
  "toSlot" : 0
} ]
```