# sqlhelper
SQL Tools ( **Dialect**, **Pagination**, **UrlParser**, **SqlStatementParser**, **WallFilter**, **BatchExecutor for Test**) based Java

## modules
**sqlhelper-dialect** : the core <br/> 
**sqlhelper-mybatis** : the mybatis tools (MyBatis Pagination Plugin)<br/> 
**sqlhelper-mybatis-spring-boot**: spring boot autoconfigure, starter for sqlhelper in mybatis environment<br/>
**sqlhelper-examples**: some examples for there tools<br/> 


## tools usage
### mybatis pagination plugin
#### 1、Supports
1. supports the following databases:
    + Oracle
    + Mysql
    + MariaDB
    + PostgreSQL
    + SQL Server
    + OSCAR (神通数据库)
    + DM (达梦数据库)
    + Kingbase (人大金仓数据库)
    + H2
    + SQLite
    + HSqlDb
    + Derby
    + DB2
    + Informix
    + Firebird
    + CacheDB
    + CUBRID
    + HANA
    + Ingres
    + Interbase
    + Teradata 
    + TimesTen
2. multiple databases are supported in one application <br>
3. auto detect dialect<br>
4. **the performance is higher than Mybatis-PageHelper**, because the limit、offset will be setted with PrepareStatement placeholder '?' 

#### 2、 installation

##### case 1, use it with spring boot application: 
 just import dependencies:

<pre>
    &lt;dependency>
        &lt;groupId>com.fjn.helper&lt;/groupId>
        &lt;artifactId>sqlhelper-mybatis-spring-boot-autoconfigure&lt;/artifactId>
        &lt;version>${sqlhelper.version}&lt;/version>
    &lt;/dependency>
    &lt;dependency>
        &lt;groupId>com.fjn.helper&lt;/groupId>
        &lt;artifactId>sqlhelper-mybatis-spring-boot-starter&lt;/artifactId>
        &lt;version>${sqlhelper.version}&lt;/version>
    &lt;/dependency>
</pre>  

also see **sqlhelper-examples** module

##### case 2, other appliction : 
1.import dependencies:
<pre>
    &lt;dependency>
        &lt;groupId>com.fjn.helper&lt;/groupId>
        &lt;artifactId>sqlhelper-dialect&lt;/artifactId>
        &lt;version>${sqlhelper.version}&lt;/version>
    &lt;/dependency>
</pre>        
2.config **mybatis-config.xml** ：
<pre>
    &lt;configuration>
        ...
        &lt;databaseIdProvider type="DB_VENDOR">
          &lt;property name="SQL Server" value="sqlserver"/>
          &lt;property name="DB2" value="db2"/>
          &lt;property name="Oracle" value="oracle" />
        &lt;/databaseIdProvider>
        ...
        &lt;settings>
            ...
            &lt;setting name="defaultScriptingLanguage" value="com.fjn.helper.sql.mybatis.plugins.pagination.CustomScriptLanguageDriver" />
            ...
        &lt;/settings>
        ...
    &lt;/configuration>
    
    &lt;plugins>
      &lt;plugin interceptor="com.fjn.helper.sql.mybatis.plugins.pagination.MybatisPaginationPlugin" />
    &lt;/plugins>
</pre>


#### 3、 How to
you can use it like this:
<pre>
    @GetMapping
    public PagingResult list(){
        User queryCondtion = new User();
        queryCondtion.setAge(10);
        PagingRequest request = new PagingRequest()
                .setPageNo(1)
                .setPageSize(10);
        PagingRequestContextHolder.getContext().setPagingRequest(request);
        List<User> users = userDao.selectByLimit(queryCondtion);
        request.getResult().setItems(users);
        return request.getResult();
    }
</pre>


