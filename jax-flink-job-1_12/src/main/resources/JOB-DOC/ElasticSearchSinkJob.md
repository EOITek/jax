# 数据输出到Elasticsearch

通过配置Elasticsearch sink的参数，把数据输出到接入指定index

接入数据为`Map<String,Object>`类型

例如配置参数如下：

- 其中index名字：eoi_test_%{+yyyy_MM_dd}， 日期变量会根据时间字段（timeFieldName）来动态赋值，也确保时间格式指定正确（timeFormat）
- 如果指定docIdField，则为按docId更新模式，注意elasticsearch在大量数据更新情况下性能会快速下降。

```
{
  "docIdField" : "id",
  "bulkSize" : 10,
  "esNodes" : [ "localhost:9200" ],
  "indexType" : "_doc",
  "timeFieldName" : "time",
  "timeFormat" : "yyyy-MM-dd HH:mm:ss",
  "bulkActions" : 10000,
  "indexPattern" : "eoi_test_%{+yyyy_MM_dd}"
}
```

以下常用的配置参数：

| name | label | type | optional | default value | description |
| :--- | :---- | :--- | :------: | :-----------: | :---------- |
| indexPattern | index pattern | string | false |  | 写入es的索引模式，支持按照时间以及提取数据内的字段作为索引名，例如 jax_%{+yyyy-MM-dd}_%{field}, 动态字段需要以 %{}包含，包含+的为日期，否则为字段提取 |
| indexType | index type | string | true |  | 针对es7.x版本, 该字段必填, 而且只能为'_doc'; 针对es6.x以及以下版本, 该字段必填, 用户可以自定义 |
| esNodes | es http节点列表 | list,string | true | localhost:9200 | es节点列表，格式为host1:9200,host2:9200,host3:port3，默认为localhost:9200。支持前缀schema，如https://host3:port3。 |
| maxActions | 单批写入最大记录条数 | int | true | 1500 | 批量提交到es的记录条数条件，注意es的批处理条件包含：记录数，大小，以及时间间隔，三个条件只要有一个满足，就会提交到es。 |
| maxSize | 单批写入最大大小 | int | true | 10 | 批量提交到es的记录大小条件(单位MB)，注意es的批处理条件包含：记录数，大小，以及时间间隔，三个条件只要有一个满足，就会提交到es。 |
| flushInterval | Flush的最大时间间隔(毫秒) | int | true | 1000 | 批量提交到es的时间间隔条件(单位毫秒)，注意es的批处理条件包含：记录数，大小，以及时间间隔，三个条件只要有一个满足，就会提交到es。 |
| flushBackOffEnable | 是否启用Flush backoff | boolean | true | false | 是否启用Flush backoff |
| backoffType | Flush backoff类型 | string | true | CONSTANT | Flush backoff类型 |
| backoffRetries | Flush backoff重试次数 | int | true | 10000 | Flush backoff重试次数 |
| backoffDelay | Flush backoff延迟 | int | true | 60000 | Flush backoff延迟 |
| docIdField | Doc ID的字段 | string | true |  | Doc ID的字段 |
| header | http header | MAP | true |  | http header，如果es设定了安全策略，可以利用该参数设定对应的token |
| requestTimeout | 请求es超时时间 | string | true | 60000 | 请求es超时时间，默认为1分钟 |
| authEnable | 是否开启es安全认证机制 | boolean | true | false | 是否开启es安全认证机制？默认为false，如果设置为true，需要提供auth.user以及auth.password |
| authUser | 访问es用户名 | string | true |  | 访问es用户名 |
| authPassword | 访问es用户密码 | string | true |  | 访问es用户密码 |
| shouldThrowExMessages | es异常黑名单 | list | true |  | es写入失败的error message包含字符串，则直接报出异常，不继续任务； 多个用逗号分隔 |
| timeFieldName | 时间字段 | string | true | @timestamp | 通过时间字段设置带日期的index名;如果index pattern里有日期变量则必填 |
| timeFormat | 时间格式 | string | true | UNIX_MS | 支持UNIX_S, UNIX_MS以及通用时间表达格式;如果指定时间字段则必填 |
| timeLocale | 时间格式方言 | string | true | en-US | 支持用户设定时间方言，默认的时区为: en-US，可选的时区参见列表: https://www.andiamo.co.uk/resources/iso-language-codes |
