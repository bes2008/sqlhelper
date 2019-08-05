package com.jn.sqlhelper.examples.test.jsqlparser;

import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.orderby.OrderByItem;
import com.jn.sqlhelper.dialect.orderby.OrderByType;
import com.jn.sqlhelper.dialect.orderby.SymbolStyleOrderByBuilder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JsqlParsertests {
    @Test
    public void test() throws Throwable {
        instrmentOrderBy("select id, name, age from user where age > 10");
        instrmentOrderBy("select id, name, age from user where age > 10 order by age");
        instrmentOrderBy("select id, name, age from user where age > 10 limit ?");
        instrmentOrderBy("select id, name, age from user where age > 10 limit ? offset ?");
        instrmentOrderBy("select id, name, age from user where age > 10 order by age limit ? offset ?");
    }


    public void instrmentOrderBy(String sql) throws Throwable {
        Statement statement = CCJSqlParserUtil.parse(sql);
        if (statement instanceof Select) {
            Select select = (Select) statement;
            System.out.println("print parsed sql statement:");
            System.out.println(select.toString());
            System.out.println("show tables:");
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            List<String> tableNames = tablesNamesFinder.getTableList(select);
            for (String tableName : tableNames) {
                System.out.println(tableName);
            }

            SelectBody selectBody = select.getSelectBody();
            if (selectBody instanceof PlainSelect) {
                PlainSelect plainSelect = (PlainSelect) selectBody;
                List<OrderByElement> orderByElements = plainSelect.getOrderByElements();

                OrderBy orderBy = SymbolStyleOrderByBuilder.MATH_SYMBOL_ORDER_BY_BUILDER.build("id, name-");
                if (orderBy.isValid()) {
                    if (orderByElements == null) {
                        orderByElements = new ArrayList<>();
                    }

                    for (OrderByItem item : orderBy) {
                        Expression exprForAppend = CCJSqlParserUtil.parseExpression(item.getExpression());
                        boolean needAppend = true;
                        for (OrderByElement orderByElement : orderByElements) {
                            Expression exprInSql = orderByElement.getExpression();
                            if (exprForAppend.getClass() == exprInSql.getClass()) {
                                if (expressionEquals(exprForAppend, exprInSql)) {
                                    needAppend = false;
                                    // do asc, desc change
                                    if (item.getType() == null) {
                                        orderByElement.setAscDescPresent(false);
                                    } else {
                                        orderByElement.setAsc(item.getType() == OrderByType.ASC);
                                    }
                                }
                            }

                        }

                        if (needAppend) {
                            OrderByElement orderByElement = new OrderByElement();
                            if (item.getType() == null) {
                                orderByElement.setAscDescPresent(false);
                            } else {
                                orderByElement.setAsc(item.getType() == OrderByType.ASC);
                            }
                            orderByElement.setExpression(exprForAppend);

                            orderByElements.add(orderByElement);
                        }
                    }

                    if (!orderByElements.isEmpty()) {
                        plainSelect.setOrderByElements(orderByElements);
                    }
                }
            }

            System.out.println("print instrumented sql:");
            System.out.println(select.toString());

            System.out.println("====================================");
        }
    }

    private static boolean columnEquals(Column column1, Column column2) {
        if (column1 == null && column2 == null) {
            return true;
        }
        if (column1 == null || column2 == null) {
            return false;
        }
        return column1.getFullyQualifiedName().equalsIgnoreCase(column2.getFullyQualifiedName());
    }

    private static boolean expressionEquals(Expression expr1, Expression expr2) {
        if (expr1 == null && expr2 == null) {
            return true;
        }
        if (expr1 == null || expr2 == null) {
            return false;
        }

        if (expr1 instanceof Column && expr2 instanceof Column) {
            return columnEquals((Column) expr1, (Column) expr2);
        }
        return expr1.toString().equalsIgnoreCase(expr2.toString());
    }
}
