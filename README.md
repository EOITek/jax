
# Introduction 简介

JAX项目由**擎创数据团队**研发并开源的数据中台项目, 帮助企业构建数据中台，提供数据集成、数据处理、数据开发、作业管理等数据服务;

JAX提供可拖拽的流批作业开发,可以轻松实现实时和离线的数据清洗、指标计算、数据探索等功能; 

JAX是一款轻量级的数据平台，JAX本身并不强制与某种数据存储、计算框架绑定，也不与数据仓库、数据湖绑定，JAX更多的是构建一层数据抽象层，将企业中纷繁复杂的大数据基础设施统一起来，用户可以以平滑的方式将既有的数据存储和计算资源与JAX配合工作。JAX也有能力对接多种多样的数据库，例如关系型数据库、数据仓库、时序数据库、文档数据库等。也支持用户可选择不同类型的计算资源, 既可用选Hadoop, 也可以使用Kubernetes提供计算资源。


# Features 功能

### 功能概况
作业管理
* 流作业开发和管理
* 批作业开发和管理
	
算子管理
* 拓展包管理
* 算子列表: 

系统管理
* 集群管理
* 框架管理


### 功能详细介绍和使用

请参照 [Jax功能模块](docs/JaxFeatures.md) 详细了解Jax功能模块。




# Compile and Deploy 编译部署

### Environment Requriment

nodejs, npm, yarn, vue-cli
tar dos2unix 

### Jax运行环境

### Jax项目编译环境

Jax项目手动编译, 可在Window/Linux/Unix 准备好如下命令
- java
- maven
- nodejs 10.0+
- yarn/yarnpkg
- vue-cli
- dos2unix
- make
- docker-compose



### Download 预编译版下载



### Compile & Package 编译打包


cd 到jax的根目录, 使用make 命令编译和打包出整个jax-all-1.0.0-xxx.tar.gz安装包(位于其tmp目录下);

make package-all主要包括: 
- mvn clean package -DskipTests		后端编译打包
- yarn install && yarn build 		前端编译打包
- wget flink-xx.tgz && tar -zxf flink-xx.tgz 	下载并解压安装flink/spark


```sh
cd jax

make package-all

tar -xvf tmp/jax-all-*.tar.gz -C /opt/jax

```

编译打包注意点: 
- 如果make package-all失败, 可用尝试分别对后端项目(mvn clean package )和 前端项目(cd jax-ui && yarnpkg install & yarnpkg build) 进行编译测试, 确认环境没问题;
- make packag-all中使用 ./mvnw 第一次执行时可能很慢(在下载maven/wrapper包), 需耐心等待,不宜打断;
- 编译中若遇到报错,可参考 [常见问题列表](https://datasalon.yuque.com/staff-dg3tgh/pg6cpg/uem0ig) 


### Deploy 项目部署

项目启动前, 需要确保Hadoop已安装好;

解压jax-all-xxx.tar.gz 项目后, 直接执行其 start.sh脚本即可启动JAX项目, 如见到打印: Jax Application is Ready, 则一般项目启动成功;
```sh
$ ./start.sh

# 项目启动成功, 由如下打印
 _ _   |_  _ _|_. ___ _ |    _ 
| | |\/|_)(_| | |_\  |_)||_|_\ 
     /               |         
                        3.1.2 
Jax Application is Ready

```

如果项目启动成功, 默认jax-web访问端口是9999, 访问 http://{hostname}:9999 即可;

如果没有此打印,或者 9999端口未开,则可能项目启动失败, 具体原因详解 logs/jax-web.error.log 排查;
- 一般启动失败是 jax/application.yml中配置缺失或配错导致;
- 注意其中 jax.home, MySQL的DB和账号密码;


##### 开启Debug

```sh
export JAX_WEB_DEBUG_PORT=45000
```
在start.sh 脚本中,若JAX_WEB_DEBUG_PORT存在则会增加jvm debug参数, 方便进行远程Debug和调试;


### Docker镜像和运行

提前确认相关环境命令: 
- node/npm/yarn
- mvn/make 
- docker/docker-compose


默认安装的flink计算引擎是1.9.1版本,并会启1个Flink Standalone模式容器, 命令如下: 

With flink 1.9 standalone cluster:
```sh
$ make image
$ docker-compose up
```



With flink 1.12 standalone cluster:

```sh
$ make image
$ FLINK_IMAGE=flink:1.12.3-scala_2.11 docker-compose up
```





# Documents 

- 编译部署和环境准备相关问题, 可参见  [** 常见问题列表 **](https://datasalon.yuque.com/staff-dg3tgh/pg6cpg/uem0ig) 
- 生产运维中常见问题, 可参考 [** 生产运维常见问题 **](https://datasalon.yuque.com/staff-dg3tgh/pg6cpg/uem0ig) 


# Contributing

# Communication

# License


