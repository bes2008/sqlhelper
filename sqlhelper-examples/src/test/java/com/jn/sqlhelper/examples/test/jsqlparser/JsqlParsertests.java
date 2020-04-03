package com.jn.sqlhelper.examples.test.jsqlparser;

import com.jn.sqlhelper.dialect.orderby.OrderByInstrumentor;
import com.jn.sqlhelper.dialect.orderby.SqlStyleOrderByBuilder;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.junit.Test;

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

            String orderBySql = OrderByInstrumentor.instrument(sql, SqlStyleOrderByBuilder.DEFAULT.build("name asc, age desc"));

            System.out.println("print instrumented sql:");
            System.out.println(orderBySql);

            System.out.println("====================================");
        }
    }
}
