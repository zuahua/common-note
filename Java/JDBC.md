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

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210421163817.png)

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



### 2.3 用户名密码 jdbc.properties

```properties
user=root
password=123456
driver-class-name=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/test?characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowMultiQueries=true
```



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

### 3.1 数据库表准备

```sql
create table user(
	id int PRIMARY KEY auto_increment,
	name VARCHAR(10),
	birth DATE,
	photo BLOB
);
```

### 3.2 操作和访问数据库

- java.sql包中有 3 个接口分别定义了对数据库调用的不同方式
  - Statement：执行静态SQL
  - PreparedStatement：SQL语句被预编译并存储在对象中，可以使用该对象多次高效调用
  - CallableStatement：执行SQL存储过程

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210421163853.png)

### 3.3 使用Statement弊端

- 拼接SQL，注入问题
- 拼串 繁琐

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210421163958.png)

### 3.4 PreparedStatement 使用

#### 3.4.1 PreparedStatement 相比 Statement 优点

1. 不拼SQL字符串，解决SQL注入问题。
2. 能操作Blob类型，而Statement不行。
3. 高效的批量操作，只预编译SQL一次。

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210421164043.png)

#### 3.4.2 封装JDBC连接 关闭 通用操作

```java
package com.zuahua1.util;

import java.io.InputStream;
import java.sql.*;
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

    /**
     * 关闭数据库资源
     *
     * @param conn
     * @param ps
     */
    public static void closeResource(Connection conn, Statement ps, ResultSet rs) {
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
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 通用的增删改操作
     *
     * @param sql  SQL
     * @param args 占位符参数
     */
    public static void commonUpdate(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtil.getConnection();
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn, ps);
        }
    }
}
```

#### 3.4.3 PrepaedStatement 增 删 改

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

##### 通用增删改

```java
/**
* 通用的增删改操作
*
* @param sql  SQL
* @param args 占位符参数
*/
public static void commonUpdate(String sql, Object... args) {
    Connection conn = null;
    PreparedStatement ps = null;
    try {
        conn = JDBCUtil.getConnection();
        ps = conn.prepareStatement(sql);

        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
        ps.execute();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        JDBCUtil.closeResource(conn, ps);
    }
}
```

##### 测试通用增删改

```java
@Test
public void commonUpdateTest() {
    //String sql = "delete from user where id = ?";
    //JDBCUtil.commonUpdate(sql, 3);
    String sql = "update user set name = ? where id = ?";
    JDBCUtil.commonUpdate(sql, "Chang", 2);
}
```

#### 3.4.4 PreparedStatement查

##### 通用资源关闭

```java
/**
* 关闭数据库资源
*
* @param conn
* @param ps
*/
public static void closeResource(Connection conn, Statement ps, ResultSet rs) {
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
    try {
        if (rs != null) {
            rs.close();
        }
    } catch (SQLException throwables) {
        throwables.printStackTrace();
    }
}
```

##### bean User

 * **ORM编程思想（Object Relational Mapping）**
    * 一张表对应一个类
    * 表中的记录对应类的对象
    * 表中的字段对应类中的字段

```java
package com.zuahua1.bean;

import java.sql.Date;

/**
 * User表对象
 * ORM编程思想（Object Relational Mapping）
 * 一张表对应一个类
 * 表中的记录对应类的对象
 * 表中的字段对应类中的字段
 *
 * @author zhanghua
 * @createTime 2021/4/20 9:42
 */
public class User {
    private Integer id;
    private String name;
    private Date birth;

    public User(Integer id, String name, Date birth) {
        this.id = id;
        this.name = name;
        this.birth = birth;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birth=" + birth +
                '}';
    }
}
```



##### 查询

```java
/**
* 查询表测试
*/
@Test
public void queryTest() {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    User user = null;
    try {
        conn = JDBCUtil.getConnection();
        String sql = "select id,name,birth from user where id = ?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, 1);

        rs = ps.executeQuery();

        user = null;
        // 处理结果集
        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            Date birth = rs.getDate(3);
            user = new User(id, name, birth);
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // 关闭资源
        JDBCUtil.closeResource(conn, ps, rs);
    }


    if (user != null) {
        System.out.printf(user.toString());
    }
}
```

##### User表 通用查询

- ResultSetMetaData 结果集元数据 能获取列名
- 反射 动态设置对象字段值

```java
/**
* 对User表的通用查询操作
*
* @param sql  SQL
* @param args 占位符参数
* @return User
*/
public User commonQueryForUser(String sql, Object... args) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
        conn = JDBCUtil.getConnection();
        ps = conn.prepareStatement(sql);
        // 设置占位符
        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
        // 执行
        rs = ps.executeQuery();
        // 获取 结果集元数据
        ResultSetMetaData rsMetaData = rs.getMetaData();

        // 处理结果集
        if (rs.next()) {
            User user = new User();
            // 获取 结果集列数
            int columnCount = rsMetaData.getColumnCount();
            // 处理列值
            for (int i = 0; i < columnCount; i++) {
                // 获取 列名 推荐使用 columnLabel
                // String columnName = rsMetaData.getColumnName(i + 1);
                String columnLabel = rsMetaData.getColumnLabel(i + 1);
                // 列值
                Object columnValue = rs.getObject(i + 1);
                // 利用反射设置对象的字段值  根据字段名(字符串)去获取字段
                Field declaredField = User.class.getDeclaredField(columnLabel);
                declaredField.setAccessible(true);
                declaredField.set(user, columnValue);
            }
            return user;
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // 关闭资源
        JDBCUtil.closeResource(conn, ps, rs);
    }
    return null;
}
```

##### User通用查询测试

```java
@Test
public void commonQueryTest() {
    String sql1 = "select name,id,birth from user where id = ?";
    User user = commonQueryForUser(sql1, 1);
    System.out.println(user);
    String sql2 = "select birth,name from user where id = ?";
    User user1 = commonQueryForUser(sql2, 2);
    System.out.println(user1);
}
```

```powershell
User{id=1, name='zuahua', birth=2000-01-01}
User{id=null, name='Chang', birth=2000-01-01}
```

#### 3.4.5 【关于表中字段与类属性名不相同的问题】

解决方案：

- Sql 语句中给字段取和类属性对应的**别名**
- 结果集元数据获取列名时，使用 getColumnLable

#### 3.4.6 Book表的通用查询 【表字段与类属性名不同】

##### 准备表

```sql
CREATE TABLE book(
	id int PRIMARY KEY AUTO_INCREMENT,
	book_id VARCHAR(6),
	book_name VARCHAR(20),
	book_price INT
);

INSERT INTO book(book_id, book_name, book_price) VALUES('102401', '沉默的羔羊', 55);
INSERT INTO book(book_id, book_name, book_price) VALUES('102402', '福尔摩斯探案集', 65);
```

##### Book类

