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
import com.jn.sqlhelper.dialect.pagination.SqlPaginations;
import org.junit.Test;

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
}
