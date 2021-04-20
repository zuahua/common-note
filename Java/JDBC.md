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

![](..\resource\images\5.png)

### 3.3 使用Statement弊端

- 拼接SQL，注入问题
- 需要区分字段大小写

### 3.4 PreparedStatement 使用

#### 3.4.1 PreparedStatement 相比 Statement 优点

1. 不拼SQL字符串，解决SQL注入问题。
2. 能操作Blob类型，而Statement不行。
3. 高效的批量操作，只预编译SQL一次。

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
create table emploee(
	id int PRIMARY KEY auto_increment,
	name VARCHAR(10),
	password VARCHAR(40),
	birth DATE,
	photo BLOB
);

INSERT INTO emploee(name, password, birth) values('XiaoMing', '123456', DATE('1990-01-01'));
INSERT INTO emploee(name, password, birth) values('XiaoHong', '123456', DATE('1990-01-01'));
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
        // 注入sql: select id,name from emploee where name = '1' or 'and password' = '1' or '1'='1'
        // name = 1' or '
        // password = 1' or '1'='1
        String sql = "select id,name from emploee where name = '" + name + "'" + " and password = '" + password + "'";
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

```powershell
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
        // 注入sql: select id,name from emploee where name = '1' or ' and password = ' = '1' or 1=1
        // name = '1' or '
        // password = ' = '1' or 1=1
        String sql = "select id,name from emploee where name = ? and password = ?";
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

```powershell
输入用户名：
'1' or '
输入密码：
' = '1' or 1=1
登陆失败
```



### 3.6 Sql 类型 与 Java 类型对应关系

![](..\resource\images\6.png)

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

#### 3.7.2 思想与技术

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