```java
package com.zuahua1.bean;

/**
 * @author zhanghua
 * @createTime 2021/4/20 11:36
 */
public class Book {
    private Integer id;
    private String bookId;
    private String bookName;
    private Integer bookPrice;

    public Book() {
    }

    public Book(Integer id, String bookId, String bookName, Integer bookPrice) {
        this.id = id;
        this.bookId = bookId;
        this.bookName = bookName;
        this.bookPrice = bookPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Integer getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(Integer bookPrice) {
        this.bookPrice = bookPrice;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", bookId='" + bookId + '\'' +
                ", bookName='" + bookName + '\'' +
                ", bookPrice=" + bookPrice +
                '}';
    }
}
```



##### 通用查询

```java
/**
* Book表通用查询
*
* @param sql  SQL
* @param args 占位符参数
* @return Book
*/
public Book commonQueryForBook(String sql, Object... args) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
        conn = JDBCUtil.getConnection();
        ps = conn.prepareStatement(sql);
        // 设置占位符
        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
        // 执行
        rs = ps.executeQuery();
        // 获取 结果集元数据
        ResultSetMetaData rsMetaData = rs.getMetaData();

        // 处理结果集
        if (rs.next()) {
            Book book = new Book();
            // 获取 结果集列数
            int columnCount = rsMetaData.getColumnCount();
            // 处理列值
            for (int i = 0; i < columnCount; i++) {
                // 获取 列名 推荐使用 columnLabel
                String columnLabel = rsMetaData.getColumnLabel(i + 1);
                // 列值
                Object columnValue = rs.getObject(i + 1);
                // 利用反射设置对象的字段值  根据字段名(字符串)去获取字段
                Field declaredField = Book.class.getDeclaredField(columnLabel);
                declaredField.setAccessible(true);
                declaredField.set(book, columnValue);
            }
            return book;
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // 关闭资源
        JDBCUtil.closeResource(conn, ps, rs);
    }
    return null;
}
```



##### 测试通用查询

```java
@Test
public void test() {
    String sql = "select id, book_id bookId, book_name bookName, book_price bookPrice from book where id = ?";
    Book book = commonQueryForBook(sql, 1);
    System.out.println(book);
}
```

```powershell
Book{id=1, bookId='102401', bookName='沉默的羔羊', bookPrice=55}
```

#### 3.4.7 多张表的通用查询 返回一条记录

- 使用泛型

##### 通用查询方法

```java
/**
* 通用查询
*
* @param clazz clazz
* @param sql   SQL
* @param args  占位符参数
* @param <T>   表对应bean类
* @return <T> T
*/
public <T> T commonQuery(Class<T> clazz, String sql, Object... args) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
        conn = JDBCUtil.getConnection();
        ps = conn.prepareStatement(sql);
        // 设置占位符
        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
        // 执行
        rs = ps.executeQuery();
        // 获取 结果集元数据
        ResultSetMetaData rsMetaData = rs.getMetaData();

        // 处理结果集
        if (rs.next()) {
            T t = clazz.newInstance();
            // 获取 结果集列数
            int columnCount = rsMetaData.getColumnCount();
            // 处理列值
            for (int i = 0; i < columnCount; i++) {
                // 获取 列名 推荐使用 columnLabel
                String columnLabel = rsMetaData.getColumnLabel(i + 1);
                // 列值
                Object columnValue = rs.getObject(i + 1);
                // 利用反射设置对象的字段值  根据字段名(字符串)去获取字段
                Field declaredField = clazz.getDeclaredField(columnLabel);
                declaredField.setAccessible(true);
                declaredField.set(t, columnValue);
            }
            return t;
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // 关闭资源
        JDBCUtil.closeResource(conn, ps, rs);
    }
    return null;
}
```

##### 测试方法

```java
@Test
public void test() {
    String sql1 = "select name,id,birth from user where id = ?";
    User user = commonQuery(User.class, sql1, 1);
    System.out.println(user);


    String sql2 = "select id, book_id bookId, book_name bookName, book_price bookPrice from book where id = ?";
    Book book = commonQuery(Book.class, sql2, 2);
    System.out.println(book);

}
```

```powershell
User{id=1, name='zuahua', birth=2000-01-01}
Book{id=2, bookId='102402', bookName='福尔摩斯探案集', bookPrice=65}
```

#### 3.4.8 多张表的通用查询 返回结果列表

##### 方法

```java
/**
* 通用查询 返回结果列表
*
* @param clazz clazz
* @param sql   SQL
* @param args  占位符参数
* @param <T>   表对应bean类
* @return <T> T
*/
public <T> List<T> commonQueryList(Class<T> clazz, String sql, Object... args) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
        conn = JDBCUtil.getConnection();
        ps = conn.prepareStatement(sql);
        // 设置占位符
        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
        // 执行
        rs = ps.executeQuery();
        // 获取 结果集元数据
        ResultSetMetaData rsMetaData = rs.getMetaData();
        ArrayList<T> list = new ArrayList<>();
        // 处理结果集
        while (rs.next()) {
            T t = clazz.newInstance();
            // 获取 结果集列数
            int columnCount = rsMetaData.getColumnCount();
            // 处理列值
            for (int i = 0; i < columnCount; i++) {
                // 获取 列名 推荐使用 columnLabel
                String columnLabel = rsMetaData.getColumnLabel(i + 1);
                // 列值
                Object columnValue = rs.getObject(i + 1);
                // 利用反射设置对象的字段值  根据字段名(字符串)去获取字段
                Field declaredField = clazz.getDeclaredField(columnLabel);
                declaredField.setAccessible(true);
                declaredField.set(t, columnValue);
            }
            list.add(t);
        }
        return list;
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // 关闭资源
        JDBCUtil.closeResource(conn, ps, rs);
    }
    return null;
}
```

##### 测试

```java
@Test
public void test2() {
    String sql1 = "select name,id,birth from user where id < ?";
    List<User> userList = commonQueryList(User.class, sql1, 3);
    userList.forEach(System.out::println);


    String sql2 = "select id, book_id bookId, book_name bookName, book_price bookPrice from book where id < ?";
    List<Book> bookList = commonQueryList(Book.class, sql2, 3);
    bookList.forEach(System.out::println);
}
```

```powershell
User{id=1, name='zuahua', birth=2000-01-01}
User{id=2, name='Chang', birth=2000-01-01}
Book{id=1, bookId='102401', bookName='沉默的羔羊', bookPrice=55}
Book{id=2, bookId='102402', bookName='福尔摩斯探案集', bookPrice=65}
```

###  3.5 SQL 注入问题

##### 准备表

```sql
create table employee(
	id int PRIMARY KEY auto_increment,
	name VARCHAR(10),
	password VARCHAR(40),
	birth DATE,
	photo BLOB
);

INSERT INTO employee(name, password, birth) values('XiaoMing', '123456', DATE('1990-01-01'));
INSERT INTO employee(name, password, birth) values('XiaoHong', '123456', DATE('1990-01-01'));
```

