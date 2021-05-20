# Spring 5

[TOC]

## 1. 教程相关

> **奇客谷Spring教程** <https://www.qikegu.com/docs/1460>
>
> **尚硅谷** <https://www.bilibili.com/video/BV1Vf4y127N5>

## 2. 概述

> 1. Spring 是轻量级开源J2EE框架
> 2. 降低开发复杂性
> 3. 核心：IOC、AOP
>    1. IOC：反转控制
>    2. AOP：面向切面编程
> 4. 特点：
>    1. 方便解耦，简化开发
>    2. AOP
>    3. 方便测试
>    4. 方便与其他框架整合
>    5. 方便事务操作
>    6. 降低API开发难度



框架模块图：

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210519115404.png)

## 3. 简单使用

### 3.1 流程

1. 下载 Spring Framework  5.2.6 <https://repo.spring.io/release/org/springframework/spring/>
2. IDEA 建一个简单工程，导入包
3. Spring 方式新建对象实例

### 3.2 下载 Spring Framework  5.2.6

<https://repo.spring.io/release/org/springframework/spring/>

目录结构：

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210519115116.png)

### 3.3 IDEA 建一个简单工程，导入包

此测试只需要导入几个核心包:

> 1. spring-beans-5.2.6.RELEASE.jar
> 2. spring-context-5.2.6.RELEASE.jar
> 3. spring-core-5.2.6.RELEASE.jar
> 4. spring-expression-5.2.6.RELEASE.jar

> 5. commons-logging-1.2.jar  (单独下)

### 3.4 Spring 方式新建对象实例

#### 3.4.1  User 类

```java
public class User {
    private String name;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

#### 3.4.2 bean1.xml  配置文件

- 这里放在 `src/resource/bean1.xml`

- IDEA中，`右击->new->xml configuration file->spring config`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <!-- 配置 Bean -->
  <bean id="user" class="main.User"></bean>
</beans>
```

#### 3.4.3 测试使用

- `ClassPathXmlApplicationContext`加载bean配置文件
- `context.getBean()` 获取配置创建的对象

```java
public class BeanTest {
    @Test
    public void t1() {
        // 1.加载bean配置文件
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean1.xml");

        // 2.获取配置创建的对象
        User user = context.getBean("user", User.class);

        user.setName("AACBB");
        System.out.println(user.getName());
    }
}
```

## 4. IOC 容器

### 4.1 IOC 概念及底层原理

#### 4.1.1 IOC 概念

> 什么是IOC?
>
> 1. 控制反转，把对象创建和对象之间的调用过程，交给`Spring` 进行管理
> 2. 目的：降低耦合度
> 3. "简单使用" 中就是使用 IOC 创建对象

#### 4.1.2 底层原理

> 1. xml 解析、工厂模式、反射

图解：

1. 原始方式

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210519144458.png)

2. 工厂方式

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210519144612.png)

3. IOC 方式

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210519144636.png)

### 4.2 IOC 接口

> 1. IOC 思想基于 IOC 容器完成，IOC 容器底层就是 对象工厂
> 2. Spring IOC 实现的两种方式(两个接口)：
>    1. `BeanFactory`：IOC 容器基本实现
>       1. 加载配置文件时不会创建对象，获取(使用)对象才去创建对象
>    2. `ApplicationContext` BeanFactory的子接口，提供更多的功能
>       1. 加载配置文件时创建对象
>       2. 主要实现类：
>          1. `FileSystemXmlApplicationContext`
>          2. `ClassPathXmlApplicationContext`



### 4.3 IOC 操作

#### 4.3.1 bean 管理的概念

> 包括：
>
> 1. Spring 创建对象
> 2. Spring 注入属性

> 两种方式：
>
> 1. 基于 xml 配置文件方式实现
> 2. 基于注解方式实现

#### 4.3.2 基于 xml 的 Bean 管理

##### 4.3.2.1 基于 xml 方式创建对象

```xml
<bean id="user" class="main.User"></bean>
```

> 1. spring 配置文件中，使用 bean 标签
> 2. bean 标签属性：
>    1. `id` 属性：唯一标识
>    2. `class`属性：类全路径
> 3. 创建对象的时候，默认**<u>执行无参构造函数</u>**

##### 4.3.2.2 基于 xml 注入属性

> <u>**DI**</u> ：依赖注入，就是注入属性

###### 注入方式一：使用 **set** 方法进行注入

1.1 创建类，定义属性和`set`方法

- Book 类

```java
public class Book {
    private String name;

    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
```

1.2 xml 配置 

- 属性配置使用 `property` 标签

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="book" class="org.learn.spring5.Book">
    <!-- 属性注入 -->
    <property name="id" value="1"></property>
    <property name="name" value="Java 核心思想"></property>
  </bean>
    
</beans>
```

1.3 测试

```java
public class BeanTest {
    @Test
    public void t1() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean1.xml");
        Book book = context.getBean("book", Book.class);

