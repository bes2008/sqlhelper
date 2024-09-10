package com.jn.sqlhelper.tests;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Lists;
import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.DialectRegistry;
import org.junit.Test;

import java.util.List;

public class QuoteTests {

    @Test
    public void test() {
        List<String> columns = Lists.newArrayList("user", "id", "role");
        List<String> dialectIds = Lists.newArrayList("mysql", "oracle", "dm", "sqlserver2012", "magicdata", "postgresql", "derby");
        testQuotedColumns(columns, dialectIds);
    }

    private void testQuotedColumns(List<String> columns, List<String> dialectIds) {
        for (String dialectId : dialectIds) {
            System.out.println("==================" + dialectId + "=================");
            Dialect dialect = DialectRegistry.getInstance().gaussDialect(dialectId);
            for (String column : columns) {
                System.out.println(StringTemplates.formatWithPlaceholder("{} => {}", column, dialect.getQuotedIdentifier(column)));
            }
        }
    }

}
