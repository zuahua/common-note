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

###### 注入属性：数组、List、Map、Set

- xml 配置 (**bean4.xml**)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="classList" class="org.learn.spring5.ClassList">
    <property name="strings">
      <array>
        <value>A</value>
        <value>B</value>
      </array>
    </property>

    <property name="list">
      <list>
        <value>C</value>
        <value>D</value>
      </list>
    </property>

    <property name="map">
      <map>
        <entry key="1" value="E"></entry>
        <entry key="2" value="F"></entry>
      </map>
    </property>

    <property name="set">
      <set>
        <value>G</value>
        <value>H</value>
      </set>
    </property>
  </bean>

</beans>
```

- **ClassList**

```java
public class ClassList {
    private String[] strings;

    private List<String> list;

    private Map<String, String> map;

    private Set<String> set;

    public String[] getStrings() {
        return strings;
    }

    public void setStrings(String[] strings) {
        this.strings = strings;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public Set<String> getSet() {
        return set;
    }

    public void setSet(Set<String> set) {
        this.set = set;
    }

    @Override
    public String toString() {
        return "ClassList{" +
                "strings=" + Arrays.toString(strings) +
                ", list=" + list +
                ", map=" + map +
                ", set=" + set +
                '}';
    }
}
```

- test

```java
@Test
public void t8() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean4.xml");

    ClassList classList = context.getBean("classList", ClassList.class);
    System.out.println(classList);
}
```

```shell
ClassList{strings=[A, B], list=[C, D], map={1=E, 2=F}, set=[G, H]}
```

###### 注入属性：集合类型

> 新的配置标签: `ref`

- xml 配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="library" class="org.learn.spring5.Library">
    <property name="books">
      <list>
        <!-- 集合 ref 方式 -->  
        <ref bean="book1"></ref>
        <ref bean="book2"></ref>
      </list>
    </property>
  </bean>

  <bean id="book1" class="org.learn.spring5.Book">
    <property name="id" value="1"></property>
    <property name="name" value="Java编程思想"></property>
  </bean>
  <bean id="book2" class="org.learn.spring5.Book">
    <property name="id" value="2"></property>
    <property name="name" value="Java核心技术"></property>
  </bean>
</beans>
```

- `Library`

```java
public class Library {
    private List<Book> books;

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "Library{" +
                "books=" + books +
                '}';
    }
}
```

- `Book`

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

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
```

- test

```java
@Test
public void t9() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean5.xml");

    Library library = context.getBean("library", Library.class);
    System.out.println(library);
}
```

```shell
Library{books=[Book{name='Java编程思想', id=1}, Book{name='Java核心技术', id=2}]}
```

###### 注入属性：集合属性提取 `util`命名空间

> 命名空间：
>
> `xmlns:util="http://www.springframework.org/schema/util"`
>
> `http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd`

- 使用 `Library`和`Book`类

- xml 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd">

  <bean id="library" class="org.learn.spring5.Library">
    <property name="books" ref="bookList">
    </property>
  </bean>

  <!-- util 方式 -->
  <util:list id="bookList">
    <bean id="book1" class="org.learn.spring5.Book">
      <property name="id" value="1"></property>
      <property name="name" value="Java编程思想"></property>
    </bean>
    <bean id="book2" class="org.learn.spring5.Book">
      <property name="id" value="2"></property>
      <property name="name" value="Java核心技术"></property>
    </bean>
  </util:list>

</beans>
```

#### 4.3.3 操作 Bean 管理 `FactoryBean`

> 1. **Spring** 有两种类型的**bean**，一种是普通的**bean**，一种是**FactoryBean**
> 2. 普通**bean**：配置文件中定义**bean**类型就是返回类型
> 3. 工厂**bean**：配置文件定义的**bean**类型可以和返回类型不一样

- 实现 **FactoryBean**

```java
public class MyBean implements FactoryBean<Book> {
    @Override
    public Book getObject() throws Exception {
        // 实际应工厂模式 + 反射，这里模拟
        Book book = new Book();
        book.setId(1);
        book.setName("A");
        return book;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
```

- 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="myBean" class="org.learn.spring5.factorybean.MyBean"></bean>

</beans>
```

- test

```java
@Test
public void t11() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean7.xml");

    Book myBean = context.getBean("myBean", Book.class);
    System.out.println(myBean);
}
```

#### 4.3.4 Bean 管理：Bean 的作用域

> 1. Spring 中，创建 Bean 实例默认是**<u>单例</u>**的
> 2. `bean`标签中，`scope`属性值：`singleton`和`prototype`
> 3. `singleton`：**单例，加载Spring配置文件时就会创建对应Bean实例**
> 4. `prototype`：多实例，调用`getBean()`方法时创建多实例对象

##### 4.3.4.1 默认单例测试，查看对象地址

1. xml 配置

```xml
<bean id="book" class="org.learn.spring5.Book1"></bean>
```

2. Book1类

```java
public class Book1 {
}
```

3. test

```java
@Test
public void t12() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean7.xml");

    Book1 book1 = context.getBean("book", Book1.class);
    Book1 book2 = context.getBean("book", Book1.class);
    System.out.println(book1);
    System.out.println(book2);
}
```

结果：**<u>地址相同</u>**

```shell
org.learn.spring5.Book1@3e58a80e
org.learn.spring5.Book1@3e58a80e
```

##### 4.3.4.2 `prototype` 多实例测试

1. xml 配置修改

`scope="prototype"`

```xml
<bean id="book" class="org.learn.spring5.Book1" scope="prototype"></bean>
```

2. 测试结果，**<u>地址不一致</u>**

```shell
org.learn.spring5.Book1@3e58a80e
org.learn.spring5.Book1@4fb61f4a
```

#### 4.3.5 Bean 管理：Bean 的生命周期

##### 4.3.5.1 Bean 生命周期

> 1. 通过构造器创建bean实例（无参构造方法）
> 2. 为bean的属性设置值和对其他bean的引用
> 3. 调用bean的初始化方法（配置初始化方法）
> 4. bean可以使用了
> 5. 关闭容器，销毁bean（配置销毁方法）

- 演示

1. xml 配置

```xml
<bean id="book2" class="org.learn.spring5.Book2" init-method="initMethod" destroy-method="destoryMethod">
    <property name="name" value="Java核心思想"></property>
