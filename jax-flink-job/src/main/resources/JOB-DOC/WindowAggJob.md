# 窗口聚合统计
按指定分组和窗口进行聚合统计；
数值类型支持 `max`,`min`,`avg`,`sum`；所有类型支持 `last`,`first`,`count`,`collect`,`calc`

- 输出正常聚合结果到 slot 0
- 输出因为晚到数据而再次触发窗口的聚合结果到slot 1

必填参数说明
- windowType: 聚合窗口类型:tumbling, sliding, session, global,请参考：https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/stream/operators/windows.html
- windowSize: 聚合窗口时间(单位毫秒), 只有当窗口类型为global时是可选；
- slidingSize： 滑动窗口滑动时间(单位毫秒)，窗口类型为sliding时需指定；
- aggDefs: 聚合定义，包括聚合字段，聚合统计方法和输出字段；其中输出字段为必填且不能重复；聚合统计方法也为必填；

## 窗口触发器（高级参数）
默认是default，按指定窗口类型的默认trigger触发；

内置了三种特殊的窗口触发器，通过triggerType参数指定；
- 1. count，按count触发：窗口内数据count数大于等于此最大值时窗口被触发
- 2. delta，按delta触发：通过triggerDeltaExp计算值大于此阈值时窗口被触发
- 3. expression，按表达式触发：通过此表达式从map数据中计算得出boolean值，如果为true时窗口被触发

```
当窗口类型为global时，需要自定义触发器，否则窗口永远不触发；
另外，窗口类型为tumbling, sliding, session时，默认是按窗口结束时间触发；
但也可以指定自定义触发器，用于覆盖原有的默认触发器；
```

可自定义四种触发后的动作：https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/stream/operators/windows.html#triggers
- CONTINUE: do nothing,
- FIRE: trigger the computation,
- PURGE: clear the elements in the window, and
- FIRE_AND_PURGE: trigger the computation and clear the elements in the window afterwards.


## 样例1

输入数据
```
  {
    "value2": 2,
    "host2": "c",
    "host": "a",
    "time": "2020-11-22 12:00:00",
    "value": 1
  },
  {
    "value2": 4,
    "host2": "c",
    "host": "a",
    "time": "2020-11-22 12:00:01",
    "value": 2
  },
  {
    "value2": 4,
    "host2": "c",
    "host": "b",
    "time": "2020-11-22 12:00:00",
    "value": 2
  },
  {
    "value2": 6,
    "host2": "c",
    "host": "b",
    "time": "2020-11-22 12:00:01",
    "value": 3
  }
```

配置如下

WatermarkJob配置

```
{
  "watermarkSource": "time_field",
  "timeFieldName": "time",
  "timeFormat": "yyyy-MM-dd HH:mm:ss"
}
```

WindowAggJob配置

```
{
  "windowType" : "tumbling",
  "windowSize" : 60000,
  "aggDefs" : [ {
    "fieldName" : "value",
    "aggMethod" : "max",
    "outputFieldName" : "max"
  }, {
    "fieldName" : "value",
    "aggMethod" : "min",
    "outputFieldName" : "min"
  }, {
    "fieldName" : "value2",
    "aggMethod" : "avg",
    "outputFieldName" : "value2_avg"
  }, {
    "fieldName" : "value2",
    "aggMethod" : "max",
    "outputFieldName" : "v2_max"
  } ],
  "groupKeys" : [ "host", "host2" ],
  "windowTimeColumn" : "@timestamp",
  "windowTimeFormat" : "yyyy-MM-dd HH:mm:ss",
  "windowTimeOffsetType" : "start"
}
```

输出
```
{"value2_avg":3.0,"min":1.0,"@timestamp":"2020-11-22 12:00:00","max":2.0,"host2":"c","v2_max":4.0,"host":"a"}
{"value2_avg":5.0,"min":2.0,"@timestamp":"2020-11-22 12:00:00","max":3.0,"host2":"c","v2_max":6.0,"host":"b"}
```

## 使用过滤条件

- 通过参数filterExp指定过滤条件表达式，基于输入数据执行表达式返回true或false
- 只有返回true的输入数据会参与聚合计算

输入数据

```
  {
    "value2": 2,
    "host2": "c",
    "host": "a",
    "time": "2020-11-22 12:00:00",
    "value": 1
  },
  {
    "value2": 4,
    "host2": "c",
    "host": "a",
    "time": "2020-11-22 12:00:01",
    "value": 2
  },
  {
    "value2": 4,
    "host2": "c",
    "host": "b",
    "time": "2020-11-22 12:00:00",
    "value": 2
  },
  {
    "value2": 6,
    "host2": "c",
    "host": "b",
    "time": "2020-11-22 12:00:01",
    "value": 3
  }
```

