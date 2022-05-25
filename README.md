
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



# QuickStart 快速构建和体验JAX

依赖环境: docker, docker-compose

预编译好的JAX已发布到 aliyun docker镜像, 这里提供与MySQL, Flink, Kafka等服务快速集成部署的docker-compose.yml
 

### 创建docker-compose.yml

使用如下命令新建 docker-compose.yml 或手动创建下文内容的 docker-compose.yml: 

```sh
tee docker-compose.yml <<'EOF'

version: "3"
services:
  web:
    image: registry.cn-hangzhou.aliyuncs.com/eoitek/jax:1.0.0
    depends_on:
      - db
      - taskmanager
    environment:
      MYSQL_HOST: db
      MYSQL_USER: root
      MYSQL_PASSWORD: my-secret-pw
    ports:
      - "49999:9999"
    volumes:
      - web-data:/app/jax/jar_dir
    networks:
      - jax
  db:
    image: mysql:5.7.25
    environment:
      MYSQL_ROOT_PASSWORD: my-secret-pw
      MYSQL_DATABASE: jax_db
    networks:
      - jax
    volumes:
      - db-data:/var/lib/mysql
  taskmanager:
    image: ${FLINK_IMAGE:-flink:1.9.1-scala_2.11}
    command: taskmanager
    depends_on:
      - jobmanager
    environment:
      - |
        FLINK_PROPERTIES=
        jobmanager.rpc.address: jobmanager
        taskmanager.numberOfTaskSlots: 4
    networks:
      - jax
  jobmanager:
    image: ${FLINK_IMAGE:-flink:1.9.1-scala_2.11}
    command: jobmanager
    environment:
      - |
        FLINK_PROPERTIES=
        jobmanager.rpc.address: jobmanager
    ports:
      - "48081:8081"
    networks:
      - jax
  zookeeper:
    image: wurstmeister/zookeeper
    networks:
      - jax
  kafka:
    image: wurstmeister/kafka
    depends_on:
      - zookeeper
    environment:
      KAFKA_ADVERTISED_HOST_NAME: ${KAFKA_AD_IP:-kafka}
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_BROKER_ID: 1
      KAFKA_CREATE_TOPICS: sourceTopic:2:1,sinkTopic:2:1
    networks:
      - jax

networks:
  jax:

volumes:
  db-data:
  web-data:

EOF

```

其中的 registry.cn-hangzhou.aliyuncs.com/eoitek/jax:1.0.0 即为已准备好的JAX docker镜像 


### 一键启动各服务

```sh

docker-compose up

```

服务验证和访问:
- 访问JAX服务: http://{ip}:49999
- 访问Flink Standalone 测试集群: http://{ip}:48081




# Compile and Deploy 编译部署


### Requriments 编译环境要求

- 基础命令: tar, make, java; 
- 前端命令: nodejs, npm, yarn/yarnpkg, vue-cli; 
- Docker命令: docker, docker-compose ; 



### Compile 编译部署命令

使用make命令(基于Makefile) 进行项目编译和打包

```sh
# 编译并打包
make package-all

# 或者直接: 打包+ 制作Docker镜像
make image

```

编译打包好的tar.gz文件即位于 tmp 目录下的 jax-all-xxx.tar.gz 压缩文件;
解压后, 需要对 jax/application.yml配置文件 配置好正确的如下变量,才能start.sh启动
- jax.home(或$JAX_HOME环境变量) 为解压的JAX安装目录;
- spring.datasource中配置正确的MySQL url账号密码


配置成功后, 启动JAX服务

```sh
cd $JAX_HOME

./start.sh


# 项目启动成功, 由如下打印
 _ _   |_  _ _|_. ___ _ |    _ 
| | |\/|_)(_| | |_\  |_)||_|_\ 
     /               |         
                        3.1.2 
Jax Application is Ready

```



### 详细编译部署手册

项目编译和部署详细文档, 可参见 [编译部署](docs/CompileAndDeploy.md) 



# Documents 

- 编译部署和环境准备相关问题, 可参见  [** 常见问题列表 **](https://datasalon.yuque.com/staff-dg3tgh/pg6cpg/uem0ig) 
- 生产运维中常见问题, 可参考 [** 生产运维常见问题 **](https://datasalon.yuque.com/staff-dg3tgh/pg6cpg/uem0ig) 


# Contributing

# Communication

