1、引入依赖

```xml
<dependency>
    <groupId>com.github.fangjinuo.sqlhelper</groupId>
    <artifactId>sqlhelper-jfinal</artifactId>
    <version>${sqlhelper.version}</version>
</dependency>
```

2、启动配置：

```text
/**
 * 配置插件
 */
public void configPlugin(Plugins me) {
    // 配置 druid 数据库连接池插件
    DruidPlugin druidPlugin = new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password").trim());
    me.add(druidPlugin);
    
    // 配置ActiveRecord插件
    ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
    arp.setDialect(new JFinalCommonDialect("h2"));
    // 所有映射在 MappingKit 中自动化搞定
    _MappingKit.mapping(arp);
    me.add(arp);
}
```