</bean>
```

2. Book2类

```java
public class Book2 {
    public Book2() {
        System.out.println("1... 调用无参构造器");
    }

    private String name;

    public void setName(String name) {
        this.name = name;
        System.out.println("2... 设置属性值");
    }

    public void initMethod() {
        System.out.println("3... 初始化方法");
    }

    public void destoryMethod() {
        System.out.println("5... 销毁bean");
    }
}
```

3. test

```java
@Test
public void t13() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean7.xml");

    Book2 book = context.getBean("book2", Book2.class);
    System.out.println("4... 获取到bean 可以使用了");

    // 关闭容器
    context.close();
}
```

4. 结果

```shell
1... 调用无参构造器
2... 设置属性值
3... 初始化方法
4... 获取到bean 可以使用了
5... 销毁bean
```

##### 4.3.5.2 bean 生命周期：后置处理器

> 1. 通过构造器创建bean实例（无参构造方法）
> 2. 为bean的属性设置值和对其他bean的引用
> 3. 将bean传递给后置处理器方法 
> 4. 调用bean的初始化方法（配置初始化方法）
> 5. 将bean传递给后置处理器方法 
> 6. bean可以使用了
> 7. 关闭容器，销毁bean（配置销毁方法）

> **注意：后置处理器，每个bean的创建都会调用**
>
> 后置处理器，实现`BeanPostProcessor`接口

- 例

1. xml 配置

```xml
<bean id="book2" class="org.learn.spring5.Book2" init-method="initMethod" destroy-method="destoryMethod">
    <property name="name" value="Java核心思想"></property>
</bean>

<!-- 配置后置处理器 -->
<bean id="postProcessor" class="org.learn.spring5.PostPro"></bean>
```

2. 后置处理器，实现`BeanPostProcessor`接口

```java
public class PostPro implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("... 初始化之前");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("... 初始化之后");
        return bean;
    }
}
```

3. Book2类见前文
4. test

```java
@Test
public void t13() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean7.xml");

    Book2 book = context.getBean("book2", Book2.class);
    System.out.println("4... 获取到bean 可以使用了");

    // 关闭容器
    context.close();
}
```

5. 结果

```shell
1... 调用无参构造器
2... 设置属性值
... 初始化之前
3... 初始化方法
... 初始化之后
4... 获取到bean 可以使用了
5... 销毁bean
```

#### 4.3.6 Bean 管理：**自动装配** (基于xml)

##### 4.3.6.1 概念

> **自动装配概念：**
>
> **<u>根据指定装配规则（属性名称或者属性类型），Spring 自动将匹配的属性值进行注入</u>**

##### 4.3.6.2 装配过程

> 配置文件`bean`标签的`aotowire`属性
>
> 属性值：`byName`、`byType`
>
> 1. `byName`：注入`bean`的`id`值和类属性名称一样
> 2. `byType`：属性类型和注入`bean`类型一样

##### 4.3.6.3 `byName` 例

1. xml 配置

```xml
<bean id="employee" class="org.learn.spring5.autowire.Employee" autowire="byName"></bean>
<!-- 属性名相同 dep -->
<bean id="dep" class="org.learn.spring5.autowire.Deptment">
    <property name="dname" value="职能部"></property>
</bean>
```

2. `Deptment`、`Employee`类

```java
public class Deptment {
    private String dname;

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

```java
public class Employee {
    private Deptment dep;

    public void setDep(Deptment dep) {
        this.dep = dep;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "dep=" + dep +
                '}';
    }
}
```

3. test

```java
@Test
public void t14() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean8.xml");

    org.learn.spring5.autowire.Employee employee = context.getBean("employee", org.learn.spring5.autowire.Employee.class);
    System.out.println(employee);
}
```

```shell
Employee{dep=Deptment{dname='职能部'}}
```

##### 4.3.6.4 `byType` 例

- xml 配置

```xml
<bean id="employee" class="org.learn.spring5.autowire.Employee" autowire="byType"></bean>
<!-- 属性类型相同 Deptment -->
<bean id="dep" class="org.learn.spring5.autowire.Deptment">
    <property name="dname" value="职能部"></property>
</bean>
```

#### 4.3.7 通过**外部文件**配置属性

> 以**Druid**数据库连接池为例

> 知识点：
>
> 新建 **properties** 配置文件
>
> xml 配置文件 `properties` 文件路径，添加 `context`名称空间
>
> 使用 `${}`方式使用属性

1. 引入 `druid` jar包
2. **jdbc.properties**

```properties
username=root
password=123456
driverClassName=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/test?characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowMultiQueries=true
```

3. **xml** 配置

> 名称空间：
>
> `xmlns:context="http://www.springframework.org/schema/context"`
>
> `http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

  <bean id="source" class="com.alibaba.druid.pool.DruidDataSource">
    <property name="username" value="${username}"></property>
    <property name="password" value="${password}"></property>
    <property name="driverClassName" value="${driverClassName}"></property>
    <property name="url" value="${url}"></property>
  </bean>
  <!-- properties 配置文件 -->  
  <context:property-placeholder location="classpath:resource/jdbc.properties" />
</beans>
```

4. 测试

```java
@Test
public void t15() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean9.xml");

    DruidDataSource source = context.getBean("source", DruidDataSource.class);
    System.out.println(source);
}
```

结果

```shell
{
	CreateTime:"2021-05-21 14:36:43",
	ActiveCount:0,
	PoolingCount:0,
	CreateCount:0,
	DestroyCount:0,
	CloseCount:0,
	ConnectCount:0,
	Connections:[
	]
}
```

### 4.4 Bean 管理：**注解方式**

#### 4.4.1 基础 概念

> **注解**：
>
> 1. 注解是代码特殊标记，格式：**@注解名称(属性名=属性值...)**
> 2. 可作用在**类、方法、属性上**
> 3. 使用注解，简化 xml 配置

> Spring 针对 **Bean 的创建** 提供的注解：
>
> 1. `Component`
> 2. `Service`
> 3. `Controller`
> 4. `Repository`

#### 4.4.2 基于注解方式实现对象创建

##### 4.4.2.1 引入 aop 依赖包

`spring-aop-5.2.6.RELEASE.jar`

##### 4.4.2.2 配置 Spring 扫描

**xml 配置，需要 context 名称空间**

- `base-package`为扫描的包路径
- 扫描多个包
  - 包名用 `,` 隔开
  - 或者使用**父路径**包名

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

