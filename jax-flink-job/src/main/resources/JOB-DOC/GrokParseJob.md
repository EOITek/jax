# 正则解析

对指定的字段进行正则解析，从而提取需要的字段。支持使用%{xxx}来引用预先定义的正则规则。
成功解析输出到slot 0，失败输出到slot 1。

## 示例1

该例子展示了如何提取timestamp和pingMsg

原始日志:

```json
{
  "message" : "[1562746060000000] 64 bytes from 180.101.49.12 (180.101.49.12): icmp_seq=1 ttl=51 time=8.34 ms"
}
```

解析规则:

```json
{
  "sourceField" : "message",
  "matches": [
    "\\[(?<timestamp>.*?)\\] (?<pingMsg>.*)"
  ]
}
```

结果：
```json
{
  "message" : "[1562746060000000] 64 bytes from 180.101.49.12 (180.101.49.12): icmp_seq=1 ttl=51 time=8.34 ms",
  "timestamp" : "1562746060000000",
  "pingMsg" : "64 bytes from 180.101.49.12 (180.101.49.12): icmp_seq=1 ttl=51 time=8.34 ms"
}
```

## 示例2

该例子展示了如何结合自定义提取和预定义提取，其中的`${USER}`和`${DATA}`都是预定义的正则。比如`${USER}`代表`[a-zA-Z0-9._-]+`。
`%{USER:user}`表示使用与定义的`${USER}`提取信息，并将结果设置到字段`user`上。

原始日志:

```json
{
  "message" : "2015-12-27T15:44:19+0800 childe - this is a test line"
}
```

解析规则:

```json
{
  "sourceField" : "message",
  "matches": [
    "^(?<logtime>\\S+) %{USER:user} (-|(?<level>\\w+)) %{DATA:msg}$"
  ]
}
```

结果：
```json
{
  "user" : "childe",
  "logtime" : "2015-12-27T15:44:19+0800",
  "msg" : "this is a test line",
  "message" : "2015-12-27T15:44:19+0800 childe - this is a test line"
}
```

## 示例3

该例子是一个典型的apache日志的解析，使用预定义正则能极大的简化解析规则的编写。

原始日志:

```json
{
  "message" : "172.16.128.28 - - [14/Jun/2019:03:58:12 +0800] \"GET /api/itoa/notice/noticeInbox/bubbleInbox HTTP/1.1\" 200 106 \"http://192.168.31.92/299898104999936/management/filter/?edit=1\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36\" \"-\" 0.016"
}
```

解析规则:

```json
{
  "sourceField" : "message",
  "matches": [
    "%{COMMONAPACHELOG} %{QS:referrer} %{QS:agent} \"(?<xforwardfor>[0-9\\-]+)\" (?<requesttime>[0-9\\.]+)"
  ]
}
```

结果：
```json
{
  "message" : "172.16.128.28 - - [14/Jun/2019:03:58:12 +0800] \"GET /api/itoa/notice/noticeInbox/bubbleInbox HTTP/1.1\" 200 106 \"http://192.168.31.92/299898104999936/management/filter/?edit=1\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36\" \"-\" 0.016",
  "clientIp" : "172.16.128.28",
  "ident" : "-",
  "auth" : "-",
  "timestamp" : "14/Jun/2019:03:58:12 +0800",
  "verb" : "GET",
  "request" : "/api/itoa/notice/noticeInbox/bubbleInbox",
  "httpversion" : "1.1",
  "response" : "200",
  "bytes" : "106",
  "referrer" : "\"http://192.168.31.92/299898104999936/management/filter/?edit=1\"",
  "agent" : "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36\"",
  "xforwardfor" : "-",
  "requesttime" : "0.016"
}
```