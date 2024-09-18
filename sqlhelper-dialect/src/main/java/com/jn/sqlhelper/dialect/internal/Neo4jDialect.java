package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.internal.limit.SkipLimitHandler;

@Name("neo4j")
public class Neo4jDialect extends AbstractDialect {
    public Neo4jDialect() {
        super();
        setLimitHandler(new SkipLimitHandler("LIMIT"));
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }
}