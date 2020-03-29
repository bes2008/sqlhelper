package com.jn.sqlhelper.dialect.sqlparser.jsqlparser;

import com.jn.sqlhelper.dialect.sqlparser.SQLParseException;
import com.jn.sqlhelper.dialect.sqlparser.SqlParser;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

public class JSqlParser implements SqlParser<JSqlParserStatementWrapper> {
    @Override
    public JSqlParserStatementWrapper parse(String sql) throws SQLParseException {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            JSqlParserStatementWrapper result = new JSqlParserStatementWrapper(statement);
            result.setOriginalSql(sql);
            return result;
        } catch (JSQLParserException ex) {
            throw new SQLParseException(ex);
        }
    }
}
