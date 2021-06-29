# 根据aviator表达式处理数据

- aviator脚本代码处理数据。可在脚本中直接引用Map的Key作为变量，引用方法请参考：https://www.yuque.com/boyan-avfmj/aviatorscript/ugbmqm#h1jfN
- 脚本的最后一行为结果返回给引擎（不需要分号，也可以通过return语法返回，参考：https://www.yuque.com/boyan-avfmj/aviatorscript/sgdfkw）。
- 如果最后以分号结束 或 最后一行为nil 表示返回null，视为丢弃这条记录（相当于过滤的功能）;
- 可以用seq.map返回单个对象；或用数组seq.list返回多个对象; 集合类型语法：https://www.yuque.com/boyan-avfmj/aviatorscript/ban32m
- 如果返回的不是Map类型，则统一使用 `__result__` 字段来存储返回值；


- 输出成功处理的数据到 slot 0
- 输出处理异常的数据到 slot 1，并添加error_msg字段说明异常原因

## 使用样例

参数说明

- script： Aviator表达式
- topLevel：设置true时，把数据Map放在doc字段下，表达式里需要通过 doc.columnName 来引用

输入数据：

```
  {
    "value2": 10.9,
    "value1": 1,
    "values": [
      2.4,
      3.6,
      4.8
    ],
    "value3": 8.9
  }
  {
    "value2": 10.9,
    "value1": 1,
    "values": [
      2.4
    ],
    "value3": 8.9
  }
```

### 样例1
返回map，异常处理返回到slot 1

Aviator表达式

```
let result = values[1] + value1 + value2*value3;
seq.map(\"result\", result, \"values\",values,\"value1\",value1,\"value2\",value2,\"value3\",value3)
```

输出（slot 0）：

```
{"result":101.61,"value2":10.9,"value1":1,"values":[2.4,3.6,4.8],"value3":8.9}
```

输出异常（slot 1）：

```
{"error_msg":"Index: 1, Size: 1","value2":10.9,"value1":1,"values":[2.4],"value3":8.9}
```

### 样例2
返回List<Map>

Aviator表达式

```
let list = seq.list();
for i in values {
    seq.add(list, seq.map(\"item\", i));
}
list
```

输出（slot 0）：

```
{"item":2.4}
{"item":3.6}
{"item":4.8}
{"item":2.4}
```

### 样例3
script编译错误

Aviator表达式

```
error script
```

任务启动失败。

### 样例4
返回values字段为List<Double>，每个item是double数值，默认使用`__result__`字段来存储返回值；

Aviator表达式

```
values
```

输出（slot 0）：

```
{"__result__":2.4}
{"__result__":3.6}
{"__result__":4.8}
{"__result__":2.4}
```

### 样例5
返回value1字段为int类型，默认使用`__result__`字段来存储返回值；

Aviator表达式

```
value1
```

输出（slot 0）：

```
{"__result__":1}
{"__result__":1}
```

### 样例6
返回Map，使用内置模块`custom_fn.av`的`min`函数处理数据

目前有4个内置函数，参数为数值类型，个数不定：
- max
- min
- avg
- sum

Aviator表达式

```
let eoi = require('custom_fn.av');
seq.map(\"min\", eoi.min(value1,value2,value3))
```

输出（slot 0）：

```
{"min":1}
{"min":1}
```

### 样例7
返回nil 或 最后一句以分号结尾，表示没有输出值

Aviator表达式

```
let eoi = require('custom_fn.av');
seq.map(\"min\", eoi.min(value1,value2,value3)); # 以分号结尾表示返回nil

或

nil
```

输出（slot 0）：

```
无
```

### 样例8 
这里通过doc对象获取所有的keys；例如：可通过doc.value1获取value1字段的值；


参数
```
{
  "script" : "seq.keys(doc)",
  "topLevel" : true // 把数据Map放在doc字段下，表达式里需要通过 doc.{columnName} 来引用；
}
```

输出（slot 0）：

```
{"__result__":"value2"}
{"__result__":"value1"}
{"__result__":"values"}
{"__result__":"value3"}
{"__result__":"value2"}
{"__result__":"value1"}
{"__result__":"values"}
{"__result__":"value3"}
```

### 样例9 
这里在doc对象新增value8字段等于value1+value2，然后返回doc

参数
```
{
  "script" : "doc.value8 = doc.value1 + doc.value2;doc",
  "topLevel" : true
}
```

输出（slot 0）：

```
{"value2":10.9,"value1":1,"values":[2.4,3.6,4.8],"value3":8.9,"value8":11.9}
{"value2":10.9,"value1":1,"values":[2.4],"value3":8.9,"value8":11.9}
```
