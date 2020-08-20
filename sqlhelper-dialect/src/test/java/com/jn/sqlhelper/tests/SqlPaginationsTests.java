/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.tests;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.sqlhelper.dialect.instrument.InstrumentedStatement;
import com.jn.sqlhelper.dialect.pagination.SqlPaginations;
import org.junit.Test;

import java.util.List;

public class SqlPaginationsTests {
    @Test
    public void extractSubqueryTest() {
        String pageSql = "select * from (select * from y where name like 'aaa%?' and age =? ) m t where (x =? and bc=?) or c=?";
        String startFlag = "[START]";
        String endFlag = "[END]";
        showPageSql(pageSql, startFlag, endFlag);

        pageSql = "select * from ([START]select * from y where name like 'aaa%?' and age =? [END]) m t where (x =? and bc=?) or c=?";
        showPageSql(pageSql, startFlag, endFlag);

        pageSql = "select * from a = 'werfwef\\??' and c in ([START]select * from y where name like 'aaa%?' and x= 'afafd\\??' and age =? [END]) m t where (x =? and bc=?) or c=? and b= '%sdfas\\??'";
        showPageSql(pageSql, startFlag, endFlag);

        pageSql = "select * from a = 'werfwef\\[??' and c in ([START]select * from y where name like 'aaa%?' and x= 'afafd\\??' and age =? [END]) m t where (x =? and bc=?) or c=? and b= '%sdfas\\??'";
        showPageSql(pageSql, startFlag, endFlag);
    }

    private void showPageSql(String pageSql, String startFlag, String endFlag) {
        String subqueryPartition = SqlPaginations.extractSubqueryPartition(pageSql, startFlag, endFlag);
        String limitedSubqueryPartition = subqueryPartition + " limit ?, ? ";
        String beforeSubqueryPartition = SqlPaginations.extractBeforeSubqueryPartition(pageSql, startFlag);
        String afterSubqueryPartition = SqlPaginations.extractAfterSubqueryPartition(pageSql, endFlag);
        pageSql = beforeSubqueryPartition + " " + limitedSubqueryPartition + " " + afterSubqueryPartition;
        System.out.println(pageSql);
        int before = SqlPaginations.findPlaceholderParameterCount(beforeSubqueryPartition);
        int after = SqlPaginations.findPlaceholderParameterCount(afterSubqueryPartition);
        System.out.println(StringTemplates.formatWithPlaceholder("before:{}, end: {}", before, after));
    }

    @Test
    public void testGetCountSql(){
        System.out.println(countSql("select * from table where 1=1 order by a ", ""));
        System.out.println(countSql(" select * from \ttable where 1=1 order by a ", ""));
        System.out.println(countSql("\tselect * from table where 1=1 \torder by a ", ""));
    }
    private final static List<String> keywordsNotAfterOrderBy = Collects.asList("select", "?", "union", "from", "where", "and", "or", "between", "in", "case");
    public String countSql(String originalSql, String countColumn) {
        if (Strings.isBlank(countColumn)) {
            countColumn = "1";
        }

        // do count
        boolean sliceOrderBy = false;
        final String lowerSql = originalSql.toLowerCase();
        final int orderIndex = originalSql.toLowerCase().lastIndexOf("order");
        if (orderIndex != -1) {
            String remainSql = lowerSql.substring(orderIndex + "order".length()).trim();
            sliceOrderBy = remainSql.startsWith("by");
            if (sliceOrderBy) {
                remainSql = Strings.replace(remainSql, "(", " ( ");
                remainSql = Strings.replace(remainSql, ")", " ) ");
                Pipeline<String> pipeline = Pipeline.<String>of(remainSql.split("[\\s,]+")).filter(new Predicate<String>() {
                    @Override
                    public boolean test(String value) {
                        return Strings.isNotEmpty(value);
                    }
                });
                if (pipeline.anyMatch(new Predicate<String>() {
                    @Override
                    public boolean test(String value) {
                        return keywordsNotAfterOrderBy.contains(value);
                    }
                })) {
                    sliceOrderBy = false;
                }
                if (sliceOrderBy) {
                    int leftBracketsCount = 0;
                    List<String> list = pipeline.asList();
                    for (int i = 0; i < list.size(); i++) {
                        String c = list.get(i);
                        if (c.equals("(")) {
                            leftBracketsCount++;
                        } else if (c.equals(")")) {
                            leftBracketsCount--;
                            if (leftBracketsCount < 0) {
                                sliceOrderBy = false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (sliceOrderBy) {
            originalSql = originalSql.substring(0, orderIndex).trim();
        }
        String countSql = "select count(" + countColumn + ") from (" + originalSql + ") tmp_count";

        return countSql;
    }

}
