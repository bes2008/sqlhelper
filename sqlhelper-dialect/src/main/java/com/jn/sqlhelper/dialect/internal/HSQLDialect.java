package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;
import com.jn.sqlhelper.dialect.scriptfile.HSQLSqlScriptParser;

/**
 * HyperSQL
 * http://hsqldb.org/doc/2.0/guide/dataaccess-chapt.html#dac_sql_select_statement
 */
@Name("hsql")
public class HSQLDialect extends AbstractDialect {

    public HSQLDialect() {
        super();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler().setSupportUsingIndexClauseInSelectEnd(true));
        setPlainSqlScriptParser(new HSQLSqlScriptParser());
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return false;
    }


}