##### 使用 Statement测试SQL注入

```java
/**
* 使用 Statement测试SQL注入
*/
@Test
public void test() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("输入用户名：");
    String name = scanner.nextLine();

    System.out.println("输入密码：");
    String password = scanner.nextLine();

    Connection conn = null;
    ResultSet rs = null;
    try {
        conn = JDBCUtil.getConnection();
        // 注入sql: select id,name from employee where name = '1' or 'and password' = '1' or '1'='1'
        // name = 1' or '
        // password = 1' or '1'='1
        String sql = "select id,name from employee where name = '" + name + "'" + " and password = '" + password + "'";
        Statement statement = conn.createStatement();
        statement.execute(sql);

        rs = statement.getResultSet();

        if (rs.next()) {
            System.out.println("登陆成功");
        } else {
            System.out.println("登陆失败");
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
```

```shell
输入用户名：
1' or '
输入密码：
1' or '1'='1
登陆成功
```

##### PreparedStatement 测试SQL注入

```java
/**
* PreparedStatement 测试SQL注入
*/
@Test
public void test2() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("输入用户名：");
    String name = scanner.nextLine();

    System.out.println("输入密码：");
    String password = scanner.nextLine();

    Connection conn = null;
    ResultSet rs = null;
    try {
        conn = JDBCUtil.getConnection();
        // 注入sql: select id,name from employee where name = '1' or ' and password = ' = '1' or 1=1
        // name = '1' or '
        // password = ' = '1' or 1=1
        String sql = "select id,name from employee where name = ? and password = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, password);

        rs = ps.executeQuery();
        if (rs.next()) {
            System.out.println("登陆成功");
        } else {
            System.out.println("登陆失败");
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
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

```shell
输入用户名：
'1' or '
输入密码：
' = '1' or 1=1
登陆失败
```



### 3.6 Sql 类型 与 Java 类型对应关系

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210421163928.png)

### 3.7 JDBC API小结

#### 3.7.1 ResultSet 和 ResultSetMetaData

```java
// 执行
rs = ps.executeQuery();
// 获取 结果集元数据
ResultSetMetaData rsMetaData = rs.getMetaData();
```

```java
// 获取 结果集列数
int columnCount = rsMetaData.getColumnCount();
```

```java
// 获取 列名 推荐使用 columnLabel
String columnLabel = rsMetaData.getColumnLabel(i + 1);
```

#### 3.7.2 资源的释放

- 通用资源关闭

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

/**
     * 关闭数据库资源
     *
     * @param conn
     * @param ps
     */
public static void closeResource(Connection conn, Statement ps, ResultSet rs) {
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
    try {
        if (rs != null) {
            rs.close();
        }
    } catch (SQLException throwables) {
        throwables.printStackTrace();
    }
}
```

#### 3.7.3 思想与技术

- 两种思想
  - 面向接口编程思想
  - **ORM编程思想（Object Relational Mapping）**
     - 一张表对应一个类
     - 表中的记录对应类的对象
     - 表中的字段对应类中的字段
- 两种技术
  - JDBC 结果集的元数据：ResultSetMetaData
    - 获取列数  getColumnCount()
    - 获取别名  getColumnLabel()
  - 通过反射，创建指定类的对象，获取指定的属性并赋值

### 3.8 接口补充

#### PreparedStatement

##### `ps.execute()`

> Returns:
> true if the first result is a ResultSet object; false if the first result is an update count or there is no result
>
> 查询时返回 **true**，增删改返回 **false**

##### `ps.executeQuery()`

> Returns:
> a ResultSet object that contains the data produced by the query; never null
>
> 返回 **ResultSet**，不为 **null**

##### `ps.executeUpdate()`

> Returns:
> either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
>
> 返回影像数据表的行数 int 

##  第四章 PreparedStatement 操作 blob

####  准备

```sql
alter table employee modify photo mediumblob;
```

#### 写入 blob 字段 （图片）

```java
@Test
public void insertBlob() {
    Connection conn = null;
    PreparedStatement ps = null;
    FileInputStream fis = null;
    try {
        conn = JDBCUtil.getConnection();
        String sql = "insert into employee(name,password,birth,photo) values(?,?,?,?)";
        ps = conn.prepareStatement(sql);
        String name = "Faker";
        String password = "123456";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birth = sdf.parse("1980-09-01");

        ps.setString(1, name);
        ps.setString(2, password);
        ps.setDate(3, new java.sql.Date(birth.getTime()));

        fis = new FileInputStream("pic.jpg");
        ps.setBlob(4, fis);

        int res = ps.executeUpdate();
        if (res > 0) {
            System.out.println("插入成功");
        } else {
            System.out.println("插入失败");
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        JDBCUtil.closeResource(conn, ps);
        try {
            if (fis != null) {
                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
```

#### 读取 blob 字段，保存为图片

- Emploee Bean

```java
package com.zuahua1.bean;

import java.sql.Blob;
import java.sql.Date;

/**
 * @author zhanghua
 * @createTime 2021/4/21 10:43
 */
public class Emploee {
    private Integer id;
    private String name;
    private String password;
    private Date birth;
    private Blob photo;

    public Emploee() {
    }

    public Emploee(Integer id, String name, String password, Date birth, Blob photo) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.birth = birth;
        this.photo = photo;
    }

    public Emploee(String name, String password, Date birth, Blob photo) {
        this.name = name;
        this.password = password;
        this.birth = birth;
        this.photo = photo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Blob getPhoto() {
        return photo;
    }

    public void setPhoto(Blob photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Emploee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", birth=" + birth +
                '}';
    }
}
```



```java
@Test
public void queryBlob() {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
        conn = JDBCUtil.getConnection();
        String sql = "select name,password,birth,photo from emploee where id = ?";
        ps = conn.prepareStatement(sql);

        ps.setInt(1, 3);

        rs = ps.executeQuery();

        if (rs.next()) {
            String name = rs.getString("name");
            String password = rs.getString("password");
            java.sql.Date birth = rs.getDate("birth");
            Blob photo = rs.getBlob("photo");
            Emploee emploee = new Emploee(name, password, birth, photo);

            System.out.println(emploee);

            // 写出图片
            FileOutputStream fos = new FileOutputStream("fos.jpg");
            InputStream is = photo.getBinaryStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

            fos.close();
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        JDBCUtil.closeResource(conn, ps, rs);
    }
}
```

#### 注意 blob 与 Mysql 限制传入大小

> Mysql 中 ***mediumblob*** 类型最大为 16M
>
> Mysql 服务 默认最大 1M

使用大图片报错如下

```powershell
com.mysql.cj.jdbc.exceptions.PacketTooBigException: Packet for query is too large (4,733,455 > 4,194,304). You can change this value on the server by setting the 'max_allowed_packet' variable.
```

解决：

