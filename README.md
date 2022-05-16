
# Introduction 简介

Jax 擎创数据团队研发并开源的数据中台项目, 提供可拖拽的流批作业开发, 作业调度和作业监控, 集群配置和集群管理, 算子包管理等功能; 

Jax 项目为搭建数据中台/数据应用开发平台的搭建, 提供了一个轻量级的解决方案; 

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
- docker-compose



### Download 预编译版下载

### Compile 编译项目


##### Package

```sh
$ make package-all
```

##### Docker

requirements

```
node/npm/yarn
wget
docker/docker compose
```

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


### Deploy 项目部署


# Documents 

- 编译部署和环境准备相关问题, 可参见  [** 常见问题列表 **](https://datasalon.yuque.com/staff-dg3tgh/pg6cpg/uem0ig) 
- 生产运维中常见问题, 可参考 [** 生产运维常见问题 **](https://datasalon.yuque.com/staff-dg3tgh/pg6cpg/uem0ig) 


# Contributing

# Communication

# License


