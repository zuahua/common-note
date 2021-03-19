## Oracle学习笔记

[TOC]

### 基础

#### 安装 Oracle 数据库

- 版本：11g r2 Experss Edition 精简版
- 下载网址：<https://www.oracle.com/database/technologies/xe-prior-releases.html>
- 解压并运行 ***exe*** 安装
- 开始->运行SQL

```sql
conn sys as sysdba
-- 密码为安装时输入的
select 1 from dual;
exit;
```

#### 创建用户和表空间

```sql
sqlplus /nolog;  -- 启动sqlplus不登陆
conn sys/密码 as sysdba; -- 通过超级管理员以dba的身份连接
create tablespace 表空间名 datafile '文件路径\\文件名.dbf' size 空间大小; -- 创建表空间
create user 用户名 identified by 密码 default tablespace 表空间; -- 创建用户并指定表空间
grant dba to 用户; -- 给用户授予dba权限，dba 最大权限
```

![image](../resource/images/1.png)

#### 约束

> 1. 主键约束 （PRIMARY KEY）
> 2. 唯一约束 （UNIQUE）
> 3. 非空约束 （NOT NULL）
> 4. 外键约束 （FOREIGN KEY）
> 5. 检查约束 （CHECK）

##### 主键

- Oracle 中的表可以没有主键
- 相当于 UNIQUE + NOT NULL 约束
- 一个表只能有一个主键
- 主键所在列必须具有索引（主键唯一约束通过索引实现），如果不存在，将会在索引添加的时候自动创建

#### PLSQL 安装

> 官网下载（包括PLSQL及其中文语言包）：<https://www.allroundautomations.com/registered-plsqldev/>
>
> 产品号：4vkjwhfeh3ufnqnmpr9brvcuyujrx3n3le
>
> 序列号：226959
>
> 口令：xs374ca

#### SQL语言介绍

> SQL (Structured Query Language)
>
> 1. DDL (Data Definition Languages)：数据定义语句，create、drop、alter等；
>
> 2. DML (Data Mainpulation Language)：数据操纵语句，insert、delete、select、update等；
>
> 3. DCL (Data Control Language)：数据控制语句，grant、revoke等。

#### 别名

- 列别名

```sql
SELECT DEPTNO 别名 FROM EMP;
SELECT DISTINCT DEPTNO AS 别名 FROM EMP;
```

- 表别名

#### 伪列和虚表

##### 伪列和表达式

说明：查询不存在的列即为伪列，当需要的结果不能直接从表中得到，而是需要计算来展示，则可是使用伪列+表达式实现。

```sql
select 1 from dual;
```

##### 虚表

- ***dual***

```sql
SELECT 999*666 FROM DUAL;
```



### 字符相关

#### 引号

使用两个单引号转义

```sql
''
```

#### 字符串拼接

```sql
SELECT (ENAME || '-' || JOB) name_job FROM EMP;
```



### 字段类型相关

### 关键字相关

#### 空值

```sql
IS NULL
IS NOT NULL
```

### 函数

#### 常用单行函数

##### 字符函数

```sql
concat(x, y)  -- 连接x, y字符串
instr(x, str, start, n)          -- 在x中查找str，可以指定从start开始，也可以查找出现第n次的字符，返回的是索引，从1开始
length(x)    -- 返回x的长度
lower(x)    -- 转换为小写
upper(x)      -- 大写
ltrim(x, trim_str)   -- 把x左边截去trim_str，缺省trim_str截去左边空格
rtrim(x, trim_str)    -- 把x右边截去trim_str，缺省trim_str截去右边空格
trim(x)              -- 去除两边空格
replace(x, old, new ) -- 在x中查找old，并替换为new
substr(x, start, length)    -- 截取字符，start为0或1都表示从第一个字符开始,截取长度为length
```

- 测试

```sql
SELECT CONCAT('hello', 'world') FROM DUAL;
SELECT INSTR('helloworld', 'l', 3, 3) FROM DUAL; -- 9
SELECT ENAME, LENGTH(ENAME) lengthname FROM EMP;

SELECT LTRIM('helloworld', 'hello') FROM DUAL; -- world
SELECT RTRIM('aaa12345abc', 'abc') FROM DUAL; -- aaa12345

SELECT TRIM('    xxx   ') FROM DUAL;

SELECT REPLACE('zxcvbnabcki', 'abc', 'jjj') FROM DUAL; -- zxcvbnjjjki

SELECT SUBSTR('123456789',1 , 5) FROM DUAL; -- 12345
```

