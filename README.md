# YMixin
YMixin是一个与Mixin类似的字节码插桩工具。与Mixin不同的是，YMixin更轻量且生成的字节码可用于热修改。

示例代码见https://github.com/yapeteam/YMixin/tree/master/src/test/java/cn/yapeteam/ymixin/example

# 用法

## 1.初始化

```java
public static void init(ClassProvider provider, ClassBytesProvider bytesProvider, @Nullable Logger logger);
```

这个是YMixin.class中的init方法，这也就意味着，你需要初始化才能使用。

你可以这样初始化:

首先，这是3个函数式接口，传入三个个参数

|               参数名字               |     参数作用      |
|:--------------------------------:|:-------------:|
|      ClassProvider provider      |  获取目标类的回调函数   |
| ClassBytesProvider bytesProvider | 获取目标类字节码的回调函数 |
|     @Nullable Logger logger      |    日志的回调函数    |

如果没有特殊的需要，你可以按照例子中的写法:

```java
YMixin.init(
  clazz -> {
    try {
      return Class.forName(clazz);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }, clazz -> {
    try {
      return readStream(Objects.requireNonNull(Main.class.getResourceAsStream("/" + clazz.getName().replace(".", "/") + ".class")));
    } catch (Exception e) {
      return null;
    }
  }, null,
  null,
  null
);
```

## 2.选择映射表 (可选)

通过映射表，你可以在经过混淆的类中动态修改他们，如果没有混淆，你可以选择跳过.

在YMixin初始化时，传入mapping_reader和mapping_content可以设置你的映射。

```java
YMixin.init(
  clazz -> {
    try {
      return Class.forName(clazz);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }, clazz -> {
    try {
      return readStream(Objects.requireNonNull(Main.class.getResourceAsStream("/" + clazz.getName().replace(".", "/") + ".class")));
    } catch (Exception e) {
      return null;
    }
  }, null,
  <mapping_reader>,
  <mapping_content>
);
```

若未提供<mapping_reader>，YMixin将默认使用SrgMappingReader

如果没有你的混淆表文件格式，你可以自己写一个解析器，实现`cn.yapeteam.ymixin.utils.Mapper.IMappingReader`

## 3.创建转换器

在新建的*Transformer*实例中使用*addMixin(class)*方法，可以添加你的插入代码.

```
byte[] bytes = transformer.transform().get("your.class.name");
```

生成的字节码可以通过自定义ClassLoader加载，或者调用JVMTI进行热修改
