package com.jn.sqlhelper.dialect.scriptfile;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.DialectNames;

public class MySqlScriptParser  extends PlainSqlScriptParser {

    @Override
    public String getName() {
        return DialectNames.MYSQL;
    }

    @Override
    protected PlainSqlStatementBuilder newSqlStatementBuilder() {
        return new MySQLSqlStatementBuilder();
    }
}