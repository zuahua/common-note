# Mysql

### 数据类型

| 名称         | 类型           | 说明                                                         |
| :----------- | :------------- | :----------------------------------------------------------- |
| INT          | 整型           | 4字节整数类型，范围约+/-21亿                                 |
| BIGINT       | 长整型         | 8字节整数类型，范围约+/-922亿亿                              |
| REAL         | 浮点型         | 4字节浮点数，范围约+/-1038                                   |
| DOUBLE       | 浮点型         | 8字节浮点数，范围约+/-10308                                  |
| DECIMAL(M,N) | 高精度小数     | 由用户指定精度的小数，例如，DECIMAL(20,10)表示一共20位，其中小数10位，通常用于财务计算 |
| CHAR(N)      | 定长字符串     | 存储指定长度的字符串，例如，CHAR(100)总是存储100个字符的字符串 |
| VARCHAR(N)   | 变长字符串     | 存储可变长度的字符串，例如，VARCHAR(100)可以存储0~100个字符的字符串 |
| BOOLEAN      | 布尔类型       | 存储True或者False                                            |
| DATE         | 日期类型       | 存储日期，例如，2018-06-22                                   |
| TIME         | 时间类型       | 存储时间，例如，12:20:59                                     |
| DATETIME     | 日期和时间类型 | 存储日期+时间，例如，2018-06-22 12:20:59                     |

日期时间类型 占用空间 日期格式 最小值 最大值 零值表示
DATETIME 8 bytes YYYY-MM-DD HH:MM:SS 1000-01-01 00:00:00 9999-12-31 23:59:59 0000-00-00 00:00:00
TIMESTAMP 4 bytes YYYYMMDDHHMMSS 19700101080001 2038 年的某个时刻 00000000000000
DATE 4 bytes YYYY-MM-DD 1000-01-01 9999-12-31 0000-00-00
TIME 3 bytes HH:MM:SS -838:59:59 838:59:59 00:00:00
YEAR 1 bytes YYYY 1901 2155 0000

### 关系模型

#### 主键

1. 自增整数类型：数据库会在插入数据时自动为每一条记录分配一个自增整数，这样我们就完全不用担心主键重复，也不用自己预先生成主键；
2. 全局唯一GUID类型：使用一种全局唯一的字符串作为主键，类似`8f55d96b-8acc-4636-8cb8-76bf8abc2f57`。GUID算法通过网卡MAC地址、时间戳和随机数保证任意计算机在任意时间生成的字符串都是不同的，大部分编程语言都内置了GUID算法，可以自己预算出主键

> 如果使用INT自增类型，那么当一张表的记录数超过2147483647（约21亿）时，会达到上限而出错。使用BIGINT自增类型则可以最多约922亿亿条记录。

> 应该使用BIGINT自增或者GUID类型



### 联接 内连 外连 全连

> INNER JOIN只返回同时存在于两张表的行数据
>
> RIGHT OUTER JOIN返回右表都存在的行
>
> LEFT OUTER JOIN则返回左表都存在的行
>
> FULL OUTER JOIN，它会把两张表的所有记录全部选择出来，并且，自动把对方不存在的列填充为NULL

### 建表相关

