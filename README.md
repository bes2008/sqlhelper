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
1. supports the following databases, your can find them in [DB Engines](https://db-engines.com/en/ranking/relational+dbms):    
    + A
        + Access
        + AgensGraph
        + Aurora (Amazon Aurora)
        + Altibase
        + Azure (Microsoft Azure Cloud Database)
    + C
        + Cache (CacheDB)
        + ClickHouse
        + ClustrixDB
        + Cockroach
        + ComDB2
        + Crate
        + CTree (CTreeACE, CTreeEDGE, CTreeRTG)
        + Cubrid       
    + D
        + DBF
        + DB2
        + Derby
        + DM (达梦数据库)
        + Drill (Apache Drill)
    + E      
        + Elasticsearch
        + EsgynDB
    + F
        + Firebird
        + FileMaker
    + G
        + GBase (南大通用)
        + Greenplum
    + H
        + H2
        + HANA (SAP HANAColumn, HANAStore)
        + Hawq (Apache Hawq)
        + Hive
        + HSQL        
    + I
        + Ignite (Apache Ignite)
        + Impala (Apache Impala)
        + Informix
        + Ingres
        + Interbase
        + Iris
    + J
        + JDataStore
    + K
        + Kingbase (人大金仓)
        + Kinetica   
        + Kognitio
    + M
        + MariaDb
        + MaxCompute
        + MAXDB (SAP MaxDB)
        + MySQL
        + Mckoi
        + Mimer
        + Monet
        + MemSQL
        + MSQL (MiniSQL)
    + N
        + Netezza (IBM Netezza)
        + NexusDB
        + NuoDB        
    + O
        + Omnisci
        + Oracle (8i,9,9i,10g,11g,12c)
        + OSACR (神通数据库)
        + Openbase
        + OpenEdge
    + P 
        + Paradox
        + PerconaMySQL
        + Phoenix (HBase JDBC Client)
        + PostgreSQL
        + Progress
    + R
        + RDMSOS2200
        + Redshift (Amazon Redshift)
        + RBase
    + S        
        + SequoiaDB (巨杉数据库)
        + SnowflakeDialect
        + SpliceMachine
        + SQLite
        + SQLServer (SQLServer 2005,2008,2012,2017)
    + T
        + Teradata
        + TiDB
        + TimesTen
    + V
        + Vertica
        + Virtuoso
        + VistaDB
        + VoltDB
    + X
        + Xtreme (EXtremeSQL, XtremeData)

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