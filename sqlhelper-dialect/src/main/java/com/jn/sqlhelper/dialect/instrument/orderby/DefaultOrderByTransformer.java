package com.jn.sqlhelper.dialect.instrument.orderby;

import com.jn.sqlhelper.dialect.instrument.AbstractClauseTransformer;
import com.jn.sqlhelper.dialect.instrument.TransformConfig;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import com.jn.sqlhelper.dialect.sqlparser.StringSqlStatementWrapper;

@SuppressWarnings("rawtypes")
public class DefaultOrderByTransformer extends AbstractClauseTransformer implements OrderByTransformer {
    private final SimpleOrderByTransformer simpleTransformer = new SimpleOrderByTransformer();


    @Override
    protected void doInit() {

    }

    @Override
    public SqlStatementWrapper transform(final SqlStatementWrapper statement, final TransformConfig config) {
        OrderByTransformer orderByTransformer = null;
        try {
            orderByTransformer = getInstrumentation().getOrderByTransformer();
            if (orderByTransformer != null) {
                return orderByTransformer.transform(statement, config);
            }
        } catch (Throwable ex) {

        }
        if (!simpleTransformer.isTransformable(statement)) {
            SqlStatementWrapper<String> sw = new StringSqlStatementWrapper();
            sw.setOriginalSql(statement.getOriginalSql());
            sw.setChanged(statement.isChanged());
            sw.setStatement(statement.getSql());
            return simpleTransformer.transform(sw, config);
        } else {
            return simpleTransformer.transform(statement, config);
        }
    }
}
