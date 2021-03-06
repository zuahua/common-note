### 1. 基础问题

#### assert的陷进

> 1.  `assert`关键字需要在运行时候**显式开启**才能生效(`VM option: -ea`)，IDEA测试环境默认开启
> 2. 用`assert`代替`if`是陷阱之二。`assert`一般用于测试。
> 3. `assert`断言失败将面临程序的退出。

#### 文件上传问题

![](https://raw.githubusercontent.com/zuahua/image/master/common-note-c/20210708175622.png)

### 2. 高级用法-JDK8

#### 2.1 List 结果分组

> 1. `list`的`item`为对象

```java
List<WaterLineDTO> waterLineDTOList = mapper.queryWaterLineWithLatestWeek();
// 按 date 分组
Map<String, List<WaterLineDTO>> collect = waterLineDTOList.stream().collect(Collectors.groupingBy(WaterLineDTO::getDate));
```

> 2. `list`的`item`为 `map`

```java
List<Map<String, Object>> list = mapper.queryWaterLine(waterLineWithDateDTO);
// 按 date 分组
Map<String, List<Map<String, Object>>> collect = list.stream().collect(Collectors.groupingBy(this::getDateKey));
```

```java
/**
     * 获取 key=date 值
     */
private String getDateKey(Map<String, Object> map) {
    return map.get("date").toString();
}
```

#### 2.2 最大最小值

> `Stream.max()`
>
> `Stream.min()`

[【Java 8 新特性】Java Stream 通过min()和max()获取列表最小值和最大值_猫巳的博客-CSDN博客](https://blog.csdn.net/qq_31635851/article/details/111167101)

#### 2.4 分组排序

```java
// 以时间分组
TreeMap<Date, List<WeatherEntity>> collect = weatherEntityList.stream().collect(Collectors.groupingBy(WeatherEntity::getDate,
                                                                           TreeMap::new, Collectors.toList()));
// 降序
NavigableMap<Date, List<WeatherEntity>> descendingMap = collect.descendingMap();
```

