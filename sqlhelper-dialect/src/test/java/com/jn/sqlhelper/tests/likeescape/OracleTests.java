/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.tests.likeescape;

import com.jn.langx.util.struct.Pair;
import com.jn.sqlhelper.dialect.internal.OracleDialect;
import com.jn.sqlhelper.dialect.likeescaper.LikeEscaper;
import com.jn.sqlhelper.dialect.likeescaper.LikeEscapers;
import org.junit.Test;

import java.util.List;

public class OracleTests {

    private final LikeEscaper likeEscaper = new OracleDialect();

    @Test
    public void test1() {
        String sql = "select *\n" +
                "from (\n" +
                "    select ID_, SOFTWARE_ID_\n" +
                "    from TM_SOFTWARE_INSTANCE\n" +
                "    where 1 =1 and (\n" +
                "        lower(NAME_) like CONCAT(CONCAT('%',?),'%')\n" +
                "        or lower(VERSION_) like CONCAT(CONCAT('%',?),'%')\n" +
                "    )\n" +
                "    order by NAME_\n" +
                ")";
        doTest(sql);
    }

    @Test
    public void test2() {
        String sql = "select *\n" +
                "from (\n" +
                "    select ID_, SOFTWARE_ID_\n" +
                "    from TM_SOFTWARE_INSTANCE\n" +
                "    where 1 =1 and \n" +
                "        lower(NAME_) like CONCAT(CONCAT('%',?),'%')\n" +
                "        or lower(VERSION_) like CONCAT(CONCAT('%',?),'%') order by NAME_\n" +
                ")";
        doTest(sql);
    }


    @Test
    public void test3() {
        String sql = "select *\n" +
                "from (\n" +
                "    select ID_, SOFTWARE_ID_\n" +
                "    from TM_SOFTWARE_INSTANCE\n" +
                "    where 1 =1 and \n" +
                "        lower(NAME_) like CONCAT(CONCAT('%',?),'%')\n" +
                "        or lower(VERSION_) like CONCAT(CONCAT('%',?),'%')\n" +
                ")";
        doTest(sql);
    }


    @Test
    public void test4() {
        String sql = "select *\n" +
                "from (\n" +
                "    select ID_, SOFTWARE_ID_\n" +
                "    from TM_SOFTWARE_INSTANCE\n" +
                "    where 1 =1 and (\n" +
                "        lower(NAME_) like CONCAT(CONCAT('%',?),'%')\n" +
                "        or lower(VERSION_) like '%?%'\n" +
                ")";
        doTest(sql);
    }


    @Test
    public void test5() {
        String sql = "select *\n" +
                "from (\n" +
                "    select ID_, SOFTWARE_ID_\n" +
                "    from TM_SOFTWARE_INSTANCE\n" +
                "    where 1 =1 and \n" +
                "        lower(NAME_) like CONCAT(CONCAT('%',?),'%')\n" +
                "        or lower(VERSION_) like '%?%'";
        doTest(sql);
    }

    private void doTest( String sql) {
        System.out.println("\n\n=================\n");
        Pair<List<Integer>, List<Integer>> pair = LikeEscapers.findEscapedSlots(sql);
        String newSql = LikeEscapers.insertLikeEscapeDeclares(sql, pair.getValue(), likeEscaper);
        System.out.println(sql);
        System.out.println("\n===>\n");
        System.out.println(newSql);
    }

}
