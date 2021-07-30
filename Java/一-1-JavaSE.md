# JavaSE 学习笔记

[TOC]

## 教程

https://how2j.cn/

## 1. 基础

### String

#### str.lastIndexOf()

**注意**：

- 从末尾开始遍历，找到的索引是***从头开始计算的索引***
- `\\`转义后是一个字符

```java
System.out.println("D:\\data\\xxxxxxxxxxxxxxxxx.doc".lastIndexOf("\\"));
```

```powershell
7
```



## 2. 十三章 IO流

### RandomAccessFile 随机存取文件流

1. 字节流
2. RandomAccessFile直接继承于java.lang.Object，实现了DataInput和DataOutput接口
3. RandomAccessFile既可以作为输入流，又可以作为输出流
4. 作为输出流时，源文件存在，对源文件内容进行覆盖（默认从开头位置开始覆盖）
5. 可实现从文件内部某个位置插入数据
6. 可实现断点续传(记录指针的方法)

测试代码：

- 复制文件
- 写入
- 文件内部指定位置插入

```java
/**
 * @author zh
 * @createTime 2021/3/6 10:17
 */
public class RandomAccessFileTest {
    @Test
    public void copyFile() {
        RandomAccessFile raf = null;
        RandomAccessFile raf1 = null;
        try {
            raf = new RandomAccessFile("1052202P6-0.jpg", "r");
            raf1 = new RandomAccessFile("1052202P6-011.jpg", "rw");

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = raf.read(buffer)) != -1) {
                raf1.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                raf1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写文件测试
     * 在源文件内容指定位置覆盖
     */
    @Test
    public void write() {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile("hello-RandomAccessFile.txt", "rw");

            byte[] b = "abc".getBytes();
            raf.write(b);

            byte[] b2 = "jkjk".getBytes();
            // 移动指针
            raf.seek(4);
            raf.write(b2);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 使用RandomAccessFile实现在源文件内容中插入新的内容，而不是覆盖
     * 从第5个位置之后插入 "mnbvcx"
     */
    @Test
    public void writeInsertBytes() {
        RandomAccessFile raf = null;
        try {
            File srcFile = new File("hello-RandomAccessFile.txt");
            raf = new RandomAccessFile(srcFile, "rw");
            // 跳过前5个字符
            raf.seek(5);

            byte[] buffer = new byte[10];
            int len = 0;
            // 每次读取到的byte存入StringBuilder
            StringBuilder sb = new StringBuilder((int) srcFile.length());
            System.out.println(srcFile.length());
            while ((len = raf.read(buffer)) != -1) {
                // 字节数组->字符串 new String(字节数组)
                sb.append(new String(buffer), 0, len);
            }
            // 读完之后，指针跳回5
            raf.seek(5);
            // 写入
            String s = "mnbvcx";
            raf.write(s.getBytes());
            // 写入读取到的
            raf.write(sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

### NIO

- non-blocking IO 非阻塞IO
- NIO支持面向缓冲区的（IO是面向流的）、基于通道的IO操作
- NIO将以更加高效的方式进行文件的读写操作
- Java API提供两种NIO: 标准输入输出NIO、网络编程NIO

#### NIO 引入的Path

- 原有File的缺陷：大多数方法出错返回失败，不返回异常信息
- Path相当于File的升级版，实际引用的资源可以不存在
- Path path = Paths.get("src.html");

### IO操作第三方包

apache的commons-io包

[阿里云Maven搜索](https://maven.aliyun.com/mvn/search)

|      group-Id      | artifact-Id |
| :----------------: | :---------: |
| org.apache.commons | commons-io  |

## 3. 十四章 网络编程

**实现网络主机的相互通信**

**两个问题**
> 1. 如何准确定位网络上一台或多台主机；定位主机上特定的应用。
> 2. 找到主机后如何进行高效地传输

- 通信双方地址
  - IP
  - 端口号
- 网络通信协议（规则）
  - OSI参口模型（理想化、未推广）
  - TCP/IP（国际标准）

| OSI参考模型 |  TCP/IP参考模型   |     TCP/IP各层协议     |
| :---------: | :---------------: | :--------------------: |
|   应用层    |      应用层       | HTTP、FTP、Telnet、DNS |
|   表示层    |        ^^         |           ^^           |
|   会话层    |        ^^         |           ^^           |
|   传输层    |      传输层       |        TCP、UDP        |
|   网络层    |      网络层       |     IP、ICMP、ARP      |
| 数据链路层  | 物理层+数据链路层 |          Link          |
|   物理层    |         -         |           -            |

### 通信要素一：IP和端口号

#### IP

1. IP: 唯一标识Internet上的计算机（通信实体）
2. 在Java中使用**InetAddress**类代表IP 
3. IP分类：IPv4和IPv6；万维网和局域网
4. 域名：域名 -DNS-> IP ; www.baidu.com
5. 本地地址 127.0.0.1 对应localhost
6. 如何实例化，两个方法: 
    1. getByName(String host) 
    2. getLocalHost()
7. 对象常用方法
    1. getHostName()
    2. getHostAddress()

##### InetAddress测试

```java
public static void main(String[] args) {
    InetAddress inet1 = null;
    try {
      // 通过IP
      inet1 = InetAddress.getByName("192.168.175.94");
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    // /192.168.175.94
    System.out.println(inet1);

    InetAddress inet2 = null;
    try {
      // 通过域名
      inet2 = InetAddress.getByName("www.baidu.com");
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    // www.baidu.com/112.80.248.75
    System.out.println(inet2);

    InetAddress inet3 = null;
    try {
      // 获取本机IP
      inet3 = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    // DESKTOP-S89QC04/172.17.***.1
    System.out.println(inet3);

    System.out.println(inet2.getHostName());
    System.out.println(inet2.getHostAddress());
}
```

#### 端口号

> **端口号**标识正在计算机运行的进程
>
> - 不同的进程有不同的端口号
> - 范围：16位，0~65535
> - 端口分类：
>   - 公认端口：0~1023。被预先定义的服务占用（如：HTTP占用端口80，FTP占用端口21，Telnet占用端口23）
>   - 注册端口：1024~49151。分配给用户进程或用户程序。（如：Tomcat 8080，MySQL 3306，Oracle 1521）
>   - 动态/私有端口：49152~65535。
>
> 端口号与IP地址的组合形成**网络套接字**，**==Socket==**。

### 通信要素二：网络协议

> **网络通信协议**
>
> - 网络通信的约定。对*速率、传输代码、代码结构、传输传输控制步骤、出错控制等制定标准*
>
> 问题：**网络通信太复杂**
>
> - 网络通信设计内容多：源地址和目标地址、加密解密、压缩解压缩、差错控制、流量控制、路由控制
>
> **通信协议分层思想**
>
> - 同层间可通信，上层可调用下层，而不与再下一层发生关系

#### TCP/IP协议簇

- 传输控制协议TCP(Transmision Control Protocol)
- 用户数据报协议UDP(User Datagram Protocol)
- IP(Internet Protocol)协议是网络层的主要协议，支持网间互联的数据通信
- TCP/IP协议模型，四层结构：==**物理链路层、IP层、传输层、应用层**==

>TCP、UDP特点与区别
>
>...

- TCP连接的三次握手
- TCP释放连接的四次挥手

### TCP网络编程

#### 例子1

1. **例子1：客户端发送数据给服务端，服务端将数据显示在控制台上。**

- **注意**：将读取到的**字节**存入**==ByteArrayOutputStream==**防止乱码

```java
/**
 * @author zh
 * @createTime 2021/3/7 13:39
 */
public class TCPTest1 {
    /**
     * 客户端，发送数据
     */
    @Test
    public void client() {
        Socket socket = null;
        OutputStream os = null;
        try {
            // 1.创建Socket对象，指明服务端IP和端口号
            InetAddress inet = InetAddress.getByName("127.0.0.1");
            socket = new Socket(inet, 8999);
            // 2.获取输出流，用于输出数据
            os = socket.getOutputStream();
            // 3.输出数据
            os.write("你好，我是客户端".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 4.关闭
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 服务端，接收数据
     */
    @Test
    public void server() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            // 1.创建ServerSocket，指明自己的端口号。
            serverSocket = new ServerSocket(8999);
            // 2.接收客户端发送过来的Socket
            socket = serverSocket.accept();
            // 3.获取输入流，并读取答应到控制台
            is = socket.getInputStream();

            // 将读取到的字节存入ByteArrayOutputStream防止乱码
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[5];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            // 打印到控制台
            System.out.println(baos.toString());
            // 获取客户端IP信息并打印
            System.out.println("接收到来自 " +
                    socket.getInetAddress().getHostAddress() + " 的信息");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 4.关闭
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

```powershell
你好，我是客户端
接收到来自 127.0.0.1 的信息
```

#### 例子2

2. 例子2：客户端发送文件给服务端，服务端接收文件保存在本地

```java
/**
 * @author zh
 * @createTime 2021/3/7 14:24
 */
public class TCPTest2 {
    /**
     * 客户端
     */
    @Test
    public void client() {
        Socket socket = null;
        OutputStream os = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            InetAddress inet = InetAddress.getByName("127.0.0.1");
            socket = new Socket(inet, 9090);

            os = socket.getOutputStream();

            // 读取要发送的文件
            fis = new FileInputStream("1052202P6-0.jpg");
            // BufferedInputStream 速度快
            bis = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                // 写入输出流
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 服务端
     */
    @Test
    public void server() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            serverSocket = new ServerSocket(9090);
            socket = serverSocket.accept();

            is = socket.getInputStream();
            fos = new FileOutputStream("1052202P6-0-save.jpg");
            bos = new BufferedOutputStream(fos);

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

#### 例子3

3. 例子3：客户端发送文件给服务端，服务端接收文件保存在本地，并发送给客户端成功的反馈

- **注意**：由于服务端再向客户端发送结果状态，服务端接收文件的**_while_**循环必须要客户端输出文件完成时，明确结束状态。否则导致客户端、服务端都==死循环==。
  - 客户端使用如下代码结束输出流

```java
socket.shutdownOutput()
```

```java
/**
 * @author zh
 * @createTime 2021/3/7 15:00
 */
public class TCPTest3 {
    /**
     * 客户端
     */
    @Test
    public void client() {
        Socket socket = null;
        OutputStream os = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        InputStream is = null;
        try {
            // 1
            InetAddress inet = InetAddress.getByName("127.0.0.1");
            socket = new Socket(inet, 9090);
            // 2
            os = socket.getOutputStream();
            // 3
            // 读取要发送的文件
            fis = new FileInputStream("1052202P6-0.jpg");
            // BufferedInputStream 速度快
            bis = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                // 写入输出流
                os.write(buffer, 0, len);
            }

            // 明确结束输出 //
            socket.shutdownOutput();

            // 4.这里接收服务端的反馈信息
            is = socket.getInputStream();
            byte[] buffer1 = new byte[5];
            int len1 = 0;
            // 防止乱码的输出流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len1 = is.read(buffer1)) != -1) {
                baos.write(buffer1, 0, len1);
            }
            System.out.println(baos.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 服务端
     */
    @Test
    public void server() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        OutputStream os = null;
        try {
            // 1
            serverSocket = new ServerSocket(9090);
            socket = serverSocket.accept();
            // 2
            is = socket.getInputStream();
            // 3
            fos = new FileOutputStream("1052202P6-0-save2.jpg");
            bos = new BufferedOutputStream(fos);

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            // 4.完成后给客户端发送信息
            os = socket.getOutputStream();
            os.write("蟹蟹，已经收到了!!!".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

```powershell
蟹蟹，已经收到了!!!
```

### UDP网络编程

```java
/**
 * @author zh
 * @createTime 2021/3/7 15:20
 * <p>
 * UDP网络编程测试
 */
public class UDPTest {
    /**
     * 发送端
     */
    @Test
    public void sender() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();

            String s = "这是发送的数据";
            byte[] data = s.getBytes();
            InetAddress host = InetAddress.getLocalHost();
            DatagramPacket packet = new DatagramPacket(data, 0, data.length, host, 9090);

            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    /**
     * 接收端
     */
    @Test
    public void receiver() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(9090);

            // 简单测试，直接定义了长度，实际可能长度过长或不够
            byte[] data = new byte[100];
            DatagramPacket packet = new DatagramPacket(data, 0, data.length);
            socket.receive(packet);

            String s = new String(packet.getData(), packet.getOffset(), packet.getLength());
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
```

### URL编程

- URL类
- URL(Uniform Resource Locator)：统一资源定位符，它表示Internet上某一资源的地址。
- URL是一种具体的URI。
- URL组成部分：
  - <传输协议>://<主机名>:<端口号>/<文件名>#片段名?参数列表
  - <主机名>:<端口号> 也可是 **域名**
  - 例：
  - <http://localhost:8080/text/text.html?username=zh>
  - <https://github.com/zuahua/JavaSENote/blob/main/JavaSE.md#%E5%8D%81%E5%9B%9B%E7%AB%A0-%E7%BD%91%E7%BB%9C%E7%BC%96%E7%A8%8B>

#### URL实例方法测试

```java
@Test
public void t1() {
    try {
        URL url = new URL("http://localhost:8080/text/text.html?username=zh");

        // 方法测试
        // 1.获取协议
        System.out.println(url.getProtocol());
        // 2.获取主机名
        System.out.println(url.getHost());
        // 3.获取端口号
        System.out.println(url.getPort());
        // 4.获取URL的文件路径
        System.out.println(url.getPath());
        // 5.获取文件名
        System.out.println(url.getFile());
        // 6.获取查询名
        System.out.println(url.getQuery());
    } catch (MalformedURLException e) {
        e.printStackTrace();
    }
}
```

```powershell
http
localhost
8080
/text/text.html
/text/text.html?username=zh
username=zh
```

#### URL练习

- 练习：从网络获取资源下载到本地
- 资源地址：http://localhost:8080/text/text.html
- **注意**：main方法中的默认地址是项目路径；_internet\\text.html_表示在项目下的_internet_包下的_text.html_文件。

```java
/**
 * @author zh
 * @createTime 2021/3/7 16:06
 */
public class URLTest1 {
    public static void main(String[] args) {
        HttpURLConnection httpUrlConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL("http://localhost:8080/text/text.html");

            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.connect();

            is = httpUrlConnection.getInputStream();

            fos = new FileOutputStream("internet\\text.html");
            byte[] buffer = new byte[10];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            httpUrlConnection.disconnect();
        }
    }
}
```

## 4. Java 执行 CMD 命令

```java
// cmd 命令 String
Process proc = Runtime.getRuntime().exec(cmd);
```

