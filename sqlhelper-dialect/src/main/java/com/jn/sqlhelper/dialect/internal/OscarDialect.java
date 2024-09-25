
package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.SqlCompatibilityType;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;
import com.jn.sqlhelper.dialect.urlparser.OscarUrlParser;

public class OscarDialect extends AbstractDialect {
    public OscarDialect() {
        setLimitHandler(new LimitOffsetLimitHandler());
        setSqlCompatibilityTypes(SqlCompatibilityType.POSTGRESQL);
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
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public SqlCompatibilityType getDefaultSqlCompatibilityType() {
        return SqlCompatibilityType.ORACLE;
    }
}
