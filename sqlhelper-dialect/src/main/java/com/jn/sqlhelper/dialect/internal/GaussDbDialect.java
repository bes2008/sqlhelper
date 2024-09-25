package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.common.ddl.SQLSyntaxCompatTable;
import com.jn.sqlhelper.dialect.SqlCompatibilityType;
import com.jn.sqlhelper.dialect.annotation.SyntaxCompat;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

@Name("gaussdb")
@SyntaxCompat(SQLSyntaxCompatTable.POSTGRESQL)
public class GaussDbDialect extends AbstractDialect {
    public GaussDbDialect() {
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
        setSqlCompatibilityTypes(SqlCompatibilityType.TERADATA,SqlCompatibilityType.MYSQL,SqlCompatibilityType.ORACLE,SqlCompatibilityType.POSTGRESQL);
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
    public SqlCompatibilityType getDefaultSqlCompatibilityType() {
        return SqlCompatibilityType.POSTGRESQL;
    }
}
