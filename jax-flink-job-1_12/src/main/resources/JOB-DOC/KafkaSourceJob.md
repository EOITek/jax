# Kafka数据接入【流批一体】

## 【流批一体】参数说明
### 流处理
- "bounded" : false ， 表示数据流无限
- stoppingOffset参数可选：默认不用设置，表示不会停止读取；也可以读取到指定位置结束任务；

```
{
  "autoCreateTopic" : true,
  "startingOffset" : {
    "offsetMode" : "earliest"
  },
  "byteArrayFieldName" : "bytes",
  "bootstrapServers" : [ "PLAINTEXT://localhost:55041" ],
  "topics" : [ "topicTest" ],
  "groupId" : "kst1618986384702",
  "kafkaConsumerProperties" : {
    "enable.auto.commit" : "false",
    "fetch.max.bytes" : "9009000"
  },
  "bounded" : false,
  "metaFieldName" : "meta"
}
```

### 批处理
- "bounded" : true ， 表示数据流有限
- stoppingOffset参数必填：指定读取到指定位置结束任务；

```
{
  "stoppingOffset" : {
    "offsetMode" : "timestamp",
    "timestamp" : 1605689317003
  },
  "autoCreateTopic" : true,
  "startingOffset" : {
    "offsetMode" : "earliest"
  },
  "byteArrayFieldName" : "bytes",
  "bootstrapServers" : [ "PLAINTEXT://localhost:55045" ],
  "topics" : [ "topicTest" ],
  "groupId" : "kst1618987004631",
  "kafkaConsumerProperties" : {
    "enable.auto.commit" : "false",
    "fetch.max.bytes" : "9009000"
  },
  "bounded" : true,
  "metaFieldName" : "meta"
}
```

### 指定offset模式
支持5种
- latest
- earliest
- committed（上次保存的位置）
- timestamp（指定时间点）
- specified（指定每个topic和partition的某一位置）

## 其他参数同KafkaByteSourceJob
通过配置kafka consumer的参数接入指定topic的数据，
可支持大部分kafka选项参数，并支持SSL安全传输和SASL安全认证机制，具体请参考相关参数详情。

输出`Map<String,Object>`类型，其中：
1. kafka message的value数据为byte数组类型，通过参数byteArrayFieldName指定输出字段名（默认为bytes）
2. kafka的元数据输出到参数metaFieldName指定字段名（默认为meta）

例如配置参数如下：

```
{
  "autoCreateTopic" : true,
  "byteArrayFieldName" : "bytes",
  "bootstrapServers" : "localhost:52098,localhost:52098",
  "topics" : [ "topicTest" ],
  "groupId" : "kst1607911850164",
  "offsetMode" : "earliest",
  "kafkaConsumerProperties" : {
    "enable.auto.commit" : "false",
    "fetch.max.bytes" : "9009000"
  },
  "metaFieldName" : "meta"
}
```
其中byte数组输出到bytes字段，元数据输出到meta字段，
则输出的数据如下：

```
{
  "bytes": "eyJ0MSI6InQxIiwidDIiOiJ0MiIsInQzIjoidDMiLCJAaG9zdG5hbWUiOiJsb2NhbGhvc3QiLCJAcGF0aCI6Ii92YXIvbG9nL25naW54L2FjY2Vzcy5sb2ciLCJAcm93bnVtYmVyIjoxLCJAQGlkIjoiaWQxIiwiQEBkZXNjcmlwdGlvbiI6IjE3Mi4xNi4xMjguMjM1IC0gLSBbMjYvTm92LzIwMTk6MTQ6MDU6MTMgKzA4MDBdIEdFVCAvYXBpL2l0b2Evbm90aWNlL25vdGljZUluYm94L2J1YmJsZUluYm94IEhUVFAvMS4xIDQwMSA0MiBodHRwOi8vMTkyLjE2OC4yMS4xMzIvbG9naW4/cmV0dXJuVXJsPSUyRnN5c3RlbSUyRnNldHRpbmdzJTNGdGFiaW5kZXglM0QlMjVFNSUyNUFEJTI1OTglMjVFNSUyNTgyJTI1QTglMjVFNSUyNTlDJTI1QjAlMjVFNSUyNTlEJTI1ODAlMjVFNyUyNUFFJTI1QTElMjVFNyUyNTkwJTI1ODYgTW96aWxsYS81LjAgKE1hY2ludG9zaDsgSW50ZWwgTWFjIE9TIFggMTBfMTRfMykgQXBwbGVXZWJLaXQvNTM3LjM2IChLSFRNTCwgbGlrZSBHZWNrbykgQ2hyb21lLzc4LjAuMzkwNC45NyBTYWZhcmkvNTM3LjM2IDc3ODggLSIsIkB0aW1lc3RhbXAiOjE1NjQ3MTQ2ODAwMDAsInN0aW1lIjoiMjAxOS84LzIgMTA6NTg6MCJ9",
  "meta": {
    "topic": "kst",
    "partition": 0,
    "offset": 0,
    "timestamp": 1607571497339
  }
}
```

kafkaConsumerProperties类型为`Map<String,String>`，key为参数名，value统一为String类型，
可添加其他原生kafka consumer配置项，具体有哪些可选填项请参考：https://kafka.apache.org/documentation/#consumerconfigs