mysql 数据 目录 **<u>my.ini</u>** 配置

**<u>C:\ProgramData\MySQL\MySQL Server 5.7\my.ini</u>**

```ini
max_allowed_packet = 16M
```

## 第五章 PreparedStatement 批量操作

#### 3.10.1 版本说明

>***mysql-connector-java***  > 5.1.37

#### 3.10.2 提高效率参数

1. 设置连接不自动提交
2. 使用批量操作 

#### 3.10.3 测试

```java
/**
* 批量插入操作
*
* @throws Exception
*/
@Test
public void insertBatch() throws Exception {
    long s = System.currentTimeMillis();

    Connection conn = JDBCUtil.getConnection();
    String sql = "insert into book(book_id,book_name,book_price) values(?,?,?)";
    PreparedStatement ps = conn.prepareStatement(sql);

    // 设置不自动提交
    conn.setAutoCommit(false);

    Random r = new Random();

    for (int i = 0; i < 10000; i++) {
        int nextInt = r.nextInt(10000);
        int nextInt1 = r.nextInt(200);
        ps.setString(1, String.valueOf(nextInt));
        ps.setString(2, "book-name-" + i);
        ps.setInt(3, nextInt1);

        // 批量操作
        ps.addBatch();

        // 每1000条执行一次
        //            if ((i + 1) % 1000 == 0) {
        //                ps.executeBatch();
        //                // 清空batch
        //                ps.clearBatch();
        //            }
    }

    // 一次性执行
    ps.executeBatch();
    ps.clearBatch();

    // 提交
    conn.commit();

    JDBCUtil.closeResource(conn, ps);

    long e = System.currentTimeMillis();
    System.out.println("花费时间：" + (e - s));
}
```

```sh
花费时间：1003
```

## 第六章 数据库事务

### 6.1 数据库事务介绍

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210421164105.png)

- 哪些操作会自动提交
  - DDL 操作一旦执行，自动提交
  - DML 默认自动提交
    - set autocommit = false 取消自动提交
  - 默认关闭连接时，自动提交

### 6.2 JDBC 事务处理

#### 介绍

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210421164808.png)

#### 表 准备

```sql
create table bank_user(
	id int primary key auto_increment,
	name varchar(10),
	password varchar(40),
	balance double 
);

insert into bank_user(name, password, balance) values('A', '123456', 1000);
insert into bank_user(name, password, balance) values('B', '123456', 1000);
```

#### 满足事务的通用增删改

```java
/**
* 通用增删改 version 2.0 考虑事务
*
* @param conn 连接
* @param sql  SQL
* @param args 占位符参数
* @return
*/
public int commonUpdate(Connection conn, String sql, Object... args) {
    PreparedStatement ps = null;
    try {
        ps = conn.prepareStatement(sql);

        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
        return ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        JDBCUtil.closeResource(null, ps);
    }
    return -1;
}
```

#### 事务测试

```java
/**
* 事务需要避免以下操作：
* 哪些操作会自动提交
* - DDL 操作一旦执行，自动提交
* - DML 默认自动提交
* - set autocommit = false 取消自动提交
* - 默认关闭连接时，自动提交
* <p>
* 模拟转账操作 A向B转账100 事务操作
*/
@Test
public void commonUpdateWithTransactionTest() {
    Connection conn = null;
    try {
        conn = JDBCUtil.getConnection();
        // 设置不自动提交
        conn.setAutoCommit(false);

        // 操作1 A转账
        String sqlA = "update bank_user set balance = balance - 100 where name = ?";
        commonUpdate(conn, sqlA, "A");

        // 模拟网络问题
        System.out.println(1 / 0);

        // 操作2 B收账
        String sqlB = "update bank_user set balance = balance + 100 where name = ?";
        commonUpdate(conn, sqlB, "B");

        // 提交操作
        conn.commit();
        System.out.println("转账成功");
    } catch (Exception e) {
        e.printStackTrace();
        // 回滚操作
        try {
            conn.rollback();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    } finally {
        // 关闭连接前，设置为默认自动提交，主要是使用数据库连接池时需要注意
        try {
            conn.setAutoCommit(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        // 关闭连接
        JDBCUtil.closeResource(conn, null);
    }
}
```

### 6.3 事务的 ACID 4 属性

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210421165401.png)

#### 6.3.1 数据库的并发问题

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210421170018.png)

>**<u>不可重复读</u>** 和 **<u>幻读</u>** 是可以接受的

#### 6.3.2 四种隔离级别

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210421171244.png)

> 上表隔离级别从上往下：***一致性增强，并发性降低***
>
> 默认隔离级别下，Oracle并发性比Mysql好

#### 6.3.3 在Mysql中设置隔离级别

####  6.3.4 在Jav中设置隔离级别

##### 6.3.4.1 考虑事务的通用查询 返回一个数据

```java
/**
     * 考虑事务的通用查询 返回一个数据 version 2.0
     *
     * @param conn  连接
     * @param clazz clazz
     * @param sql   SQL
     * @param args  占位符参数
     * @param <T>   表对应bean类
     * @return <T> T
     */
public <T> T commonQuery(Connection conn, Class<T> clazz, String sql, Object... args) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
        ps = conn.prepareStatement(sql);
        // 设置占位符
        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
        // 执行
        rs = ps.executeQuery();
        // 获取 结果集元数据
        ResultSetMetaData rsMetaData = rs.getMetaData();

        // 处理结果集
        if (rs.next()) {
            T t = clazz.newInstance();
            // 获取 结果集列数
            int columnCount = rsMetaData.getColumnCount();
            // 处理列值
            for (int i = 0; i < columnCount; i++) {
                // 获取 列名 推荐使用 columnLabel
                String columnLabel = rsMetaData.getColumnLabel(i + 1);
                // 列值
                Object columnValue = rs.getObject(i + 1);
                // 利用反射设置对象的字段值  根据字段名(字符串)去获取字段
                Field declaredField = clazz.getDeclaredField(columnLabel);
                declaredField.setAccessible(true);
                declaredField.set(t, columnValue);
            }
            return t;
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // 关闭资源
        JDBCUtil.closeResource(conn, ps, rs);
    }
    return null;
}
```

##### 6.3.4.2 测试 读未提交

```java
/**
     * 测试修改连接的数据库隔离级别 读
     */
@Test
public void transactionQueryTest() throws Exception {
    Connection conn = JDBCUtil.getConnection();
    // 设置隔离级别为读未提交
    conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
    String sql = "select id,name,password,balance from bank_user where id = ?";
    BankUser bankUser = commonQuery(conn, BankUser.class, sql, 1);
    System.out.println(bankUser);
}

/**
     * 测试修改连接的数据库隔离级别 更新
     */
@Test
public void transactionUpdateTest() throws Exception {
    Connection conn = JDBCUtil.getConnection();
    // 设置不自动提交
    conn.setAutoCommit(false);
    // 设置隔离级别为读未提交
    conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
    String sql = "update bank_user set balance=balance-100 where id = ?";
    int i = commonUpdate(conn, sql, 1);
    // 15s
    Thread.sleep(15000);
    //conn.commit();
}
```

