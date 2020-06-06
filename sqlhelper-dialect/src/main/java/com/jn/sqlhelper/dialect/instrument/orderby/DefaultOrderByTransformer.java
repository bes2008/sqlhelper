package com.jn.sqlhelper.dialect.instrument.orderby;

import com.jn.sqlhelper.dialect.instrument.AbstractClauseTransformer;
import com.jn.sqlhelper.dialect.instrument.TransformConfig;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import com.jn.sqlhelper.dialect.sqlparser.StringSqlStatementWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 是自定义 OrderByTransformer 的代理，优先使用自定义的 OrderByTransformer 进行SQL转换，如果找不到自定义的 OrderByTransformer，或者自定义的 OrderByTransformer
 * 转换失败，则会自动采用 SimpleOrderByTransformer 进行转换
 */
@SuppressWarnings("rawtypes")
public class DefaultOrderByTransformer extends AbstractClauseTransformer implements OrderByTransformer {
    private final SimpleOrderByTransformer simpleTransformer = new SimpleOrderByTransformer();
    private static final Logger logger = LoggerFactory.getLogger(DefaultOrderByTransformer.class);

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
            logger.debug(ex.getMessage(), ex);
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
