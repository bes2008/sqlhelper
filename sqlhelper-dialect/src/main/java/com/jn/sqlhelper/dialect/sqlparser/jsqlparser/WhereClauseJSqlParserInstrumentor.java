package com.jn.sqlhelper.dialect.sqlparser.jsqlparser;

import com.jn.sqlhelper.dialect.instrument.ClauseInsturmentor;
import com.jn.sqlhelper.dialect.instrument.InstrumentConfig;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import net.sf.jsqlparser.statement.Statement;

public class WhereClauseJSqlParserInstrumentor implements ClauseInsturmentor<Statement> {
    @Override
    public void instrument(SqlStatementWrapper<Statement> statement, InstrumentConfig config) {

    }
}
