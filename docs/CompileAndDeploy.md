

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


首先需要生成 jax docker镜像
```sh
make image
```


默认安装的flink计算引擎是1.9.1版本,并会启1个Flink Standalone模式容器, 
本测试Docker Demo中会启1个Zookeeper/Kafka服务, 需要指定一个用于外网可访问的Kafka广播IP环境变量: $KAFKA_AD_IP
可先通过如下命令生成 KAFKA_AD_IP=宿主机IP变量, 再 docker-compose up 构建和启动 mysql,redis,kafka,jax等服务;

```sh
export KAFKA_AD_IP=$(ip addr|grep "global ens"| tail -n1|awk '{print $2}'|awk -F"/" '{print $1}')
# 或者在命令行直接赋值环境变量: KAFKA_AD_IP
# export KAFKA_AD_IP=192.168.51.124
echo $KAFKA_AD_IP
```

With flink 1.9 standalone cluster:
```sh
docker-compose up
```

With flink 1.12 standalone cluster:

```sh
$ make image
$ FLINK_IMAGE=flink:1.12.3-scala_2.11 docker-compose up
```

