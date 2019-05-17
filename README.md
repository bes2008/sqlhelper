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
1. Supported 90+ databases, you can find them ***[here](https://github.com/f1194361820/sqlhelper/wiki/Pagination_Database)***. Conatins almost all chinese database:
    + TiDB (北京平凯星辰科技))
    + MaxCompute (阿里巴巴)
    + K-DB (浪潮)
    + GBase (南大通用)
    + DM (达梦)
    + OSCAR (神州通用)
    + HighGo (瀚高数据库)
    + KingBase (金仓)
    + OpenBase (东软)
    + SequoiaDB (巨杉数据库)
    
    If you want know all RDMBS ranking, you can find them in [DB Engines](https://db-engines.com/en/ranking/relational+dbms).
    
2. Supports multiple databases in one application 
3. Supports auto detect dialect, so the dialect option is optional
4. **The performance is higher than Mybatis-PageHelper**, because the limit、offset will be setted with PrepareStatement placeholder '?'
5. Supports spring boot 1.x , 2.x
6. Supports mybatis 3.x 
7. Supports JDK6+

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