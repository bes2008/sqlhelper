package com.jn.sqlhelper.dialect.instrument;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.orderby.OrderByItem;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;

public class SimpleOrderByInstrumentor implements OrderByInstrumentor<String>{
    @Override
    public void instrument(SqlStatementWrapper<String> sw, InstrumentConfig config) {
        String sql = sw.get();
        OrderBy orderBy = config.getOrderBy();
        sql = instrumentOrderByUsingStringAppend(sql, orderBy);
        sw.setStatement(sql);
        sw.setChanged(true);
    }

    private static String instrumentOrderByUsingStringAppend(String sql, final OrderBy orderBy) {
        final StringBuilder builder = new StringBuilder(sql);
        builder.append(" ORDER BY ");
        Collects.forEach(Collects.asList(orderBy), new Consumer2<Integer, OrderByItem>() {
            @Override
            public void accept(Integer index, OrderByItem orderByItem) {
                if (index > 0) {
                    builder.append(",");
                }
                builder.append(" ").append(orderByItem.getExpression()).append(" ").append(orderByItem.getType().name());
            }
        });
        return builder.toString();
    }
}
