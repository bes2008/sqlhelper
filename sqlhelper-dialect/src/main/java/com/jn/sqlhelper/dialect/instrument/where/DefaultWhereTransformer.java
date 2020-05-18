package com.jn.sqlhelper.dialect.instrument.where;

import com.jn.sqlhelper.dialect.instrument.AbstractClauseTransformer;
import com.jn.sqlhelper.dialect.instrument.TransformConfig;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;

/**
 * @author huxiongming
 */
public class DefaultWhereTransformer extends AbstractClauseTransformer implements WhereTransformer {
    @Override
    public SqlStatementWrapper transform(SqlStatementWrapper statement, TransformConfig config) {
        WhereTransformer whereTransformer = getInstrumentation().getWhereTransformer();
        return whereTransformer.transform(statement, config);
    }
}