  <context:component-scan base-package="org.learn.spring5"></context:component-scan>

</beans>
```

##### 4.4.2.3 类

- `(value = "employeeService")`可省略，默认 `value = "employeeService"` ,也就是类名的小驼峰写法作为`value`

```java
@Service(value = "employeeService")
public class EmployeeService {
    public void add() {
        System.out.println("add...");
    }
}
```

##### 4.4.2.4 测试

```java
@Test
public void t16() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean-1.xml");

    EmployeeService service = context.getBean("employeeService", EmployeeService.class);
    service.add();
}
```

#### 4.4.3 扫描配置扩展

> **use-default-filters** ：表示不使用默认扫描，只扫描`context:exclude-filter`配置内容

- 只扫描 Controller

```xml
<context:component-scan base-package="org.learn.spring5" use-default-filters="false">
    <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
</context:component-scan>
```

- 除 Controller 都扫描

```xml
  <context:component-scan base-package="org.learn.spring5">
    <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
  </context:component-scan>
```

#### 4.4.4 注解方式：属性注入

> 1. @AutoWired：根据属性类型自动装配
> 2. @Qualifier：根据属性名称进行注入
> 3. @Resource：可根据类型，也可根据名称
> 4. @Value：注入普通类型属性

##### 4.4.4.1 @AutoWired

> service 中 注入 dao

> **@AutoWired**：根据类型，自动去找 **dao** 的实现类，并且实现类只有一个

1. xml 配置扫描

```xml
<context:component-scan base-package="org.learn.spring5"></context:component-scan>
```

2. dao、daoImpl

```java
public interface EmployeeDao {
    void add();
}
```

```java
@Repository
public class EmployeeDaoImpl implements EmployeeDao {
    @Override
    public void add() {
        System.out.println("dao add ...");
    }
}
```

3. service

```java
@Service(value = "employeeService")
public class EmployeeService {

    @Autowired
    private EmployeeDao employeeDao;

    public void add() {
        employeeDao.add();
        System.out.println("service add...");
    }
}
```

4. test

```java
@Test
public void t16() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean-1.xml");

    EmployeeService service = context.getBean("employeeService", EmployeeService.class);
    service.add();
}
}
```

##### 4.4.4.2 @Qualifier

> 需要配合 `@AutoWired`使用
>
> 使用场景：若 **dao** 有多个实现类，需要**指定 某一个实现类的名称**

- service

```java
@Service(value = "employeeService")
public class EmployeeService {

    @Autowired
    @Qualifier(value = "emloyeeDaoImpl")
    private EmployeeDao employeeDao;

    public void add() {
        employeeDao.add();
        System.out.println("service add...");
    }
}
```

- EmployeeDaoImpl

> `@Repository(value = "employeeDaoImpl")`
>
> 参数省略，默认 `employeeDaoImpl`

```java
@Repository
public class EmployeeDaoImpl implements EmployeeDao {
    @Override
    public void add() {
        System.out.println("dao add ...");
    }
}
```

##### 4.4.4.3 @Resource

> **@Resource：可根据类型，也可根据名称**

- service

```java
@Resource
private EmployeeDao employeeDao;
```

```java
@Resource(name = "employeeDaoImpl")
private EmployeeDao employeeDao;
```

##### 4.4.4.4 @Value

> 基本类型属性

```java
@Service(value = "employeeService")
public class EmployeeService {

    @Value("ABC")
    private String name;

    @Resource(name = "employeeDaoImpl")
    private EmployeeDao employeeDao;

    public void add() {
        employeeDao.add();
        System.out.println("service add...");
        System.out.println(name);
    }
}
```

### 4.5 **完全注解开发**

#### 4.5.1 **创建配置类**，替代xml配置文件

> 实际开发中 Spring Boot 

> @Configuration
> @ComponentScan(basePackages = {"org.learn"})

```java
@Configuration
@ComponentScan(basePackages = {"org.learn"})
public class SpringConfig {
}
```

#### 4.5.2 测试类

> 类： **AnnotationConfigApplicationContext**

```java
@Test
public void t17() {
    // 配置类
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);

    EmployeeService service = context.getBean("employeeService", EmployeeService.class);
    service.add();
}
```

```shell
dao add ...
service add...
ABC
```

## 5.AOP

### 5.1 介绍

> AOP (Aspect Oriented Programming)：
>
> 1. 面向切面编程，隔离各部分业务逻辑，降低耦合度，提高可重用性，提高开发效率

例 图：

![image-20210530095033722](https://raw.githubusercontent.com/zuahua/image/master/common-note/20210530095042.png)

### 5.2 AOP 底层原理

#### 5.2.1 AOP底层使用**动态代理**

##### 5.2.1.1 有接口情况，JDK动态代理

![image-20210530100237242](https://raw.githubusercontent.com/zuahua/image/master/common-note/20210530100237.png)

##### 5.2.1.2 无接口情况，CGLIB动态代理

> 创建子类的代理对象，增强类的方法

![image-20210530100553640](https://raw.githubusercontent.com/zuahua/image/master/common-note/20210530100553.png)

### 5.3 AOP JDK动态代理

#### 5.3.1 Proxy类说明

> `java.lang.reflect.Proxy`

> 1. JDK动态代理，使用`Proxy`类里面的方法创建代理对象
> 2. 使用`newProxyInstance()`方法

```java
public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
```

> 参数说明：
>
> 1. `ClassLoader` 类加载器
> 2. `Class<?>[] interfaces`实现增强方法所在接口的类，支持多个
> 3. `InvocationHandler` 实现**InvocationHandler**类，写增强方法

#### 5.3.2 实现例

> 实现 `UserDao` 的代理类
>
> 1. UserDao 接口
> 2. UserDaoImpl 接口实现类
> 3. JDKProxy 代理类

1. `UserDao`

```java
public interface UserDao {
    int add(int a, int b);

