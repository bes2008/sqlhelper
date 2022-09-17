## MyBatis Plus 2.x + Spring Boot 下安装

1、前置条件

保证依赖在满足如下版本： 
```yaml
+ mybatis 3.4.0+ (更低版本尚未测试，如有需要，自行测试即可)
+ JDK 1.7+
+ mybatis-plus 2.3.x (更低版本尚未测试，如有需要，自行测试即可)

```

2、 移除 mybatis-plus-boot-starter


```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>2.x</version>
</dependency>

```

2、 加入 sqlhelper-mybatisplus_2x-spring-boot-starter

内部迁移了mybatis-plus-boot-starter的代码

```xml
<dependency>
    <groupId>io.github.bes2008.solution.sqlhelper</groupId>
    <artifactId>sqlhelper-mybatisplus_2x-spring-boot-starter</artifactId>
    <version>${sqlhelper.version}</version>
</dependency>
```

3、configure application.yml (Optional):

```yaml
sqlhelper:
  mybatis:
    instrumentor:
      cache-instrumented-sql: true
      subquery-paging-start-flag: "[PAGING_StART]"
      subquery-paging-end-flag: "[PAGING_END]"
    pagination:
      count: true
      default-page-size: 10
      use-last-page-if-page-no-out: true
      count-suffix: _COUNT
```

or configure application.properties (Optional):

```properties
sqlhelper.mybatis.instrumentor.cacheInstrumentedSql=true
sqlhelper.mybatis.instrumentor.dialect=mysql
sqlhelper.mybatis.instrumentor.subqueryPagingStartFlag=[PAGING_START]
sqlhelper.mybatis.instrumentor.subqueryPagingEndFlag=[PAGING_END]
sqlhelper.mybatis.count= true
sqlhelper.mybatis.defaultPageSize= 10
sqlhelper.mybatis.countSuffix=_COUNT
```


4、使用

参见用例：${sqlhelper-examples}/${sqlhelper-examples-service}/${sqlhelper-examples-service-mybatisplus_2x}


5、配置

所有配置项参见 [configuration](../configuration.md)