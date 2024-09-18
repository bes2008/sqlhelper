package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.util.Strings;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlDelimiter;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;
import com.jn.sqlhelper.dialect.internal.urlparser.MySqlUrlParser;

import java.util.regex.Pattern;

/**
 * https://hughes.com.au/products/msql/msql-4.0-manual.pdf
 */
public class MSQLDialect extends AbstractDialect {
    public MSQLDialect() {
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
        setUrlParser(new MySqlUrlParser());
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
    public boolean isSupportsLimit() {
        return true;
    }






}