首先运行（读）  ` transactionQueryTest()`

```shell
BankUser{id=1, name='A', password='123456', balance=700.0}
```

再运行（更新）`transactionUpdateTest()`

在 `Thread.sleep(15000)` 时间内再次运行（读）  ` transactionQueryTest()`

```shell
BankUser{id=1, name='A', password='123456', balance=600.0}
```

在 （更新）`transactionUpdateTest()` 线程结束后，再次运行（读）  ` transactionQueryTest()`

```shell
BankUser{id=1, name='A', password='123456', balance=700.0}
```

## 第七章 DAO 及其实现类

### 【BaseDAO.java】

- 通用增删改
- 通用查询（返回一个数据）
- 通用查询（返回多个数据）
- 通用单个特殊值查询

```java
package com.zuahua1.dao;

import com.zuahua1.util.JDBCUtil;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanghua
 * @createTime 2021/4/22 11:34
 */
public abstract class BaseDAO {
    /**
     * 考虑事务的通用查询 返回一个数据 version 2.0
     *
     * @param conn  连接
     * @param clazz clazz
     * @param sql   SQL
     * @param args  占位符参数
     * @param <T>   表对应bean类
     * @return <T> T
     */
    public <T> T commonQuery(Connection conn, Class<T> clazz, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            // 设置占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            // 执行
            rs = ps.executeQuery();
            // 获取 结果集元数据
            ResultSetMetaData rsMetaData = rs.getMetaData();

            // 处理结果集
            if (rs.next()) {
                T t = clazz.newInstance();
                // 获取 结果集列数
                int columnCount = rsMetaData.getColumnCount();
                // 处理列值
                for (int i = 0; i < columnCount; i++) {
                    // 获取 列名 推荐使用 columnLabel
                    String columnLabel = rsMetaData.getColumnLabel(i + 1);
                    // 列值
                    Object columnValue = rs.getObject(i + 1);
                    // 利用反射设置对象的字段值  根据字段名(字符串)去获取字段
                    Field declaredField = clazz.getDeclaredField(columnLabel);
                    declaredField.setAccessible(true);
                    declaredField.set(t, columnValue);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            JDBCUtil.closeResource(null, ps, rs);
        }
        return null;
    }

    /**
     * 通用查询 返回结果列表 考虑事务 version 2.0
     *
     * @param clazz clazz
     * @param sql   SQL
     * @param args  占位符参数
     * @param <T>   表对应bean类
     * @return <T> T
     */
    public <T> List<T> commonQueryList(Connection conn, Class<T> clazz, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            // 设置占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            // 执行
            rs = ps.executeQuery();
            // 获取 结果集元数据
            ResultSetMetaData rsMetaData = rs.getMetaData();
            ArrayList<T> list = new ArrayList<>();
            // 处理结果集
            while (rs.next()) {
                T t = clazz.newInstance();
                // 获取 结果集列数
                int columnCount = rsMetaData.getColumnCount();
                // 处理列值
                for (int i = 0; i < columnCount; i++) {
                    // 获取 列名 推荐使用 columnLabel
                    String columnLabel = rsMetaData.getColumnLabel(i + 1);
                    // 列值
                    Object columnValue = rs.getObject(i + 1);
                    // 利用反射设置对象的字段值  根据字段名(字符串)去获取字段
                    Field declaredField = clazz.getDeclaredField(columnLabel);
                    declaredField.setAccessible(true);
                    declaredField.set(t, columnValue);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            JDBCUtil.closeResource(null, ps, rs);
        }
        return null;
    }

    /**
     * 通用增删改 version 2.0 考虑事务
     *
     * @param conn 连接
     * @param sql  SQL
     * @param args 占位符参数
     * @return
     */
    public int commonUpdate(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(null, ps);
        }
        return -1;
    }

    /**
     * 用于查询特殊值的通用方法
     *
     * @param conn
     * @param sql
     * @param args
     * @param <E>
     * @return
     */
    public <E> E getValue(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            ps.executeQuery();
            rs = ps.getResultSet();

            if (rs.next()) {
                return (E) rs.getObject(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn, ps, rs);
        }
        return null;
    }
}
```

### 【EmployeeDAO.java】

```java
package com.zuahua1.dao;

import com.zuahua1.bean.Employee;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

/**
 * @author zhanghua
 * @createTime 2021/4/22 11:55
 */
public interface EmployeeDAO {
    /**
     * 插入数据
     *
     * @param conn
     * @param emploee
     */
    void insert(Connection conn, Employee emploee);

    /**
     * 通过id删除
     *
     * @param conn
     * @param id
     */
    void deleteById(Connection conn, int id);

    /**
     * 通过id更新
     *
     * @param conn
     * @param emploee
     */
    void update(Connection conn, Employee emploee);

    /**
     * 通过id查询
     *
     * @param conn
     * @param id
     * @return
     */
    Employee getEmployeeById(Connection conn, int id);

    /**
     * 获取所有数据
     *
     * @param conn
     * @return
     */
    List<Employee> getAll(Connection conn);

    /**
     * 获取行数
     *
     * @param conn
     * @return
     */
    Long getCount(Connection conn);

    /**
     * 获取最大生日
     *
     * @param conn
     * @return
     */
    Date getMaxBirth(Connection conn);
}
```

### 【EmployeeDAOImpl.java】

```java
package com.zuahua1.dao;

import com.zuahua1.bean.Employee;
import com.zuahua1.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author zhanghua
 * @createTime 2021/4/22 13:22
 */
public class EmployeeDAOImpl extends BaseDAO implements EmployeeDAO {
    @Override
    public void insert(Connection conn, Employee employee) {
        String sql = "insert into employee(name,password,birth,photo) values(?,?,?,?)";
        commonUpdate(conn, sql, employee.getName(), employee.getPassword(), employee.getBirth(), employee.getPhoto());
    }

    @Override
    public void deleteById(Connection conn, int id) {
        String sql = "delete from employee where id = ?";
        commonUpdate(conn, sql, id);
    }

    @Override
    public void update(Connection conn, Employee employee) {
        String sql = "update employee set name=?,password=?,birth=? where id = ?";
        commonUpdate(conn, sql, employee.getName(), employee.getPassword(), employee.getBirth(), employee.getId());
    }

    @Override
    public Employee getEmployeeById(Connection conn, int id) {
        String sql = "select id,name,password,birth from employee where id = ?";
        Employee employee = commonQuery(conn, Employee.class, sql, id);
        return employee;
    }

    @Override
    public List<Employee> getAll(Connection conn) {
        String sql = "select id,name,password,birth from employee";
        List<Employee> employeeList = commonQueryList(conn, Employee.class, sql);
        return employeeList;
    }

    @Override
    public Long getCount(Connection conn) {
        String sql = "select count(*) from employee";
        Long count = getValue(conn, sql);
        JDBCUtil.closeResource(conn, null);
        return count;
    }

    @Override
    public Date getMaxBirth(Connection conn) {
        String sql = "select birth from employee order by birth desc limit 1";
        return (Date) getValue(conn, sql);
    }
}
```

