# Work with mybatis、Spring Boot

## installation

1、import dependencies:
```xml
<dependency>
    <groupId>io.github.bes2008.solution.sqlhelper</groupId>
    <artifactId>sqlhelper-mybatis-spring-boot-starter</artifactId>
    <version>${sqlhelper.version}</version>
</dependency>
```

2、configure application.yml (Optional):

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

3、使用

参见用例：${sqlhelper-examples}/${sqlhelper-examples-service}/${sqlhelper-examples-service-mybatis}

4、配置

所有配置项参见 [configuration](../configuration.md)

