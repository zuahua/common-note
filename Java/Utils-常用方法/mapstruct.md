## MapStruct

### 1. 文档-教程

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
> 2. 属性名不一致

### 3. 总结

#### 3.1 优缺点

**优点**

> 1. 效率
>
>    BeanUtils.copyProperties()采用的是反射，实际上当重复调用时效率是比较低的。（实际测试实际测试Spring的BeanUtils在生成 次数为1000000时需要1.6秒，而使用MapStruct仅需要69毫秒）
>
> 2. 功能强

**缺点**

> 不利于项目的重构 。
>
> 假如你在DTO里把`a`字段改成了`b`字段，mapstruct都贴心的为你忽略了这些变化。你的项目代码并不会提示错误，风险将直接带到运行时