### 测试【EmployeeDAOImplTest.java】

```java
package com.zuahua1.junit;

import com.zuahua1.bean.Employee;
import com.zuahua1.dao.EmployeeDAOImpl;
import com.zuahua1.util.JDBCUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**
 * @author zhanghua
 * @createTime 2021/4/23 15:22
 */
class EmployeeDAOImplTest {
    EmployeeDAOImpl dao = new EmployeeDAOImpl();

    @Test
    void insert() {
        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            dao.insert(conn, new Employee(null, "Tname", "123456", new Date(345678L), null));
            System.out.println("插入成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn, null);
        }
    }

    @Test
    void deleteById() {
        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            dao.deleteById(conn, 1);
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn, null);
        }
    }

    @Test
    void update() {
        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            Employee employee = new Employee(5, "Bx", null, new Date(888888L), null);
            dao.update(conn, employee);
            System.out.println("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn, null);
        }
    }

    @Test
    void getEmploeeById() {
        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            Employee employee = dao.getEmployeeById(conn, 5);
            System.out.println(employee);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn, null);
        }
    }

    @Test
    void getAll() {
        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            List<Employee> employeeList = dao.getAll(conn);
            for (Employee e : employeeList) {
                System.out.println(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn, null);
        }
    }

    @Test
    void getCount() {
        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            Long count = dao.getCount(conn);
            System.out.println(count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn, null);
        }
    }

    @Test
    void getMaxBirth() {
        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            java.util.Date birth = dao.getMaxBirth(conn);
            System.out.println(birth);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn, null);
        }
    }
}
```

### 优化 DAO 及其 DAOImpl

> `EmployeeDAOImpl.java`中 调用通用查询方法时，都使用了 `Employee.class`参数
>
> 现在希望去掉 `Employee.class` 参数，使用 `泛型`的方式，`BaseDAO.java`  改造成 `泛型类`
>
> 在 实例化 `EmployeeDAOImpl`类时，需要获取到 `父类的泛型`

#### BaseDAO.java

```java
package com.zuahua1.dao.yh;

import com.zuahua1.util.JDBCUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanghua
 * @createTime 2021/4/22 11:34
 */
public abstract class BaseDAO<T> {
    private Class<T> clazz = null;

    {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) genericSuperclass;
        Type[] types = paramType.getActualTypeArguments();
        clazz = (Class<T>) types[0];
    }

    /**
     * 考虑事务的通用查询 返回一个数据 version 2.0
     *
     * @param conn 连接
     * @param sql  SQL
     * @param args 占位符参数
     * @return  T
     */
    public T commonQuery(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            // 设置占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            // 执行
            rs = ps.executeQuery();
            // 获取 结果集元数据
            ResultSetMetaData rsMetaData = rs.getMetaData();

            // 处理结果集
            if (rs.next()) {
                T t = clazz.newInstance();
                // 获取 结果集列数
                int columnCount = rsMetaData.getColumnCount();
                // 处理列值
                for (int i = 0; i < columnCount; i++) {
                    // 获取 列名 推荐使用 columnLabel
                    String columnLabel = rsMetaData.getColumnLabel(i + 1);
                    // 列值
                    Object columnValue = rs.getObject(i + 1);
                    // 利用反射设置对象的字段值  根据字段名(字符串)去获取字段
                    Field declaredField = clazz.getDeclaredField(columnLabel);
                    declaredField.setAccessible(true);
                    declaredField.set(t, columnValue);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            JDBCUtil.closeResource(null, ps, rs);
        }
        return null;
    }

    /**
     * 通用查询 返回结果列表 考虑事务 version 2.0
     *
     * @param sql  SQL
     * @param args 占位符参数
     * @return  T
     */
    public List<T> commonQueryList(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            // 设置占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            // 执行
            rs = ps.executeQuery();
            // 获取 结果集元数据
            ResultSetMetaData rsMetaData = rs.getMetaData();
            ArrayList<T> list = new ArrayList<>();
            // 处理结果集
            while (rs.next()) {
                T t = clazz.newInstance();
                // 获取 结果集列数
                int columnCount = rsMetaData.getColumnCount();
                // 处理列值
                for (int i = 0; i < columnCount; i++) {
                    // 获取 列名 推荐使用 columnLabel
                    String columnLabel = rsMetaData.getColumnLabel(i + 1);
                    // 列值
                    Object columnValue = rs.getObject(i + 1);
                    // 利用反射设置对象的字段值  根据字段名(字符串)去获取字段
                    Field declaredField = clazz.getDeclaredField(columnLabel);
                    declaredField.setAccessible(true);
                    declaredField.set(t, columnValue);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            JDBCUtil.closeResource(null, ps, rs);
        }
        return null;
    }

    /**
     * 通用增删改 version 2.0 考虑事务
     *
     * @param conn 连接
     * @param sql  SQL
     * @param args 占位符参数
     * @return
     */
    public int commonUpdate(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(null, ps);
        }
        return -1;
    }

    /**
     * 用于查询特殊值的通用方法
     *
     * @param conn
     * @param sql
     * @param args
     * @param <E>
     * @return
     */
    public <E> E getValue(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            ps.executeQuery();
            rs = ps.getResultSet();

            if (rs.next()) {
                return (E) rs.getObject(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn, ps, rs);
        }
        return null;
    }
}
```

#### EmployeeDAOImpl.java

```java
package com.zuahua1.dao.yh;

import com.zuahua1.bean.Employee;
import com.zuahua1.util.JDBCUtil;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

/**
 * @author zhanghua
 * @createTime 2021/4/22 13:22
 */
public class EmployeeDAOImpl extends BaseDAO<Employee> implements EmployeeDAO {
    @Override
    public void insert(Connection conn, Employee employee) {
        String sql = "insert into employee(name,password,birth,photo) values(?,?,?,?)";
        commonUpdate(conn, sql, employee.getName(), employee.getPassword(), employee.getBirth(), employee.getPhoto());
    }

    @Override
    public void deleteById(Connection conn, int id) {
        String sql = "delete from employee where id = ?";
        commonUpdate(conn, sql, id);
    }

    @Override
    public void update(Connection conn, Employee employee) {
        String sql = "update employee set name=?,password=?,birth=? where id = ?";
        commonUpdate(conn, sql, employee.getName(), employee.getPassword(), employee.getBirth(), employee.getId());
    }

    @Override
    public Employee getEmployeeById(Connection conn, int id) {
        String sql = "select id,name,password,birth from employee where id = ?";
        Employee employee = commonQuery(conn, sql, id);
        return employee;
    }

    @Override
    public List<Employee> getAll(Connection conn) {
        String sql = "select id,name,password,birth from employee";
        List<Employee> employeeList = commonQueryList(conn, sql);
        return employeeList;
    }

    @Override
    public Long getCount(Connection conn) {
        String sql = "select count(*) from employee";
        Long count = getValue(conn, sql);
        JDBCUtil.closeResource(conn, null);
        return count;
    }

    @Override
    public Date getMaxBirth(Connection conn) {
        String sql = "select birth from employee order by birth desc limit 1";
        return (Date) getValue(conn, sql);
    }
}
```