以下常用的配置参数以供参考：

| name | label | type | optional | default value | description |
| :--- | :---- | :--- | :------: | :-----------: | :---------- |
| max.poll.records | 单次拉取最大条数 | int | true | 500 | Kafka Client 从 Kafka Server 单次拉取最大条数 |
| max.poll.interval.ms | 拉取数据超时时长 | int | true | 300000 | Kafka Client 从Kafka Server 拉取数据超时时长, 单位：毫秒 |
| session.timeout.ms | Session超时时长 | int | true | 10000 | 一个Kafka 会话的超时时长, 单位：毫秒 |
| heartbeat.interval.ms | Kafka心跳时长 | int | true | 3000 | Kafka Client到Kafka Server心跳时长，单位：毫秒 |
| enable.auto.commit | 自动commit配置 | boolean | true | true | 是否自动Commit Kafka Offset, true: 自动commit，false:不自动commit, 需要手工commit。注意当flink启用checkpoint后，flink会强制把这个参数设置为false，flink会接管offset的提交，在每次checkpoint时，会提交offset。 |
| auto.commit.interval.ms | 自动commit时间长度 | int | true | 5000 | 当enable.auto.commit=true时，Kafka Offset自动Commit时间长度,单位毫秒 |
| fetch.max.bytes | 单次拉取最大字节数 | int | true | 52428800 | 单次拉取最大字节数, 默认：50MB |
| max.partition.fetch.bytes | 单分区最大查询字节数 | int | true | 52428800 | 单分区最大查询字节数, 默认: 50MB |


以下常用SASL认证参数以供参考：

| name | label | type | optional | default value | description |
| :--- | :---- | :--- | :------: | :-----------: | :---------- |
| security.protocol | 认证协议 | string | true | PLAINTEXT | Protocol used to communicate with brokers. Valid values are: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL |
| sasl.kerberos.service.name | 认证服务名 | string | true | kafka | The Kerberos principal name that Kafka runs as. This can be defined either in Kafka's JAAS config or in Kafka's config. |
| sasl.mechanism | 认证策略 | string | true | GSSAPI | 用来指定SASL认证协议的策略：GSSAPI (Kerberos)，PLAIN，SCRAM-SHA-256(暂不支持)，SCRAM-SHA-512(暂不支持)，OAUTHBEARER(暂不支持)。 SASL mechanism used for client connections. This may be any mechanism for which a security provider is available. GSSAPI is the default mechanism. |
| sasl.jaas.config | sasl.jaas.config | string | true | GSSAPI | 用来指定SASL认证配置，例如：com.sun.security.auth.module.Krb5LoginModule required useKeyTab=true storeKey=true keyTab="/etc/security/keytabs/kafka_client.keytab" principal="kafkaclient1@EXAMPLE.COM";   说明： JAAS login context parameters for SASL connections in the format used by JAAS configuration files. JAAS configuration file format is described http://docs.oracle.com/javase/8/docs/technotes/guides/security/jgss/tutorials/LoginConfigFile.html". The format for the value is: 'loginModuleClass controlFlag (optionName=optionValue)*;'. For brokers, the config must be prefixed with listener prefix and SASL mechanism name in lower-case. For example, listener.name.sasl_ssl.scram-sha-256.sasl.jaas.config=com.example.ScramLoginModule required; |

以下常用SSL认证参数以供参考：

| name | label | type | optional | default value | description |
| :--- | :---- | :--- | :------: | :-----------: | :---------- |
| ssl.truststore.location | ssl.truststore.location | string | true |  | The location of the trust store file.  |
| ssl.truststore.password | ssl.truststore.password | string | true |  | The password for the trust store file. If a password is not set access to the truststore is still available, but integrity checking is disabled. |
| ssl.key.password | ssl.key.password | string | true |  | The password of the private key in the key store file. This is optional for client. |
| ssl.keystore.location | ssl.keystore.location | string | true |  | The location of the key store file. This is optional for client and can be used for two-way authentication for client. |
| ssl.keystore.password | ssl.keystore.password | string | true |  | The store password for the key store file. This is optional for client and only needed if ssl.keystore.location is configured.  |
| ssl.keystore.type | ssl.keystore.type | string | true | JKS | The file format of the key store file. This is optional for client. |
| ssl.truststore.type | ssl.truststore.type | string | true | JKS | The file format of the trust store file. |
| ssl.enabled.protocols | ssl.enabled.protocols | string | true | TLSv1.2,TLSv1.1,TLSv1 | The list of protocols enabled for SSL connections. |
| ssl.endpoint.identification.algorithm | ssl.endpoint.identification.algorithm | string | true | https | The endpoint identification algorithm to validate server hostname using server certificate.  |
| autoCreateTopic | 自动创建topics | boolean | true | false | 是否自动创建topic,  true: 自动，false:不创建, 默认: false |
| autoCreateTopicPartitions | 自动创建topics分区数 | int | true | 1 | autoCreateTopic=true时，自动创建topics分区数,默认1 |
| autoCreateTopicReplicationFactor | 自动创建topics副本数 | int | true | 1 | autoCreateTopic=true时，自动创建topics副本数,默认1 |
