package com.jn.sqlhelper.dialect.scriptfile;

import com.jn.sqlhelper.dialect.DialectNames;

public class MagicDataSqlScriptParser extends PostgreSQLScriptParser {
    @Override
    public String getName() {
        return DialectNames.MAGICDATA;
    }
}