##### 数学函数

```sql
abs(x)        x的绝对值

ceil(x)         向上取整

floor(x)       向下取整

mod(x, y)     取模
```

##### 日期函数

```sql
sysdate           当前系统时间
current_date      返回当前系统日期
add_months(d1, n1) 返回在日期d1基础上再加上 n1 个月后的新日期
last_day(d1)       返回日期d1所在月份的最后一天的日期
months_between(d1, d2)    返回日期d1到日期d2之间的月数，d1-d2
next_day(d1, [c1])        返回日期d1，在下周星期几(参数c1)的日期
```

- 测试

```sql
SELECT SYSDATE FROM DUAL; -- 2021-03-16 19:27:40
SELECT CURRENT_DATE FROM DUAL; -- 2021-03-16 19:28:11
-- 加天数
SELECT SYSDATE + 20 FROM DUAL; -- 2021-04-05 19:30:57
-- 加月数
SELECT ADD_MONTHS(TO_DATE('2021-03-16', 'yyyy-MM-dd'), 5) FROM DUAL; -- 2021-08-16 00:00:00
SELECT LAST_DAY(SYSDATE) FROM DUAL; -- 2021-03-31 19:34:54
-- -6.48973902329749103942652329749103942652
SELECT MONTHS_BETWEEN(SYSDATE, TO_DATE('2021-10-01', 'yyyy-MM-dd')) FROM DUAL;

SELECT NEXT_DAY(SYSDATE, '星期二') FROM DUAL; -- 2021-03-23 19:39:27
```

##### 转换函数

```sql
to_char(x, c)     将日期或数据x，按照c的格式转换为char数据类型
to_date(x, c)     
to_number(x)
```

- 测试

```sql
SELECT ENAME, HIREDATE, TO_CHAR(HIREDATE, 'yyyy/MM/dd hh24:mi:ss') FROM EMP;
SELECT ENAME, HIREDATE, TO_CHAR(HIREDATE, 'yyyy "年" MM "月" dd "日" hh24:mi:ss') FROM EMP;

SELECT TO_DATE('2020/11/11 12:12:12', 'yyyy-mm-dd hh24:mi:ss') FROM DUAL;
-- 当前时间 精确到5位毫秒 16-3月 -2021 19:58:55.36000
select to_char(current_timestamp(5), 'DD-MON-YYYY HH24:MI:SSxFF') from dual; 
```

#### 组函数

组函数同时对多条记录进行操作，并返回一个结果

> avg()
>
> sum()
>
> min()
>
> max()
>
> count()
>
> **注意：null 不参与运算**

```sql
-- 统计记录数
SELECT COUNT(*) FROM EMP;

-- 去重统计(不会统计重复DEPTNO)
SELECT COUNT(DISTINCT DEPTNO) FROM EMP;

-- 处理null值后再统计
SELECT COUNT(NVL(COMM, 0)) FROM EMP;
```

#### 分组

- select ... from ... where ... group by
- 执行顺序：from -> where -> group by -> having -> select

```sql
-- 每个部门的平均工资
SELECT DEPTNO, AVG(SAL) FROM EMP GROUP BY DEPTNO;

-- 查询相同工种的平均工资以及相同工种的人数
SELECT JOB, AVG(SAL), count(*) FROM EMP GROUP BY JOB;
```

- having 对组信息过滤

```sql
-- 获取平均工资大于2000的部门信息
SELECT DEPTNO, AVG(SAL) FROM EMP GROUP BY DEPTNO HAVING AVG(SAL) > 2000; 
-- 执行顺序 from -> group by -> having -> select

-- 查询部门人数大于3的部门
SELECT DEPTNO, COUNT(*) FROM EMP GROUP BY DEPTNO HAVING COUNT(*) > 3;
```

- where 对行信息进行过滤

