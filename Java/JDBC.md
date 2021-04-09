# JDBC

## 第一章 JDBC 概述

### 1.1 数据的持久化

### 1.2 Java 中的数据存储技术

- Java中，数据库存储技术分为以下几类
  - **JDBC** 直接访问数据库
  - **JDO（Java Data Object）**技术
  - 第三方 O/R工具，如 **Hibernate，Mybatis** 等
- JDBC 是 Java 访问数据库的基石，JDO、Hibernate、MyBatis等只是更好的**封装**了 JDBC。

### 1.3 JDBC 介绍

- JDBC (Java Database Connectivity) 是一个独立于特定数据库管理系统、通用的SQL数据库存取和操作的**公共接口(一组API)**；定义了用来访问数据库的标准类库(Java.sql，Javax.sql)，使用这些类库可以以一种标**准的方法**，方便地访问数据库资源

#### 1.4 JDBC 体系结构

- JDBC 接口(API) 两个层次
  - 面向应用的 API : Java API 抽象接口，供应用程序开发人员使用(连接数据库、执行 SQL 语句，获得结果)。
  - 面向数据库的 API : Java Driver API，供开发商开发数据库驱动程序用。

> JDBC 是 SUN 公司提供的一套用于数据操作的接口，Java 程序员只需面向这套接口编程即可。
>
> 不同的数据库厂商，需要针对这套接口，提供不同的实现，不同实现的集合，即为不同数据库的驱动。
>
> ​																																				--- 面向接口编程

#### 1.5 JDBC 编写步骤

![](..\resource\images\4.png)

## 第二章 获取数据库连接

### 2.1 Driver 接口实现类

- **url**

```java
jdbc:mysql://localhost:3306/test?characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowMultiQueries=true
```

#### 注意点

##### 驱动版本

- mysql-connector-java 5.0

使用

```java
com.mysql.jdbc.Driver
```

- mysql-connector-java 8.0

使用

```java
com.mysql.cj.jdbc.Driver
```



















