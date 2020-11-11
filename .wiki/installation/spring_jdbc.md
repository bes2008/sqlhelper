# Work with Spring-JDBC

1、import dependencies:
```xml
<dependency>
    <groupId>com.github.fangjinuo.sqlhelper</groupId>
    <artifactId>sqlhelper-springjdbc-spring-boot-starter</artifactId>
    <version>${sqlhelper.version}</version>
</dependency>
```

2、配置：

```yaml
sqlhelper:
  springjdbc:
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


3、使用

参见用例：${sqlhelper-examples}/${sqlhelper-examples-service}/${sqlhelper-examples-service-springjdbc}

