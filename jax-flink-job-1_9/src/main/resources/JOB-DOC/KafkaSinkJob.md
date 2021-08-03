# 数据输出到Kafka

通过配置kafka sink的参数，把数据输出到接入指定topic，
可支持大部分kafka选项参数，并支持SSL安全传输和SASL安全认证机制，具体请参考相关参数详情。

接入数据为`Map<String,Object>`类型

例如配置参数如下：

```
{
  "autoCreateTopic" : true,
  "bootstrapServers" : [ "localhost:58272" ],
  "topic" : "topicSink",
  "semantic" : "AT_LEAST_ONCE",
  "kafkaPartitionType" : "ROUND_ROBIN",
  "kafkaProducerProperties" : {
    "max.request.size" : "10240",
    "batch.size" : "100"
  }
}
```

kafkaProducerProperties`Map<String,String>`，key为参数名，value统一为String类型，
可添加其他原生kafka producer配置项，具体有哪些可选填项请参考：https://kafka.apache.org/documentation/#producerconfigs

以下常用的配置参数以供参考：

| name | label | type | optional | default value | description |
| :--- | :---- | :--- | :------: | :-----------: | :---------- |
| max.request.size | 最大请求大小 | int | true | 1024000 | 最大请求大小，单位字节 |
| batch.size | 单次批请求大小 | int | true | 500 | 单次批请求大小,这个配置的值最好不要超过32MB，有系统的硬限制在，单位字节 |
| compression.type | 压缩方式 | string | true | lz4 | kafka 消息生产压缩方式，支持类型如下：none: 不压缩, gzip: gzip格式压缩，snappy: snappy格式压缩, lz4: lz4格式压缩.  |


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
