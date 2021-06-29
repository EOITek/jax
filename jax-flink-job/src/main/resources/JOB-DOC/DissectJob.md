# 文本解构

文本解构的目标是将非结构化的文本，基于某种规则模式，提取结构化的信息。与正则提取相比，文本解构具有如下优点：

1. 更容易编写规则和模式，不需要学习复杂的正则表达式
2. 解析速度比正则快，因为文本解构几乎不会回溯，所以性能上正则要好很多

详细的规则和模式见：[Dissect processor](https://www.elastic.co/guide/en/elasticsearch/reference/current/dissect-processor.html)

下面是一个简单的文本解构的例子，目标是提取web请求日志中的各种字段：

```json
{"message": "10.0.0.2 - - [30/Apr/1998:22:00:52 +0000] \"GET /english/venues/cities/images/montpellier/18.gif HTTP/1.0\" 200 3171" }
{"message": "10.0.0.1 - - [30/Apr/1998:22:01:52 +0000] \"GET /english/venues/cities/images/montpellier/19.gif HTTP/1.0\" 200 3172" }
```

配置如下：

```json
{
    "sourceField": "message",
    "dissectPattern": "%{clientip} %{ident} %{auth} [%{timestamp}] \"%{verb} %{request} HTTP/%{httpversion}\" %{status} %{size}"
}
```

输出结果如下，提取的结果同意作为字符串处理：

```json
{
    "message":"10.0.0.2 - - [30/Apr/1998:22:00:52 +0000] \"GET /english/venues/cities/images/montpellier/18.gif HTTP/1.0\" 200 3171",
    "request":"/english/venues/cities/images/montpellier/18.gif",
    "size":"3171",
    "auth":"-",
    "ident":"-",
    "clientip":"10.0.0.2",
    "verb":"GET",
    "httpversion":"1.0",
    "timestamp":"30/Apr/1998:22:00:52 +0000",
    "status":"200"
}

{
    "message":"10.0.0.1 - - [30/Apr/1998:22:01:52 +0000] \"GET /english/venues/cities/images/montpellier/19.gif HTTP/1.0\" 200 3172",
    "request":"/english/venues/cities/images/montpellier/19.gif",
    "size":"3172",
    "auth":"-",
    "ident":"-",
    "clientip":"10.0.0.1",
    "verb":"GET",
    "httpversion":"1.0",
    "timestamp":"30/Apr/1998:22:01:52 +0000",
    "status":"200"
}
```

**右补操作(`->`)**

解构算法默认是严格按照模式字符串进行提取的。例如对于模式`%{fookey} %{barkey}`(中间一个空格)，只能匹配"foo bar"(中间一个空格)，无法匹配"foo  bar"(中间l两个空格)。
右补操作可以处理这种需求。对于这个需求模式`%{fookey->} %{barkey}`，既可以匹配1个空格，也可以匹配2个或更多个空格。

右补操作还可以用于跳过不需要匹配的部分。例如，给定模式`[%{ts}]%{->}[%{level}]`，匹配文本`[1998-08-10T17:15:42,466]            [WARN]`
的结果是

```
ts = 1998-08-10T17:15:42,466
level = WARN
```

中间的空格被跳过了

**拼接操作(`+`)**

支持将多个结果拼接成一个结果，通过空格分隔。例如：`%{+name} %{+name} %{+name} %{+name}`匹配`john jacob jingleheimer schmidt`的结果为：

```
name = john jacob jingleheimer schmidt
```

可以结合`+`和`\n`改变拼接的顺序。例如：`%{+name/2} %{+name/4} %{+name/3} %{+name/1}`匹配`	
john jacob jingleheimer schmidt`的结果为(配置拼接字符为逗号时)：

```
name = schmidt,john,jingleheimer,jacob
```

**引用操作(`*和&`)**

引用操作类似于`key-value`提取，例如`[%{ts}] [%{level}] %{*p1}:%{&p1} %{*p2}:%{&p2}`
匹配`[2018-08-10T17:15:42,466] [ERR] ip:1.2.3.4 error:REFUSED`的结果为：

```
ts = 1998-08-10T17:15:42,466
level = ERR
ip = 1.2.3.4
error = REFUSED
```

通过引用文本中已有的值作为提取的key处理
