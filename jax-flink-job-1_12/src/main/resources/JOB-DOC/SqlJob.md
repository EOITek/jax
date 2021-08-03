# SQL执行

上游DataStream数据用StreamToTableJob转为table后，再执行SqlJob；下游可以通过TableToStreamJob再转回DataStream；
- 仅支持select语句做数据转化
- flink sql语法请参考： https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/table/sql.html


## 样例说明

sql操作一般的三部曲：

- 输入数据通过StreamToTableJob按event模式转化table，输出row_time做为watermark；
- 再执行SqlJob进行数据处理；
- 通过TableToStreamJob再转回DataStream，下游可以做其他的DataStream处理或sink；


输入如下：

```
{"ProviderId":"100002","ColSrcFlag":"1","MsgId":"a","timestamp":1610000383339,"DtgrmRl":1,"ReturnCode":""}
{"ProviderId":"100002","ColSrcFlag":"1","MsgId":"a","timestamp":1610000386339,"DtgrmRl":4,"ReturnCode":"0001"}
```

StreamToTableJob参数如下：

```
{
  "columns": [
    {
      "columnName": "ProviderId",
      "columnType": "string"
    },
    {
      "columnName": "ColSrcFlag",
      "columnType": "string"
    },
    {
      "columnName": "MsgId",
      "columnType": "string"
    },
    {
      "columnName": "timestamp",
      "columnType": "long"
    },
    {
      "columnName": "DtgrmRl",
      "columnType": "int"
    },
    {
      "columnName": "ReturnCode",
      "columnType": "string"
    }
  ],
  "eventTimeEnabled": true,
  "eventTimeOutputField": "row_time",
  "allowedLateness": 0,
  "timeFieldName": "timestamp",
  "timeFormat": "UNIX_MS",
  "eventTimeFromSource": false,
  "procTimeEnabled": false,
  "tableName": "req_resp",
  "windowOffsetBeiJing": false
}
```

SqlJob参数如下：

```
{
  "sql": "select * , (respTime - reqTime) as Duration from (select SESSION_START(row_time, interval '10' second) as `trans_time`,\nmax(`timestamp`) as `timestamp`,\nmax(ProviderId) as ProviderId,\nmax(ColSrcFlag) as ColSrcFlag,\nMsgId,\nmax(CASE WHEN DtgrmRl = 4 then ReturnCode else '' end) as ReturnCode,\nmax(CASE WHEN DtgrmRl = 4 then `timestamp` else 0 end) as respTime,\nmax(CASE WHEN DtgrmRl = 1 then `timestamp` else 0 end) as reqTime,\nmax(CASE WHEN ProviderId not in ('100002','100003') then 'master' WHEN ProviderId = '100002' then 'slave' else '' end) as CoreType\nfrom req_resp where ColSrcFlag ='1' and ProviderId like '1000%'\ngroup by MsgId, SESSION(row_time, interval '10' second)\n)",
  "tableName": "output"
}
```

TableToStreamJob无需指定参数；

输出聚合结果如下：

```
{
  "ProviderId": "100002",
  "ReturnCode": "0001",
  "ColSrcFlag": "1",
  "respTime": 1610000386339,
  "trans_time": 1609971583339,
  "Duration": 3000,
  "reqTime": 1610000383339,
  "CoreType": "slave ",
  "MsgId": "a",
  "timestamp": 1610000386339
}
```

