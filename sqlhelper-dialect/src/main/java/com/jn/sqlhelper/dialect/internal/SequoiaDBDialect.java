package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.SqlCompatibilityType;
import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;

/**
 * http://doc.sequoiadb.com/cn/sequoiadb-cat_id-1432190960-edition_id-0
 * <p>
 * support mysql sql syntax
 * support PostgreSQL sql syntax
 */
public class SequoiaDBDialect extends AbstractDialect {
    public SequoiaDBDialect() {
        super();
        setLimitHandler(new LimitCommaLimitHandler());
        setSqlCompatibilityTypes(SqlCompatibilityType.MYSQL,SqlCompatibilityType.POSTGRESQL);
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public SqlCompatibilityType getDefaultSqlCompatibilityType() {
        return SqlCompatibilityType.POSTGRESQL;
    }
}
