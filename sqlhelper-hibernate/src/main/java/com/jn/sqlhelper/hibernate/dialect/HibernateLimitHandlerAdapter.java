package com.jn.sqlhelper.hibernate.dialect;

import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.internal.AbstractDialect;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.engine.spi.RowSelection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * @since 3.6.1
 */
class HibernateLimitHandlerAdapter implements LimitHandler {
    private Dialect sqlhelper;

    HibernateLimitHandlerAdapter(AbstractDialect dialect) {
        this.sqlhelper = dialect;
    }

    @Override
    public boolean supportsLimit() {
        return sqlhelper.isSupportsLimit();
    }

    @Override
    public boolean supportsLimitOffset() {
        return sqlhelper.isSupportsLimitOffset();
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        return sqlhelper.getLimitHandler().processSql(sql, HibernateSqlHelpers.toSqlHelperRowSelection(selection));
    }

    @Override
    public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException {
        return sqlhelper.bindLimitParametersAtStartOfQuery(HibernateSqlHelpers.toSqlHelperRowSelection(selection), statement, index);
    }

    @Override
    public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException {
        return sqlhelper.bindLimitParametersAtEndOfQuery(HibernateSqlHelpers.toSqlHelperRowSelection(selection), statement, index);
    }

    @Override
    public void setMaxRows(RowSelection selection, PreparedStatement statement) throws SQLException {
        sqlhelper.setMaxRows(HibernateSqlHelpers.toSqlHelperRowSelection(selection), statement);
    }
}
