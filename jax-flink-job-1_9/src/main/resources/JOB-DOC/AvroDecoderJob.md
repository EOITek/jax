# 解码byte数组为Avro格式

根据指定的avro schema（json格式定义），把byte数组按avro格式decode
- 输出slot 0， 类型为`Map<String, Object>`，decode成功数据
- 输出slot 1， 类型为`Map<String, Object>`，decode失败数据（错误原因添加在error_msg字段）

例如输入的消息如下：

```
{
    "bytes" : "jJiT81sEYWEAAAAAAAAABEA=",
    "metaData" : {
      "topic" : null,
      "partition" : 0,
      "offset" : 0,
      "timestamp" : 1607596831371
    }
}
{
    "bytes" : "oJiT81sEYmIC",
    "metaData" : {
      "topic" : null,
      "partition" : 0,
      "offset" : 0,
      "timestamp" : 1607596831371
    }
}
{
  "bytes" : "YWE=",
  "metaData" : {
    "topic" : null,
    "partition" : 0,
    "offset" : 0,
    "timestamp" : 1607596831371
  }
}
```

参数说明

- avroSchema： avro schema定义,json格式表示
- byteArrayFieldName：输入数据里byte数组所在字段名
- outputFieldName：反序列化后内容输出到指定的字段名，如果不指定则输出为整个顶层Map

参数如下：

```
{
  "byteArrayFieldName" : "bytes",
  "outputFieldName" : "output",
  "avroSchema" : "{  \"type\": \"record\",  \"name\": \"test\",  \"fields\": [    {      \"name\": \"a\",      \"type\": \"long\"    },  {      \"name\": \"b\",      \"type\": \"string\"    },{      \"name\": \"c\",      \"type\": [\"double\",\"null\"]   }  ]}"
}
```


输出的数据如下（slot 0）：

```
{"output":{"a":12334556678,"b":"aa","c":2.5},"metaData":{"topic":null,"partition":0,"offset":0,"timestamp":1607596831371}}
{"output":{"a":12334556688,"b":"bb","c":null},"metaData":{"topic":null,"partition":0,"offset":0,"timestamp":1607596831371}}

```

输出的错误数据（slot 1）：

```
{"metaData":{"topic":null,"partition":0,"offset":0,"timestamp":1607596831371},"error_msg":"Malformed data. Length is negative: -49","bytes":"YWE="}
```