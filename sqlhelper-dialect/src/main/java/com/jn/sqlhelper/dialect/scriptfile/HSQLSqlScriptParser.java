package com.jn.sqlhelper.dialect.scriptfile;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.DialectNames;

public class HSQLSqlScriptParser extends PlainSqlScriptParser {

    @Override
    public String getName() {
        return DialectNames.HSQL;
    }

    @Override
    protected PlainSqlStatementBuilder newSqlStatementBuilder() {
        return new HsqlSqlStatementBuilder();
    }
}