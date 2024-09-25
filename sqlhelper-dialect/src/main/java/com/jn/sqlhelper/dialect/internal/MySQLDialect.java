package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.SqlCompatibilityType;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;
import com.jn.sqlhelper.dialect.urlparser.MySqlUrlParser;
import com.jn.sqlhelper.dialect.scriptfile.MySqlScriptParser;

import java.sql.CallableStatement;
import java.sql.SQLException;

public class MySQLDialect extends AbstractDialect {

    public MySQLDialect() {
        super();
        setLimitHandler(new LimitCommaLimitHandler());
        setLikeEscaper(BackslashStyleEscaper.INSTANCE);
        setSqlCompatibilityTypes(SqlCompatibilityType.MYSQL);
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
    public boolean isSupportsVariableLimitInSubquery() {
        return false;
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

    @Override
    public boolean isSupportsBatchUpdates() {
        return true;
    }

    @Override
    public boolean isSupportsBatchSql() {
        return true;
    }

    @Override
    public SqlCompatibilityType getDefaultSqlCompatibilityType() {
        return SqlCompatibilityType.MYSQL;
    }
}
