## Tomcat 9

### 相关问题

#### 控制台日志乱码

- conf/logging.properties
- 改为 GBK

```properties
java.util.logging.ConsoleHandler.encoding = GBK
```

#### 指定的服务未安装

- 在 bin目录下运行

```powershell
service.bat install
```

### 配置调优

#### 端口

***server.xml***

```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />
```

#### 并发配置

- `maxThreads` 
  - tomcat接收客户端请求的最大线程数，也就是同时处理任务的个数，它的默认大小为`200`；一般来说，在高并发的I/O密集型应用中，这个值设置为`1000`左右比较合理

```xml
修改conf/server.xml文件
<Executor name="tomcatThreadPool" namePrefix="catalina-exec-"
        maxThreads="500" minSpareThreads="20" maxSpareThreads="50" maxIdleTime="60000"/>

<Connector executor="tomcatThreadPool"
               port="9090" protocol="HTTP/1.1"
               URIEncoding="UTF-8"
               connectionTimeout="30000"
               enableLookups="false"
               disableUploadTimeout="false"
               connectionUploadTimeout="150000"
               acceptCount="300"
               keepAliveTimeout="120000"
               maxKeepAliveRequests="1"
               compression="on"
               compressionMinSize="2048"
               compressableMimeType="text/html,text/xml,text/javascript,text/css,text/plain,image/gif,image/jpg,image/png" 
               redirectPort="8443" />
```

### JVM 参数配置

修改bin/ catalina.bat文件
添加

```powershell
set CATALINA_OPTS="
-server 
-Xms6000M 
-Xmx6000M 
-Xss512k 
-XX:NewSize=2250M 
-XX:MaxNewSize=2250M 
-XX:PermSize=128M
-XX:MaxPermSize=256M  
-XX:+AggressiveOpts 
-XX:+UseBiasedLocking 
-XX:+DisableExplicitGC 
-XX:+UseParNewGC 
-XX:+UseConcMarkSweepGC 
-XX:MaxTenuringThreshold=31 
-XX:+CMSParallelRemarkEnabled 
-XX:+UseCMSCompactAtFullCollection 
-XX:LargePageSizeInBytes=128m 
-XX:+UseFastAccessorMethods 
-XX:+UseCMSInitiatingOccupancyOnly
-Duser.timezone=Asia/Shanghai 
-Djava.awt.headless=true"
```

```java
set CATALINA_OPTS=-server -Xms6000M -Xmx6000M -Xss512k -XX:NewSize=2250M -XX:MaxNewSize=2250M -XX:PermSize=128M9 -XX:MaxPermSize=256M -XX:+AggressiveOpts -XX:+UseBiasedLocking -XX:+DisableExplicitGC -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -Duser.timezone=Asia/Shanghai -Djava.awt.headless=true
```

不要将-XX：MaxTenuringThreshold设置为大于15的值   可选0-15

```java
set JAVA_OPTS=-Xms128m -Xmx350m 
```

























