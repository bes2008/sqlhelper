package com.jn.sqlhelper.jsqlparser.expression;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.sqlhelper.dialect.expression.ColumnExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Database;
import net.sf.jsqlparser.schema.Table;

import java.util.ArrayList;
import java.util.List;

public class ColumnExpressionConverter implements ExpressionConverter<ColumnExpression, Column> {
    @Override
    public Column toJSqlParserExpression(ColumnExpression expression) {
        Column column = new Column();
        Database database = null;
        if (Strings.isNotEmpty(expression.getCatalog())) {
            database = new Database(expression.getCatalog());
        }
        String schema = expression.getSchema();
        String tableName = expression.getTable();
        Table table = database == null ? new Table() : new Table(database, schema, tableName);
        column.setTable(table);
        column.setColumnName(expression.getColumn());
        return column;
    }

    @Override
    public ColumnExpression fromJSqlParserExpression(Column expression) {
        return null;
    }

    @Override
    public Class<ColumnExpression> getStandardExpressionClass() {
        return ColumnExpression.class;
    }

    @Override
    public Class<Column> getJSqlParserExpressionClass() {
        return Column.class;
    }
}