配置

```
{
  "windowType" : "tumbling",
  "windowSize" : 60000,
  "aggDefs" : [ {
    "fieldName" : "value",
    "aggMethod" : "sum",
    "outputFieldName" : "v_sum",
    "filterExp" : "host == 'a' && value >= 2",
  } ],
  "groupKeys" : [ "host" ],
  "windowTimeFormat" : "yyyy-MM-dd HH:mm:ss"
}
```

输出

```
{"@timestamp":"2020-11-22 12:00:00","host":"a","v_sum":2.0}
```

## 使用表达式计算变量

- 指定"aggMethod"为"calc"，并填入"calc"计算表达式参数
- 通过其他方法的输出字段 或 窗口时间字段 或 分组字段 为变量来指定aviator表达式，计算聚合结果；
- 变量名如果包含特殊字符串，需要以 # 符号开始,再使用两个 \` 符号来包围，例如： #\`@timestamp\`；

输入数据
```
{"value2":1.3333333,"host2":"c","host":"a","time":"2020-11-22 12:00:00","value":1}
{"value2":1.3333333,"host2":"c","host":"a","time":"2020-11-22 12:00:01","value":2}
{"value2":1.3333333,"host2":"c","host":"b","time":"2020-11-22 12:00:00","value":2}
{"value2":1.3333333,"host2":"c","host":"b","time":"2020-11-22 12:00:01","value":3}
```

配置

```
{
  "windowType" : "tumbling",
  "windowSize" : 60000,
  "aggDefs" : [ {
    "fieldName" : "value",
    "aggMethod" : "max",
    "outputFieldName" : "max"
  }, {
    "fieldName" : "value",
    "aggMethod" : "min",
    "outputFieldName" : "min"
  }, {
    "fieldName" : "value2",
    "aggMethod" : "avg",
    "scale" : 3,
    "outputFieldName" : "_value2_avg" 
  }, {
    "fieldName" : "value2",
    "aggMethod" : "sum",
    "outputFieldName" : "v2_sum"
  }, {
    "fieldName" : "calc1",
    "aggMethod" : "calc",
    "calc" : "max + 2*min + _value2_avg",
    "outputFieldName" : "calc1"
  }, {
    "fieldName" : "calc2",
    "aggMethod" : "calc",
    "calc" : "#`@timestamp` + '.000+08:00'",
    "outputFieldName" : "calc2"
  } ],
  "groupKeys" : [ "host", "host2" ],
  "windowTimeFormat" : "yyyy-MM-dd HH:mm:ss"
}
```

输出

```
{"v2_sum":2.6666666,"min":1.0,"@timestamp":"2020-11-22 12:00:00","max":2.0,"_value2_avg":1.333,"host2":"c","host":"a","calc1":5.333,"calc2":"2020-11-22 12:00:00.000+08:00"}
{"v2_sum":2.6666666,"min":2.0,"@timestamp":"2020-11-22 12:00:00","max":3.0,"_value2_avg":1.333,"host2":"c","host":"b","calc1":8.333,"calc2":"2020-11-22 12:00:00.000+08:00"}
```

## 使用collect聚合收集数据

- 当aggMethod为collect时，需要指定收集的字段名列表（collectFieldNames）
- 可限制收集条数（limitCount）
- 可指定收集策略（collectType），与limitCount一起使用，取前多少条（top），取后多少条（bottom），随机取多少条（random）
- 根据窗口内数据量大小，collect操作可能会消耗较大内存，使用时请谨慎评估。

输入数据
```
{"host":"a","time":"2020-11-22 12:00:00","value":1}
{"host":"a","time":"2020-11-22 12:00:01","value":2}
{"host":"b","time":"2020-11-22 12:00:00","value":2}
{"host":"b","time":"2020-11-22 12:00:01","value":3}

```

配置

