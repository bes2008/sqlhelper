package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.SqlCompatibilityType;
import com.jn.sqlhelper.dialect.internal.limit.SkipLimitHandler;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;

/**
 * https://www.it610.com/article/1281786034576703488.htm
 */
@Name("gbase8s")
public class GBase8sDialect extends AbstractDialect {
    public GBase8sDialect() {
        setLimitHandler(new SkipLimitHandler());
        setLikeEscaper(new BackslashStyleEscaper(true));
        setSqlCompatibilityTypes(SqlCompatibilityType.MYSQL, SqlCompatibilityType.ORACLE);
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
    public SqlCompatibilityType getDefaultSqlCompatibilityType() {
        return SqlCompatibilityType.ORACLE;
    }
}
