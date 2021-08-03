# 数据输出到Elasticsearch

根据redisCommand输出到redis的数据结构中；

其中对于HSET命令，输出是否为纯json格式要通过saveAsJson指定，具体输出格式请参考下面样例。


## 样例

输入的Map数据：

```
{
  "time" : "2020-11-22 12:00:00",
  "id" : "1",
  "value" : 1.2,
  "ts" : 1605689317000
}
{
  "time" : "2020-11-22 12:01:00",
  "id" : "2",
  "value" : 2.9,
  "ts" : 1605689317001
}
```

配置参数如下, saveAsJson 为false

```
{
  "keyField" : "id",
  "password" : "123456",
  "hosts" : [ "192.168.21.54:6379" ],
  "saveAsJson" : false,
  "redisMode" : "single",
  "redisCommand" : "HSET",
  "additionKey" : "redisSinkTest1"
}
```

输出到redis

```
192.168.21.54:6379> hgetall redisSinkTest11
1) "time"
2) "2020-11-22 12:00:00"
3) "id"
4) "1"
5) "value"
6) "1.2"
7) "ts"
8) "1605689317000"
192.168.21.54:6379> hgetall redisSinkTest12
1) "time"
2) "2020-11-22 12:01:00"
3) "id"
4) "2"
5) "value"
6) "2.9"
7) "ts"
8) "1605689317001"
```

配置参数如下, saveAsJson 为true

```
{
  "keyField" : "id",
  "password" : "123456",
  "hosts" : [ "192.168.21.54:6379" ],
  "saveAsJson" : true,
  "redisMode" : "single",
  "redisCommand" : "HSET",
  "additionKey" : "redisSinkTest1"
}
```

输出到redis

```
192.168.21.54:6379> HGETALL redisSinkTest1
1) "2"
2) "{\"time\":\"2020-11-22 12:01:00\",\"id\":\"2\",\"value\":2.9,\"ts\":1605689317001}"
3) "1"
4) "{\"time\":\"2020-11-22 12:00:00\",\"id\":\"1\",\"value\":1.2,\"ts\":1605689317000}"
```
