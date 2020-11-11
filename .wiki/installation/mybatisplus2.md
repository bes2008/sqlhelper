## MyBatis Plus 2.x + Spring Boot 下安装

1、 移除 mybatis-plus-boot-starter

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>2.x</version>
</dependency>

```

2、 加入 sqlhelper-mybatisplus_2x-spring-boot-starter


```xml
<dependency>
    <groupId>com.github.fangjinuo.sqlhelper</groupId>
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


