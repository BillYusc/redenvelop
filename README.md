# 项目说明文档

[文档地址](https://www.wolai.com/zywy/6JJ2ZFWLH1tLet2AEBxPAb?theme=light)

## 开发思路

### 红包算法

    1. 切绳子：红包发出时，随机值就分好。

      - 优点：保证了红包金额的准确性，与随机性。

      - 缺点：发红包时计算量较大

    1. 二倍均值法：每次随机范围设置为红包剩余值对于未抢过人数均值的二倍

      - 更符合抢红包这一过程的直观感受，相比于切绳子，计算量小。

      - 单个红包上限有限。

    本项目采用切绳子的方法

### 防重复抢包

    使用Redis Hash来存储用户抢包记录，如果抢过拒绝请求 

### 性能优化

    - 缓存

      分好的红包以List结构存入Redis，以提高查询性能。

    - 消息队列

      领取红包后的入账写库操作，可以用消息队列做异步处理

### 准确性保证

    - 分布式锁

      使用Redisson提供的分布式锁，以便于多节点同时对一个红包进行操作红包超发问题

### 回收过期红包

    - 延时队列

      使用Redis，Zset实现。Value值为红包id，Score值为红包发送时间戳，使用SpringBoot提供的Schedule方式来轮询Score值超过过期时间的Value，将过期红包退回。

## 后续拓展

- [ ] 使用Nginx做请求的负载

- [ ]  微服务拆分，引入注册中心保证集群可用性或一致性

- [x] 消息队列异步写入账户增加

- [x] 延时队列，或定时任务，返还超时过期红包

- [ ] 用户量很大的情况下，需要分库分表，读写分离

- [ ] 红包id目前是借助于MySQL自增主键，在集群环境下会有性能瓶颈，使用SnowFlake

- [ ] Redis使用Codis或者一致性Hash的方式进行扩容

## 附录：本地docker环境搭建

#### MySQL

```Bash
docker run -itd --name mysql-test -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql
```

#### Redis

```Bash
docker run -d --name 6379redis -p 6379:6379  redis  --requirepass "123456"
```

#### Kafka

```YAML
version: "3.3"
services:
  # zookeeper
  zookeeper:
    image: wurstmeister/zookeeper
  # kafka
  kafka:
    image: wurstmeister/kafka
    command: [ start-kafka.sh ]
    ports:
      - 9092:9092
    environment:
      KAFKA_ADVERTISED_HOST_NAME: localhost # host ip or host name, only for one brokers
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    depends_on:
      - zookeeper
```

```Bash
docker-compose up -d
```




