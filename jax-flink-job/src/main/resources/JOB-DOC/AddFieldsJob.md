# 列操作添加

参数说明

- fields： 列操作定义列表

## 添加或替换列操作

参数如下：

```
{
  "fields" : [ {
    "key" : "newCol1",
    "expression" : "value * 2"
  }, {
    "key" : "newCol2",
    "expression" : "1/0",
    "fallback" : "default value"
  }, {
    "key" : "newCol1",
    "expression" : "#time"
  }, {
    "key" : "value",
    "expression" : "value + 100",
    "isReplace" : true
  }, {
    "key" : " 'v-' + value",
    "keyIsExp" : true,
    "expression" : "value + 100",
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
{"newCol2":"default value","newCol1":2,"host":"a","time":"2020-11-22 12:00:00","value":101,"v-101":201}
{"newCol2":"default value","newCol1":4,"host":"b","v-102":202,"time":"2020-11-22 12:00:01","value":102}
```

## 通过内置函数计算新增的列值

在aviator表达式里，内置了eoi模块，其中有max，min，avg，sum方法可供调用，参数都是不定参数列表。

参数如下：

```
{
  "fields" : [ {
    "key" : "max",
    "expression" : "eoi.max(value1,value2,value3)"
  }, {
    "key" : "min",
    "expression" : "eoi.min(value1,value2,value3)"
  }, {
    "key" : "avg",
    "expression" : "eoi.avg(value1,value2,value3)"
  }, {
    "key" : "sum",
    "expression" : "eoi.sum(value1,value2,value3)"
  } ]
}
```

输入如下：

```
{
  "value1": 1,
  "value2", 10.9,
  "value3", 8.9,
  "host", "h1"
}
```

输出的数据如下：

```
{"min":1,"avg":6.933333333333334,"value2":10.9,"value1":1,"max":10.9,"value3":8.9,"host":"h1","sum":20.8}
```