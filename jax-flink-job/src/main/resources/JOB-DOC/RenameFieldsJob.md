# 列操作重命名

参数说明

- fields： 列操作定义列表

例如:

参数如下：

```
{
  "fields" : [ {
    "oldField" : "host",
    "newField" : "HOST"
  }, {
    "oldField" : "value",
    "newField" : "time"
  }, {
    "oldField" : "time",
    "newField" : "value",
    "isReplace" : true
  } ]
}
```

输入如下：

```
{
  "host" : "a",
  "time" : "2020-11-22 12:00:00",
  "value" : 1
}
{
  "host" : "b",
  "time" : "2020-11-22 12:00:01",
  "value" : 2
}
```

输出的数据如下：

```
{"HOST":"a","value":"2020-11-22 12:00:00"}
{"HOST":"b","value":"2020-11-22 12:00:01"}
```