```sql
-- 用户表
drop table if exists t_user; 
create table t_user(
	id bigint unsigned auto_increment,
	login_name varchar(32) comment '唯一登录名',
	name varchar(32) comment '姓名',
	password varchar(64) comment '密码',
	email varchar(64) comment '邮箱',
	mobile varchar(32) comment '手机号',
	is_deleted tinyint unsigned default 0 comment '是否删除',
	gmt_create datetime default now() comment '创建时间',
	gmt_modified datetime default now() comment '更新时间',
	constraint pk_id primary key(id),
	constraint uk_login_name unique key(login_name)
);
insert into t_user(login_name,name,password,email,mobile) values('zh','zh','123456','innochangfox@foxmail.com','18784212478');

-- 角色表
drop table if exists t_role;
create table t_role(
	id bigint unsigned auto_increment,
	name varchar(32) comment '角色名称',
	remark varchar(64) comment '名称备注',
	is_deleted tinyint unsigned default 0 comment '是否删除',
	gmt_create datetime default now() comment '创建时间',
	gmt_modified datetime default now() comment '更新时间',
	constraint pk_id primary key(id)
);
insert into t_role(name,remark) values('superAdmin','超级管理员');
insert into t_role(name,remark) values('admin','管理员');
insert into t_role(name,remark) values('generalUser','普通注册用户');
insert into t_role(name,remark) values('visitor','游客');


-- 用户角色表
drop table if exists t_user_role;
create table t_user_role(
	id bigint unsigned auto_increment,
	user_id bigint unsigned not null,
	role_id bigint unsigned not null,
	is_deleted tinyint unsigned default 0 comment '是否删除',
	gmt_create datetime default now() comment '创建时间',
	gmt_modified datetime default now() comment '更新时间',
	constraint pk_id primary key(id)
);
insert into t_user_role(user_id,role_id) values(1,1);

-- 权限表
drop table if exists t_authority;
create table t_authority(
	id bigint unsigned auto_increment,
	name varchar(32) not null comment '权限名称',
	remark varchar(64) not null comment '权限备注',
	is_deleted tinyint unsigned default 0 comment '是否删除',
	gmt_create datetime default now() comment '创建时间',
	gmt_modified datetime default now() comment '更新时间',
	constraint pk_id primary key(id)
);
insert into t_authority(name,remark) values('/user/query','查-用户权限');
insert into t_authority(name,remark) values('/user/add','添加-用户权限');
insert into t_authority(name,remark) values('/user/update','修改-用户权限');
insert into t_authority(name,remark) values('/user/delete','删除-用户权限');

-- 角色权限表
drop table if exists t_role_authority;
create table t_role_authority(
	id bigint unsigned auto_increment,
	role_id bigint unsigned not null,
	authority_id bigint unsigned not null,
	is_deleted tinyint unsigned default 0 comment '是否删除',
	gmt_create datetime default now() comment '创建时间',
	gmt_modified datetime default now() comment '更新时间',
	constraint pk_id primary key(id)
);
-- 超级管理员
insert into t_role_authority(role_id,authority_id) values(1,1);
insert into t_role_authority(role_id,authority_id) values(1,2);
insert into t_role_authority(role_id,authority_id) values(1,3);
insert into t_role_authority(role_id,authority_id) values(1,4);
-- 管理员
insert into t_role_authority(role_id,authority_id) values(2,1);
insert into t_role_authority(role_id,authority_id) values(2,2);
insert into t_role_authority(role_id,authority_id) values(2,3);
insert into t_role_authority(role_id,authority_id) values(2,4);
-- 注册用户
insert into t_role_authority(role_id,authority_id) values(3,1);
insert into t_role_authority(role_id,authority_id) values(3,3);
```



```sql
create table user(
	id int PRIMARY KEY auto_increment,
	name VARCHAR(10),
	birth DATE,
	photo BLOB
);
```

修改表字段类型

```sql
alter table 表名 modify 字段 字段类型;
```

### 函数

left()

right()

substring()

### 索引

```sql
# 创建多列索引
ALTER TABLE students ADD INDEX idx_name_score (name, score);
# 创建唯一索引
ALTER TABLE students ADD UNIQUE INDEX uni_name (name);
```

