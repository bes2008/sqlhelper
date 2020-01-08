package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.internal.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

@Name("gaussdb")
public class GaussDbDialect extends AbstractDialect {
    public GaussDbDialect() {
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
        setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);
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