## 第八章 数据库连接池

### 8.1 数据库连接池的必要性

<img src="https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210423171957.png" style="zoom:150%;" />![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210423172752.png)

### 8.2 数据库连接池技术

<img src="https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210423171957.png" style="zoom:150%;" />![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210423172752.png)

- 工作原理

<img src="https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210423173114.png" style="zoom:150%;" />

- 数据库连接池的优点

<img src="https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210423173242.png" style="zoom:150%;" />



### 8.3 多种开源数据库连接池

<img src="https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210423173605.png" style="zoom:150%;" />



#### 8.3.1 C3P0 数据库连接池

##### 8.3.1.1 文档、依赖

> 文档地址： <https://www.mchange.com/projects/c3p0/>
>
> Maven地址：<https://mvnrepository.com/artifact/com.mchange/c3p0/0.9.5>

依赖：

```xml
<!-- https://mvnrepository.com/artifact/com.mchange/c3p0 -->
<dependency>
    <groupId>com.mchange</groupId>
    <artifactId>c3p0</artifactId>
    <version>0.9.5</version>
</dependency>

##### 8.3.1.2 C3P0连接数据库

###### 方式一:手写配置

```java
@Test
public void c3p0Test() throws Exception {
    // 获取c3p0数据库连接池
    ComboPooledDataSource cpds = new ComboPooledDataSource();
    cpds.setDriverClass("com.mysql.cj.jdbc.Driver"); //loads the jdbc driver
    cpds.setJdbcUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowMultiQueries=true");
    cpds.setUser("root");
    cpds.setPassword("123456");

    // 设置相关参数，对数据库连接池进行管理
    // 设置初始池大小
    cpds.setInitialPoolSize(10);

    Connection conn = cpds.getConnection();
    System.out.println(conn);

    // cpds.close();
    // DataSources.destroy(cpds);
}
```

###### 方式二：xml配置文件方式

```java
@Test
public void c3p0Test2() throws Exception {
    ComboPooledDataSource cpds = new ComboPooledDataSource("hello-c3p0");
    Connection conn = cpds.getConnection();
    System.out.println(conn);
}
```

`c3p0-config.xml` 文件：

```xml
<?xml version="1.0" encoding="utf-8" ?>
<c3p0-config>
  <named-config name="hello-c3p0">
    <!--提供连接的4个基本信息-->
    <property name="com.mysql.cj.jdbc.Driver">5</property>
    <property name="jdbcUrl">jdbc:mysql://localhost:3306/test?characterEncoding=utf8&amp;serverTimezone=Asia/Shanghai&amp;useSSL=false&amp;allowMultiQueries=true</property>
    <property name="user">root</property>
    <property name="password">123456</property>

    <!-- 数据库连接池管理 基本信息 -->
    <!-- 当数据库连接池中连接数不够时，一次性向数据库服务器申请的连接数 -->
    <property name="acquireIncrement">5</property>
    <!-- 初始化池的可连接数 -->
    <property name="initialPoolSize">10</property>
    <!-- 维护的最小可连接数 -->
    <property name="minPoolSize">10</property>
    <!-- 维护的最大可连接数 -->
    <property name="maxPoolSize">100</property>
    <!-- 最多维护的Statement的个数 -->
    <property name="maxStatements">50</property>
    <!-- 每个连接最多维护的Statement的个数 -->
    <property name="maxStatementsPerConnection">5</property>
  </named-config>
</c3p0-config>
```

##### 8.3.1.3 JDBCUtil  C3P0

`JDBCUtil.java` 中添加 获取C3P0通用连接

```java
private static ComboPooledDataSource cpds = new ComboPooledDataSource("hello-c3p0");

/**
* 获取C3P0连接
*
* @return conn
* @throws Exception e
*/
public static Connection getC3P0Connection() throws Exception {
    Connection conn = cpds.getConnection();
    return conn;
}
```



#### 8.3.2 DBCP 数据库连接池

##### 8.3.2.1 文档与Maven

> 文档：<https://javadoc.io/doc/commons-dbcp/commons-dbcp/1.4/org/apache/commons/dbcp/package-summary.html>
>
> Maven：<https://mvnrepository.com/artifact/commons-dbcp/commons-dbcp/1.4>

依赖：

```xml
<!-- https://mvnrepository.com/artifact/commons-dbcp/commons-dbcp -->
<dependency>
    <groupId>commons-dbcp</groupId>
    <artifactId>commons-dbcp</artifactId>
    <version>1.4</version>
</dependency>

<!-- https://mvnrepository.com/artifact/commons-pool/commons-pool -->
<dependency>
    <groupId>commons-pool</groupId>
    <artifactId>commons-pool</artifactId>
    <version>1.5.5</version>
</dependency>
```

##### 8.3.2.2 DBCP 连接

###### 方式一：写死配置

```java
@Test
public void dbcpGetConnection() throws Exception {
    // 获取dbcp数据库连接池
    BasicDataSource source = new BasicDataSource();

    // 设置4个基本信息
    source.setDriverClassName("com.mysql.cj.jdbc.Driver");
    source.setUrl("jdbc:mysql://localhost:3306/test?characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowMultiQueries=true");
    source.setUsername("root");
    source.setPassword("123456");

    // 设置数据库连接池的管理参数
    source.setInitialSize(10);
    source.setMaxActive(10);
    // ...

    // 获取连接
    Connection conn = source.getConnection();

    System.out.println(conn);
}
```

###### 方式二：配置文件方式

```java
@Test
public void getDBCPConnection2() throws Exception {
    // 获取配置文件流
    InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");
    // 加载流
    Properties prop = new Properties();
    prop.load(is);
    // 连接池
    DataSource source = BasicDataSourceFactory.createDataSource(prop);

    // 获取连接
    Connection conn = source.getConnection();

    System.out.println(conn);
}
```

`dbcp.properties` 文件

```properties
username=root
password=123456
driverClassName=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/test?characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowMultiQueries=true
initialSize=10
maxActive=10
```

##### 8.3.2.3 JDBCUtil.java 获取 DBCP 连接

```java
private static DataSource source;

