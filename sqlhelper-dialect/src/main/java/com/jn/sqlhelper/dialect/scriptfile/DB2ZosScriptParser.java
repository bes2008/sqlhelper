package com.jn.sqlhelper.dialect.scriptfile;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.DialectNames;

public class DB2ZosScriptParser extends PlainSqlScriptParser {
    @Override
    public String getName() {
        return DialectNames.DB2Zos;
    }
    protected PlainSqlStatementBuilder newSqlStatementBuilder(){
        return new DB2ZosSqlStatementBuilder();
    }

}
