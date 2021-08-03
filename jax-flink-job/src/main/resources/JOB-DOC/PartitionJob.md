# PartitionJob

对于stream进行partitioning，用于做数据平衡解决数据倾斜问题。

## 参数说明

Partition类型，partitionType：

- rebalance(Round-robin partitioning)
- shuffle(Random partitioning)
- rescale(local Round-robin partitioning)

具体逻辑请参考：
https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/stream/operators/#physical-partitioning

