# JDBC数据接入

通过jdbc定时执行查询sql语句获取源数据，每次都全量返回查询语句的返回结果。

适用场景：适用于字典表或维度表此类比较小的查询结果，定时返回全量数据；

下游可以通过广播job（EnrichByBroadcastJob）来丰富字段

## 全量获取

例如配置参数如下：

```
{
  "querySql" : "select * from (SELECT * FROM insper_dev.tb_config_template) a where updateTime > 1577435917626",
  "jdbcDriver" : "com.mysql.jdbc.Driver",
  "password" : "User@123",
  "dataSourceUrl" : "jdbc:mysql://192.168.31.46:3306/insper_dev?characterEncoding=utf8&useSSL=false&allowMultiQueries=true",
  "interval" : 1000,
  "maximumPoolSize" : 10,
  "userName" : "root"
}
```

输出类型`Map<String,Object>`，返回查询语句所有的select列。

## 增量获取

- 必须指定自增列（incrementField，数值类型且保持单调自增），每次查询会大于上次获取到的最大值。
- JDBCSourceJob会自动记录获取上次获取到的最大自增列值，重启任务后可以自动恢复。
- 选填自增列起始值（incrementStart），可指定从大于起始值开始获取数据。

例如配置参数如下：

```
{
  "querySql" : "SELECT * FROM insper_dev.tb_config_template",
  "jdbcDriver" : "com.mysql.jdbc.Driver",
  "password" : "User@123",
  "dataSourceUrl" : "jdbc:mysql://192.168.31.46:3306/insper_dev?characterEncoding=utf8&useSSL=false&allowMultiQueries=true",
  "incrementField" : "updateTime",
  "incrementStart" : "1577435917626",
  "interval" : 1000,
  "maximumPoolSize" : 2,
  "userName" : "root"
}
```
