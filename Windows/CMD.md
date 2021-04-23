# CMD

## 常用指令

```shell
# 进入d盘某目录
cd /d D:\JavaProject\landtaxserver
```

### 服务/进程

```shell
# 查看所有服务
net start
# 查看所有端口占用情况
netstat -aon
# 查看指定端口占用
netstat -aon|findstr "8080"
# 查看对应进程任务
tasklist|findstr "进程ID"
# 根据进程号杀进程
taskkill /f /pid 进程号
```

