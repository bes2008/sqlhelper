package com.jn.sqlhelper.tests;

import com.jn.sqlhelper.dialect.likeescaper.LikeEscaper;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import org.junit.Test;

public class LikeEscapeTests {
    @Test
    public void test(){
        LikeEscaper escaper = new BackslashStyleEscaper();
        String sql = "afs%fl";
        System.out.println(escaper.escape(sql));
    }
}