```sql
-- 查询工资大于2000并且所在部门人数大于2的员工
SELECT DEPTNO, COUNT(*) FROM EMP WHERE SAL > 1000 GROUP BY DEPTNO HAVING COUNT(*) >= 2;
```



#### substr()

**注意**：字段索引 start 为 0 或 为 1都表示从第一个位置，共end个

```sql
SUBSTR(字段, start, end)
```

#### null 处理 nvl()

##### nvl()

- nvl(exp1, exp2)
- exp1：表达式
- exp2：exp1为 null 时，替换为 参数2的值

```sql
SELECT ENAME, SAL, COMM, (SAL+NVL(COMM, 0)) income FROM EMP;
```

##### 排序时 null 处理

- `nulls first`    空值排前面
- `nulls last `       空值排后面

```sql
-- 先用COMM降序，NULL值放后面；再用income升序
SELECT ENAME, SAL, COMM, (SAL+NVL(COMM, 0)) income FROM EMP ORDER BY COMM DESC NULLS LAST, income ASC;
```





### 建表相关

#### 主键

DBeaver 中：

给**表**添加 **约束**

| 约束字段 |  名称  | 所有者 |    类型     | 条件 |
| :------: | :----: | :----: | :---------: | :--: |
| 对应字段 | 自定义 |  表名  | PRIMARY KEY | 不填 |

#### 自增

1. 添加序列

| 序列名称 |           值            | 最小值 |       最大值        | 步长 |
| :------: | :---------------------: | :----: | :-----------------: | :--: |
|  自定义  | 1（自动表中自增最大值） |   1    | 9999999999999999999 |  1   |

2. 给表添加触发器
   - 代码方式

```sql
CREATE OR REPLACE
TRIGGER 触发器名称 BEFORE
INSERT
	ON
	添加触发器的表 FOR EACH ROW
	WHEN(NEW.ID IS NULL)
BEGIN
		SELECT
	前面新建的序列名称.Nextval
INTO
	:NEW.ID
FROM
	dual;
END;
```

- DBeaver 手动方式

|    字段    |                             内容                             |
| :--------: | :----------------------------------------------------------: |
|    名称    |                            自定义                            |
|     表     |                             表名                             |
|  对象类型  |                            TABLE                             |
| 触发器类型 |                       BEFORE EACH ROW                        |
|    事件    |                            INSERT                            |
|    字段    |                          （无）不填                          |
|  参照名称  |              REFERENCING NEW AS NEW OLD AS OLD               |
| When 子句  |                        NEW.ID is NULL                        |
|    状态    |                           ENABLED                            |
|    描述    | 触发器名称   <br/>BEFORE INSERT ON 表名    <br/>	for each row |
|  动作类型  |                            PL/SQL                            |

### 查询相关

#### 去重

```sql
select distinct 列名 from 表
```

#### 排序

```sql
-- order by 字句在 select 之后执行
SELECT DEPTNO 别名 FROM EMP ORDER BY 别名 DESC; -- 降序

SELECT DEPTNO 别名 FROM EMP ORDER BY 别名 ASC; -- 升序

SELECT DEPTNO 别名 FROM EMP ORDER BY 别名; -- 默认升序
```

##### 多个字段排序

```sql
SELECT ENAME, SAL, DEPTNO FROM EMP ORDER BY DEPTNO ASC, SAL DESC;
```

#### 查询存在一个表而不在另一个表中的数据记录

