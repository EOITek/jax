# JS数据发生器(json)

使用javascript脚本来生成模拟数据。由于脚本比较灵活，可以方便在生成模拟数据的时候植入逻辑。
js代码的最后一行结果会作为最终结果返回，可以是一个对象，也可以是一个数组。如果是数组，每次会产生多条数据。
js引擎(nashorn)完全支持 ECMAScript 5.1 规范以及一些扩展。

例如，使用如下脚本

```javascript
var code = ["200", "101", "404", "500", "403"];
var a = {
	timestamp: new Date().getTime(), 
	bytes: Math.floor(Math.random() * 101),
	code: code[Math.floor(Math.random() * 5)]
};
a
```

对应生成如下示例数据

```
{"code":"403","bytes":62.0,"timestamp":1.606208627228E12}
{"code":"403","bytes":12.0,"timestamp":1.606208629028E12}
{"code":"101","bytes":66.0,"timestamp":1.606208629546E12}
{"code":"404","bytes":32.0,"timestamp":1.606208630052E12}
{"code":"500","bytes":15.0,"timestamp":1.606208630557E12}
{"code":"200","bytes":15.0,"timestamp":1.606208631061E12}
{"code":"101","bytes":66.0,"timestamp":1.606208631565E12}
{"code":"200","bytes":9.0,"timestamp":1.60620863207E12}
{"code":"500","bytes":87.0,"timestamp":1.606208632573E12}
```

例如，使用下面代码可以产生具备一定随机性的交易串联数据

```
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
var rnd_pick = function(candidates) { return candidates[Math.floor(Math.random() * candidates.length)]; };
var t1 = new Date(); 
var t2 = new Date(t1.getTime() + Math.floor(Math.random() * 100));
var provider = ["100002", "100003", "100004", "xxxxxxx"];
var code = ["0002", "0001", "0000"];
var trans = ["tc1", "tc2", "tc3", "tc4", "tc5", "tc6"];
var flag = ["0", "1"];
var f = rnd_pick(flag);
var messageId = t1.getTime() + "";
[{time: t1.Format("yyyy-MM-dd hh:mm:ss.S"), messageId: messageId, type: "request" , provider: rnd_pick(provider), trans: rnd_pick(trans), flag: f },
{time: t2.Format("yyyy-MM-dd hh:mm:ss.S"), messageId: messageId, type: "response", provider: rnd_pick(provider), code: rnd_pick(code), trans: rnd_pick(trans), flag: f }]
```

- 上述例子使用`prototype`为Date增加了Format方法，以输出格式化的时间
- 随机产生100ms以内的随机"时延"
- 通过返回数组，可以同时产生成对的的请求响应消息

下面是样例输出：

```
{"flag":"0","provider":"xxxxxxx","messageId":"1606568756966","time":"2020-11-28 21:05:56.966","type":"request","trans":"tc1"}
{"code":"0000","flag":"0","provider":"100004","messageId":"1606568756966","time":"2020-11-28 21:05:56.984","type":"response","trans":"tc4"}
{"flag":"0","provider":"xxxxxxx","messageId":"1606568758149","time":"2020-11-28 21:05:58.149","type":"request","trans":"tc2"}
{"code":"0000","flag":"0","provider":"100004","messageId":"1606568758149","time":"2020-11-28 21:05:58.200","type":"response","trans":"tc6"}
{"flag":"0","provider":"xxxxxxx","messageId":"1606568758665","time":"2020-11-28 21:05:58.665","type":"request","trans":"tc3"}
{"code":"0002","flag":"0","provider":"100003","messageId":"1606568758665","time":"2020-11-28 21:05:58.707","type":"response","trans":"tc6"}
{"flag":"0","provider":"100004","messageId":"1606568759174","time":"2020-11-28 21:05:59.174","type":"request","trans":"tc4"}
{"code":"0001","flag":"0","provider":"100002","messageId":"1606568759174","time":"2020-11-28 21:05:59.194","type":"response","trans":"tc2"}
{"flag":"1","provider":"100002","messageId":"1606568759681","time":"2020-11-28 21:05:59.681","type":"request","trans":"tc5"}
{"code":"0002","flag":"1","provider":"100002","messageId":"1606568759681","time":"2020-11-28 21:05:59.712","type":"response","trans":"tc4"}
{"flag":"0","provider":"xxxxxxx","messageId":"1606568760189","time":"2020-11-28 21:06:00.189","type":"request","trans":"tc3"}
```