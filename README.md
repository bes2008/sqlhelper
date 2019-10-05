[![License](https://img.shields.io/badge/license-LGPL3.0-green.svg)](https://github.com/fangjinuo/sqlhelper/blob/master/LICENSE)

[![Build Status](https://www.travis-ci.org/fangjinuo/sqlhelper.svg?branch=master)](https://travis-ci.org/fangjinuo/sqlhelper)
[![code quality](https://codebeat.co/badges/37791135-62dd-4d5e-800f-35668895324a)](https://codebeat.co/projects/github-com-fangjinuo-sqlhelper-master)
[![CodeFactor](https://www.codefactor.io/repository/github/fangjinuo/sqlhelper/badge/master)](https://www.codefactor.io/repository/github/fangjinuo/sqlhelper/overview/master)


[![MyBatis](https://img.shields.io/badge/MyBatis-3.x-green.svg)](http://www.mybatis.org/mybatis-3/index.html)
[![jFinal](https://img.shields.io/badge/jFinal-3.x-green.svg)](https://github.com/jfinal/jfinal)
[![jFinal](https://img.shields.io/badge/jFinal-4.x-green.svg)](https://github.com/jfinal/jfinal)
[![EBean](https://img.shields.io/badge/Ebean-11.x-green.svg)](https://ebean.io/docs/query/sqlquery)
[![Mango](https://img.shields.io/badge/Mango-1.6.x-green.svg)](https://github.com/jfaster/mango)

[![JDK](https://img.shields.io/badge/JDK-1.6+-green.svg)](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![SpringBoot](https://img.shields.io/badge/SpringBoot-1.x-green.svg)](https://spring.io/projects/spring-boot/)
[![SpringBoot](https://img.shields.io/badge/SpringBoot-2.x-green.svg)](https://spring.io/projects/spring-boot/)

[![maven](https://img.shields.io/badge/maven-v2.0.4-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.sqlhelper%20AND%20v:2.0.4)
[![maven](https://img.shields.io/badge/maven-v2.0.3-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.sqlhelper%20AND%20v:2.0.3)
[![maven](https://img.shields.io/badge/maven-v2.0.2-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.sqlhelper%20AND%20v:2.0.2)
[![maven](https://img.shields.io/badge/maven-v2.0.1-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.sqlhelper%20AND%20v:2.0.1)
[![maven](https://img.shields.io/badge/maven-v2.0.0-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.sqlhelper%20AND%20v:2.0.0)
[![maven](https://img.shields.io/badge/maven-v1.2.3-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.sqlhelper%20AND%20v:1.2.3)
[![maven](https://img.shields.io/badge/maven-v1.2.1-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.sqlhelper%20AND%20v:1.2.1)
[![maven](https://img.shields.io/badge/maven-v1.2.0-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.sqlhelper%20AND%20v:1.2.0)
[![maven](https://img.shields.io/badge/maven-v1.1.1-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.sqlhelper%20AND%20v:1.1.1)
[![maven](https://img.shields.io/badge/maven-v1.1.RELEASE-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo%20AND%20v:1.1-RELEASE)
[![maven](https://img.shields.io/badge/maven-v1.0.RELEASE-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo%20AND%20v:1.0-RELEASE)


# sqlhelper
SQL Tools ( **Dialect**, **Pagination**, **UrlParser**, **SqlStatementParser**, **WallFilter**, **BatchExecutor for Test**) based Java

## modules
|   module         | sqlhelper version | JDK |  Description      |
|------------------|-------------------|-----|-------------------|
|sqlhelper-dialect | 1.0+              |1.6+ |  the core         |
|sqlhelper-mybatis | 1.0+              |1.6+ |  the mybatis tools (MyBatis Pagination Plugin) |
|sqlhelper-mybatis-spring-boot | 1.0+  |1.6+ |  spring boot autoconfigure, starter for sqlhelper-mybatis|
|sqlhelper-mybatis-over-pagehelper|1.0+|1.6+ |  migrate your application from ***mybatis-pagehelper*** |
|sqlhelper-jfinal  | 1.2+              |1.6+ |  supports jfinal  |
|sqlhelper-ebean   | 1.2+              |1.6+ |  supports ebean   |
|sqlhelper-hibernate| 1.2+             |1.6+ |  supports hibernate|
|sqlhelper-mango   | 1.2+              |1.6+ |  supports mango   |
|sqlhelper-batchinsert|1.2+            |1.6+ |  Batch insert data to database, use it for performance |
|sqlhelper-springjdbc | 2.0.2+         |1.6+ |  supports spring-jdbc |
|sqlhelper-springjdbc-spring-jdbc| 2.0.2+|1.8+| spring boot autoconfigure, starter for sqlhelper-springjdbc |
|sqlhelper-examples|1.0+               |1.8+ |  some examples for there tools |


## tools usage
### pagination plugin
#### Key Features
1. Supports MyBatis, JFinal, EBean, Mango, Hibernate, SpringJdbc
2. Supported 100+ databases, you can find them ***[here](https://github.com/f1194361820/sqlhelper/wiki/Pagination_Database)***. Conatins almost all chinese database:
    + AliSQL (阿里 MySQL)
    + AntDB (亚信)
    + CirroDB (东方国信 行云)
    + CynosDB (腾讯云数据库)
    + Doris (Apache Doris，百度研发)
    + DM (达梦)
    + EsgynDB (易鲸捷)
    + GaussDB (华为 高斯)
    + GBase (南大通用)
    + GoldenDB (中兴)
    + HHDB (恒辉数据库)
    + HighGo (瀚高)
    + HybridDB (阿里巴巴 分布式PostgreSQL)
    + K-DB (浪潮)
    + KingBase (金仓)
    + MaxCompute (阿里巴巴)
    + OBase (上海丛云信息科技)
    + OceanBase (阿里巴巴/蚂蚁金服 兼容mysql)
    + OSCAR (神州通用)
    + OpenBase (东软)
    + RadonDB (青云)
    + SequoiaDB (巨杉)
    + TDSQL (腾讯 分布式MySQL)
    + TiDB (北京平凯星辰科技))
    + Trafodion (易鲸捷 EsgynDB的开源版)
    
    
    If you want know all RDMBS ranking, you can find them in [DB Engines](https://db-engines.com/en/ranking/relational+dbms).
    
3. Supports multiple databases in one application 
4. Supports auto detect dialect, so the dialect option is optional (current supports this mode in mybatis environment)
5. **The performance is higher than Mybatis-PageHelper**, because the limit、offset will be setted with PrepareStatement placeholder '?'
6. Supports plugin use Java SPI
7. Supports spring boot 1.x , 2.x
8. Supports mybatis 3.x 
9. Supports JDK6+
10. Supports Memory Pagination
11. Supports SqlSymbolMapper, use it you can mapping any symbol to SQL symbol

#### sqlhelper vs mybatis-pagehelper
|  metric                  | mybatis-pagehelper |      sqlhelper    |
|--------------------------|:------------------:|:-----------------:|
|  databases               |         13         |         90+       |
|  multiple databases in runtime |   √          |         √         |
|  auto detect dialect     |         √          |         √         |
|  plugin                  |         √          |         √         |
|  PrepareStatement with '?'|         X         |         √         |                             
|  mybatis                 |         3.x        |         3.x       |
|  spring boot             |         1.x, 2.x   |         1.x, 2.x  |
|  JDK                     |         1.6+       |         1.6+      |
|  jFinal                  |         X          |         √         |
|  Mango                   |         X          |         √         |
|  EBean                   |         X          |         √         | 
|  国产数据库               |         X          | √ （参见上述列表）  |
|  Spring JDBC             |         X          |         1.6+      | 
|  SqlSymbol Mapping       |         X          |         √         |   
                     


# Pagination
* [Quick Start](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart)
  + [MyBatis application](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_MyBatis)
    - [Installation](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_MyBatis)
    - [How To](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_MyBatis)
    - [Migrate from mybatis-pagehelper application](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_MyBatis)
  + [SpringJDBC application](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_SpringJDBC)
    - [Installation](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_SpringJDBC)
    - [How To](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_SpringJDBC)  
  + [jFinal application](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_jFinal)
    - [Installation](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_jFinal)
    - [How To](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_jFinal)
  + [EBean application](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_ebean)
* Advanced Usage
# UrlParser
Parse jdbc url.
## usage:
<pre>
String url = "jdbc:mysql://${localhost}:${port}....";
DatabaseInfo dbinfo = new JdbcUrlParser().parse(url)
...
</pre> 

# Batch Insert Tool
 If you want to optimize SQL performance, it is essential to create a large amount of sample data. This tool can meet your needs.

# [FAQ](https://github.com/fangjinuo/sqlhelper/wiki/FAQ)

# Contact
QQ Group: 750929088   
![QQ Group](https://github.com/fangjinuo/sqlhelper/blob/master/_images/qq_group.png)