> ## [MySQL添加主键、索引](https://www.cnblogs.com/kenwong/p/4645337.html)
>
> **查看索引** 
>
> SHOW INDEX FROM  数据库表名
>
> 比如：SHOW INDEX FROM order_info;
>
> **添加索引** 
>
> alter table 数据库add index 索引名称(数据库字段名称) 
>
> **主键索引**
>
> ALTER TABLE `table_name` ADD PRIMARY KEY ( `column` ) 
>
> 比如： ALTER TABLE order_info ADD PRIMARY KEY (order_id);
>
> UNIQUE(唯一索引)
>
> ALTER TABLE `table_name` ADD UNIQUE (`column`) 
>
> INDEX(普通索引)
> mysql>ALTER TABLE `table_name` ADD INDEX index_name ( `column` )  
>
> 
> FULLTEXT(全文索引)
> ALTER TABLE `table_name` ADD FULLTEXT ( `column` )
>
>  
>
> 删除索引
>
> ALTER TABLE table_name DROP INDEX index_name
>
> DROP INDEX index_name ON talbe_name
>
> 
> 多列索引
> ALTER TABLE `table_name` ADD INDEX index_name ( `column1`, `column2`, `column3` )
>
> 1.普通索引。
> 这是最基本的索引，它没有任何限制。它有以下几种创建方式：
> （1）创建索引：CREATE INDEX indexName ON tableName(tableColumns(length));如果是CHAR,VARCHAR类型，length可以小于字段实际长度;如果是BLOB 和 TEXT 类型，必须指定length，下同。
> （2）修改表结构：ALTER tableName ADD INDEX [indexName] ON (tableColumns(length)) 
> （3）创建表的时候直接指定：CREATE TABLE tableName ( [...], INDEX [indexName] (tableColumns(length)) ;
>
> 2.唯一索引。
> 它与前面的"普通索引"类似，不同的就是：索引列的值必须唯一，但允许有空值。如果是组合索引，则列值的组合必须唯一。它有以下几种创建方式：
> （1）创建索引：CREATE UNIQUE INDEX indexName ON tableName(tableColumns(length))
> （2）修改表结构：ALTER tableName ADD UNIQUE [indexName] ON (tableColumns(length))
> （3）创建表的时候直接指定：CREATE TABLE tableName ( [...], UNIQUE [indexName] (tableColumns(length));
>
> 3.主键索引
> 它是一种特殊的唯一索引，不允许有空值。一般是在建表的时候同时创建主键索引：CREATE TABLE testIndex(i_testID INT NOT NULL AUTO_INCREMENT,vc_Name VARCHAR(16) NOT NULL,PRIMARY KEY(i_testID)); 当然也可以用ALTER命令。

### log-bin 恢复数据

> 参考：<[一句DELETE引发的加班(Mysql 恢复Delete删除的数据) - 为乐而来 - 博客园 (cnblogs.com)](https://www.cnblogs.com/q149072205/p/11940591.html)>

> 说明：必须事前开启 log-bin 功能
>
> 不适用于 drop 表操作
>
> 适用场景：误 delete、update 全表操作

#### 查看 log-bin 是否打开

```sql
# 查看 log-bin 是否打开
show variables like '%log_bin%';
```

下表 log_bin 已打开

| Variable_name                   | Value                                                      |
| ------------------------------- | ---------------------------------------------------------- |
| log_bin                         | ON                                                         |
| log_bin_basename                | D:\ProgramData\MySQL\MySQL Server 5.7\Data\mysql-bin       |
| log_bin_index                   | D:\ProgramData\MySQL\MySQL Server 5.7\Data\mysql-bin.index |
| log_bin_trust_function_creators | OFF                                                        |
| log_bin_use_v1_row_events       | OFF                                                        |
| sql_log_bin                     | ON                                                         |

#### 开启log-bin

```sql
# 查看数据库数据位置 my.ini在此目录
show variables like '%datadir%';
```

| Variable_name | Value                                       |
| ------------- | ------------------------------------------- |
| datadir       | D:\ProgramData\MySQL\MySQL Server 5.7\Data\ |

```ini
# my.ini 添加，开启 log-bin
# log-bin
log-bin=mysql-bin 
binlog_format = row
```

服务中 重启 mysql

> 操作日志文件例：（这里本地创建一个表，插入数据，再删除所有行操作）
>
> D:\ProgramData\MySQL\MySQL Server 5.7\Data\mysql-bin.000001

#### 通过mysqlbinlog 恢复删除的数据日志记录

```sql
# mysql 安装目录找到 mysqlbinlog.exe
show variables like "%basedir%";
```

| Variable_name | Value                                    |
| ------------- | ---------------------------------------- |
| basedir       | D:\Program Files\MySQL\MySQL Server 5.7\ |

CMD运行

```shell
# 通过mysqlbinlog 恢复删除的数据日志记录
mysqlbinlog --base64-output=decode-rows -v --database=DBName --start-datetime="2019-11-26 18:00:00" --stop-datetime="2019-11-26 18:10:00" D:\MySQL\Data\mysql-bin.000028 > mysqllog.sql
```

```shell
mysqlbinlog 命令的参数说明
--base64-output=decode-rows //数据转换正常的字符，如果不设置这个参数将显示base64的数据
--database=DBName  //数据库名（一个mysql数据库比较多，指定方便恢复）
--start-datetime="2019-11-26 18:00:00"  //恢复起始时间
--stop-datetime="2019-11-26 18:10:00"  //恢复结束时间
D:\MySQL\Data\mysql-bin.000028  //为数据恢复的日志文件
mysqllog.sql    //恢复以后我们需要的文件名
```

```sql
cd D:\Program Files\MySQL\MySQL Server 5.7\bin
# 测试例 路径有空格使用双引号
mysqlbinlog --base64-output=decode-rows -v --database=test --start-datetime="2021-07-06 13:40:00" --stop-datetime="2021-07-06 13:47:00" "D:\ProgramData\MySQL\MySQL Server 5.7\Data\mysql-bin.000001" > mysqllog.sql
```

> 执行后在bin目录下出现 mysqllog.sql

#### delete 转 insert

Linux 下指令（比较方便，这里使用 windows 的 ubuntu 子系统）

> [Win10安装Ubuntu子系统超详细攻略 - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/62658094#:~:text=Win10安装Ubuntu子系统超详细攻略 1 1.首先点击开始，然后点击设置 2 2.选择更新和安全 3 3.在左边点击开发者选项 4,17 17.一共200多M，速度看个人吧，我这里不算太快，大约下载了10min吧 18 19.然后会弹出一个shell窗口，正在安装 19 20.大约5分钟，安装完会提示输入用户名和密码，输入自己的就可以 20 21.然后打开cmd，输入bash即可享受你的linux系统了)

```shell
cat mysqllog.sql | sed -n '/###/p' | sed 's/### //g;s/\/\*.*/,/g;s/DELETE FROM/;INSERT INTO/g;s/WHERE/SELECT/g;' |sed -r 's/(@17.*),/\1;/g' | sed 's/@1=//g'| sed 's/@[1-9]=/,/g' | sed 's/@[1-9][0-9]=/,/g' > mysqllogOK.sql
```

测试 mysqllogOK.sql 结果：

```sql
;INSERT INTO `test`.`user`
SELECT
  1
  ,'zh'
  ,'2020:01:01'
;INSERT INTO `test`.`user`
SELECT
  2
  ,'zhx'
  ,'2020:01:01'
;INSERT INTO `test`.`user`
SELECT
  3
  ,'zhg'
  ,'2020:01:01'
;INSERT INTO `test`.`user`
SELECT
  4
  ,'zha'
  ,'2020:01:01'
```

> 去掉开头的 `;` 去数据库执行 `sql`，大功告成



## Mysql 56 忘记密码

```shell
# cd 到mysql目录，任务管理器中关闭 mysqld.exe再执行
mysqld --skip-grant-tables
# 新开一个CMD
cd C:\Program Files\MySQL\MySQL Server 5.6\bin
# 直接登录
mysql

use mysql;

select user,host,password from user;

update user set password=password('123456') where user='root' and host='localhost';

update user set password=PASSWORD("") where User='root';
```

```shell
update user set Password=PASSWORD('123456') where user='root'; 

FLUSH PRIVILEGES;
```

```shell
alter user 'root'@'localhost' identified by '123456';

mysqld --init-file='C:\Program Files\MySQL\MySQL Server 5.6\mysqlc.txt' --console
```

```shell
grant all privileges on *.* to 'root'@'localhost' identified by '123456';

# mysql错误：The MySQL server is running with the --skip-grant-tables option so it cannot execute this statement解决方法

FLUSH PRIVILEGES;
```

```shell
update mysql.user set authentication_string=password('123456') where user='root' and host ='localhost';

select user,host,password,authentication_string from user;
```













