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

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import org.junit.Test;

import java.util.List;

public class CountSQLTests {
    @Test
    public void test() {
        String sql = "select * from (select a, b from x where a>0 and b>0 order by a, \t b)     n \nwhere a>0";
        System.out.println(countSql(sql, null));
        sql = "select * from (select a, b from x where a>0 and b>0 order by a, \t b)     n ";
        System.out.println(countSql(sql, null));
        sql = "select a, b from x where a>0 and b>0 order by a, \t b ";
        System.out.println(countSql(sql, null));
    }

    private String countSql(String originalSql, String countColumn) {
        if (Strings.isBlank(countColumn)) {
            countColumn = "1";
        }

        // do count
        boolean sliceOrderBy = false;
        final String lowerSql = originalSql.toLowerCase().trim();
        final int orderIndex = originalSql.toLowerCase().lastIndexOf("order");
        if (orderIndex != -1) {
            String remainSql = lowerSql.substring(orderIndex + "order".length()).trim();
            sliceOrderBy = remainSql.startsWith("by");
            if (sliceOrderBy) {

                remainSql =Strings.replace(remainSql, "("," ( ");
                remainSql = Strings.replace(remainSql,")"," ) ");


                Pipeline<String> pipeline = Pipeline.<String>of(remainSql.split("\\s+")).filter(new Predicate<String>() {
                    @Override
                    public boolean test(String value) {
                        return Strings.isNotEmpty(value);
                    }
                });
                if (pipeline.anyMatch(new Predicate<String>() {
                    //String[]{"select","union","from","where","and","or","between","in","case"}

                    @Override
                    public boolean test(String value) {
                        return Collects.asList("select", "union", "from", "where", "and", "or", "between", "in", "case").contains(value);
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
            originalSql = originalSql.trim().substring(0, orderIndex).trim();
        }
        return "select count(" + countColumn + ") from (" + originalSql + ") tmp_count";
    }
}
