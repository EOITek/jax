# 键值提取

KV用于将某字段的kv样式的值转为json类型

## 示例1

这是一个KV解析的例子

原始日志:

```json
{
    "msg" : "a=aaa b=bbb c=ccc xyz=123 123=xyz"
}
```

解析规则:

```json
{
    "sourceField" : "msg",
    "fieldSplit" : " ",
    "valueSplit" : "=",
    "excludeKeys" : [ "b", "c" ],
    "includeKeys" : [ "a", "b", "xyz"]
}
```

结果:

```json
{
    "msg" : "a=aaa b=bbb c=ccc xyz=123 123=xyz",
    "a" : "aaa",
    "xyz" : "123"
}

```

## 示例2

这是一个将结果输出个指定字段的例子

原始日志:

```json
{
    "msg" : "a=aaa b=bbb c=ccc xyz=123 123=xyz"
}
```

解析规则:

```json
{
    "sourceField" : "msg",
    "fieldSplit" : " ",
    "valueSplit" : "=",
    "excludeKeys" : [ "b", "c" ],
    "includeKeys" : [ "a", "b", "xyz"],
    "outputField": "kv"
}
```

结果:

```json
{
    "msg" : "a=aaa b=bbb c=ccc xyz=123 123=xyz",
    "kv" : {
        "a" : "aaa",
        "xyz" : "123"
    }
}
```