# FAQ

**1. Sqlhelper 各个包的作用**
```
  * sqlhelper-common sqlhelper整个项目的基础模块
  * sqlhelper-dialect 核心类库，里面有分页功能、URL解析、SQL解析、OrderBy构造等，后续还会补充其他功能。支持近100+数据库
  * sqlhelper-mybatis 支持 mybatis，目前提供了分页功能
  * sqlhelper-mybatis-spring-boot-starter, sqlhelper-mybatis-spring-boot-autoconfigure 支持SpringBoot环境下使用sqlhelper-mybatis
  * sqlhelper-mybatis-over-pagehelper 用于无缝迁移 mybatis-pageHelper应用到sqlhelper的
  * sqlhelper-springjdbc 支持SpringJDBC 分页
  * sqlhelper-springjdbc-spring-jdbc 支持SpringBoot 环境下使用sqlhelper-springjdbc
  * sqlhelper-batchinsert 支持批量插入数据库，为了SQL性能优化、稳定性测试等
  * sqlhelper-jfinal 支持国产ORM框架jfinal
  * sqlhelper-ebean 支持小众ORM框架ebean
  * sqlhelper-dbutils 支持Apache Commons-DBUtils 
  * sqlhelper-mango 支持小众ORM 框架mango
  * sqlhelper-mybatisplus 支持使用了mybatisplus的应用
  * sqlhelper-mybatisplus-spring-boot 支持在spring boot 环境下的mybatisplus应用
  * sqlhelper-cli 提供DDL dump等命令行工具
```
从这些包的作用上，我们就可以知道如何选择相关的依赖了

**2. 启动时出现Can't find any supported JSON libraries : [gson, jackson, fastjson]**
```
因为sqlhelper依赖了easyjson, easyjson 是一款用于适配各个JSON库的工具，
easyjson会将任何一个JSON库适配到fastjson, gson, jackson之一。
所以你需要在classpath下加入 :
（fastjson, easyjson-fastjson ）或者（gson, easyjson-fastjson）或者（jackson, easyjson-jackson）

如果你加了相关的包，仍然有这个错误，请确认sqlhelper的依赖项目（easyjson, langx） 版本是否匹配，他们三个基本上是同步升级的。所以版本需要对应上。
```

**3. sqlhelper-example 启动失败**
```
sqlhelper—example 用到了H2内存数据库来测试相关功能，使用前查看examples的配置保证h2可用
```

**4. 不能正常识别你的数据库时的解决方案**

sqlhelper 是自动识别你的数据库的.
```
MyBatis应用的识别顺序：
1) mybatis 语句里的 databaseId
2) 配置项 sqlhelper.mybatis.instrumentor.dialect=${databaseId} ，无默认值
3) mybatis 自动识别的 databaseId。  
mybatis 是根据 DatabaseIdProvider来从JDBC 驱动获取的，本质是 通过DatabaseMetaData.getDatabaseProductName() 
来映射到定义的一个databaseId来完成的。
```
MyBatis自动识别databaseId的方案 参考：[Mybatis databaseIdProvider](https://mybatis.org/mybatis-3/configuration.html#databaseIdProvider)

```
Spring-JDBC 应用的识别顺序：
1) 配置项 sqlhelper.springjdbc.instrumentor.dialect=${databaseId} ，无默认值
2) 根据数据库DatabaseMetaData自动识别的 databaseId。 
```

无论是SpringJDBC应用，还是mybatis应用，还是其他应用（apache commons-dbutils, jfinal, ebean, mango等），如果需要自动指定databaseId， 可以根据 [BuitIn-databaseId](https://github.com/fangjinuo/sqlhelper/blob/master/sqlhelper-dialect/src/main/resources/sqlhelper-dialect-databaseid.properties)中的配置项的value来指定

**5. 启动时提示：Can't find a suitable or enabled SQL instrumentation**
从3.0.3 版本开始，做了调整，需要手动引入 sqlhelper-jsqlparser, 在后续版本中，会内置引入。


**6. Spring Boot环境下，自定义SqlSessionFactory后，功能失败。

首先告诉这种情况下失败的原因：启动时是需要配置一些特定的类到SqlSessionFactory中的，一旦你自定义了，但又没有配置相关的类，就会出错。

如果是Spring Boot环境下，根本没有必要自己去冲定义 SqlSessionFactory，因为 mybatis spring boot autoconfigure中提供了一个很方便的类： ConfigurationCustomizer

