package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.common.ddl.SQLSyntaxCompatTable;
import com.jn.sqlhelper.dialect.SqlCompatibilityType;
import com.jn.sqlhelper.dialect.annotation.SyntaxCompat;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * http://www.highgo.com/content.php?catid=75
 * based on PostgreSQL
 */
@Name("highgo")
@SyntaxCompat(SQLSyntaxCompatTable.POSTGRESQL)
public class HighGoDialect extends AbstractDialect {
    public HighGoDialect() {
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
        setSqlCompatibilityTypes(SqlCompatibilityType.ORACLE, SqlCompatibilityType.MYSQL,SqlCompatibilityType.POSTGRESQL);
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
