package com.github.fangjinuo.sqlhelper.mango;

import com.github.fangjinuo.sqlhelper.dialect.RowSelection;
import org.jfaster.mango.binding.BoundSql;
import org.jfaster.mango.interceptor.Parameter;
import org.jfaster.mango.interceptor.QueryInterceptor;
import org.jfaster.mango.mapper.SingleColumnRowMapper;
import org.jfaster.mango.plugin.page.Page;
import org.jfaster.mango.plugin.page.PageException;
import org.jfaster.mango.transaction.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

public class MangoCommonPagingInterceptor extends QueryInterceptor {
    @Override
    public void interceptQuery(BoundSql boundSql, List<Parameter> parameters, DataSource dataSource) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        DatabaseMetaData databaseMetaData = null;
        try {
            databaseMetaData = conn.getMetaData();
        }catch (Throwable ex){
            return;
        }
        if(!MangoPagingContext.instrumentor.beginIfSupportsLimit(databaseMetaData)){
            return;
        }
        Page page = findPageRequest(parameters);
        if(page==null){
            return;
        }
        // 参数检测
        int pageNum = page.getPageNum();
        int pageSize = page.getPageSize();
        if (pageNum < 0) {
            throw new PageException("pageNum need >= 0, but pageNum is " + pageNum);
        }
        if (pageSize < 0) {
            throw new PageException("pageSize need >= 0, but pageSize is " + pageSize);
        }

        if(page.isFetchTotal()){
            String countSql = MangoPagingContext.instrumentor.countSql(boundSql.getSql());
            BoundSql countBoundSql = boundSql.copy();
            countBoundSql.setSql(countSql);
            SingleColumnRowMapper<Integer> mapper = new SingleColumnRowMapper<Integer>(int.class);
            int total = getJdbcOperations().queryForObject(dataSource, countBoundSql, mapper);
            page.setTotal(total);
        }

        int offset = pageNum<=0?0:(pageNum-1)*pageSize;
        RowSelection rowSelection = new RowSelection();
        rowSelection.setLimit(pageSize);
        rowSelection.setOffset(offset);
        boundSql.setSql(MangoPagingContext.instrumentor.instrumentSql(boundSql.getSql(), rowSelection));
        MangoPagingContext.pagingRequest.set(rowSelection);
    }

    private Page findPageRequest(List<Parameter> parameters) {
        for (Parameter parameter : parameters) {
            if (Page.class.equals(parameter.getRawType())) {
                Object val = parameter.getValue();
                if (val == null) {
                    throw new PageException("Parameter page is null");
                }
                return (Page) val;
            }
        }
        return null;
    }
}