        System.out.println(book.getId() + ": " + book.getName());
    }
}
```

打印结果：

```shell
1: Java 核心思想
```

**注意：**没有`set`方法报错如下

```shell
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'book' defined in class path resource [resource/bean1.xml]: Error setting property values; nested exception is org.springframework.beans.NotWritablePropertyException: Invalid property 'name' of bean class [org.learn.spring5.Book]: Bean property 'name' is not writable or has an invalid setter method. Does the parameter type of the setter match the return type of the getter?
```

###### 注入方式二：使用有参构造函数

1. 创建类，定义有参构造函数

- Person 类

```java
public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
```

2. xml 配置

- `constructor-arg` 标签注入属性

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="person" class="org.learn.spring5.Person">
    <!-- 使用有参构造器注入属性 -->
    <constructor-arg name="name" value="Chang"></constructor-arg>
    <constructor-arg name="age" value="19"></constructor-arg>
  </bean>

</beans>
```

3. 测试

```java
@Test
public void t2() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean1.xml");
    Person person = context.getBean("person", Person.class);

    System.out.println(person.getName() + "  " + person.getAge());
}
```

结果：

```shell
Chang  19
```

###### 方式一补充： `set`方式，使用`p名称空间`的属性注入

- p名称空间 `xmlns:p="http://www.springframework.org/schema/p"`
- 用法：`p:属性`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="book2" class="org.learn.spring5.Book" p:id="2" p:name="Java编程思想"></bean>

</beans>
```

###### 注入属性: `null`值与**特殊符号处理**

1. `null`值注入

- `<null></null>`

```xml
<bean id="book3" class="org.learn.spring5.Book">
    <property name="id" value="3"></property>
    <property name="name">
        <null></null>
    </property>
</bean>
```

2. 特殊符号处理

- `<![CDATA[<<JVM>>]]]>`输出 `<<JVM>>`
- 方式二：转义，小于`&lt`，大于`&gt`

```xml
<bean id="book4" class="org.learn.spring5.Book">
    <property name="id" value="3"></property>
    <property name="name">
        <value>
            <![CDATA[<<JVM>>]]]>
        </value>
    </property>
</bean>
```

###### 注入属性：外部 bean 的注入

> 例：
>
> 1. 创建：Service类和Dao类
>
> 2. 在Service中调用Dao中的方法

> 新的知识点：
>
> 1. xml 配置 `property`的`ref`属性

- **Dao** 及其 **DaoImpl**类

```java
public interface UserDao {
    void update();
}
```

```java
public class UserDaoImpl implements UserDao {
    @Override
    public void update() {
        System.out.println("dao update...");
    }
}
```

- **Service** 类

> 包含属性：**UserDao**
>
> **set** 方法

```java
public class UserService {
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void add() {
        userDao.update();

        System.out.println("service add...");
    }
}
```

- **xml** 配置 （`resource/bean2.xml`）

> <property name="userDao" `ref`="userDaoImpl"></property>

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <!-- 配置service和dao -->
  <bean id="userService" class="org.learn.spring5.service.UserService">
    <!-- 注入外部bean属性 -->
    <property name="userDao" ref="userDaoImpl"></property>
  </bean>

  <bean id="userDaoImpl" class="org.learn.spring5.dao.UserDaoImpl"></bean>

</beans>
```

- 测试

```java
@Test
public void t5() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean2.xml");
    UserService userService = context.getBean("userService", UserService.class);

    userService.add();
}
```

###### 注入属性：内部bean

> 例：Employee类中包含Deptment属性，Deptment具有自己的属性

> 新知识点：配置 `property` 标签下 嵌入`bean`标签

- xml 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="employee" class="org.learn.spring5.Employee">
    <property name="ename" value="Chang"></property>
    <property name="gender" value="女"></property>
	<!-- 内部bean -->
    <property name="dep">
      <bean id="deptment" class="org.learn.spring5.Deptment">
        <property name="dname" value="CIA部门"></property>
      </bean>
    </property>
  </bean>

</beans>
```

- Employee

```java
public class Employee {
    private String ename;
    private String gender;
    private Deptment dep;

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Deptment getDep() {
        return dep;
    }

    public void setDep(Deptment dep) {
        this.dep = dep;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "ename='" + ename + '\'' +
                ", gender='" + gender + '\'' +
                ", dep=" + dep +
                '}';
    }
}
```

- Deptment

```java
public class Deptment {
    private String dname;

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    @Override
    public String toString() {
        return "Deptment{" +
                "dname='" + dname + '\'' +
                '}';
    }
}
```

- test

```java
@Test
public void t6() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean3.xml");
    Employee employee = context.getBean("employee", Employee.class);

    System.out.println(employee);
}
```

```shell
Employee{ename='Chang', gender='女', dep=Deptment{dname='CIA部门'}}
```

###### 注入属性：级联赋值

> 使用上文 Employee、Deptment

- xml 配置方式一

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <!-- 级联赋值 -->
  <bean id="employee" class="org.learn.spring5.Employee">
    <property name="ename" value="Chang"></property>
    <property name="gender" value="女"></property>

    <property name="dep" ref="deptment"></property>
  </bean>

  <bean id="deptment" class="org.learn.spring5.Deptment">
    <property name="dname" value="CIA部门"></property>
  </bean>
</beans>
```

- xml 配置方式二

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <!-- 级联赋值 -->
  <bean id="employee" class="org.learn.spring5.Employee">
    <property name="ename" value="Chang"></property>
    <property name="gender" value="女"></property>

    <property name="dep" ref="deptment"></property>
    <!-- dep.dname 方式赋值，需要有getter -->
    <property name="dep.dname" value="FIB部门"></property>
  </bean>

  <bean id="deptment" class="org.learn.spring5.Deptment"></bean>
</beans>
```



#### 4.3.3 基于 注解 的 Bean 管理





























