package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * http://www.actordb.com/docs-howitworks.html#dbengine
 * depend on SQLite
 */
@Name("actor")
public class ActorDBDialect extends AbstractDialect {
    public ActorDBDialect() {
        setLimitHandler(new LimitOffsetLimitHandler());
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
}
