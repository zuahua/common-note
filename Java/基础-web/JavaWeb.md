# JavaWeb

[TOC]

## Servlet

### 介绍

1. ***Servlet*** 是***Java EE*** 接口。
2. ***Servelet*** 是 ***Java Web*** 三大组件之一。（Servlet 程序、Filter 过滤器、Listener 监听器）
3. Servlet 是运行在服务器上的小型程序，接受请求并响应。

###  Servlet

#### 实现例

```java
public class HelloServlet implements Servlet {
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        System.out.println("into service method");
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
```

#### web.xml  配置 Servlet

```xml
<!-- 配置 servlet 程序 -->
<servlet>
    <!-- 别名 -->
    <servlet-name>HelloServlet</servlet-name>
    <!-- 类全名 -->
    <servlet-class>org.changhua.servlet.HelloServlet</servlet-class>
</servlet>

<!-- 配置 servlet 程序访问地址 -->
<servlet-mapping>
    <!-- 给哪个 servlet 程序配置地址 -->
    <servlet-name>HelloServlet</servlet-name>
    <!-- 访问地址：路径相对于：http://ip:port/工程路径 -->
    <url-pattern>/hello</url-pattern>
</servlet-mapping>
```

#### Servlet 生命周期

1. 执行Servlet 构造方法
2. 执行 init 初始化 方法

（1、2在第一次访问时，执行一次）

3. 执行 service 方法

（3、每次进入都会执行）

4. 执行 destroy 方法

（销毁时执行）

### HttpServlet

#### 例

```java
public class HelloHttpServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("----> GET");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("-----> POST");
    }
}
```

web.xml配置

```xml
<servlet>
    <servlet-name>helloHttpServlet</servlet-name>
    <servlet-class>org.changhua.servlet.HelloHttpServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>helloHttpServlet</servlet-name>
    <url-pattern>/hellohttpservlet</url-pattern>
</servlet-mapping>
```

#### IDEA 新建 Servlet 类

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210513135757.png)



### Servlet 继承关系

<img src="https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210513141404.png" style="zoom:100%;" />



### ServletConfig

作用：

1. 获取 servlet 别名
2. 获取初始化参数 init-param
3. 获取 ServletContext 对象

```java
public class HelloServlet implements Servlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        System.out.println("2 init method invoked");
        // 获取 servlet 别名
        String servletName = servletConfig.getServletName();
        System.out.println(servletName);

        // 获取初始化参数 init-param
        String user = servletConfig.getInitParameter("name");
        System.out.println(user);

        // 获取 ServletContext 对象
        ServletContext servletContext = servletConfig.getServletContext();
        System.out.println(servletContext);
    }


}
```

### ServletContext

#### 介绍

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210513150055.png)



#### 作用

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210513153808.png)



#### 例

```java
public class Servlet5 extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();
        // 获取上下文参数 chang
        String username = context.getInitParameter("username");
        System.out.println(username);

        // 获取工程路径  /javaweblearn
        String contextPath = context.getContextPath();
        System.out.println(contextPath);

        // 获取资源绝对路径 D:\JavaProject\allStudyProject\javaweblearn\out\artifacts\javaweblearn_war_exploded\css
        // 参数是拼接到绝对路径后
        String realPath = context.getRealPath("/css");
        System.out.println(realPath);
    }
}
```

web.xml配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

  <context-param>
    <param-name>username</param-name>
    <param-value>chang</param-value>
  </context-param>

  <servlet>
    <servlet-name>Servlet5</servlet-name>
    <servlet-class>org.changhua.servlet.Servlet5</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>Servlet5</servlet-name>
    <url-pattern>/hello5</url-pattern>
  </servlet-mapping>
</web-app>
```

## HTTP协议、请求、响应

### HTTP 协议

#### 请求的 HTTP 协议格式

##### HTTP GET 请求

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210513162837.png)



##### HTTP POST 请求

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210513162737.png)





![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210513162844.png)



##### 常用请求头

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210513163022.png)

##### 响应的 HTTP 协议介绍

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514091751.png)



##### 常见状态码

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514092104.png)

##### MIME 类型说明

MDN <https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types>

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514092908.png)



### HttpServletRequest

#### 作用

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514095909.png)

#### HttpServletRequest 常用方法

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514100105.png)

#### 乱码问题

- ***setCharacterEncoding("UTF-8")***

#### 请求转发

> 请求转发是指，服务器收到请求后，从一个资源跳转到另一个资源的操作叫做请求转发。

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514110231.png)

- 特点

  - 浏览器地址栏没有变化
  - 他们是一次请求
  - 他们共享Request域中的数据
  - 可以转发到WEB-INF目录下的资源
  - 不可转发工程以外资源

  

例：

- Servlet6

```java
public class Servlet6 extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 模拟请求转发功能 //

        String username = request.getParameter("username");
        System.out.println(username + "开始 business1");
        request.setAttribute("business1", true);
        System.out.println(username + "完成 business1");

        // 转发到 servlet7
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/hello7");
        requestDispatcher.forward(request, response);
    }
}
```

- Servlet7

```java
public class Servlet7 extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        boolean business1 = (boolean) request.getAttribute("business1");
        if(!business1) {
            System.out.println(username + " 验证失败，business 未完成");
        }

        System.out.println(username + "开始 business2");
    }
}
```

- web.xml

```xml
<servlet>
    <servlet-name>Servlet6</servlet-name>
    <servlet-class>org.changhua.servlet.Servlet6</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>Servlet6</servlet-name>
    <url-pattern>/hello6</url-pattern>