```
{
  "windowType" : "tumbling",
  "windowSize" : 60000,
  "aggDefs" : [ {
    "fieldName" : "Collect1",
    "aggMethod" : "collect",
    "collectFieldNames" : [ "time", "value" ],
    "outputFieldName" : "Collect1"
  }, {
    "fieldName" : "Collect2",
    "aggMethod" : "collect",
    "collectFieldNames" : [ "time", "value", "host", "notExisted" ],
    "limitCount" : 1,
    "collectType" : "top",
    "outputFieldName" : "Collect2"
  }, {
    "fieldName" : "Collect3",
    "aggMethod" : "collect",
    "collectFieldNames" : [ "time", "value", "host" ],
    "limitCount" : 1,
    "collectType" : "bottom",
    "outputFieldName" : "Collect3"
  }, {
    "fieldName" : "Collect4",
    "aggMethod" : "collect",
    "collectFieldNames" : [ "time", "value", "host" ],
    "limitCount" : 3,
    "collectType" : "random",
    "outputFieldName" : "Collect4"
  } ],
  "groupKeys" : [ "host" ],
  "windowTimeFormat" : "yyyy-MM-dd HH:mm:ss"
}
```

输出

```
{"@timestamp":"2020-11-22 12:00:00","Collect4":[{"host":"a","time":"2020-11-22 12:00:00","value":1},{"host":"a","time":"2020-11-22 12:00:01","value":2}],"Collect3":[{"host":"a","time":"2020-11-22 12:00:01","value":2}],"Collect2":[{"notExisted":null,"host":"a","time":"2020-11-22 12:00:00","value":1}],"Collect1":[{"time":"2020-11-22 12:00:00","value":1},{"time":"2020-11-22 12:00:01","value":2}],"host":"a"}
{"@timestamp":"2020-11-22 12:00:00","Collect4":[{"host":"b","time":"2020-11-22 12:00:00","value":2},{"host":"b","time":"2020-11-22 12:00:01","value":3}],"Collect3":[{"host":"b","time":"2020-11-22 12:00:01","value":3}],"Collect2":[{"notExisted":null,"host":"b","time":"2020-11-22 12:00:00","value":2}],"Collect1":[{"time":"2020-11-22 12:00:00","value":2},{"time":"2020-11-22 12:00:01","value":3}],"host":"b"}
```

## 使用表达式计算聚合字段的值

- 通过指定聚合字段表达式（fieldExp）计算得出参与聚合字段的值

输入数据

```
{"value2":100,"host":"a","time":"2020-11-22 12:00:00","value":1}
{"value2":100,"host":"a","time":"2020-11-22 12:00:01","value":2}
{"value2":100,"host":"b","time":"2020-11-22 12:00:00","value":2}
{"value2":100,"host":"b","time":"2020-11-22 12:00:01","value":3}
```

配置

```
{
  "windowType" : "tumbling",
  "windowSize" : 60000,
  "slidingSize" : null,
  "aggDefs" : [ {
    "fieldExp" : "value + value2",
    "aggMethod" : "sum",
    "filterExp" : "__valueByExp__ > 0", // 过滤时可通过特殊变量：__valueByExp__ 引用到fieldExp计算得出的值
    "outputFieldName" : "fx"
  } ],
  "groupKeys" : [ "host" ],
  "windowTimeFormat" : "yyyy-MM-dd HH:mm:ss"
}
```

输出

```
{"fx":203.0,"@timestamp":"2020-11-22 12:00:00","host":"a"}
{"fx":205.0,"@timestamp":"2020-11-22 12:00:00","host":"b"}
```


## 聚合字段的值去重后聚合

- 设置是否去重计算（distinct）为true，对于窗口内所有输入数据聚合字段的值，去重后再聚合

输入数据

```
{"value2":100,"host":"a","time":"2020-11-22 12:00:00","value":1}
{"value2":100,"host":"a","time":"2020-11-22 12:00:01","value":2}
{"value2":100,"host":"b","time":"2020-11-22 12:00:00","value":2}
{"value2":100,"host":"b","time":"2020-11-22 12:00:01","value":3}
```

配置

```
{
  "windowType" : "tumbling",
  "windowSize" : 60000,
  "aggDefs" : [ {
    "fieldName" : "value2",
    "aggMethod" : "sum",
    "distinct" : true,
    "outputFieldName" : "value2_distinct_sum"
  }, {
    "fieldName" : "value2",
    "aggMethod" : "count",
    "distinct" : true,
    "outputFieldName" : "value2_distinct_count"
  }, {
    "fieldName" : "c2",
    "aggMethod" : "collect",
    "collectFieldNames" : [ "value2" ],
    "distinct" : true,
    "outputFieldName" : "value2_distinct_collect"
  } ],
  "groupKeys" : [ "host" ],
  "windowTimeFormat" : "yyyy-MM-dd HH:mm:ss"
}
```

输出

```
{"@timestamp":"2020-11-22 12:00:00","value2_distinct_collect":[{"value2":100}],"host":"a","value2_distinct_sum":100.0,"value2_distinct_count":1}
{"@timestamp":"2020-11-22 12:00:00","value2_distinct_collect":[{"value2":100}],"host":"b","value2_distinct_sum":100.0,"value2_distinct_count":1}
```

