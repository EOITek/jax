# 列转行（宽表转高表）

多列转到同一列，即宽表转高表；原始字段名放到高表的目标字段名列，原始字段值放到高表的目标字段值列
   

- 输出成功转换的数据到 slot 0
- 输出缺失转换的数据到 slot 1

例如


参数如下：

```
{
  "sourceFields" : [ "n1", "n2", "n3" ],
  "targetKeyField" : "name",
  "targetValueField" : "value"
}
```

输入如下：

```
{
  "country" : "美国",
  "n1" : "mike",
  "n2" : "mary",
  "n3" : "tom",
  "isp" : "加州大学"
}
{
  "country" : "中国",
  "n1" : "Jason",
  "n2" : "Jack",
  "isp" : "联通"
}
{
  "country" : "美国",
  "n2" : "Trump",
  "isp" : "加州大学"
}
{
  "country" : "美国",
  "isp" : "加州大学"
}
{
  "country" : "美国",
  "n1" : null,
  "isp" : "加州大学"
}
```

输出的成功转换数据如下（slot 0）：

```
{"country":"美国","isp":"加州大学","name":"n1","value":"mike"}
{"country":"美国","isp":"加州大学","name":"n2","value":"mary"}
{"country":"美国","isp":"加州大学","name":"n3","value":"tom"}
{"country":"中国","isp":"联通","name":"n1","value":"Jason"}
{"country":"中国","isp":"联通","name":"n2","value":"Jack"}
{"country":"美国","isp":"加州大学","name":"n2","value":"Trump"}
{"country":"美国","isp":"加州大学","name":"n1","value":null}
```

输出的缺失转换数据（slot 1）：

```
{"country":"中国","isp":"联通","name":"n3","value":null}
{"country":"美国","isp":"加州大学","name":"n1","value":null}
{"country":"美国","isp":"加州大学","name":"n3","value":null}
{"country":"美国","isp":"加州大学","name":"n1","value":null}
{"country":"美国","isp":"加州大学","name":"n2","value":null}
{"country":"美国","isp":"加州大学","name":"n3","value":null}
{"country":"美国","isp":"加州大学","name":"n2","value":null}
{"country":"美国","isp":"加州大学","name":"n3","value":null}
```