    String update(String id);
}
```

2. `UserDaoImpl`

```java
public class UserDaoImpl implements UserDao {
    @Override
    public int add(int a, int b) {
        System.out.println("add 方法被调用");
        return a + b;
    }

    @Override
    public String update(String id) {
        System.out.println("update 方法调用");
        return id;
    }
}
```

3. `JDKProxy`

```java
public class JDKProxy {
    public static void main(String[] args) {
        // 代理接口类
        Class[] interfaces = {UserDao.class};

        // handler
        UserDaoImpl userDao = new UserDaoImpl();
        UserDaoProxyHandler handler = new UserDaoProxyHandler(userDao);

        // 代理类
        UserDao userDaoProxy = (UserDao) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), interfaces, handler);
        userDaoProxy.add(1, 2);
        userDaoProxy.update("1");

    }
}

class UserDaoProxyHandler implements InvocationHandler {

    /**
     * 通过构造方法将代理目标类传进来
     */
    private Object obj;

    public UserDaoProxyHandler(Object obj) {
        this.obj = obj;
    }

    /**
     * 增强代理方法
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 方法之前
        System.out.println(method.getName() + " 方法之前执行..." + " : 传递的参数：" + Arrays.toString(args));

        // 调用被代理的方法
        Object invoke = method.invoke(obj, args);

        // 方法之后
        System.out.println(method.getName() + " 方法之后执行...");

        return invoke;
    }
}
```

### 5.4 AOP术语

#### 5.4.1 连接点

> 类里面哪些方法可以被增强，这些方法称为**连接点**

#### 5.4.2 切入点

> 实际被真正增强的方法，称为切入点

#### 5.4.3 通知（增强）

> 1. 实际增强的逻辑部分称为通知（增强）
> 2. 通知有多种类型：
>     1. 前置通知
>     2. 后置通知
>     3. 环绕通知
>     4. 异常通知
>     5. 最终通知  finally

#### 5.4.4 切面

> 是动作
>
> 把通知应用到切入点的过程。

### 5.5 AOP操作（准备）

1. Spring 框架一般使用**AspectJ**实现AOP操作
    1. **AspectJ**不是Spring组成部分，是独立的AOP框架，一般把AspectJ和Spring框架一起使用，进行AOP操作。
2. 基于AspectJ实现AOP操作
    1. xml配置实现
    2. 注解
3. 依赖引入

> 1. spring的aspect依赖 `spring-aspects-5.2.6.RELEASE.jar`

> 外部与 aop 相关依赖：
>
> 1. `com.springsource.net.sf.cglib-2.2.0.jar`
> 2. `com.springsource.org.aopalliance-1.0.0.jar`
> 3. `com.springsource.org.aspectj.weaver-1.6.8.RELEASE.jar`

4. 切入点表达式

（1）作用：对哪个类的哪个方法进行增强；

（2）语法结构：

```java
execution([权限修饰符][返回类型][类全路径][方法名称]([参数类型]))

举例1：对org.learn.dao.BookDao的add方法进行增强
execution(* org.learn.dao.BookDao.add(..))  
  
举例2：对org.learn.dao.BookDao的所有方法进行增强
execution(* org.learn.dao.BookDao.*(..))    
  
举例3：对org.learn.dao包下所有类所有方法进行增强
execution(* org.learn.dao.*.*(..))    
```

### 5.6 AOP 操作（AspectJ **注解方式**）

#### 5.6.1 被增强的类与增强类

1. `UserDao`

```java
public class UserDao {
    public void add() {
        System.out.println("add...");
    }
}
```

2. `UserDaoProxy`

```java
public class UserDaoProxy {
    public void before() {
        System.out.println("before..");
    }
}
```

#### 5.6.2 配置

流程：

> 1. 配置文件，开启注解扫描
> 2. 注解创建 `UserDao` `UserDaoProxy`对象
> 3. 在增强类添加 @Aspect 注解
> 4. spring 配置文件开启生成代理对象

##### 5.6.2.1 spring 配置文件

> 添加`context` `aop` 名称空间
>
> 开启注解扫描
>
> 开启 aspect

**bean-aop1.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--注解扫描-->
    <context:component-scan base-package="org.learn.spring5.aopanno"></context:component-scan>
    <!--开启Aspect生成代理对象-->
    <aop:aspectj-autoproxy></aop:aspectj-autoproxy>

</beans>
```

##### 5.6.2.2 UserDao

```java
@Component
public class UserDao {
    public void add() {
        System.out.println("add...");
    }
}
```

##### 5.6.2.3 增强类 UserDaoProxy，几种通知

> 几种通知：
>
> 1. @Before
> 2. @After
> 3. @AfterReturning
> 4. @AfterThrowing
> 5. @Around

```java
@Component
@Aspect // 生成代理对象
public class UserDaoProxy {
    // Before注解表示前置通知
    @Before(value = "execution(* org.learn.spring5.aopanno.UserDao.add(..))")
    public void before() {
        System.out.println("before..");
    }

    // 最终通知，有无异常都会执行
    @After(value = "execution(* org.learn.spring5.aopanno.UserDao.add(..))")
    public void after() {
        System.out.println("after..");
    }

    // 后置通知（返回通知）
    @AfterReturning(value = "execution(* org.learn.spring5.aopanno.UserDao.add(..))")
    public void afterReturning() {
        System.out.println("afterReturning..");
    }

    // 异常通知
    @AfterThrowing(value = "execution(* org.learn.spring5.aopanno.UserDao.add(..))")
    public void afterThrowing() {
        System.out.println("afterThrowing..");
    }

    // 环绕通知
    @Around(value = "execution(* org.learn.spring5.aopanno.UserDao.add(..))")
    public void Around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("环绕之前");
        // 被增强方法执行
        proceedingJoinPoint.proceed();
        System.out.println("环绕之后");
    }
}
```

#### 5.6.3 测试

> 通知执行顺序异同

```java
@Test
public void t1() {
  ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean-aop1.xml");
  UserDao userDao = context.getBean("userDao", UserDao.class);
  userDao.add();
}
```

```shell
环绕之前
before..
add...
环绕之后
after..
afterReturning..
```

add方法中添加除0代码再测试：

```shell
环绕之前
before..
after..
afterThrowing..

java.lang.ArithmeticException: / by zero
```

#### 5.6.4 补充：相同切入点的抽取

> `@Pointcut(value = "execution(* org.learn.spring5.aopanno.UserDao.add(..))")`

UserDaoProxy代理类

```java
@Component
@Aspect // 生成代理对象
public class UserDaoProxy {
	// 抽取公共切入点
    @Pointcut(value = "execution(* org.learn.spring5.aopanno.UserDao.add(..))")
    public void pointcut() {

    }
	// 使用公共切入点
    // Before注解表示前置通知
    @Before(value = "pointcut()")
    public void before() {
        System.out.println("before..");
    }
}
```

#### 5.6.5 补充：多个增强类对同一个类进行增强，设置优先级

> `@Order(int value)` value越小，优先级越高

1. 新建增强类**PersonUserProxy**

```java
@Component
@Aspect
@Order(0)
public class PersonUserProxy {
    @Before(value = "execution(* org.learn.spring5.aopanno.UserDao.add(..))")
    public void before() {
        System.out.println("Person before");
    }
}
```

2. 增强类 **UserDaoProxy** 前添加注解

```java
@Order(2)
```

3. 测试结果(测试代码接上文)

```shell
Person before
环绕之前
before..
add...
环绕之后
after..
afterReturning..
```

#### 5.6.6 补充：完全注解方式（不使用配置文件）

##### 5.6.6.1 配置类

> @Configuration
> @ComponentScan(basePackages = {"org.learn.spring5.aopanno"})
> @EnableAspectJAutoProxy(proxyTargetClass = true)

```java
package org.learn.spring5.aopanno;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author zh
 * @createTime 2021/6/6 10:10
 */
@Configuration
@ComponentScan(basePackages = {"org.learn.spring5.aopanno"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SpringConfig {
}
```

##### 5.6.6.2 测试

> AnnotationConfigApplicationContext

```java
@Test
public void t2() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
    UserDao userDao = context.getBean("userDao", UserDao.class);
    userDao.add();
}
```

### 5.7 AOP操作（AspectJ配置文件方式）

#### 5.7.1 增强类与被增强类

```java
public class BookProxy {
    public void before() {
        System.out.println("before");
    }
}
```

```java
public class Book {
    public void buy() {
        System.out.println("book buy");
    }
}
```

#### 5.7.2 配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--创建对象-->
    <bean id="book" class="org.learn.spring5.aopxml.Book"></bean>
    <bean id="bookProxy" class="org.learn.spring5.aopxml.BookProxy"></bean>

    <!--aop配置-->
    <aop:config>
        <!--切入点-->
        <aop:pointcut id="p1" expression="execution(* org.learn.spring5.aopxml.Book.buy(..))"/>
        <!--配置切面-->
        <aop:aspect ref="bookProxy">
            <!--增强作用到的具体的方法上-->
            <aop:before method="before" pointcut-ref="p1"/>
        </aop:aspect>
    </aop:config>

</beans>
```

#### 5.7.3 测试

```java
@Test
public void t3() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean-aop-xml.xml");
    Book book = context.getBean("book", Book.class);
    book.buy();
}
```

```shell
before
book buy
```

## 6.JDBCTemplate

### 6.1 JDBCTemplate概念

> 什么是JDBCTemplate?
>
> Spring框架对JDBC进行封装，使用JDBCTemplate方便实现对数据库操作。

### 6.2 准备工作

#### 6.2.1 引入相关**jar**包

前文基础上，还需引入

> mysql-connector-java-5.1.10.jar
>
> spring下：
>
> spring-jdbc-5.2.6.RELEASE.jar
>
> spring-orm-5.2.6.RELEASE.jar              (整合MyBatis等数据持久层框架)
>
> spring-tx-5.2.6.RELEASE.jar                  (事务相关)

#### 6.2.2 配置数据库连接池

使用外部文件方式 (4.3.7节)

1. **jdbc.properties**

```properties
jdbc.username=root
password=123456
driverClassName=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/test?characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowMultiQueries=true
```

2. **bean.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">
    <!-- 数据库连接池 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="username" value="${username}"></property>
        <property name="password" value="${password}"></property>
        <property name="driverClassName" value="${driverClassName}"></property>
        <property name="url" value="${url}"></property>
    </bean>
    <!-- properties 配置文件 -->
    <context:property-placeholder location="classpath:resource/jdbc.properties"/>

</beans>
```

#### 6.2.3 配置 JDBCTemplate对象

**bean.xml**

```xml
<!--JDBCTemplate对象-->
<bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
    <!--注入dataSource-->
    <property name="dataSource" ref="dataSource"></property>
</bean>
```

#### 6.2.4 创建service类、dao类，在dao中注入jdbcTemplate对象

1. 配置组件扫描

```xml
<!--组件扫描-->
<context:component-scan base-package="org.learn"></context:component-scan>
```

2. service类

```java
@Service
public class BookService {
    @Autowired
    private BookDao bookDao;
}
```

3. dao类

```java
@Repository
public interface BookDao {
}
```

```java
@Repository
public class BookDaoImpl implements BookDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
}
```

### 6.3 JDBCTemplate操作

#### 6.3.1 book entity

```java
/**
 * @author zh
 * @createTime 2021/6/9 10:37
 */
public class Book {
    private Integer id;
    private String name;
    private Integer status;

