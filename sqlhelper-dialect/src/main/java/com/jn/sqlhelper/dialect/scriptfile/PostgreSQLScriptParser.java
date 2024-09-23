package com.jn.sqlhelper.dialect.scriptfile;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.DialectNames;

public class PostgreSQLScriptParser extends PlainSqlScriptParser {

    @Override
    public String getName() {
        return DialectNames.POSTGRESQL;
    }

    @Override
    protected PlainSqlStatementBuilder newSqlStatementBuilder() {
        return new PostgreSQLSqlStatementBuilder();
    }
}
