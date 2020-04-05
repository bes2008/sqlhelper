package com.jn.sqlhelper.dialect.sqlparser.jsqlparser;

import com.jn.langx.util.Emptys;
import com.jn.sqlhelper.dialect.instrument.ClauseInsturmentor;
import com.jn.sqlhelper.dialect.instrument.InstrumentConfig;
import com.jn.sqlhelper.dialect.instrument.WhereClauseExpressionInstrumentConfig;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import net.sf.jsqlparser.statement.Statement;

import java.util.List;

public class WhereClauseJSqlParserInstrumentor implements ClauseInsturmentor<Statement> {
    @Override
    public void instrument(SqlStatementWrapper<Statement> statementWrapper, InstrumentConfig config) {
        if(Emptys.isEmpty(statementWrapper) || Emptys.isEmpty(config)){
            return;
        }
        Statement statement = statementWrapper.get();
        List<WhereClauseExpressionInstrumentConfig> expressionConfigs = config.getWhereClauseExpressionInstrumentConfigs();
        if(Emptys.isEmpty(statement) || Emptys.isEmpty(expressionConfigs)){
            return;
        }
        if(!Jsqlparsers.isDML(statement)){
            return;
        }



    }
}
