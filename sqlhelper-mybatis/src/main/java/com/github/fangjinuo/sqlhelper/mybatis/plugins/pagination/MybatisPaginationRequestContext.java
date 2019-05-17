package com.github.fangjinuo.sqlhelper.mybatis.plugins.pagination;

import com.github.fangjinuo.sqlhelper.dialect.pagination.PagingRequestContext;
import org.apache.ibatis.mapping.BoundSql;

public class MybatisPaginationRequestContext extends PagingRequestContext {
    private BoundSql countSql;
    private BoundSql querySql;

    public BoundSql getCountSql() {
        return countSql;
    }

    public void setCountSql(BoundSql countSql) {
        this.countSql = countSql;
    }

    public BoundSql getQuerySql() {
        return querySql;
    }

    public void setQuerySql(BoundSql querySql) {
        this.querySql = querySql;
    }
}
