# Work with MyBatis

## installation

1、import dependencies:
```xml
<dependency>
    <groupId>com.github.fangjinuo.sqlhelper</groupId>
    <artifactId>sqlhelper-mybatis</artifactId>
    <version>${sqlhelper.version}</version>
</dependency>
```

2、configure the mybatis-config.xml:

```xml
<configuration>
     ...
     <databaseIdProvider type="DB_VENDOR">
         <property name="SQL Server" value="sqlserver"/>
         <property name="DB2" value="db2"/>
         <property name="Oracle" value="oracle" />
         ...
     </databaseIdProvider>
     ...
     <settings>
         ...
         <setting name="defaultScriptingLanguage" value="com.jn.sqlhelper.mybatis.plugins.pagination.CustomScriptLanguageDriver" />
         ...
     </settings>
         ...

    <plugins>
        <plugin interceptor="com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin">
            <property name ="sqlhelper.mybatis.instrumentor.dialect" value="mysql" />
            <property name ="sqlhelper.mybatis.instrumentor.cacheInstruemtedSql" value="true" />
            <property name ="sqlhelper.mybatis.pagination.count" value="true" />
            <property name ="sqlhelper.mybatis.pagination.defaultPageSize" value="defaultPageSize" />
            <property name ="sqlhelper.mybatis.pagination.useLastPageIfPageNoOut" value="useLastPageIfPageNoOut" />
            <property name ="sqlhelper.mybatis.pagination.countSuffix" value="_COUNT" />
            ...
        </plugin>
    </plugins>

</configuration>

```


 


