# JS脚本处理

通过javascript对数据进行1对1，或者1对N解析。输入的Map类型的数据，在js中视为 `doc` json对象。可使用js语言对`doc`对象进行修改。
js代码的最后一行结果会作为最终结果返回，可以是一个对象，也可以是一个数组。如果是对象，相当于1对1解析；如果是数组，相当于1对N解析。
另外一种特殊情况是，如果返回null，表示丢弃这条数据。

javascript在处理json对象的时候，具有方便灵活的特点。
js引擎(nashorn)完全支持 ECMAScript 5.1 规范以及一些扩展。

例如输入的消息如下（此消息可能是经过正则提取后的web访问日志，省略了无关的字段）：

```
{"bytes": "4056", "url": "/api/v1/jars", "code": "200", "respTime": "20"}
{"bytes": "289767", "url": "/api/v1/jobs", "code": "200", "respTime": "200"}
{"bytes": "18761187", "url": "/api/v1/file/DGddayS", "code": "200", "respTime": "5500"}
{"bytes": "2348", "url": "/api/v1/jars/1", "code": "200", "respTime": "20"}
{"bytes": "230", "url": "/api/v1/aa", "code": "404", "respTime": "20"}
{"bytes": "230", "url": "/api/v1/bb", "code": "404", "respTime": "19"}
{"bytes": "5341", "url": "/api/v1/bb", "code": "500", "respTime": "30"}
{"bytes": "1025", "url": "/api/v1/auth", "code": "403", "respTime": "34"}
```

希望做如下解析

1. 将 bytes 转化为 float 类型，保留 2 位小数
2. 根据 bytes 大小，增加 1 个字段，如果 bytes < 1024，增加 dw = bytes， 如果 bytes > 1024， bytes = bytes / 1024，增加 dw = kb， 如果 bytes > 1024 * 1024， bytes = bytes / 1024 / 1024 ，等价 dw = mb 
3. 将 response 返回码替换为中文说明，如 1xx: 服务器收到请求 2xx: 请求成功 3xx: 重定向 4xx：客户端错误 5xx: 服务器错误，注意是替换，不是新增字段

script如下：

```javascript
var dw = doc.bytes;
if (dw > 1024 * 1024) 
    dw = (parseFloat(doc.bytes)/1024.0/1024.0).toFixed(2) + 'mb'; 
else if (dw > 1024) 
    dw = (parseFloat(doc.bytes)/1024.0).toFixed(2) + 'kb';
var code_desc = doc.code; 
if (doc.code.startsWith('1')) 
    code_desc = '服务器收到请求'; 
else if (doc.code.startsWith('2')) 
    code_desc = '请求成功'; 
else if (doc.code.startsWith('3')) 
    code_desc = '重定向'; 
else if (doc.code.startsWith('4')) 
    code_desc = '客户端错误'; 
else if (doc.code.startsWith('5')) 
    code_desc = '服务器错误'; 
doc.dw = dw; 
doc.code = code_desc; 
doc
```


输出的数据如下：

```
{"code":"请求成功","bytes":"4056","respTime":"20","url":"/api/v1/jars","dw":"3.96kb"}
{"code":"请求成功","bytes":"289767","respTime":"200","url":"/api/v1/jobs","dw":"282.98kb"}
{"code":"请求成功","bytes":"18761187","respTime":"5500","url":"/api/v1/file/DGddayS","dw":"17.89mb"}
{"code":"请求成功","bytes":"2348","respTime":"20","url":"/api/v1/jars/1","dw":"2.29kb"}
{"code":"客户端错误","bytes":"230","respTime":"20","url":"/api/v1/aa","dw":"230"}
{"code":"客户端错误","bytes":"230","respTime":"19","url":"/api/v1/bb","dw":"230"}
{"code":"服务器错误","bytes":"5341","respTime":"30","url":"/api/v1/bb","dw":"5.22kb"}
{"code":"客户端错误","bytes":"1025","respTime":"34","url":"/api/v1/auth","dw":"1.00kb"}
```