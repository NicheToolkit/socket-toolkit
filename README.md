## [Socket-Toolkit](https://github.com/NicheToolkit/socket-toolkit) socket开发工具组

[![GitHub license](https://img.shields.io/badge/license-Apache-blue.svg)](https://github.com/NicheToolkit/socket-toolkit/blob/master/LICENSE)[![Maven Release](https://img.shields.io/maven-central/v/io.github.nichetoolkit/socket-toolkit-sever-starter.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22io.github.nichetoolkit%22%20AND%20a%3A%socket-toolkit-sever-starter%22)
![Tests](https://github.com/NicheToolkit/socket-toolkit/workflows/Tests/badge.svg)

&emsp;&emsp; 依赖[rest-toolkit](https://github.com/NicheToolkit/rest-toolkit)组件下的Tcp/Udp服务器.

## Release介绍

-  [Socket-Toolkit@1.0.2](https://github.com/NicheToolkit/socket-toolkit/tree/master/release/1.0.2.md)

### v1.0.2 Release

# [socket-toolkit-server-stater](https://github.com/NicheToolkit/socket-toolkit/tree/master/socket-toolkit-jt808-starter)

1、基于`netty`与`mina`实现的通用`SocketServer`组件，可根据配置选择使用基于哪种核心的`SocketServer`实现，支持自定义消息解析及不同通信协议下的消息码的实现。

2、[EnableSocketServer](https://github.com/NicheToolkit/socket-toolkit/blob/master/socket-toolkit-server-starter/src/main/java/io/github/nichetoolkit/socket/server/EnableSocketServer.java)
   可以通过添加`EnableSocketServer`注解开启`Socket`服务。
   
3、[SocketPackage](https://github.com/NicheToolkit/socket-toolkit/blob/master/socket-toolkit-server-starter/src/main/java/io/github/nichetoolkit/socket/server/SocketPackage.java)
   可以通过添加`SocketPackage`注解自定义实现不同数据包的解析。
   
4、[SocketServerProperties](https://github.com/NicheToolkit/socket-toolkit/blob/master/socket-toolkit-server-starter/src/main/java/io/github/nichetoolkit/socket/configure/SocketServerProperties.java)
   在`SocketServerProperties`中定义了`SocketServer`的配置选项，服务器默认监听端口号(`port`): `9999`, 默认服务器类型(`serverType`): `NETTY`, 默认协议(`protocol`): `tcp`, 
   线程池核心数量(`corePoolSize`): `1`, 线程池最大数量(`maxPoolSize`): `10`。
   
   
# [socket-toolkit-jt808-starter](https://github.com/NicheToolkit/socket-toolkit/tree/master/socket-toolkit-jt808-starter)

1、基于`JT808`《道路运输车辆卫星定位系统终端通讯协议及数据格式》实现的数据包解析组件。具体实现参考[io.github.nichetoolkit.socket.handler](https://github.com/NicheToolkit/socket-toolkit/tree/master/socket-toolkit-jt808-starter/src/main/java/io/github/nichetoolkit/socket/handler)
   包下的包数据处理类。
   
2、[SocketJt808Properties](https://github.com/NicheToolkit/socket-toolkit/blob/master/socket-toolkit-jt808-starter/src/main/java/io/github/nichetoolkit/socket/configure/SocketJt808Properties.java)
     在`SocketJt808Properties`中定义了`jt808`的配置选项，服务器默认鉴权消息Id号(`port`): `0102,0100`。


## Maven Central

-  [Maven Central Repository](https://search.maven.org/search?q=g:io.github.nichetoolkit)

-  [Sonatype Central Repository](https://central.sonatype.dev/search?q=io.github.nichetoolkit)

## 依赖环境
 > [Spring Boot](https://spring.io/projects/spring-boot) 2.6.6.RELEASE\
 > [Maven](https://maven.apache.org/) 3.6.0+\
 > [JDK](https://www.oracle.com/java/technologies/downloads/#java8) 1.8
 
## socket-toolkit-server-stater
 * Maven (`pom.xml`)
```xml
  <dependency>
    <groupId>io.github.nichetoolkit</groupId>
    <artifactId>socket-toolkit-server-stater</artifactId>
    <version>1.0.2</version>
  </dependency>
```

## socket-toolkit-jt808-stater
 * Maven (`pom.xml`)
```xml
  <dependency>
    <groupId>io.github.nichetoolkit</groupId>
    <artifactId>socket-toolkit-jt808-stater</artifactId>
    <version>1.0.2</version>
  </dependency>
```

## 使用方式

参考[socket-toolkit-test-web](https://github.com/NicheToolkit/socket-toolkit/tree/master/socket-toolkit-test-web)模块.

 ## 依赖参考

 [rest-toolkit](https://github.com/NicheToolkit/rest-toolkit)
 
 ## License 

 [Apache License](https://www.apache.org/licenses/LICENSE-2.0)
 
 ## Dependencies
 
  [Spring Boot](https://github.com/spring-projects/spring-boot)
  
  [Rest-Toolkit](https://github.com/NicheToolkit/rest-toolkit)
  
  [Netty-Home](https://netty.io/)
  
  [Apache-Mina](https://mina.apache.org/)