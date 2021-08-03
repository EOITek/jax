# 数据输出到文件系统

- 通过配置File sink的参数，把数据输出到接入指定文件系统；
- 支持本地文件或hdfs；
- 写入数据格式支持json或csv格式；

## 重要提示
- checkpoint必须启用
- 正在写入未归档的文件有.inprogress后缀，只有在checkpoint成功后是才会去掉此后缀，成功归档文件
- 手动删除.inprogress文件会导致 File does not exist异常

## 配置参数如下

- basePath： 文件输出的路径，如：hdfs://192.168.31.133:9000/tmp
- fields：指定输出字段名列表
- bucketFormatString：存储文件的周期，在每个周期里会生成一目录,例如：分钟(yyyyMMddHHmm), 小时(yyyyMMddHH), 天(yyyyMMdd), 此处时间为服务器当前时间，并非事件时间
- dataFormat：数据存储格式（json 或 csv）
- maxPartSize：[rolling policy]文件最大size
- rolloverInterval：[rolling policy]文件滚动最大时间
- inactivityInterval：[rolling policy]文件闲置最大时间

例如：
```
{
  "inactivityInterval" : "1000",
  "basePath" : "hdfs://eoiNameService/tmp",
  "rolloverInterval" : "1000",
  "maxPartSize" : "1",
  "fields" : [ "time", "id", "value", "ts" ]
}
```
