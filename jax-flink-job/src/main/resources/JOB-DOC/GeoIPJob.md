# IP地址反查地理信息

通过IPv4地址查询地理信息，可输出国家、省、市、乡镇、运营商


例如:

参数如下：

- ipField: 指定ip字段名
- outputPrefix：指定输出地理信息字段名的前缀，以避免覆盖原始日志字段

```
{
  "ipField" : "ip",
  "outputPrefix" : "eoi_"
}
```

输入如下：

```
{
  "ip" : "169.235.24.133"
}
{
  "ip" : "220.248.44.62"
}
{
  "ip" : "180.167.157.90"
}
{
  "ip" : "278.248.44.62"
} 
```

输出的数据如下：

```
{"eoi_isp":"加州大学","ip":"169.235.24.133","eoi_province":"其他","eoi_town":"其他","eoi_city":"其他","eoi_country":"美国"}
{"eoi_isp":"联通","ip":"220.248.44.62","eoi_province":"上海","eoi_town":"其他","eoi_city":"其他","eoi_country":"中国"}
{"eoi_isp":"电信","ip":"180.167.157.90","eoi_province":"上海","eoi_town":"其他","eoi_city":"其他","eoi_country":"中国"}
{"ip":"278.248.44.62"}
```
