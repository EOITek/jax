# 时间格式转换

按指定输入输出时间格式进行转换,并可输出到指定字段
- 输出slot 0，转换成功数据
- 输出slot 1，转换失败数据（错误原因添加在error_msg字段）


##参数说明

| name | label | type | optional | default value | description |
| :--- | :---- | :--- | :------: | :-----------: | :---------- |
| timestampField | 输入时间字段 | string | false |  | 输入时间字段名 |
| outputTimestampField | 输出时间字段 | string | true | 同输入时间字段 | 输出时间字段名 |
| fromFormat | 输入时间格式 | string | false |  | 支持UNIX_S, UNIX_MS以及通用时间表达格式 |
| fromLocale | 输入时间格式时区 | string | true | en-US | 支持用户设定时间时区，默认的时区为: en-US，可选的时区参见列表: https://www.andiamo.co.uk/resources/iso-language-codes |
| timestampTimezone | 时区 | string | true |  | 时间所在时区, 空字符串表示不需要考虑时区, `Europe/London`,`PST` or `GMT+5` |
| toFormat | 输出时间格式 | string | true | UNIX_MS | 与时间字段对应的，在转化成功后，再次转化的目标格式，支持UNIX_S, UNIX_MS以及通用时间表达格式 |
| toLocale | 输出时间格式时区 | string | true | en-US | 支持用户设定时间时区，默认的时区为: en-US，可选的时区参见列表: https://www.andiamo.co.uk/resources/iso-language-codes |


例如参数如下：

```
{
  "timestampField" : "time",
  "fromFormat" : "yyyy-MM-dd'T'HH:mm:ss.SSS",
  "fromLocale" : "zh-CN",
  "timestampTimezone" : "GMT+01:00",
  "toFormat" : "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
  "toLocale" : "en-US"
}
```

例如输入的消息如下：

```
{"time":"2020-11-22T12:00:10.000"}
{"time":"2020-11-22 12:00:10"}
```

输出的数据（slot 0）：

```
{"time":"2020-11-22T05:00:10.000+0100"}
```

输出的错误数据（slot 1）：

```
{"error_msg":"Invalid format: \"2020-11-22 12:00:10\" is malformed at \" 12:00:10\"","time":"2020-11-22 12:00:10"}
```