[参考](https://blog.csdn.net/weixin_42342702/article/details/92842741)

```sql
1 select A.ID from A left join B on A.ID=B.ID where B.ID is null
```

#### 条件查询

```sql
select 查询内容 from 数据来源 where 条件
```

##### 条件运算

`=、>、<、<=、>=、<>、!=、between and、in、not`

```sql
-- between ... and ... 是 闭区间 []
SELECT ENAME, SAL FROM EMP WHERE SAL BETWEEN 2500 AND 5000 ORDER BY SAL;
-- in里面满足任意一个值就返回
SELECT ENAME, DEPTNO FROM EMP WHERE DEPTNO IN (10, 30);
-- not 取反
SELECT ENAME, DEPTNO FROM EMP WHERE NOT DEPTNO=20;

-- 对空值处理后条件查询
SELECT * FROM EMP WHERE nvl(COMM, 0) <= 0;
```

#### 模糊查询

`_`   `%`  

```sql
-- ENAME 第二个字母为A
SELECT * FROM EMP WHERE ENAME LIKE '_A%';
```

#### 特殊字符处理

- ESCAPE(‘a’)        a只是一个标识，可以为任意字符，其后面第一个字符会被转义

```sql
--- 查询包含%的
SELECT * FROM EMP WHERE ENAME LIKE '%a%%' ESCAPE('a');
-- 查询 包含 'b%'，以 b 作为标识
SELECT * FROM EMP WHERE ENAME LIKE '%bbb%%' ESCAPE('b');
-- 查询既有 % %后又有 _ 的
SELECT * FROM EMP WHERE ENAME LIKE '%c%%c_%' ESCAPE('c');
```

#### where 子句

```sql
SELECT * FROM EMP WHERE DEPTNO=(SELECT DEPTNO FROM DEPT WHERE DNAME = 'ACCOUNTING');

－－　查询工资在等级３的员工信息
SELECT * FROM EMP WHERE SAL BETWEEN (SELECT LOSAL FROM SALGRADE WHERE GRADE = 3) AND (SELECT HISAL FROM SALGRADE WHERE GRADE = 3) 
```



### 插入相关

#### 基础

```sql
insert into 表名(字段1, 字段2, ...) values(值1, 值2, ...)
```

#### 将查询结果插入另一表中

表1和表2有相同 ***字段1***

```sql
INSERT INTO 表1(字段1) SELECT 字段1 FROM (SELECT DISTINCT 字段1 FROM 表2 ORDER BY 字段1 DESC);
```

### 高级

#### 存储过程

> 教程： <https://zhuanlan.zhihu.com/p/137643958>
>
> 常用结构：<https://zhuanlan.zhihu.com/p/54890263>



- 创建

```sql
create or replace procedure PROC_ZS_YJSE_RKRQ as
begin
  INSERT INTO ZS_YJSE_RKRQ(RKRQ) SELECT DISTINCT RKRQ FROM ZS_YJSF_STATISTICS1 s WHERE s.RKRQ NOT IN (SELECT RKRQ FROM ZS_YJSE_RKRQ) ORDER BY RKRQ DESC;
  commit;
end;
```

- 调用

```sql
call PROC_NAME(参数)
```



#### 创建定时器

参考：<https://blog.csdn.net/qq_25615395/article/details/79316368>

1. 创建存储过程
   - PROC_ZS_YJSE_RKRQ 过程名
   - begin 后语句 为 SQL 语句

```sql
create or replace procedure PROC_ZS_YJSE_RKRQ as
begin
  INSERT INTO ZS_YJSE_RKRQ(RKRQ) SELECT DISTINCT RKRQ FROM ZS_YJSF_STATISTICS1 s WHERE s.RKRQ NOT IN (SELECT RKRQ FROM ZS_YJSE_RKRQ) ORDER BY RKRQ DESC;
  commit;
end;
```

2. 创建定时任务
   - 看情况修改 存储过程名称、初次执行时间、循环执行时间
   - job number 不用改

```sql
declare
  job number;
BEGIN
  DBMS_JOB.SUBMIT(  
        JOB => job,  /*自动生成JOB_ID*/  
        WHAT => 'PROC_ZS_YJSE_RKRQ;',  /*需要执行的存储过程名称*/  
        NEXT_DATE => sysdate,  /*初次立即执行*/  
        INTERVAL => 'ADD_MONTHS(trunc(sysdate,''yyyy''),12)+1/24' /*每年1月1日凌晨1点执行*/
      );  
  commit;
end;
```

3. 查看用户的定时器

```sql
SELECT * FROM user_jobs;
```



### 回滚与恢复

#### 表的回滚

```sql
-- 查看记录时间点的数据
select * from 表名 as of timestamp to_date('2021-03-10 18:10:10', 'yyyy-mm-dd hh24:mi:ss');
-- 让表能够回滚
alter table SYS_API enable row movement;
-- 闪回到时间点
flashback table 表名 to timestamp to_date('2021-03-10 18:10:10', 'yyyy-mm-dd hh24:mi:ss');
```

