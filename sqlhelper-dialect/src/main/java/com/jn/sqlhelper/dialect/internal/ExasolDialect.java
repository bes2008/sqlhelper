package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.annotation.Driver;
import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 *
 * https://docs.exasol.com/db/latest/sql/select.htm
 *
 *
 * limit $offset, $count
 * limit $count offset $offset
 */
@Name("exa")
@Driver("com.exasol.jdbc.EXADriver")
public class ExasolDialect extends AbstractDialect {

    public ExasolDialect() {
        super();
        setLimitHandler(new LimitCommaLimitHandler());
        setLikeEscaper(BackslashStyleEscaper.INSTANCE);
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
    public boolean isSupportsBatchUpdates() {
        return true;
    }

    @Override
    public boolean isSupportsBatchSql() {
        return true;
    }
}