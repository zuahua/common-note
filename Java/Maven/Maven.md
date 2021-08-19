## Maven 配置与使用

### settings.xml

阿里云maven目录

### IDEA 配置

Maven 设置文件设置为自己的

### 下载失败

[geotools工具包下载maven、maven下载失败解决方案汇总](https://blog.csdn.net/qq_37306786/article/details/113933062)

需要过滤不能下载的 id 

<mirrorOf>

```xml
<mirror>  
    <id>nexus-aliyun</id>  
    <mirrorOf>*,!osgeo,!GeoSolutions,!osgeo-snapshot,!alfresco,!maven2-repository.dev.java.net,!boundless</mirrorOf>
    <name>Nexus aliyun</name>  
    <url>https://maven.aliyun.com/nexus/content/groups/public</url>  
</mirror>
```

实在不行切回原始下载一次

