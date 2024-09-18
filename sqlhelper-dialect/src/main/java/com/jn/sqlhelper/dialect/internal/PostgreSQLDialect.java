package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.scriptfile.PostgreSQLScriptParser;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;
import com.jn.sqlhelper.dialect.urlparser.PostgreSQLUrlParser;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * https://www.postgresql.org/docs/current/queries-limit.html
 */
@Name("postgresql")
public class PostgreSQLDialect extends AbstractDialect {

    public PostgreSQLDialect() {
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
        setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);
    }

    @Override
    public IdentifierCase unquotedIdentifierCase() {
        return IdentifierCase.LOWER_CASE;
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
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col)
            throws SQLException {
        statement.registerOutParameter(col++, 1111);
        return col;
    }


}
