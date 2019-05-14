# sqlhelper
SQL Tools ( **Dialect**, **Pagination**, **UrlParser**, **SqlStatementParser**, **WallFilter**, **BatchExecutor for Test**) based Java

## modules
**sqlhelper-dialect** : the core<br/>
**sqlhelper-mybatis** : the mybatis tools (MyBatis Pagination Plugin)<br/> 
**sqlhelper-mybatis-spring-boot**: spring boot autoconfigure, starter for sqlhelper in mybatis environment<br/>
**sqlhelper-mybatis-over-pagehelper**: migrate your application from ***mybatis-pagehelper*** to us <br/> 
**sqlhelper-examples**: some examples for there tools<br/>
 


## tools usage
### mybatis pagination plugin
#### Key Features
1. supports the following databases:
    + A
        + Access
    + C
        + Cache (CacheDB)
        + CUBRID
    + D
        + DBF
        + DB2
        + Derby
        + DM (达梦数据库)
    + E      
        + Elasticsearch
        + Excel
    + F
        + Firebird
        + Fontbase
    + H
        + H2
        + HANA (HANAColumn, HANAStore)
        + HSQL        
    + I
        + Informix
        + Ingres
        + Interbase
    + J
        + JDataStore
    + K
        + Kingbase (人大金仓)        
    + M
        + MariaDb
        + MySQL
        + Mckoi
        + Mimer
        + Monet
    + O
        + Oracle (8i,9,9i,10g,11g,12c)
        + OSACR (神通数据库)
        + Openbase
    + P 
        + Paradox
        + Phoenix (HBase JDBC Client)
        + Pointbase
        + PostgreSQL
        + Progress
    + R
        + RDMSOS2002
    + S
        + SAPMAXDB
        + SQLite
        + SQLServer (SQLServer 2005,2008,2012)
    + T
        + Teradata
        + Text
        + TimesTen
    + V
        + Vertica

2. supports multiple databases in one application 
3. supports auto detect dialect, so the dialect option is optional
4. **the performance is higher than Mybatis-PageHelper**, because the limit、offset will be setted with PrepareStatement placeholder '?'
5. supports spring boot 1.x , 2.x
6. supports mybatis 3.x 

#### Installation

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


#### How to
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


### Migrate from mybatis-pagehelper
just replace mybatis-pagehelper dependencies to sqlhelper-mybatis-over-pagehelper:
<pre>
    &lt;dependency>
        &lt;groupId>com.fjn.helper&lt;/groupId>
        &lt;artifactId>sqlhelper-mybatis-over-pagehelper&lt;/artifactId>
        &lt;version>${sqlhelper.version}&lt;/version>
    &lt;/dependency>
</pre>
use it, your code will not make any changes