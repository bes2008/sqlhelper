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

package com.jn.sqlhelper.dialect.instrument;

import com.jn.sqlhelper.dialect.orderby.OrderBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstrumentedSelectStatement extends InstrumentedStatement{
    private String countSql;
    /**
     * key: dialect
     * value: limit sql
     */
    private Map<String, List<String>> dialectLimitSql = new HashMap<String, List<String>>();
    /**
     * key: order by
     * value: orderBySql
     */
    private Map<OrderBy, String> orderBySql = new HashMap<OrderBy, String>();

    /**
     * key: orderBy
     * value:{dialect: sqls}
     */
    private Map<OrderBy, Map<String, List<String>>> orderByLimitSql = new HashMap<OrderBy, Map<String, List<String>>>();

    public String getCountSql() {
        return countSql;
    }

    public void setCountSql(String countSql) {
        this.countSql = countSql;
    }

    public String getLimitSql(String dialect, boolean hasOffset) {
        return getLimitSql(dialectLimitSql, dialect, hasOffset);
    }

    private static String getLimitSql(Map<String, List<String>> dialectLimitSql, String dialect, boolean hasOffset) {
        List<String> pagingSqls = dialectLimitSql.get(dialect);
        if (pagingSqls == null) {
            return null;
        }
        return pagingSqls.get(hasOffset ? 1 : 0);
    }

    public String getOrderBySql(OrderBy orderBy) {
        return orderBySql.get(orderBy);
    }

    public String getOrderByLimitSql(OrderBy orderBy, String dialect, boolean hasOffset) {
        Map<String, List<String>> dialectLimitSql = orderByLimitSql.get(orderBy);
        if (dialectLimitSql == null) {
            return null;
        }
        return getLimitSql(dialectLimitSql, dialect, hasOffset);
    }

    public void setLimitSql(String dialect, String limitSql, boolean hasOffset) {
        setLimitSql(dialectLimitSql, dialect, limitSql, hasOffset);
    }

    private static void setLimitSql(Map<String, List<String>> dialectLimitSql, String dialect, String limitSql, boolean hasOffset){
        List<String> pagerSqls = dialectLimitSql.get(dialect);
        if (pagerSqls == null) {
            pagerSqls = new ArrayList<String>(2);
            pagerSqls.add(null);
            pagerSqls.add(null);
            dialectLimitSql.put(dialect, pagerSqls);
        }
        pagerSqls.set((hasOffset ? 1 : 0), limitSql);
    }

    public void setOrderBySql(OrderBy orderBy, String sql) {
        orderBySql.put(orderBy, sql);
    }

    public void setOrderByLimitSql(OrderBy orderBy, String dialect, String sql, boolean hasOffset) {
        Map<String, List<String>> dialectLimitSql = orderByLimitSql.get(orderBy);
        if (dialectLimitSql == null) {
            dialectLimitSql = new HashMap<String, List<String>>();
            orderByLimitSql.put(orderBy, dialectLimitSql);
        }

        setLimitSql(dialectLimitSql, dialect, sql, hasOffset);
    }

}
