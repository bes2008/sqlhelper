欢迎您来了解SQLHelper

SQLHelper提供了如下功能:
  1) Pagination （通用的分页插件，可应用于大部分数据库开发框架里）
  2) URL Parser （用于解析连接数据库的url）
  3) DDL Dumper （dump数据库的DDL，可以用于软件版本升级时验证SQL升级脚本的正确性，以及其他场景）
  4) Batch Inserter （批量插入框架，用于做性能优化时批量插入数据）

# 模块说明
  |   module         | sqlhelper version | JDK |  Description      |
  |------------------|-------------------|-----|-------------------|
  | sqlhelper-common | 2.3.0+            |1.6+ |通用模块          |
  | sqlhelper-dialect| 1.0+              |1.6+ |方言模块，所有数据库的支持都在这个模块完成 |
  | sqlhelper-batchinsert |1.2+          |1.6+ |批量插入工具框架     | 
  | sqlhelper-cli    | 2.3.1             |1.8+ |提供数据库表DDL相关的一些命令 |
  | sqlhelper-cli-assembly| 2.3.1        |1.8+ |打包sqlhelper-cli 为一个zip包，并提供startup, shutdown 脚本|
  | sqlhelper-ebean | 1.2+               |1.6+ |为EBean应用提供SQL分页功能|
  | sqlhelper-mango | 1.2+               |1.6+ |为Mango应用提供SQL分页功能|
  | sqlhelper-jfinal| 1.2+               |1.6+ |为JFinal应用提供SQL分页功能|
  | sqlhelper-mybatis| 1.0+              |1.6+ |为mybatis 3.x应用提供SQL分页功能|
  | sqlhelper-mybatis-solon-plugin| 3.1+ |1.8+ |为mybatis 3.x应用提供Solon 的扩展插件|
  | sqlhelper-mybatis-spring-boot|1.0+   |1.8+ | 为mybaits 3.x应用提供Spring Boot启动方式|
  | sqlhelper-mybatis-over-pagehelper|1.0+|1.6+| 提供将现有的使用了pagehelper业务系统为无缝的迁移到sqlhelper来|
  | sqlhelper-mybatisplus|2.0.7+         |1.6+ | 为mybatis-plus应用提供SQL分页功能|
  | sqlhelper-mybatisplus-spring-boot|2.0.7+|1.8+| 为mybatis-plus应用提供Spring Boot启动方式|
  | sqlhelper-springjdbc|2.0.2+          |1.6+ | 为Spring JDBC应用提供SQL分页功能, 支持Spring2.x,3.x,4.x,5.x|
  | sqlhelper-springjdbc-spring-boot|2.0.2+|1.8+| 为Spring JDBC应用提供Spring Boot启动方式|
  | sqlhelper-examples|1.0+              |1.8+ | 测试用例|

# Pagination

### 关键特性
  + 可以与多种数据库开发框架结合使用，目前已支持：mybatis, mybatis-plus, spring-jdbc, apache commons-dbutils, ebean, jfinal, mango等  
  + 自动识别dialect，支持统一应用里使用多个数据库
  + 0 配置
  + JDK6+
  + 支持物理分页、内存分页
  + 采用SPI机制支持自定义扩展
  + 支持子查询分页（目前支持mybatis, mybatis-plus, spring-jdbc, apache commons-dbutils）
  + 支持分页条件中自定义 order by 
  + 支持从 PageHelper 无缝迁移
  + 支持[110+ 数据库](supported_dbs.md)，且支持绝大部分国产数据库。想要了解数据库排名，可以在[DB Engines](https://db-engines.com/en/ranking/relational+dbms)查看
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

### 使用说明
  像这样使用即可：
```
    @GetMapping
    public PagingResult list(){
        User queryCondtion = new User();
        queryCondtion.setAge(10);
   
        PagingRequest request = SqlPaginations.preparePagination(1,10);
        List<User> users = userDao.selectByLimit(queryCondtion); // users is the data list        
        return request.getResult(); // result has: total, pagesize, pageNo, maxPage ...
    }
```
  
### VS mybatis-PageHelper
|  metric                  | mybatis-pagehelper |      sqlhelper    |
|--------------------------|:------------------:|:-----------------:|
|  databases               |         10+        |         110+      |
|  multiple databases in runtime |   √          |         √         |
|  auto detect dialect     |         √          |         √         |
|  plugin                  |         √          |         √         |
|  PrepareStatement with '?'|        √          |         √         |                             
|  mybatis                 |         3.x        |         3.x       |
|  spring boot             |         1.x, 2.x   |         1.x, 2.x  |
|  JDK                     |         1.6+       |         1.6+      |
|  jFinal                  |         X          |         √         |
|  Mango                   |         X          |         √         |
|  EBean                   |         X          |         √         | 
|  国产数据库               |         少量        |绝大部分（参见上述列表）|
|  Spring JDBC             |         X          | 2.x,3.x,4.x,5.x   | 
|  SqlSymbol Mapping       |         X          |         √         |
|  MyBatis-Plus            |         X          |         √         |   
|  Apache Commons-DBUtils  |         X          |         √         |
|  Subquery pagination     |         X          |         √         |  


# UrlParser

# DDL Dump
   1. 解压
      ```sh
      unzip sqlhelper-cli-assembly/target/sqlhelper-cli.zip
      ```
   2. 启动
      ```sh
      cd sqlhleper-cli/bin
      ./startup.sh 或 startup.bat
      ```
   3. 注册连接
      ```sh
      jdbc create --name test --driver org.h2.Driver --username sa --url jdbc:h2:~/test
      ```
   4. 列出所有的表:
      ```
      show tables --connection-name test
      ```
# [FAQ](https://github.com/fangjinuo/sqlhelper/wiki/FAQ)