[![License](https://img.shields.io/badge/license-LGPL3.0-green.svg)](https://github.com/fangjinuo/sqlhelper/blob/master/LICENSE)

[![Build Status](https://www.travis-ci.org/fangjinuo/sqlhelper.svg?branch=master)](https://travis-ci.org/fangjinuo/sqlhelper)
[![code quality](https://codebeat.co/badges/37791135-62dd-4d5e-800f-35668895324a)](https://codebeat.co/projects/github-com-fangjinuo-sqlhelper-master)
[![CodeFactor](https://www.codefactor.io/repository/github/fangjinuo/sqlhelper/badge/master)](https://www.codefactor.io/repository/github/fangjinuo/sqlhelper/overview/master)

[![JDK](https://img.shields.io/badge/JDK-1.6+-green.svg)](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![MyBatis](https://img.shields.io/badge/MyBatis-3.x-green.svg)](http://www.mybatis.org/mybatis-3/index.html)
[![SpringBoot](https://img.shields.io/badge/SpringBoot-1.x-green.svg)](https://spring.io/projects/spring-boot/)
[![SpringBoot](https://img.shields.io/badge/SpringBoot-2.x-green.svg)](https://spring.io/projects/spring-boot/)

[![maven](https://img.shields.io/badge/maven-v1.1.1-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.sqlhelper)
[![maven](https://img.shields.io/badge/maven-v1.1.RELEASE-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo)
[![maven](https://img.shields.io/badge/maven-v1.0.RELEASE-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo)


# sqlhelper
SQL Tools ( **Dialect**, **Pagination**, **UrlParser**, **SqlStatementParser**, **WallFilter**, **BatchExecutor for Test**) based Java

## modules
**sqlhelper-dialect** : the core<br/>
**sqlhelper-mybatis** : the mybatis tools (MyBatis Pagination Plugin)<br/> 
**sqlhelper-mybatis-spring-boot**: spring boot autoconfigure, starter for sqlhelper in mybatis environment<br/>
**sqlhelper-mybatis-over-pagehelper**: migrate your application from ***mybatis-pagehelper*** to us <br/> 
**sqlhelper-examples**: some examples for there tools<br/>
 


## tools usage
### pagination plugin
#### Key Features
1. Supports MyBatis, JFinal
2. Supported 90+ databases, you can find them ***[here](https://github.com/f1194361820/sqlhelper/wiki/Pagination_Database)***. Conatins almost all chinese database:
    + TiDB (北京平凯星辰科技))
    + Doris (Apache Doris，百度研发)
    + MaxCompute (阿里巴巴)
    + K-DB (浪潮)
    + GBase (南大通用)
    + DM (达梦)
    + OSCAR (神州通用)
    + HighGo (瀚高)
    + KingBase (金仓)
    + OpenBase (东软)
    + SequoiaDB (巨杉)
    
    If you want know all RDMBS ranking, you can find them in [DB Engines](https://db-engines.com/en/ranking/relational+dbms).
    
3. Supports multiple databases in one application 
4. Supports auto detect dialect, so the dialect option is optional (current supports this mode in mybatis environment)
5. **The performance is higher than Mybatis-PageHelper**, because the limit、offset will be setted with PrepareStatement placeholder '?'
6. Supports plugin use Java SPI
7. Supports spring boot 1.x , 2.x
8. Supports mybatis 3.x 
9. Supports JDK6+

#### sqlhelper vs mybatis-pagehelper
|  metric                  | mybatis-pagehelper |      sqlhelper    |
|--------------------------|:------------------:|:-----------------:|
|  databases               |         13         |         90+       |
|  multiple databases in runtime |   √         |         √        |
|  auto detect dialect     |         √         |         √        |
|  plugin                  |         √         |         √        |
|  PrepareStatement with '?'|         X         |         √        |                             
|  mybatis                 |         3.x        |         3.x       |
|  spring boot             |         1.x, 2.x   |         1.x, 2.x  |
|  JDK                     |         1.6+       |         1.6+      |
|  jFinal                  |         X          |         √        |
|  国产数据库               |         X          | √ （参加上述列表）|
                     


#### Installation

##### case 1, use it with spring boot application: 
 just import dependencies:

<pre>
    &lt;dependency>
        &lt;groupId>com.github.fangjinuo.sqlhelper&lt;/groupId>
        &lt;artifactId>sqlhelper-mybatis-spring-boot-autoconfigure&lt;/artifactId>
        &lt;version>${sqlhelper.version}&lt;/version>
    &lt;/dependency>
    &lt;dependency>
        &lt;groupId>com.github.fangjinuo.sqlhelper&lt;/groupId>
        &lt;artifactId>sqlhelper-mybatis-spring-boot-starter&lt;/artifactId>
        &lt;version>${sqlhelper.version}&lt;/version>
    &lt;/dependency>
</pre>  

also see **sqlhelper-examples** module

##### case 2, other application (without springboot): 
1.import dependencies:
<pre>
    &lt;dependency>
        &lt;groupId>com.github.fangjinuo.sqlhelper&lt;/groupId>
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
            &lt;setting name="defaultScriptingLanguage" value="com.github.fangjinuo.sqlhelper.mybatis.plugins.pagination.CustomScriptLanguageDriver" />
            ...
        &lt;/settings>
        ...
    &lt;/configuration>
    
    &lt;plugins>
      &lt;plugin interceptor="com.github.fangjinuo.sqlhelper.mybatis.plugins.pagination.MybatisPaginationPlugin" />
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
        &lt;groupId>com.github.fangjinuo.sqlhelper&lt;/groupId>
        &lt;artifactId>sqlhelper-mybatis-over-pagehelper&lt;/artifactId>
        &lt;version>${sqlhelper.version}&lt;/version>
    &lt;/dependency>
</pre>
use it, your code will not make any changes