    public Book() {
    }

    public Book(Integer id, String name, Integer status) {
        this.id = id;
        this.name = name;
        this.status = status;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
```

#### 6.3.2 dao 使用 jdbcTemplate （增删改）

```java
@Repository
public interface BookDao {
    int add(Book book);

    int update(Book book);

    int delete(int id);
}
```

```java
@Repository
public class BookDaoImpl implements BookDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int add(Book book) {
        String sql = "insert into book(id,name,status) values(?,?,?)";
        Object[] args = {book.getId(), book.getName(), book.getStatus()};
        return jdbcTemplate.update(sql, args);
    }

    @Override
    public int update(Book book) {
        String sql = "update book set name=?,status=? where id=?";
        Object[] args = {book.getName(), book.getStatus(), book.getId()};
        return jdbcTemplate.update(sql, args);
    }

    @Override
    public int delete(int id) {
        String sql = "delete from book where id=?";
        return jdbcTemplate.update(sql, id);
    }
}
```

#### 6.3.3 service

```java
@Service
public class BookService {
    @Autowired
    private BookDao bookDao;

    public int addBook(Book book) {
        return bookDao.add(book);
    }

    /**
     * 修改操作
     * @param book book
     */
    public int updateBook(Book book) {
        return bookDao.update(book);
    }