## 聚合窗口类型和自定义触发器count

- 当窗口类型为global时，需要自定义触发器，否则窗口永远不触发

下面样例以global window 加 count trigger为例：

- triggerMaxCount： 窗口内数据count数大于等于此最大值时窗口被触发

输入数据

```
{"host":"a","time":"2020-11-22 12:00:00","value":1}
{"host":"a","time":"2020-11-22 12:00:01","value":2}
{"host":"b","time":"2020-11-22 12:00:00","value":2}
{"host":"b","time":"2020-11-22 12:00:01","value":3}
```

配置

```
{
  "windowType" : "global",
  "triggerType" : "count",
  "triggerResultType" : "FIRE_AND_PURGE",
  "triggerMaxCount" : 1,
  "aggDefs" : [ {
    "fieldName" : "value",
    "aggMethod" : "count",
    "distinct" : false,
    "outputFieldName" : "value"
  } ],
  "groupKeys" : [ "host" ]
}
```

输出

```
{"host":"a","value":1}
{"host":"a","value":1}
{"host":"b","value":1}
{"host":"b","value":1}
```

## 聚合窗口类型和自定义触发器delta

下面样例以tumbling window 加 delta trigger为例：

输入数据

```
{"host":"a","time":"2020-11-22 12:00:00","value":1}
{"host":"a","time":"2020-11-22 12:00:01","value":2}
{"host":"b","time":"2020-11-22 12:00:00","value":2}
{"host":"b","time":"2020-11-22 12:00:01","value":3}
```

配置

- triggerDeltaValueField ： 比较值字段名，需为double类型
- triggerDeltaExp：阈值计算表达式，此表达式内可以使用变量为oldValue（上一条数据的delta比较值）和 newValue（本条数据的delta比较值）;例如：newValue - oldValue*2.5 ，通过此表达式计算得出delta阈值
- triggerDeltaThreshold：触发阈值，通过triggerDeltaExp计算值大于此阈值时窗口被触发


```
{
  "windowType" : "tumbling",
  "triggerType" : "delta",
  "triggerResultType" : "FIRE",
  "triggerDeltaValueField" : "value",
  "triggerDeltaExp" : "newValue - oldValue",
  "triggerDeltaThreshold" : 0.9,
  "windowSize" : 60000,
  "aggDefs" : [ {
    "fieldName" : "value",
    "aggMethod" : "count",
    "distinct" : false,
    "outputFieldName" : "value"
  } ],
  "groupKeys" : [ "host" ],
  "windowTimeFormat" : "yyyy-MM-dd HH:mm:ss"
}
```

输出

```
{"@timestamp":"2020-11-22 12:00:00","host":"a","value":2}
{"@timestamp":"2020-11-22 12:00:00","host":"b","value":2}
```

## 聚合窗口类型和自定义触发器 表达式

下面样例以tumbling window 加 Expression trigger为例：

输入数据

```
{"host":"a","time":"2020-11-22 12:00:00","value":1}
{"host":"a","time":"2020-11-22 12:00:01","value":2}
{"host":"b","time":"2020-11-22 12:00:00","value":2}
{"host":"b","time":"2020-11-22 12:00:01","value":3}
```

配置

- triggerExpression ：通过此表达式从map数据中计算得出boolean值，如果为true时窗口被触发

```
{
  "windowType" : "tumbling",
  "triggerType" : "expression",
  "triggerResultType" : "FIRE_AND_PURGE",
  "triggerExpression" : "value >= 2",
  "windowSize" : 60000,
  "aggDefs" : [ {
    "fieldName" : "value",
    "aggMethod" : "count",
    "distinct" : false,
    "outputFieldName" : "value"
  } ],
  "groupKeys" : [ "host" ],
  "windowTimeFormat" : "yyyy-MM-dd HH:mm:ss"
}
```

输出

```
{"@timestamp":"2020-11-22 12:00:00","host":"a","value":2}
{"@timestamp":"2020-11-22 12:00:00","host":"b","value":1}
{"@timestamp":"2020-11-22 12:00:00","host":"b","value":1}
```

如果窗口触发动作从FIRE_AND_PURGE改为FIRE

```
"triggerResultType" : "FIRE",
```

则输出为

```
{"@timestamp":"2020-11-22 12:00:00","host":"a","value":2}
{"@timestamp":"2020-11-22 12:00:00","host":"b","value":1}
{"@timestamp":"2020-11-22 12:00:00","host":"b","value":2}
```