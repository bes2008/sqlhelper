package com.jn.sqlhelper.dialect.scriptfile;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;

public class PostgreSQLScriptParser extends PlainSqlScriptParser {
    @Override
    protected PlainSqlStatementBuilder newSqlStatementBuilder() {
        return new PostgreSQLSqlStatementBuilder();
    }
}