    /**
     * 删除
     * @param id id
     * @return number
     */
    public int deleteBook(int id) {
        return bookDao.delete(id);
    }
}
```

#### 6.3.4 test

```java
@Test
public void t1() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean.xml");
    BookService bookService = context.getBean("bookService", BookService.class);
    Book book = new Book(1, "核心思想", 1);
    //        int res = bookService.addBook(book);
    //        System.out.println(res);
    //        int i = bookService.updateBook(book);
    int i = bookService.deleteBook(1);
    System.out.println(i);
}
```

#### 6.3.5 出现异常 注意

异常： **Access denied for user 'hua'@'localhost' (using password: YES)**

> 数据库 配置文件
>
> 因为在系统中也有个username属性，这时系统变量覆盖了Properties中的值，这时取得username的值为系统的用户名Administrator（主机名），密码为properties中的password去查询数据库，此时用户名名和密码并不匹配就会报错。

使用前缀：

```properties
jdbc.username=root
```

### 6.4 查询操作

#### 6.4.1 查询单独的值

> jdbcTemplate.queryForObject(sql, Integer.class);

1. bookDao 添加方法

```java
int count();
```

2. BookDaoImpl 实现方法

```java
@Override
public int count() {
    String sql = "select count(*) from book";
    return jdbcTemplate.queryForObject(sql, Integer.class);
}
```

3. BookService 调用

```java
public int count() {
    return bookDao.count();
}
```

4. 测试

```java
@Test
public void t2() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean.xml");
    BookService bookService = context.getBean("bookService", BookService.class);

    int count = bookService.count();
    System.out.println(count);
}
```

#### 6.4.2 查询单个对象

> jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Book>(Book.class), id);

1. BookDao

```java
Book queryById(int id);
```

2. BookDaoImpl

```java
@Override
public Book queryById(int id) {
    String sql = "select * from book where id=?";
    return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Book>(Book.class), id);
}
```

3. BookService

```java
public Book queryOneBook(int id) {
    return bookDao.queryById(id);
}
```

4. 测试

```java
@Test
public void t3() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean.xml");
    BookService bookService = context.getBean("bookService", BookService.class);

    Book book = bookService.queryOneBook(1);
    System.out.println(book);
}
```

#### 6.4.3 查询 返回集合

1. BookDao

```java
List<Book> queryAll();
```

2. BookDaoImpl

```java
@Override
public List<Book> queryAll() {
    String sql = "select * from book";
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Book>(Book.class));
}
```

3. BookService

```java
public List<Book> queryAll() {
    return bookDao.queryAll();
}
```

4. 测试

```java
@Test
public void t4() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean.xml");
    BookService bookService = context.getBean("bookService", BookService.class);

    List<Book> books = bookService.queryAll();
    System.out.println(books);
}
```

```shell
[Book{id=1, name='java', status=0}, Book{id=2, name='js', status=1}]
```

### 6.5 批量插入、修改、删除

> public int[] batchUpdate(String sql, List<Object[]> batchArgs)
>
> 参数：
>
> sql：sql语句
>
> batchArgs：sql中占位符的对象数组集合

> 以批量添加为例
>
> 批量修改、删除：sql、batchArgs两参数改变

1. BookDao

```java
void batchAdd(List<Object[]> books);
```

2. BookDaoImpl

```java
@Override
public void batchAdd(List<Object[]> books) {
    String sql = "insert into book(name,status) values(?,?)";
    int[] ints = jdbcTemplate.batchUpdate(sql, books);
    System.out.println(Arrays.toString(ints));
}
```

3. BookService

```java
public void batchAdd() {
    Object[] o1 = {"Java", 0};
    Object[] o2 = {"Mysql", 1};
    Object[] o3 = {"Spring", 0};
    List<Object[]> objects = new ArrayList<>();
    objects.add(o1);
    objects.add(o2);
    objects.add(o3);
    bookDao.batchAdd(objects);
}
```

4. 测试

```java
@Test
public void t5() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean.xml");
    BookService bookService = context.getBean("bookService", BookService.class);

    bookService.batchAdd();
}
```

```shell
[1, 1, 1]
```

## 7. Spring 事务

### 7.1 事务概念

1. 概念

> 对数据库的一系列操作，要么都成功，要么都失败

2. 事务的四个特征(**ACID**)

> （1）原子性
>
> （2）一致性
>
> （3）隔离性
>
> （4）持久性

### 7.2 事务操作准备

> 以转账为例

#### 7.2.1 数据表

```sql
CREATE TABLE t_account(
	id INT PRIMARY KEY auto_increment,
	username VARCHAR(20),
	money INT
); 

