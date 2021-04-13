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

