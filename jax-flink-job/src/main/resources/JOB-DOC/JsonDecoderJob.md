# 解码byte数组为JSON格式

先把byte数组转换为String，然后按json格式decode，
- 输出slot 0， 类型为`Map<String, Object>`，decode成功数据
- 输出slot 1， 类型为`Map<String, Object>`，decode失败数据（错误原因添加在error_msg字段）

例如输入的消息如下：

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

参数说明

- charset： byte数组解码为string的字符集 
- byteArrayFieldName：输入数据里byte数组所在字段名
- outputFieldName：反序列化后内容输出到指定的字段名，如果不指定则输出为整个顶层Map

如果按UTF-8解码byte数组为string，然后反序列化为json，并输出到jsonOutput字段
参数如下：

```
{
  "charset" : "UTF-8",
  "byteArrayFieldName" : "bytes",
  "outputFieldName" : "jsonOutput",
  "removeByteArrayField" : true
}
```


输出的数据如下（slot 0）：

```
{
  "jsonOutput": {
    "t1": "t1",
    "t2": "t2",
    "t3": "t3",
    "@hostname": "localhost",
    "@path": "/var/log/nginx/access.log",
    "@rownumber": 1,
    "@@id": "id1",
    "@@description": "172.16.128.235 - - [26/Nov/2019:14:05:13 +0800] GET /api/itoa/notice/noticeInbox/bubbleInbox HTTP/1.1 401 42 http://192.168.21.132/login?returnUrl=%2Fsystem%2Fsettings%3Ftabindex%3D%25E5%25AD%2598%25E5%2582%25A8%25E5%259C%25B0%25E5%259D%2580%25E7%25AE%25A1%25E7%2590%2586 Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36 7788 -",
    "@timestamp": 1564714680000,
    "stime": "2019/8/2 10:58:0"
  },
  "meta": {
    "topic": "kst",
    "partition": 0,
    "offset": 0,
    "timestamp": 1607571497339
  }
}
```

输出的错误数据（slot 1）：

```
{
  "error_msg": "Unrecognized token 'rrrr': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n at [Source: (String)\"rrrr\"; line: 1, column: 5]",
  "bytes": "cnJycg==",
  "meta": {
    "topic": "kst",
    "partition": 0,
    "offset": 130,
    "timestamp": 1607578767479
  }
}
```