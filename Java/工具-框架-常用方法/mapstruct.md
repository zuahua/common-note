## MapStruct

### 1. 文档-教程-参考

> BO  DO DTO VO 映射转换 **框架**
>
> 官网:<https://mapstruct.org/>
>
> [Release 1.4.2.Final · mapstruct/mapstruct (github.com)](https://github.com/mapstruct/mapstruct/releases/tag/1.4.2.Final)
>
> [MapStruct 1.4.2.Final Reference Guide](https://mapstruct.org/documentation/stable/reference/html/)
>
> 
>
> Maven安装：<https://mapstruct.org/documentation/installation/>
>
> 用法：<https://juejin.cn/post/6937529361839063047>
>
> 详细教程：<https://blog.csdn.net/qq_40194399/article/details/110162124>

### 2. 属性拷贝常见问题

> 1. `null` 值复制问题
>
> 2. 属性名不一致
>
> 3. 属性类型
>
> 4. 集合的复制
>
>    …

### 3. 例

#### 3.1 **Transform** (定义映射接口)

> `@Mapper`
>
> 1. `nullValueCheckStrategy` (对属性复制的**null**值控制策略)
>    1. `NullValueCheckStrategy.ALWAYS`  （总是判断 **null**，属性复制，不为空才赋值）
>    2. `NullValueCheckStrategy.ON_IMPLICIT_CONVERSION` （不判断 **null**）
> 2. `nullValuePropertyMappingStrategy` （对属性更新的**null**值映射策略）
>    1. `NullValuePropertyMappingStrategy.IGNORE`
>    2. `NullValuePropertyMappingStrategy.SET_TO_DEFAULT`
>    3. `NullValuePropertyMappingStrategy.SET_TO_NULL`

> `@Mapping`

```java
package com.imagesky.demo.mapstruct;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
        , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface Transform {
    Transform INSTANCE = Mappers.getMapper(Transform.class);

    @Mapping(source = "numberOfSeats", target = "seatCount")
    CarDto carToCarDto(Car car);

    /**
     * 2. 设置为枚举属性
     */
    @Mapping(source = "numberOfSeats", target = "seatCount")
    @Mapping(target = "type", expression = "java(car.getType().getName())")
    @Named("carToCarDto2")
    CarDto carToCarDto2(Car car);

    /**
     * 3. 日期格式化
     */
    @Mapping(source = "numberOfSeats", target = "seatCount")
    @Mapping(target = "type", expression = "java(car.getType().getName())")
    @Mapping(target = "produceTime", source = "produceTime", dateFormat = "yyyy-MM-dd")
    CarDto carToCarDto3(Car car);

    /**
     * 4. 多源映射
     */
    School toSchool(Teacher teacher, Student student);

    /**
     * 5. 集合映射
     */
    @IterableMapping(elementTargetType = CarDto.class, qualifiedByName = "carToCarDto2")
    List<CarDto> toCarDtoList(List<Car> carList);


    /**
     * 更新 CarDto
     */
    @Mapping(source = "numberOfSeats", target = "seatCount")
    @Mapping(target = "produceTime", source = "produceTime", dateFormat = "yyyy-MM-dd")
    void updateCarDto(@MappingTarget CarDto carDto, Car car);
}
```

#### 3.2 Entity、DTO、enum

##### 3.2.1 Car

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    private String make;
    private int numberOfSeats;
    private CarType type;
    private Date produceTime;
}
```

##### 3.2.2 CarDto

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarDto {
    private String make;
    private int seatCount;
    private String type;
    private String produceTime;
}
```

##### 3.2.3 CarType

```java
public enum CarType {
    /**
     * 类型
     */
    TYPE1("name1"), TYPE2("name2"), TYPE3("name3");


    private String name;

    CarType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

##### 3.2.4 School

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class School {
    private Teacher teacher;

    private Student student;
}
```

##### 3.2.5 Student

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private String name;

    private Integer age;
}
```

##### 3.2.6 Teacher

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    private String name;

    private Integer age;
}
```

### 4. 总结

#### 4.1 优缺点

**优点**

> 1. 效率
>
>    `BeanUtils.copyProperties()`采用的是反射，实际上当重复调用时效率是比较低的。（实际测试实际测试Spring的BeanUtils在生成 次数为1000000时需要1.6秒，而使用MapStruct仅需要69毫秒）
>
> 2. 功能强

**缺点**

> 不利于项目的重构 。
>
> 假如你在DTO里把`a`字段改成了`b`字段，mapstruct都贴心的为你忽略了这些变化。你的项目代码并不会提示错误，风险将直接带到运行时