</servlet-mapping>

<servlet>
    <servlet-name>Servlet7</servlet-name>
    <servlet-class>org.changhua.servlet.Servlet7</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>Servlet7</servlet-name>
    <url-pattern>/hello7</url-pattern>
</servlet-mapping>
```

#### html base 标签

- 作用：页面中 <a>标签 跳转时，相对路径的参考路径

```html
<head>
    <base href="http://127.0.0.1/a/b">
</head>
```

#### Java Web 中 斜杠 的不同意义

1. 在 web 中 `/`  是一种绝对路径
2. `/` 斜杠被浏览器解析，得到的地址是：`http://ip:port/`
   1. `<a href='/'>斜杆</a>`
3. `/` 斜杠被服务器解析，得到的地址是：`http://ip:port/工程名`
   1. web.xml 配置中：`<url-pattern>/servlet1</url-pattern>`
   2. servletContext.getRealPath("/")
   3. request.getRequestDispatcher("/")
4. 特殊情况：`response.sendRedirect("/")`;把斜杠给浏览器解析，得到 `http://ip:port/`

### HttpServletResponse

#### 作用

- 每次请求过来，Tomcat服务器都会创建一个Response对象传递给 Servlet 程序去使用；`HttpServletResponse` 表示所有响应信息。
- 如果需要设置返回给客户端的信息，都可以通过HttpServletResponse对象来设置。

#### 两个输出流说明

- response.getWriter()
- response.getOutputStream()

```java
public class Servlet8 extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "text/html; charset=utf-8");
        // response.setContentType("text/html; charset=utf-8");		
        PrintWriter writer = response.getWriter();

        writer.write("中文显示");
    }
}
```

#### 请求重定向

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514145947.png)



## 监听器 Listener

### 介绍

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514150420.png)

### ServletContextListener

- 监听 ServletContext 对象的创建和销毁
- ServletContext 对象，在 Web 工程启动时创建，在Web 工程停止时销毁；



- ServletContextListener.java

```java
public interface ServletContextListener extends EventListener {
    /*
    * ServletContext 创建之后马上执行
    */
    default void contextInitialized(ServletContextEvent sce) {
    }

    /*
    * ServletContext 销毁之后执行
    **/
    default void contextDestroyed(ServletContextEvent sce) {
    }
}
```

使用步骤：

1. 编写类去实现 ServletContextListener
2. 实现两个回调方法
3. web.xml 配置

例：

```java
public class ServletContextListenerTest implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("ServletContext 被创建了");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("ServletContext 被销毁了");
    }
}
```

web.xml 配置

```xml
<listener>
    <listener-class>org.changhua.listener.ServletContextListenerTest</listener-class>
</listener>
```

## Cookie、Session

### Cookie

#### 介绍

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514152348.png)



#### 操作 Cookie

##### 后端 修改Cookie 值

方案一：

1. new 一个同名 Cookie("名", "值")
2. response.addCookie()

方案二：

1. 找到此Cookie
2. 调用 setValue() 方法
3. response.addCookie()

##### setMaxAge()

- setMaxAge(0) 立即删除
- setMaxAge(-1) 默认存活时间

##### Path 路径

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514155550.png)



### Session

#### 介绍

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514160303.png)

#### 创建、获取Session

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514160640.png)

#### 过期时间

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514161601.png)

##### 工程文件 web.xml 配置：整个工程生效，过期时间

```xml
<session-config>
    <session-timeout>30</session-timeout>
</session-config>
```



##### 设置某个 Session 过期时间

- request.getSession().setMaxInactiveInterval();



##### 设置马上失效

- invalidate()

##### Session 与 客户端的关联

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514162908.png)



## Filter

### 介绍

![](https://raw.githubusercontent.com/zuahua/image/master/commom-note/20210514164400.png)

### 使用例

- 实现功能：要求 admin 目录下的所有文件登陆后可访问

#### 实现 Filter ，以及配置

```java
public class FilterTest implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Object isLogin = request.getSession().getAttribute("isLogin");
        // 没有登陆转发到登陆页面
        if (isLogin == null) {
            request.getRequestDispatcher("/index.jsp").forward(request, servletResponse);
        } else {
            // 已登录 通过过滤
            filterChain.doFilter(servletRequest, servletResponse);
        }


    }

    @Override
    public void destroy() {

    }
}
```

- web.xml

```xml
<!--配置 Filter  -->
<filter>
    <filter-name>FilterTest</filter-name>
    <filter-class>org.changhua.filter.FilterTest</filter-class>
</filter>
<filter-mapping>
    <filter-name>FilterTest</filter-name>
    <!-- 配置工程目录/admin/ 下的所有文件 -->
    <url-pattern>/admin/*</url-pattern>
</filter-mapping>
```

#### 首页 jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
  $END$
  <form method="post" action="/javaweblearn/hello9">
    <input value="登录" type="submit">
  </form>

  <a href="/javaweblearn/admin/2.jpg">jpg</a>
  <a href="/javaweblearn/admin/a.html">html</a>
  </body>
</html>
```

























