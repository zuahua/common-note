# JDBC

## JDBC 概述

### 1.1 数据的持久化

### 1.2 Java 中的数据存储技术

- Java中，数据库存储技术分为以下几类
  - **JDBC** 直接访问数据库
  - **JDO（Java Data Object）**技术
  - 第三方 O/R工具，如 **Hibernate，Mybatis** 等
- JDBC 是 Java 访问数据库的基石，JDO、Hibernate、MyBatis等只是更好的**封装**了 JDBC。

### 1.3 JDBC 介绍

- JDBC (Java Database Connectivity) 是一个独立于特定数据库管理系统、通用的SQL数据库存取和操作的公共接口(一组API)；定义了用来访问数据库的标准类库(Java.sql，Javax.sql)，