INSERT INTO t_account(username, money) VALUES('A', 1000);
INSERT INTO t_account(username, money) VALUES('B', 1000);
```

#### 7.2.2 entity dao service

##### 7.2.2.1 AccountEntity

```java
public class AccountEntity {
    private Integer id;
    private String username;
    private Integer money;

    public AccountEntity() {
    }

    public AccountEntity(Integer id, String username, Integer money) {
        this.id = id;
        this.username = username;
        this.money = money;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", money=" + money +
                '}';
    }
}
```

##### 7.2.2.2 AccountDao

```java
public interface AccountDao {
    void addMoney();

    void reduceMoney();
}
```

##### 7.2.2.3 AccountDaoImpl

```java
@Repository
public class AccountDaoImpl implements AccountDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * A加100
     */
    @Override
    public void addMoney() {
        String sql = "update t_account set money=money+? where username=?";
        jdbcTemplate.update(sql, 100, "A");
    }

    /**
     * B减100
     */
    @Override
    public void reduceMoney() {
        String sql = "update t_account set money=money-? where username=?";
        jdbcTemplate.update(sql, 100, "B");
    }
}
```

##### 7.2.2.4 AccountService

```java
@Service
public class AccountService {
    @Autowired
    private AccountDao accountDao;

    public void account() {
        // 模拟转账
        // B - 100
        accountDao.reduceMoney();
        // A + 100
        accountDao.addMoney();
    }
}
```

#### 7.2.3 配置文件

##### 7.2.3.1 bean.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">
    <!--组件扫描-->
    <context:component-scan base-package="org.learn.spring5"></context:component-scan>

    <!-- 数据库连接池 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="username" value="${jdbc.username}"></property>
        <property name="password" value="${password}"></property>
        <property name="driverClassName" value="${driverClassName}"></property>
        <property name="url" value="${url}"></property>
    </bean>
    <!-- properties 配置文件 -->
    <context:property-placeholder location="classpath:resource/jdbc.properties"/>

    <!--JDBCTemplate对象-->
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <!--注入dataSource-->
        <property name="dataSource" ref="dataSource"></property>
    </bean>
</beans>
```

##### 7.2.3.2 jdbc.properties

```properties
jdbc.username=root
password=hua123456
driverClassName=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/test?characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowMultiQueries=true
```

#### 7.2.4 测试

```java
@Test
public void t1() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean.xml");
    AccountService accountService = context.getBean("accountService", AccountService.class);
    accountService.account();
}
```

### 7.3 Spring 事务管理介绍

1. **事务添加到**JavaEE三层结构的**Service层**
2. Spring 进行事务管理操作两种方式
    1. 编程式事务管理（try-catch 方式，有异常再回滚操作）
    2. **申明式事务管理(使用)**
3. 申明式事务管理
    1. **注解方式**
    2. xml配置
4. Spring 申明式事务管理，**底层使用AOP**
5. Spring 事务管理API

提供了`PlatformTransactionManager`的实现类

![image-20210615095759730](https://raw.githubusercontent.com/zuahua/image/master/common-note/20210615095759.png)

### 7.4 申明式**注解方式**事务管理操作

#### 7.4.1 在Spring配置文件配置事务管理器（对应类）

```xml
<!--创建事务管理器-->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <!--注入数据源-->
    <property name="dataSource" ref="dataSource"></property>
</bean>
```

#### 7.4.2 开启事务注解

> 1. **tx** 名称空间
> 2. 开启事务注解

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">
    <!--组件扫描-->
    <context:component-scan base-package="org.learn.spring5"></context:component-scan>

    <!-- 数据库连接池 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="username" value="${jdbc.username}"></property>
        <property name="password" value="${password}"></property>
        <property name="driverClassName" value="${driverClassName}"></property>
        <property name="url" value="${url}"></property>
    </bean>
    <!-- properties 配置文件 -->
    <context:property-placeholder location="classpath:resource/jdbc.properties"/>

    <!--JDBCTemplate对象-->
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <!--注入dataSource-->
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!--创建事务管理器-->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <!--注入数据源-->
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!--开启事务注解-->
    <tx:annotation-driven transaction-manager="transactionManager"></tx:annotation-driven>
</beans>
```

#### 7.4.3 在Service类或方法上添加注解

> @Transactional

```java
@Service
@Transactional
public class AccountService {
    @Autowired
    private AccountDao accountDao;

    public void account() {
        // 模拟转账
        // B - 100
        accountDao.reduceMoney();

        // 模拟异常
        int a = 10 / 0;

        // A + 100
        accountDao.addMoney();
    }
}
```

#### 7.4.4 测试

```java
@Test
public void t1() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean.xml");
    AccountService accountService = context.getBean("accountService", AccountService.class);
    accountService.account();
}
```

结果：出现异常，自动回滚

### 7.5 事务注解参数

#### 7.5.1 参数

> `@Transactional(参数1...)`

![image-20210616094825267](https://raw.githubusercontent.com/zuahua/image/master/common-note/20210616094832.png)

#### 7.5.2 propagation：事务传播行为

多事务方法之间进行调用，事务如何管理；

![image-20210616095635832](https://raw.githubusercontent.com/zuahua/image/master/common-note/20210616095636.png)

![image-20210616095726202](https://raw.githubusercontent.com/zuahua/image/master/common-note/20210616095726.png)

#### 7.5.3 ioslation：事务隔离级别

并发问题：

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210421170018.png)

隔离级别

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210421171244.png)

#### 7.5.4 timeout：超时时间

> 事务需要在一定的时间提交，不提交需要回滚
>
> 默认值-1.单位：s

#### 7.5.5 readOnly：只读

> 默认值：false，操作可查询可修改
>
> true：表示只能查询

#### 7.5.6 rollbackFor：回滚

> 设置出现哪些异常才回滚

#### 7.5.7 noRollbackFor：不回滚

> 设置出现哪些异常不进行回滚

### 7.6 申明式 **xml 配置方式**的事务管理操作

#### 7.6.1 Spring 配置文件进行配置

1. 配置事务管理器

2. 配置通知

3. 配置切入点、切面

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">
    <!--组件扫描-->
    <context:component-scan base-package="org.learn.spring5"></context:component-scan>

    <!-- 数据库连接池 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="username" value="${jdbc.username}"></property>
        <property name="password" value="${password}"></property>
        <property name="driverClassName" value="${driverClassName}"></property>
        <property name="url" value="${url}"></property>
    </bean>
    <!-- properties 配置文件 -->
    <context:property-placeholder location="classpath:resource/jdbc.properties"/>

    <!--JDBCTemplate对象-->
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <!--注入dataSource-->
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!-- 1.创建事务管理器 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <!--注入数据源-->
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!-- 2.配置通知 -->
    <tx:advice id="txAdvice">
        <!-- 配置事务参数 -->
        <tx:attributes>
            <tx:method name="account" propagation="REQUIRED"/>
        </tx:attributes>
    </tx:advice>

    <!-- 3.配置切入点和切面 -->
    <aop:config>
        <!-- 配置切入点 -->
        <aop:pointcut id="pt" expression="execution(* org.learn.spring5.service.AccountService.*(..))"/>
        <!-- 配置切面 -->
        <aop:advisor advice-ref="txAdvice" pointcut-ref="pt"/>
    </aop:config>
</beans>
```

