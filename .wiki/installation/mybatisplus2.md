## MyBatis Plus 2.x + Spring Boot 下安装

1. 移除 mybatis-plus-boot-starter

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>2.x</version>
</dependency>

```

2. 加入 sqlhelper-mybatisplus_2x-spring-boot-starter


```xml
<dependency>
    <groupId>com.github.fangjinuo.sqlhelper</groupId>
    <artifactId>sqlhelper-mybatisplus_2x-spring-boot-starter</artifactId>
    <version>${sqlhelper.version}</version>
</dependency>
```



