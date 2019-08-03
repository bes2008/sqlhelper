package com.jn.sqlhelper.mybatis.plugins.pagination;

import com.jn.sqlhelper.dialect.pagination.PagingRequestContext;
import org.apache.ibatis.mapping.BoundSql;

public class MybatisPaginationRequestContext extends PagingRequestContext {
    private BoundSql countSql;
    private String querySqlId;

    public BoundSql getCountSql() {
        return countSql;
    }

    public void setCountSql(BoundSql countSql) {
        this.countSql = countSql;
    }

    public String getQuerySqlId() {
        return querySqlId;
    }

    public void setQuerySqlId(String querySqlId) {
        this.querySqlId = querySqlId;
    }
}
