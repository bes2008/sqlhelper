[![License](https://img.shields.io/badge/license-LGPL3.0-green.svg)](https://github.com/fangjinuo/sqlhelper/blob/master/LICENSE)

[![Build Status](https://www.travis-ci.org/fangjinuo/sqlhelper.svg?branch=master)](https://travis-ci.org/fangjinuo/sqlhelper)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9c27e94ffa3a4ee58279c36236b2b075)](https://www.codacy.com/manual/fs1194361820/sqlhelper?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fangjinuo/sqlhelper&amp;utm_campaign=Badge_Grade)

[![JDK](https://img.shields.io/badge/JDK-1.6+-green.svg)](https://www.oracle.com/technetwork/java/javase/downloads/index.html)

[![MyBatis](https://img.shields.io/badge/MyBatis-3.x-green.svg)](http://www.mybatis.org/mybatis-3/index.html)
[![MyBatis](https://img.shields.io/badge/MyBatisPlus-3.x-green.svg)](http://www.mybatis.org/mybatisplus-3/index.html)
[![jFinal](https://img.shields.io/badge/jFinal-3.x-green.svg)](https://github.com/jfinal/jfinal)
[![jFinal](https://img.shields.io/badge/jFinal-4.x-green.svg)](https://github.com/jfinal/jfinal)
[![EBean](https://img.shields.io/badge/Ebean-11.x-green.svg)](https://ebean.io/docs/query/sqlquery)
[![Mango](https://img.shields.io/badge/Mango-1.6.x-green.svg)](https://github.com/jfaster/mango)
[![Commons-DBUtils](https://img.shields.io/badge/Apache_Commons_DBUtils-1.7.x-green.svg)](http://commons.apache.org/proper/commons-dbutils/index.html)
[![Solon](https://img.shields.io/badge/solon-1.x-green.svg)](https://github.com/noear/solon)


[![Spring-Jdbc](https://img.shields.io/badge/SpringJdbc-2.x-green.svg)](https://spring.io/projects/spring-framework)
[![Spring-Jdbc](https://img.shields.io/badge/SpringJdbc-3.x-green.svg)](https://spring.io/projects/spring-framework)
[![Spring-Jdbc](https://img.shields.io/badge/SpringJdbc-4.x-green.svg)](https://spring.io/projects/spring-framework)
[![Spring-Jdbc](https://img.shields.io/badge/SpringJdbc-5.x-green.svg)](https://spring.io/projects/spring-framework)

[![SpringBoot](https://img.shields.io/badge/SpringBoot-1.x-green.svg)](https://spring.io/projects/spring-boot/)
[![SpringBoot](https://img.shields.io/badge/SpringBoot-2.x-green.svg)](https://spring.io/projects/spring-boot/)

[![maven](https://img.shields.io/badge/maven-v3.2.3-green.svg)](https://search.maven.org/search?q=g:com.github.fangjinuo.sqlhelper%20AND%20v:3.2.3)


Tutorial : https://fangjinuo.gitee.io/docs/index.html

## [GitHub地址](https://github.com/fangjinuo)
## [Gitee地址](https://githee.com/fangjinuo)


# sqlhelper
SQL Tools ( **Dialect**, **Pagination**, **DDL Dump**, **UrlParser**, **SqlStatementParser**, **WallFilter**, **BatchExecutor for Test**) based Java

## modules
|   module         | sqlhelper version | JDK |  Description      |
|------------------|-------------------|-----|-------------------|
|sqlhelper-dialect | 1.0+              |1.6+ |  the core （绝大部分功能都离不开它）|
|sqlhelper-cli | 2.0+              |1.8+ |  CLI 命令行工具 |
|sqlhelper-cli-assembly | 2.0+              |1.8+ | 为 CLI 命令行工具 提供打包功能|
|sqlhelper-mybatis | 1.0+              |1.6+ |  提供对MyBatis的支持 (主要包括：通用分页、通用批量操作) |
|sqlhelper-mybatis-spring-boot | 1.0+  |1.6+ |  为MyBatis + SqlHelper 提供了Spring Boot 快速启动|
|sqlhelper-mybatis-over-pagehelper|1.0+|1.6+ |  引入它就可以无缝的从 ***mybatis-pagehelper*** 迁移到 SqlHelper|
|sqlhelper-jfinal  | 1.2+              |1.6+ |  对国产框架jfinal支持 (主要包括：通用分页) |
|sqlhelper-ebean   | 1.2+              |1.6+ |  对 ebean 支持(主要包括：通用分页)  |
|sqlhelper-hibernate| 1.2+             |1.6+ |  对 hibernate 支持(主要包括：通用分页) |
|sqlhelper-mango   | 1.2+              |1.6+ |  对 mango 支持 支持(主要包括：通用分页) |
|sqlhelper-batchinsert|1.2+            |1.6+ |  为了 **性能测试** 提供的批量入库工具 |
|sqlhelper-springjdbc | 2.0.2+         |1.6+ |  对 spring-jdbc 支持 (主要包括：通用分页)|
|sqlhelper-springjdbc-spring-boot| 2.0.2+|1.8+| 对 spring-jdbc 应用提供了 Spring Boot 快速启动|
|sqlhelper-mybatisplus|2.0.7+         |1.6+ |  对 mybatis-plus 支持|
|sqlhelper-mybatisplus-spring-boot|2.0.7+ |1.8+ | 对 mybatis-plus 应用提供了 Spring Boot 快速启动|
|sqlhelper-tkmapper-spring-boot-starter|3.1.0+ |1.8+ | 对 tk.mybatis Mapper 应用提供了 Spring Boot 快速启动|
|sqlhelper-dbutils | 2.2.0            |1.6+| 对 Apache Commons-DBUtils 支持 (主要包括：通用分页) |
|sqlhelper-jsqlparser | 3.0.3          |1.6+| 对于要基于SQL Parser接口来完成的功能 由jsqlparser 这个库来实现|
|sqlhelper-mybatis-solon-plugin|3.2.0|1.8+| 支持solon 框架|
|sqlhelper-examples|1.0+               |1.8+ | 为这些工具提供 **测试用例** |


## tools usage
### 关键特性
1. 支持 MyBatis, MyBatis-Plus, SpringJdbc, Apache Commons-DBUtils, JFinal, EBean, Mango, Hibernate 等众多对数据库操作的框架 
2. 支持 110+ 数据库（所有功能均支持，不限于分页功能）, 支持的数据库列表参考： ***[here](https://github.com/f1194361820/sqlhelper/wiki/Pagination_Database)***. 如果你想了解这些数据库的排名，可以参考这里：[DB Engines](https://db-engines.com/en/ranking/relational+dbms).下面是所有支持的国产数据库:
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
  + SinoDB (星瑞格)
  + TDSQL (腾讯 分布式MySQL)
  + TiDB (北京平凯星辰科技))
  + Trafodion (易鲸捷 EsgynDB的开源版)
  + UxDB (优炫数据库)
3. 支持多个数据库在同一个应用中并存，且不需要做任何的区分，就能自动识别 
4. 支持自动获取数据库 dialect （方言），也可以指定
5. 性能要比**Mybatis-PageHelper**更高, 因为SQL中的 limit、offset 会以占位符 '?'的形式存在，支持缓存等，总之PageHelper支持的这里都支持，PageHelper不支持的这里也支持。
6. 对于 select count语句，会自动的排除掉 order by 子句，以提升查询效率 
7. 可以基于 Java SPI 规范来自定义Dialect，以此来扩展数据库
8. 分页功能支持**子查询**：mybatis, mybatis-plus, spring-jdbc, apache commons-dbutils
9. 支持 spring boot 1.x , 2.x
10. 支持 JDK6+
11. 支持 **Memory Pagination**
12. 支持 SqlSymbolMapper, 可以使用它来自动映射数据库字段、表名等
13. 支持 **Dump DDL**
14. 支持 **like parameter escape [%]** : mybatis, mybatis-plus

#### sqlhelper vs mybatis-pagehelper
|  metric                  | mybatis-pagehelper |      sqlhelper    |
|--------------------------|:------------------:|:-----------------:|
|  databases               |         13         |         100+      |
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
|  Spring JDBC             |         X          | 2.x,3.x,4.x,5.x   | 
|  SqlSymbol Mapping       |         X          |         √         |
|  MyBatis-Plus            |         X          |         √         |   
|  Apache Commons-DBUtils  |         X          |         √         |
|  Subquery pagination     |         X          |         √         |  
|  solon                   |         X          |         √         |
                     


# 分页工具使用说明
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
  + [MyBatis-Plus application](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_MyBatis_Plus)
  + [EBean application](https://github.com/fangjinuo/sqlhelper/wiki/Pagination_QuickStart_ebean)
* Advanced Usage

更多说明，参见教程：
```html
1. https://fangjinuo.gitee.io/docs/index.html
2. https://fangjinuo.github.io
```



# UrlParser
Parse jdbc url.
## usage:
<pre>
String url = "jdbc:mysql://${localhost}:${port}....";
DatabaseInfo dbinfo = new JdbcUrlParser().parse(url)
...
</pre> 

# Batch Insert Tool
 如果你想要进行业务SQL性能优化，又苦于数据库里没有太多数据，可以使用它来进行批量插入数据，大大的节省你造数据的时间。具体参加sqlhelper-batchinsert模块。

# [FAQ](https://github.com/fangjinuo/sqlhelper/wiki/FAQ)
这里提供了一些常见问题，对于刚使用SQLHelper时，可能对你很有帮助

# Contact
QQ 交流群: 750929088   
![QQ Group](https://github.com/fangjinuo/sqlhelper/blob/master/_images/qq_group.png)


##  [推广](https://github.com/fangjinuo)
+ langx 系列
    - [langx-js](https://github.com/fangjinuo/langx-js)：TypeScript, JavaScript tools
    - [langx-java](https://github.com/fangjinuo/langx-java): Java tools ，可以替换guava, apache commons-lang,io, hu-tool等
+ [easyjson](https://github.com/fangjinuo/easyjson): 一个通用的JSON库门面，可以无缝的在各个JSON库之间切换，就像slf4j那样。
+ [sqlhelper](https://github.com/fangjinuo/sqlhelper): SQL工具套件（通用分页、DDL Dump、SQLParser、URL Parser、批量操作工具等）。
+ [esmvc](https://github.com/fangjinuo/es-mvc): ElasticSearch 通用客户端，就像MyBatis Mapper那样顺滑
+ [redisclient](https://github.com/fangjinuo/redisclient): 基于Spring RestTemplate提供的客户端
+ [audit](https://github.com/fangjinuo/audit)：通用的Java应用审计框架

## 鸣谢
最后，感谢 Jetbrains 提供免费License，方便了开源项目的发展。

[![Jetbrains](https://github.com/fangjinuo/sqlhelper/blob/master/_images/jetbrains.png)](https://www.jetbrains.com/zh-cn/)