static {
    try {
        // 获取配置文件流
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");
        // 加载流
        Properties prop = new Properties();
        prop.load(is);
        // 连接池
        source = BasicDataSourceFactory.createDataSource(prop);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

/**
     * 获取DBCP连接
     * @return
     * @throws Exception
     */
public static Connection getDBCPConnection() throws Exception {
    // 获取连接
    Connection conn = source.getConnection();
    return conn;
}
```

#### 8.3.3 Druid 数据库连接池

##### 8.3.3.1 文档、依赖

> 文档：<https://github.com/alibaba/druid/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98>
>
> Maven：<https://mvnrepository.com/artifact/com.alibaba/druid/1.1.10>

```xml
<!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.10</version>
</dependency>
```

##### 8.3.3.2 配置信息

> 注意：jdbcUrl在配置中为名为 `url`

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210507105704.png)

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210507105745.png)

![image-20210507105849595](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20210507105849595.png)

> 图源：<https://blog.csdn.net/yunnysunny/article/details/8657095>

##### 8.3.3.3 Druid获取连接

###### 配置文件方式

```java
@Test
public void getDruidConnection() throws Exception {
    InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
    Properties prop = new Properties();
    prop.load(is);

    DataSource dataSource = DruidDataSourceFactory.createDataSource(prop);
    Connection conn = dataSource.getConnection();

    System.out.println(conn);
}
```

配置文件：`druid.properties`

```properties
username=root
password=123456
driverClassName=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/test?characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowMultiQueries=true

initialSize=10
maxActive=20
```

##### 8.3.3.4JdbcUtil.java

```java
private static DataSource dataSource = null;

static {
    try {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        Properties prop = new Properties();
        prop.load(is);

        dataSource = DruidDataSourceFactory.createDataSource(prop);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

/**
     * 获取Druid连接
     * @return conn
     * @throws Exception e
     */
public static Connection getDruidConnection() throws Exception {
    Connection conn = dataSource.getConnection();
    return conn;
}
```



## 第九章 Apache-DBUtils 实现 CRUD 操作

### 9.1 Apache-DBUtils介绍

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210507113159.png)



###  9.2文档、Maven

>文档：<https://commons.apache.org/proper/commons-dbutils/apidocs/>
>
>Maven：<https://mvnrepository.com/artifact/commons-dbutils/commons-dbutils>

### 9.3 测试

#### 9.3.1 update：增删改

```java
@Test
public void updateTest() {
    Connection conn = null;
    try {
        conn = JDBCUtil.getDruidConnection();

        QueryRunner queryRunner = new QueryRunner();
        String sql = "insert into employee(name,password,birth) values(?,?,?)";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = sdf.parse("1991-01-11");

        int count = queryRunner.update(conn, sql, "vvvv", "qqqqqqq", new java.sql.Date(parse.getTime()));
        System.out.println("插入" + count + "条数据");
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        JDBCUtil.closeResource(conn, null);
    }
}
```



#### 9.3.2 查

##### 9.3.2.1 查的结果为实体类相关

结果集，ResultSetHandler的实现类包括

- BeanHandler
- BeanListHandler
- MapHandler
- MapListHandler
- ...

```java
/**
     * BeanHandler：ResultSetHandler的实现类，对应实体类
     *
     * @throws Exception e
     */
@Test
public void queryTest1() {
    Connection conn = null;
    try {
        conn = JDBCUtil.getDruidConnection();

        QueryRunner queryRunner = new QueryRunner();
        String sql = "select * from employee where id = ?";

        BeanHandler<Employee> handler = new BeanHandler<>(Employee.class);
        Employee employee = queryRunner.query(conn, sql, handler, 5);
        System.out.println(employee);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        JDBCUtil.closeResource(conn, null);
    }
}

/**
     * BeanListHandler：ResultSetHandler的实现类，对应实体类List
     *
     * @throws Exception e
     */
@Test
public void queryTest2() {
    Connection conn = null;
    try {
        conn = JDBCUtil.getDruidConnection();

        QueryRunner queryRunner = new QueryRunner();
        String sql = "select id,name,password,birth from employee where id < ?";

        BeanListHandler<Employee> handler = new BeanListHandler<>(Employee.class);
        List<Employee> employeeList = queryRunner.query(conn, sql, handler, 5);
        employeeList.forEach(System.out::println);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        JDBCUtil.closeResource(conn, null);
    }
}
```



##### 9.3.2.2 查单个特殊值

- ScalarHandler

```java
/**
     * ScalarHandler：ResultSetHandler的实现类，查特殊值
     *
     * @throws Exception e
     */
@Test
public void queryTest3() {
    Connection conn = null;
    try {
        conn = JDBCUtil.getDruidConnection();

        QueryRunner queryRunner = new QueryRunner();
        String sql = "select count(*) from employee";

        ScalarHandler handler = new ScalarHandler();

        Long count = (Long) queryRunner.query(conn, sql, handler);
        System.out.println(count);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        JDBCUtil.closeResource(conn, null);
    }
}

/**
     * ScalarHandler：ResultSetHandler的实现类，查特殊值
     *
     * @throws Exception e
     */
@Test
public void queryTest4() {
    Connection conn = null;
    try {
        conn = JDBCUtil.getDruidConnection();

        QueryRunner queryRunner = new QueryRunner();
        String sql = "select max(birth) from employee";

        ScalarHandler handler = new ScalarHandler();

        Date date = (Date) queryRunner.query(conn, sql, handler);
        System.out.println(date);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        JDBCUtil.closeResource(conn, null);
    }
}
```

##### 9.3.2.3 自定义ResultSetHandler

- 自定义ResultSetHandler

```java
/**
     * 自定义ResultSetHandler
     */
@Test
public void queryTest5() {
    Connection conn = null;
    try {
        conn = JDBCUtil.getDruidConnection();

        QueryRunner queryRunner = new QueryRunner();
        String sql = "select id,name,password,birth from employee where id = ?";

        ResultSetHandler<Employee> handler = new ResultSetHandler<Employee>() {
            @Override
            public Employee handle(ResultSet rs) throws SQLException {
                if(rs.next()) {
                    int id = rs.getInt("id");
                    String password = rs.getString("password");
                    String name = rs.getString("name");
                    java.sql.Date birth = rs.getDate("birth");
                    Employee employee = new Employee(id, name, password, birth);
                    return employee;
                }
                return null;
            }
        };

        Employee employee = queryRunner.query(conn, sql, handler, 5);
        System.out.println(employee);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        JDBCUtil.closeResource(conn, null);
    }
}
```

#### 9.3.3 Apache-DBUtils 关闭资源

```java
/**
     * 关闭数据库资源
     *
     * @param conn
     * @param ps
     */
public static void closeResourceApacheDbUtils(Connection conn, Statement ps, ResultSet rs) {
    DbUtils.closeQuietly(conn);
    DbUtils.closeQuietly(ps);
    DbUtils.closeQuietly(rs);
}
```

