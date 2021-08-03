# 数据过滤分流

根据指定过滤的aviator的boolean表达式，分流原始数据流为2个数据流。
字段引用方法请参考：https://www.yuque.com/boyan-avfmj/aviatorscript/ugbmqm#h1jfN

- 输出表达式为true的数据到 slot 0
- 输出表达式为false的数据到slot 1

例如

参数说明

- rule： aviator的boolean表达式

参数如下：

```
{
  "rule" : "time > '2020-11-22 11:59:00' && value * 2 < 5 && #nested.n[0].n1 == 'a'"
}
```

输入如下：

```
{"host":"a","time":"2020-11-22 12:00:00","value":1,"nested":{"n":[{"n1":"a"}]}}
{"host":"a","time":"2020-11-22 12:00:01","value":2}
{"host":"b","time":"2020-11-22 12:00:00","value":2}
{"host":"b","time":"2020-11-22 12:00:01","value":3}
```

输出的条件为true如下（slot 0）：

```
{"host":"a","time":"2020-11-22 12:00:00","value":1,"nested":{"n":[{"n1":"a"}]}}
```

输出的条件为false数据（slot 1）：

```
{"host":"a","time":"2020-11-22 12:00:01","value":2}
{"host":"b","time":"2020-11-22 12:00:00","value":2}
{"host":"b","time":"2020-11-22 12:00:01","value":3}
```