package com.jn.sqlhelper.tests;

import com.jn.sqlhelper.dialect.orderby.SymbolStyleOrderByBuilder;
import org.junit.Test;

public class SymbolStyleOrderByBuilderTests {
    @Test
    public void test() {
        SymbolStyleOrderByBuilder builder = SymbolStyleOrderByBuilder.MATH_SYMBOL_ORDER_BY_BUILDER;
        System.out.println(builder.build(null).toString());
        System.out.println(builder.build(" ").toString());
        System.out.println(builder.build("  \t  ").toString());
        System.out.println(builder.build(" a ").toString());
        System.out.println(builder.build(" a ,").toString());
        System.out.println(builder.build(" a   \t +").toString());
        System.out.println(builder.build(" abc   \n  -").toString());
        System.out.println(builder.build(" a+").toString());
        System.out.println(builder.build(" a+,").toString());
        System.out.println(builder.build(" a +").toString());
        System.out.println(builder.build(" abc -").toString());
        System.out.println(builder.build(" -a").toString());
        System.out.println(builder.build(" +a,").toString());
        System.out.println(builder.build(" + a").toString());
        System.out.println(builder.build(" - abc").toString());
        System.out.println(builder.build("- a , b ").toString());
        System.out.println(builder.build("- a , b +").toString());
    }
}
