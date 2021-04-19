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

### 2.2 要素二 URL

- JDBC URL 用于表示一个被注册的驱动程序

- JDBC URL 组成

  - jdbc:mysql://localhost:3306/test
  - 协议:子协议 子名称



### 2.3 用户名密码

### 2.4 连接举例

```java
/**
* 方式五：最终优化方式，将连接信息声明在配置文件下
* 优点：
* 1.数据与代码分离，解耦
* 2.如果需要修改配置信息，可以避免重新打包
*/
@Test
public void jdbc5() throws Exception {
    // 从文件中读取
    InputStream is = JdbcTest.class.getClassLoader().getResourceAsStream("jdbc.properties");

    Properties pro = new Properties();
    pro.load(is);

    String user = pro.getProperty("user");
    String pass = pro.getProperty("password");
    String url = pro.getProperty("url");
    String driver = pro.getProperty("driver-class-name");

    // 加载驱动
    Class.forName(driver);
    // 可省略 因为 Driver 实现类 内部的静态代码块自己注册了
    // Driver driver = (Driver) clazz.newInstance();
    // DriverManager.registerDriver(driver);


    Connection conn = DriverManager.getConnection(url, user, pass);
    System.out.println(conn);
}
```

## 第三章 使用PreparedStatement实现CRUD操作

### 3.1 操作和访问数据库

- java.sql包中有 3 个接口分别定义了对数据库调用的不同方式
  - Statement：执行静态SQL
  - PreparedStatement：SQL语句被预编译并存储在对象中，可以使用该对象多次高效调用
  - CallableStatement：执行SQL存储过程

![](..\resource\images\5.png)

### 3.2 使用Statement弊端

- 拼接SQL，注入问题
- 需要区分字段大小写

### 3.3 PreparedStatement 使用

#### 封装JDBC连接 关闭 通用操作

```java
package com.zuahua1.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 通用数据库 连接 关闭
 *
 * @author zhanghua
 * @createTime 2021/4/19 17:26
 */
public class JDBCUtil {
    /**
     * 获取数据库连接
     *
     * @return conn
     * @throws Exception
     */
    public static Connection getConnection() throws Exception {
        Connection conn = null;
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");

        Properties pro = new Properties();
        pro.load(is);

        String user = pro.getProperty("user");
        String pass = pro.getProperty("password");
        String url = pro.getProperty("url");
        String driver = pro.getProperty("driver-class-name");
        Class.forName(driver);
        conn = DriverManager.getConnection(url, user, pass);
        return conn;
    }

    /**
     * 关闭数据库资源
     *
     * @param conn
     * @param ps
     */
    public static void closeResource(Connection conn, Statement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
```



#### 增 删 改

##### 增

```java
/**
* 关闭数据库资源
*
* @param conn
* @param ps
*/
public static void closeResource(Connection conn, Statement ps) {
    try {
        if (ps != null) {
            ps.close();
        }
    } catch (SQLException throwables) {
        throwables.printStackTrace();
    }
    try {
        if (conn != null) {
            conn.close();
        }
    } catch (SQLException throwables) {
        throwables.printStackTrace();
    }
}
```

















