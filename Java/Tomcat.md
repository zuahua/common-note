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



























