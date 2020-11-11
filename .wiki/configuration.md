# Configuration

### 1、Instrumentator configurations

+ dialect: 数据库dialect, 如果SQLHelper不能找到正确的dialect，配置它即可
+ dialectClassName: 配置你自定义的dialect 类
+ cacheInstrumentedSql: 是否缓存修改后的SQL，默认false
+ cacheInitialCapacity: 缓存初始容量，默认1000
+ cacheMaxCapacity: 缓存最大容量，默认Integer.MAX
+ cacheExpireAfterRead: 缓存最大时间，默认 300s
+ subqueryPagingStartFlag: 子查询分页的开始标记 ,默认值为： [PAGING_START]
+ subqueryPagingEndFlag: 子查询分页的结束标记 ,默认值为： [PAGING_END]
+ extractDialectUseNativeEnabled: 是否根据真实的数据源自动判断
+ escapeLikeParameter: 是否启用like 参数转义，默认false

### 2、pagination plugin common configurations:
(you can find out com.jn.sqlhelper.dialect.pagination.PaginationProperties from source)

+ count : 执行分页查询时，是否查询count
+ defaultPageSize: pageSize的默认值
+ useLastPageIfPageOut: 当 pageNo > maxPages （即当你查询的页码大于最大的页码时，）是否使用最后一页的数据