package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * https://docs.qingcloud.com/product/database_cache/radondb/supported_sql.html#1-select-%E8%AF%AD%E5%8F%A5
 */
@Name("radon")
public class RadonDBDialect extends AbstractDialect {

    public RadonDBDialect() {
        super();
        setLimitHandler(new LimitCommaLimitHandler());
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
        return col;
    }

    @Override
    public char getBeforeQuote() {
        return '`';
    }

    @Override
    public char getAfterQuote() {
        return '`';
    }
}
