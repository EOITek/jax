# 字段丰富（异步查询）

通过jdbc异步查询从指定表获取数据来丰富字段。

适用场景：适用于存量数据比较大的表或时效要求较高的场景，每一条数据都做异步关联查询。

使用flink async io的方式提高查询效率，但不保证输出数据的原始顺序：https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/stream/operators/asyncio.html


根据查询结果输出分3种情况：

- 根据指定的key关联，查询到结果（默认取第一条），把表里的其他字段丰富到数据里。
- 没有查询到结果，则返回原始数据
- 查询发生异常（包括数据库，sql异常或超时异常等），则返回原始数据 加上 `__error_msg__` 字段说明错误原因。

## 根据foreignKeys定义自动拼接关联条件的样例

参数如下：

其中foreignKeys定义了原始数据与数据库表之前的关联外键的字段名和类型。所有关联外键之间为 AND 的关系。

```
{
  "timeout" : 3000,
  "capacity" : 100,
  "jdbcDriver" : "com.mysql.jdbc.Driver",
  "dataSourceUrl" : "jdbc:mysql://192.168.31.46:3306/insper_dev?characterEncoding=utf8&useSSL=false&allowMultiQueries=true",
  "userName" : "root",
  "maximumPoolSize" : 2,
  "password" : "User@123",
  "tableName" : "tb_config_template",
  "foreignKeys" : [ {
    "foreignKey" : "confId",
    "type" : "long"
  }, {
    "foreignKey" : "confName",
    "type" : "string"
  }, {
    "foreignKey" : "createBy",
    "type" : "long"
  }, {
    "foreignKey" : "blackEnable",
    "type" : "boolean"
  } ]
}
```

以上配置动态生成的sql语句为：

```
select * from tb_config_template 
where confId = ?  and confName = ?  and createBy in (1, 2, 3)  and blackEnable is null 
```

输入原始数据：

```
{
  "confId" : 930549760263168,
  "confName" : "Firewall日志聚类模版",
  "createBy" : [ 1, 2, 3 ],
  "originCol" : "930549760263168"
}
```


正常查询到数据的输出：

```
{
  "confId": 930549760263168,
  "confDescription": "样例日志：Aug  1 00:46:41 182.241.132.23 snmpd[17677]: truncating integer value > 32 bits",
  "clzConfig": "{\"fieldName\":\"\",\"similarity\":0.6}",
  "whiteEnable": null,
  "deManEnable": null,
  "originCol": "930549760263168",
  "deAlgConfig": null,
  "updateTime": 1577435948106,
  "confName": "Firewall日志聚类模版",
  "createBy": 1,
  "deManConfig": null,
  "deAlgEnable": null,
  "createTime": 1577434925006,
  "updateBy": 1,
  "blackEnable": null
}
```

未查询到关联数据的输出同原始数据。

发生查询异常输出`__error_msg__`字段：

```
{
  "confId": 930549760263168,
  "confName": "Firewall日志聚类模版",
  "createBy": [1,2,3],
  "originCol": "930549760263168",
  "__error_msg__": "com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException: Table 'insper_dev.tb_config_template1' doesn't exist"
}
```

## 根据whereClause自定义拼接关联条件的样例

输入原始数据：

```
{
  "confId" : 930549760263168,
  "confName" : "",
  "createBy" : 1,
  "originCol" : "930549760263168"
}
```

配置中指定whereClause参数，需要确保参数个数和顺序与foreignKeys定义保持一致

```
{
  "timeout" : 3000,
  "capacity" : 100,
  "jdbcDriver" : "com.mysql.jdbc.Driver",
  "dataSourceUrl" : "jdbc:mysql://192.168.31.46:3306/insper_dev?characterEncoding=utf8&useSSL=false&allowMultiQueries=true",
  "userName" : "root",
  "maximumPoolSize" : 2,
  "password" : "User@123",
  "tableName" : "tb_config_template",
  "foreignKeys" : [ {
    "foreignKey" : "confId",
    "type" : "long"
  }, {
    "foreignKey" : "confName",
    "type" : "string"
  }, {
    "foreignKey" : "createBy",
    "type" : "long"
  } ],
  "whereClause" : "(confId = ? OR confName = ? ) and createBy = ?"
}
```

其中whereClause里有3个问号，代表3个参数，与foreignKeys定义里保持顺序一致。

拼接的sql语句

```
select * from tb_config_template 
where (confId = 930549760263168 OR confName = '' ) and createBy = 1
```

正常查询到数据的输出：

```
{
  "confId": 930549760263168,
  "confDescription": "样例日志：Aug  1 00:46:41 182.241.132.23 snmpd[17677]: truncating integer value > 32 bits",
  "clzConfig": "{\"fieldName\":\"\",\"similarity\":0.6}",
  "whiteEnable": null,
  "deManEnable": null,
  "originCol": "930549760263168",
  "deAlgConfig": null,
  "updateTime": 1577435948106,
  "confName": "Firewall日志聚类模版",
  "createBy": 1,
  "deManConfig": null,
  "deAlgEnable": null,
  "createTime": 1577434925006,
  "updateBy": 1,
  "blackEnable": null
}
```