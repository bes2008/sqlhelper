package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.SqlCompatibilityType;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * 在使用 DM数据库时，通常要关闭大小写敏感
 */
public class DmDialect extends AbstractDialect {
    public DmDialect() {
        setLimitHandler(new LimitOffsetLimitHandler());
        setSqlCompatibilityTypes(SqlCompatibilityType.ORACLE);
        setSqlCompatibilityTypes(SqlCompatibilityType.SQL92, SqlCompatibilityType.ORACLE, SqlCompatibilityType.MYSQL, SqlCompatibilityType.SQLSERVER, SqlCompatibilityType.TERADATA, SqlCompatibilityType.NON_COMPATIBILITY);
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
        return IdentifierCase.UPPER_CASE;
    }

    @Override
    public SqlCompatibilityType getDefaultSqlCompatibilityType() {
        return SqlCompatibilityType.ORACLE;
    }
}
