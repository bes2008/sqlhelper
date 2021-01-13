# 动态数据源

## 开发计划

+ [ ] 通用框架
    + [X] 连接池实现
        + [X] 支持常见连接池：HikariCP, Alibaba Druid, tomcat-jdbc, dbcp2, c3p0 
        + [X] 支持在配置中使用哪个连接池实现
        + [X] 支持所用连接池实现的特有配置项
    + [X] DataSource 配置   
        + [X] 支持分组配置，通常一个业务群一个分组，或者一个数据库集群用一个分组
        + [X] 兼容 SpringBoot 默认 DataSource 配置
        + [X] 支持多种异构数据源
    + [X] DataSource 识别
        + [X] 提供 @DataSource 注解，可以在 method, class 级别使用
        + [X] 支持 自定义注解。
        + [X] 支持多层级 注解配置。筛选时，按照最近优先原则
        + [X] 支持自定义识别算法。例如可以基于此自行实现负载均衡，读写分离，随机等众多识别算法。
        + [X] 支持多层嵌套。
        + [X] 可自定义AOP拦截位置。
    + [ ] Router
        + [X] 路由算法
            + [X] 随机路由
            + [X] 读写分离路由
            + [X] 负载均衡路由
        + [ ] 故障转移
+ [ ] 框架支持
    + [X] mybatis & spring boot 应用
    + [X] mybatis 2.x, 3x & spring boot 应用
    + [X] tk.mapper & spring boot 应用
    + [ ] spring jdbc
    + [ ] apache DBUtils
    + [ ] jfinal  


## 核心概念

### 1. DataSourceKey
```text
既然是建设并使用多个数据源，那么核心问题自然是对多个数据源的筛选、使用上。  所以需要对数据源进行命名，以进行区分。
又因为程序面临的业务场景是复杂多样的，不同的业务可能会用到不同的数据库，或者相同的应用也会用到不同的数据库，所以需要对数据源进行分组处理。
```
 
DataSourceKey 类的设计：
```java
public class DataSourceKey implements Node {
    @NonNull
    private String group = DataSources.DATASOURCE_PRIMARY_GROUP;
    @NonNull
    private String name;

    public DataSourceKey() {
    }


    public DataSourceKey(String group, String name) {
        setGroup(group);
        setName(name);
    }
}
```

### 2. DataSourceKeySelector

接口定义：
```java
public interface DataSourceKeySelector<I> {
    DataSourceKey select(I input);
}
```
接口本身很简单，根据输入进行筛选，选出适当的 DataSourceKey。

它有一个核心实现类：MethodInvocationDataSourceKeySelector 。这个实现的入参是MethodInvocation，也就是在运行时，会扫描
相关注解 @DataSource (你也可以自定注解，获取其他方式)。该注解支持在方法上，类上使用，采用就近原则。


最近原则：
```text
由于存在嵌套调用的场景， 所以对于扫描到的注解，内部会维护一个 stack ，会一个个的入栈，方法调用完毕后出栈。
```

### 3. 事务管理

关于事务管理，内部提供了一套，只保证多个数据源下的本地事务OK，不处理分布式调用下的多数据源OK。

有关事务管理器的选择，请遵循如下原则：

1）业务系统是单实例的，并且是单数据源： 使用Spring的DataSourceTransactionManager即可
2）业务系统是单实例的，并且是多数据源： 可以采用 sqlhelper 提供的事务管理
3）业务系统是分布式的，且需要保证各个实例间的事务的一致性：采用 seata, Hmily, roketmq, 等等
4）业务系统是分布式的，各个实例间的事务不是紧耦合的：可以采用 sqlhelper 提供的事务管理



## Configuration

```yaml
sqlhelper:
  dynamic-datasource: # 该节点下为动态数据源的配置
    enabled: true # 动态数据源的开关
    datasources:
      - group: primary # {required} the group
        name: master # {required} the name 
        implementation: # {optional} the datasource pool implementation, options: c3p0, dbcp2, druid, hikaricp, tomcat
        primary: true|false # {optional}
      
        driver-class-name: org.h2.Driver
        url: jdbc:h2:file:${user.dir}/sqlhelper-examples/sqlhelper-examples-db/src/main/resources/test
        username: sa
        password: 123456 
      - group: primary # {required} the group
        name: slave-1 # {required} the name 
        implementation: # {optional} the datasource pool implementation, options: c3p0, dbcp2, druid, hikaricp, tomcat
    
        driver-class-name: org.h2.Driver
        url: jdbc:h2:file:${user.dir}/sqlhelper-examples/sqlhelper-examples-db/src/main/resources/test
        username: sa
        password: 123456 
    groups: # {optional} 用于对组进行一些自定义的配置
      - name: primary
        write-pattern: save*;insert*;add*;delete*;remove*;update*;modify*; # 配置那些方法名是写操作，对于写操作会自动选择组内的 master，读操作会采用负载算法。
    key-choices: # {optional} 用于定义可以从哪些类、方法上来提取@DataSource注解等，其中expression是Spring集成 Aspectj时，配置的基于 expression 的pointcut
      expression:
    transaction: # {optional} 用于定义可以从哪些类、方法上会启用 @Transactional 注解，可以用Spring 的@Transactional注解，也可以用 SQLHelper的 @Transactional 注解
      expression: execution(public * com.jn.sqlhelper.examples.mybatis.controller.*Controller.*(..))
```


 





    