# Json反序列化

将将字符串反序列化为Map：
- 输出slot 0， 类型为`Map<String, Object>`，decode成功数据
- 输出slot 1， 类型为`String`，原始字符串

假设输入数据如下：
```
{"name":"张三","age":1}
{"name":"李四","age":2}
{"name":"王五","age":
```

slot 0输出
```
{"name":"张三","age":1}
{"name":"李四","age":2}
```

slot 1输出
```
{"name":"王五","age":
```