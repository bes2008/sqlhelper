package com.jn.sqlhelper.tests;

import com.jn.sqlhelper.dialect.LikeEscaper;
import com.jn.sqlhelper.dialect.internal.likeescaper.CStyleEscaper;
import org.junit.Test;

public class LikeEscapeTests {
    @Test
    public static void test(){
        LikeEscaper escaper = new CStyleEscaper();

    }
}
