## PostgreSQL

### 1. 教程-文档

**对象-关系数据库服务器(ORDBMS)**

> [PostgreSQL 教程 - 菜鸟](https://www.runoob.com/postgresql/postgresql-tutorial.html)

### 2. DDL

#### 2.1 注释

```sql
--语法
comment on [type] [target] is [comment]  
--为表添加注释
comment on table user is 'The user table';
--为字段注释
comment on column user.userid is 'The user ID';
```

#### 2.2 索引

```sql
create index on tabel_name(field1, field2);
```

#### 2.3 主键自增-序列

https://blog.csdn.net/weixin_42845682/article/details/107111996

1. 创建序列

```sql
CREATE SEQUENCE 
test_id_seq
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1; 
```

2. 主键使用

```sql
nextval('test_id_seq')

alter tabl表名 alter column id set default nextval(‘表名_id_seq’);
```

3. **serial**

```sql
create table test(
	id serial primary key,
	age int
)
```





#### 例

```sql
CREATE TABLE t_gep_item_qy_test (
	id int8 NOT NULL DEFAULT nextval('item_id_seq'::regclass),
	geoid varchar(40) null,
	zbid int4 null,
	ecoid int4 null,
	jzwz float8 null,
	iyear bpchar(10) null,
	CONSTRAINT t_gep_item_qy_test_pk_id PRIMARY KEY(id)
);
comment on table t_gep_item_qy_test is '测试-item';
comment on column t_gep_item_qy_test.geoid is '地块id，其他表关联此id';
comment on column t_gep_item_qy_test.zbid is '指标id, 对应 t_gep_zb';
comment on column t_gep_item_qy_test.ecoid is 'eco 类型id, 对应 t_gep_ecosys';
comment on column t_gep_item_qy_test.jzwz is '对应指标值';
comment on column t_gep_item_qy_test.iyear is '年份';
```



### 3. 操作