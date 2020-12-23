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
        + [X] 支持多层级 注解配置。筛选时，安装最近优先原则
        + [X] 支持自定义识别算法。例如可以基于此自行实现负载均衡，读写分离，随机等众多识别算法。
        + [X] 支持多层嵌套。
        + [X] 可自定义AOP拦截位置。
    + [ ] Router
        + [ ] 随机路由
        + [ ] 粘性会话路由
        + [ ] 读写分离路由
        + [ ] 负载均衡路由
+ [ ] 框架支持
    + [X] mybatis & spring boot 应用
    + [X] mybatis 2.x, 3x & spring boot 应用
    + [X] tk.mapper & spring boot 应用
    + [ ] spring jdbc
    + [ ] apache DBUtils
    + [ ] jfinal  