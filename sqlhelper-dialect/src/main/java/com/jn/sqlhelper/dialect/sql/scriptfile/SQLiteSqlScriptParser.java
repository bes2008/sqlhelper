package com.jn.sqlhelper.dialect.sql.scriptfile;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;

public class SQLiteSqlScriptParser  extends PlainSqlScriptParser {
    @Override
    protected PlainSqlStatementBuilder newSqlStatementBuilder() {
        return new SQLiteSqlStatementBuilder();
    }
}
