# 设置watermark(1.12)

在EventTime时间模式下设置watermark，为窗口聚合做准备。


参数说明

- watermarkSource: 来源包括：数据内时间字段（time_field）或系统时间（sys_time）
- timeFieldName: 时间字段：通过时间字段设置水位线；watermarkSource为time_field时必填
- timeFormat: 时间格式：支持UNIX_S, UNIX_MS以及通用时间表达格式；watermarkSource为time_field时必填

1.12新增参数
- idlenessTime：最大没有输入数据时间（秒），当某个分区没有数据超过此设置时，系统会生成watermark时会忽略此分区的事件时间，
请参考：https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/event_timestamps_watermarks.html#dealing-with-idle-sources
- reuseWatermark：是否复用已有的watermark，当数据流之前已经设置过watermark，是否复用

## 最大可容忍乱序时间（lagBehindTime）
生成的watermark等于当前数据时间减去此配置时间，也就是聚合窗口延迟关闭，可容忍数据晚到时间范围

例子：输入数据

```
{"host":"a","time":"2020-11-22 12:00:09","value":1}  #时间已超过12:00:00
{"host":"a","time":"2020-11-22 11:59:00","value":1}  #上一个窗口数据
{"host":"b","time":"2020-11-22 12:00:00","value":1}
```

配置如下

```
{
  "watermarkSource" : "time_field",
  "timeFieldName" : "time",
  "timeFormat" : "yyyy-MM-dd HH:mm:ss",
  "lagBehindTime" : 10 #最大可容忍乱序时间为10s
}
```

输出

```
{"@timestamp":"2020-11-22 11:59:00","count":1,"host":"a"}
{"@timestamp":"2020-11-22 12:00:00","count":1,"host":"b"}
{"@timestamp":"2020-11-22 12:00:00","count":1,"host":"a"}
```
- 最大可容忍乱序时间为10s，则直到12:00:10才会关闭11:59:00~12:00:00的窗口
- 第一条2020-11-22 12:00:09输入时，11:59:00~12:00:00的窗口还没有关闭，所以可以被统计到。
- 如果lagBehindTime设置为0，则第一条2020-11-22 12:00:09输入时，11:59:00~12:00:00的窗口会被关闭，导致2020-11-22 11:59:00的统计窗口就无法输出

## 最大可跳跃时间（maxGapTime）
数据时间可超过当前watermark的最大时间，如果超过则不设置watermark，0表示不限制

例子：输入数据

```
{"host":"a","time":"2020-11-22 12:00:09","value":1}  
{"host":"a","time":"2020-11-22 11:59:00","value":1}  
{"host":"b","time":"2020-11-22 12:00:00","value":1}
{"host":"a","time":"2020-11-22 12:01:10","value":1} # 12:01:10比之前最大12:00:09跳跃超过60s，不设置watermark
{"host":"a","time":"2020-11-22 12:00:59","value":1}
```

配置如下

```
{
  "watermarkSource" : "time_field",
  "timeFieldName" : "time",
  "timeFormat" : "yyyy-MM-dd HH:mm:ss",
  "maxGapTime" : 60 # 最大可跳跃时间60s
}
```

输出

```
{"@timestamp":"2020-11-22 12:00:00","count":2,"host":"a"}
{"@timestamp":"2020-11-22 12:00:00","count":1,"host":"b"}
{"@timestamp":"2020-11-22 12:01:00","count":1,"host":"a"}
```
- 12:01:10比之前最大12:00:09跳跃超过60s, 不设置watermark，使得12:00:00 ~ 12:01:00窗口没有提前触发，使得后来的12:00:59的a可以被统计到

## 最大可偏离时间（maxDeviateTime）
数据时间可偏离当前系统时间的最大值，如果超过则不设置watermark，0表示不限制

例子：输入数据
- 例如当前时间为：1608186842842

```
{"host":"a","time":1608186903842,"value":1} # 超过当前时间61s
{"host":"a","time":1608186842842,"value":1} 

```

配置如下

```
{
  "watermarkSource" : "time_field",
  "timeFieldName" : "time",
  "timeFormat" : "yyyy-MM-dd HH:mm:ss",
  "maxDeviateTime" : 60 # 最大可偏离时间60s
}
```

输出

```
{"@timestamp":1608186840000,"count":1,"host":"a"}
{"@timestamp":1608186900000,"count":1,"host":"a"}
```
- 1608186903842超过当前时间61s, 不设置watermark，使得1608186840000 ~ 1608186900000窗口没有提前触发，使得后来的1608186842842的a可以被统计到
