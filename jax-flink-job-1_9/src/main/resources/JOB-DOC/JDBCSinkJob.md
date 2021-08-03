# 数据输出到JDBC数据库

把数据通过jdbc批量写入到表中,需要通过columns参数来定义表的字段；

## 样例

主要参数如下:

```
{
  "password": "test",
  "dataSourceUrl": "jdbc:mysql://localhost:55037/test",
  "userName": "test",
  "columns": [
    {
      "columnType": "string",
      "columnName": "job_name"
    },
    {
      "columnType": "long",
      "columnName": "create_time"
    }
  ],
  "table": "tb_job"
}
```

注意：
- 需要把dataSourceUrl里用到的jdbc driver的jar包放在flink/lib，以确保运行时可以找到；
- 目前支持的jdbc方言有：ClickHouse,Vertica,Derby,MySQL,Postgres
