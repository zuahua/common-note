## GeoServer 笔记

### 基础介绍

> GeoServer是用于共享地理空间数据的开源服务器。
>
> 专为实现互操作性而设计，它使用开放标准发布来自任何主要空间数据源的数据。

官网：<http://geoserver.org/>

下载：<http://geoserver.org/release/stable/> [^下载说明]

[^下载说明]:包括二进制、Javadoc等

### 快速开始

#### 使用 Web 管理界面

文档：<https://docs.geoserver.org/latest/en/user/gettingstarted/web-admin-quickstart/index.html>

1. 下载二进制文件，解压后 **运行** ***bin/startup.bat***。
2. 服务启动在本地 `http://localhost:8080/geoserver/web/`
3. 登录，默认用户名 ***admin***，密码 ***geoserver***
4. ***Layer Preview***
   1. 点击 ***OpenLayers*** 预览
   2. 点击 表格头Title ，可按字母排序

#### 发布 shapefile

1. 将 ***shapefile*** 复制到 ***<GEOSERVER_DATA_DIR>/data_dir*** （默认）目录下
2. 创建 **工作区**
   1. (左侧导航栏)->数据->工作区；添加新的工作区。填入 Name 和 资源 URI
      1. Name: nyc (自定义)
      2. 命名空间 URI: http://localhost:8080/geoserver/nyc_roads
3. 创建 **数据存储**
   1. 数据存储->添加新的数据存储；点击 ***Directory of spatial files (shapefiles)***
   2. 页面填写表单
      - 工作区：nyc (下拉选择)
      - 数据源名称：NYC Roads (自定义)
      - 说明：Roads in New York City (自定义)
      - 连接参数下，shapefiles 文件目录，选择 nyc_roads (官方文档是选择文件Location，可能版本不一致)
   3. 保存
4. 发布 **图层**
   1. 图层页面，新建图层，下拉选择 nyc: NCY Roads，点击 **发布**
   2. 发布页面表单
      - 标题: Subset of NYC Roads
      - 摘要: Shapefile of NYC Roads.
      - 通过单击“***根据数据计算***”然后单击“***Compute from native bounds***”链接来生成图层的边界框。
      - 保存
5. 预览

#### 发布 PostGIS

需要安装 PostgreSQL

1. 



