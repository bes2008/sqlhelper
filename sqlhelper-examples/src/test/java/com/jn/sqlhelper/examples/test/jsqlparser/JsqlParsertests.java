package com.jn.sqlhelper.examples.test.jsqlparser;

import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.orderby.SymbolStyleOrderByBuilder;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
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
        String sql = "select id, name, age from user where age > 10 order by age limit 10 offset 20";
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
                if(orderBy.isValid()){
                    if(orderByElements==null){
                        orderByElements = new ArrayList<>();
                    }
                    for (OrderByElement orderByElement : orderByElements) {
                        orderByElement.getExpression().toString();
                    }
                    plainSelect.setOrderByElements(orderByElements);
                }
            }
        }
    }

}
