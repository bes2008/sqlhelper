package com.jn.sqlhelper.common.tests;

import com.jn.sqlhelper.common.symbolmapper.SqlSymbolMapper;
import com.jn.sqlhelper.common.symbolmapper.UnderlineToCamelSymbolMapper;
import org.junit.Test;

public class UnderlineToCamelSymbolMapperTests {
    @Test
    public void test(){
        SqlSymbolMapper m = new UnderlineToCamelSymbolMapper();
        System.out.println(m.apply("a_b_c"));
        System.out.println(m.apply("a_bbb_c"));
        System.out.println(m.apply("_BC_c"));
    }
}