#### 7.6.2 Service层修改

```java
@Service
public class AccountService {
    @Autowired
    private AccountDao accountDao;

    public void account() {
        // 模拟转账
        // B - 100
        accountDao.reduceMoney();

        // 模拟异常
        int a = 10 / 0;

        // A + 100
        accountDao.addMoney();
    }
}
```

#### 7.6.3 测试

```java
@Test
public void t2() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("resource/bean2.xml");
    AccountService accountService = context.getBean("accountService", AccountService.class);
    accountService.account();
}
```

结果：除0异常，事务自动回滚

### 7.7 完全注解申明式事务管理

#### 7.7.1 创建配置类代替xml配置文件

> @PropertySource(value = "classpath:resource/jdbc.properties") // 读取properties文件 数据库属性
>
> @Bean

**TxConfig.java**

```java
@Configuration // 配置类
@ComponentScan(basePackages = "org.learn.spring5") // 组件扫描
@EnableTransactionManagement // 开启事务
@PropertySource(value = "classpath:resource/jdbc.properties") // 数据库属性
public class TxConfig {
    @Value(value = "${jdbc.username}")
    private String username;

    @Value(value = "${password}")
    private String password;

    @Value(value = "${driverClassName}")
    private String driverClassName;

    @Value(value = "${url}")
    private String url;

    /**
     * DruidDataSource 数据库
     *
     * @return
     */
    @Bean
    public DruidDataSource getDruidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        return dataSource;
    }

    /**
     * JdbcTemplate
     *
     * @param dataSource
     * @return
     */
    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) { // IOC容器中找dataSource（根据类型）
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        // 注入 dataSource
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    /**
     * DataSourceTransactionManager 事务管理器
     *
     * @return
     */
    @Bean
    public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
```

#### 7.7.2 测试

```java
@Test
public void t3() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TxConfig.class);
    AccountService accountService = context.getBean("accountService", AccountService.class);
    accountService.account();
}
```

### 7.8 Spring5新功能 整合日志框架 **Log4j2** 

#### 7.8.1 介绍

> 1. Spring5 框架自带了通用的日志封装
>
> 2. Spring5 已经移除了Log4jConfigListener，官方建议使用**Log4j2**

#### 7.8.2 引入 jar 包

> log4j-api-2.14.1.jar
>
> log4j-core-2.14.1.jar
>
> log4j-slf4j-impl-2.14.1.jar
>
> slf4j-api-1.7.30.jar

#### 7.8.3 创建 **log4j2.xml** 配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- 日志级别以及优先级排序：OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL -->
<!-- Configuration后面的status用于设置log4j2自身内部的信息输出 -->
<Configuration status="INFO">
    <Appenders>
        <!-- 默认打印到控制台 -->
        <Console name="Console" target="SYSTEM_OUT">
            <!-- 打印格式 -->
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
    </Appenders>
    <!-- 定义Logger，引入Appender -->
    <!-- root：用于指定项目的根日志，如果没有单独指定Logger，则会使用root作为默认的日志输出 -->
    <Loggers>
        <!-- 打印日志级别 -->
        <Root level="info">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

#### 7.8.4 测试

```java
package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zh
 * @createTime 2021/6/17 10:14
 */
public class LogTest {
    private static final Logger log = LoggerFactory.getLogger(LogTest.class);

    public static void main(String[] args) {
        log.info("Test 1");
        log.warn("Test 2");
    }
}
```

```shell
2021-06-17 10:16:33.179 [main] INFO  test.LogTest - Test 1
2021-06-17 10:16:33.183 [main] WARN  test.LogTest - Test 2
```

### 7.9 Spring5 新功能 @Nullable

> 可用在方法、属性、参数上，表示可为空

```java
@Nullable
public int getId();
```

```java
@Nullable
public String str;
```

```java
public String getName(@Nullable int id){}
```

### Spring5 新功能 核心容器支持函数式风格 GenericApplicationContext

> 用户注册对象到Spring容器
>
> `GenericApplicationContext`

```java
@Test
public void t4() {
    // 1 创建 GenericAppplicationContext 对象
    GenericApplicationContext context = new GenericApplicationContext();
    // 2 注册对象
    context.refresh();
    context.registerBean("logTest1", LogTest.class, () -> new LogTest());
    // 3 获取注册对象
    LogTest logTest1 = context.getBean("logTest1", LogTest.class);
    System.out.println(logTest1);
}
```



















