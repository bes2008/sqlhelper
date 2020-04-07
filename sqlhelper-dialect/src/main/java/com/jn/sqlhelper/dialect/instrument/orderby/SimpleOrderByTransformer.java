package com.jn.sqlhelper.dialect.instrument.orderby;

import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.sqlhelper.dialect.instrument.TransformConfig;
import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.orderby.OrderByItem;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;

public class SimpleOrderByTransformer implements OrderByTransformer<String> {
    @Override
    public void init() throws InitializationException {
    }

    @Override
    public final boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isTransformable(SqlStatementWrapper<String> statementWrapper) {
        Object statement =  statementWrapper.get();
        if(!(statement instanceof String)){
            return false;
        }
        return Strings.isNotBlank(statement.toString());
    }

    @Override
    public SqlStatementWrapper transform(SqlStatementWrapper<String> sw, TransformConfig config) {
        String sql = sw.get();
        OrderBy orderBy = config.getOrderBy();
        sql = instrumentOrderByUsingStringAppend(sql, orderBy);
        sw.setStatement(sql);
        sw.setChanged(true);
        return sw;
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
