# 从PageHelper迁移到SQLHelper

## installation

1. 移除 pagehelper 依赖

2. 加入 sqlhelper 迁移依赖
```xml
  <dependency>
    <groupId>com.github.fangjinuo.sqlhelper</groupId>
    <artifactId>sqlhelper-mybatis-over-pagehelper</artifactId>
    <version>${project.version}</version>
  </dependency>
```

3. 其他的依赖请参考安装向导

