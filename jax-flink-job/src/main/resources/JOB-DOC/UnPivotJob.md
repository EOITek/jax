# 列转行

对指定的字段进行列转行
- 输出slot 0，转换成功数据
- 输出slot 1，转换失败数据

## 示例：

参数如下：

```
{
  "sourceField" : "names",
  "targetField" : "name",
  "delimiter" : ","
}
```

例如输入的消息如下：

```
{
  "country" : "美国",
  "names" : "mike,mary,tom,jerry",
  "isp" : "加州大学"
}
{
  "country" : "中国",
  "names" : [ "Jason", "Jack" ],
  "isp" : "联通"
}
{
  "country" : "美国",
  "isp" : "加州大学"
}
{
  "country" : "美国",
  "names" : null,
  "isp" : "加州大学"
}
{
  "country" : "美国",
  "names" : "",
  "isp" : "加州大学"
}
{
  "country" : "美国",
  "names" : [ ],
  "isp" : "加州大学"
} 
```

输出的数据（slot 0）：

```
{"country":"美国","names":"mike,mary,tom,jerry","isp":"加州大学","name":"mike"}
{"country":"美国","names":"mike,mary,tom,jerry","isp":"加州大学","name":"mary"}
{"country":"美国","names":"mike,mary,tom,jerry","isp":"加州大学","name":"tom"}
{"country":"美国","names":"mike,mary,tom,jerry","isp":"加州大学","name":"jerry"}
{"country":"中国","names":["Jason","Jack"],"isp":"联通","name":"Jason"}
{"country":"中国","names":["Jason","Jack"],"isp":"联通","name":"Jack"}
```

输出的错误数据（slot 1）：

```
{"country":"美国","isp":"加州大学"}
{"country":"美国","names":null,"isp":"加州大学"}
{"country":"美国","names":"","isp":"加州大学"}
{"country":"美国","names":[],"isp":"加州大学"}
```
