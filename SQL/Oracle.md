## Oracle学习笔记

[TOC]

### 字符相关

#### 引号

使用两个单引号转义

```sql
''
```

### 字段类型相关

### 关键字相关

#### 空值

```sql
IS NULL
IS NOT NULL
```

### 内置函数

#### 系统时间

`sysdate`

#### substr()

**注意**：字段索引 start 为 0 或 为 1都表示从第一个位置，共end个

```sql
SUBSTR(字段, start, end)
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

#### 查询存在一个表而不在另一个表中的数据记录

[参考](https://blog.csdn.net/weixin_42342702/article/details/92842741)

```sql
1 select A.ID from A left join B on A.ID=B.ID where B.ID is null
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

