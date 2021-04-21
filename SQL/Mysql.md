# Mysql

## 建表相关

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

