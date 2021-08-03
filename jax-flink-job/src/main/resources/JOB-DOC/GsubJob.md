# 文本替换

对字符串进行普通替换或正则替换

- slot 0，转换成功结果
- slot 1，转换失败数据

## 示例1

将`,`替换为`-`，参数如下：

```json
{
  "sourceField": "message",
  "replaceMatch": ",",
  "replaceValue": "-"
}
```

输入数据：
```json
{
  "message": "china,shanghai,pudong"
}
```

输出数据：
```json
{
  "message": "china-shanghai-pudong"
}
```

## 示例2

使用正则替换对手机号进行掩码，`$1`和`$3`指正则匹配的第1组和第3组的值

```json
{
  "sourceField": "message",
  "regexReplace": true,
  "replaceMatch": "(\\d{3})(\\d{4})(\\d{4})",
  "replaceValue": "$1****$3"
}
```

输入数据：
```json
{
  "message": "18964722367"
}
```

输出数据：
```json
{
  "message": "189****2367"
}
```