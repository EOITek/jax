# 字段映射

`字段映射`类似一种基于字典的替换，常用于对一些不可读，但取值有限的字段，基于给定的字典替换成可读的字段。替换之后的值均为字符串类型

## 示例1
例如如下，原始日志：

```json
{
  "responseCode" : "00000"
}
```

将code翻译为可读含义，配置如下：

```json
{
  "sourceField" : "responseCode",
  "dictionary" : {
    "00000" : "成功",
    "00001" : "失败",
    "55555" : "异常"
  }
}
```

结果如下：

```json
{
  "responseCode" : "成功"
}
```

## 示例2
原始日志：

```json
{
  "responseCode" : "00000"
}
```

将翻译结果生成新的字段

```json
{
  "sourceField" : "responseCode",
  "outputField" : "responseStatus",
  "dictionary" : {
    "00000" : "成功",
    "00001" : "失败",
    "55555" : "异常"
  }
}
```

结果如下：

```json
{
  "responseCode" : "00000",
  "responseStatus" : "成功"
}
```


