package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.SqlCompatibilityType;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

public class KingbaseDialect extends AbstractDialect {
    public KingbaseDialect() {
        setLimitHandler(new LimitOffsetLimitHandler());
        setSqlCompatibilityTypes(SqlCompatibilityType.ORACLE, SqlCompatibilityType.POSTGRESQL, SqlCompatibilityType.MYSQL);
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }

    @Override
    public IdentifierCase unquotedIdentifierCase() {
        return IdentifierCase.LOWER_CASE;
    }

    @Override
    public SqlCompatibilityType getDefaultSqlCompatibilityType() {
        return SqlCompatibilityType.ORACLE;
    }
}
