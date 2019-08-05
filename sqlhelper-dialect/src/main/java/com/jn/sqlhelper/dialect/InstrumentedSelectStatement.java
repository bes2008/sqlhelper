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

package com.jn.sqlhelper.dialect;

import com.jn.sqlhelper.dialect.orderby.OrderBy;

import java.util.HashMap;
import java.util.Map;

public class InstrumentedSelectStatement {
    private String originalSql;
    private String countSql;
    /**
     * key: dialect
     * value: limit sql
     */
    private Map<String, String> dialectLimitSql = new HashMap<String, String>();
    private Map<OrderBy, String> orderBySql = new HashMap<OrderBy, String>();
    private Map<OrderBy, Map<String, String>> orderByLimitSql = new HashMap<OrderBy, Map<String, String>>();

    public String getOriginalSql() {
        return originalSql;
    }

    public void setOriginalSql(String originalSql) {
        this.originalSql = originalSql;
    }

    public String getCountSql() {
        return countSql;
    }

    public void setCountSql(String countSql) {
        this.countSql = countSql;
    }

    public String getLimitSql(String dialect) {
        return dialectLimitSql.get(dialect);
    }

    public String getOrderBySql(OrderBy orderBy) {
        return orderBySql.get(orderBy);
    }

    public String getOrderByLimitSql(OrderBy orderBy, String dialect) {
        Map<String, String> dialectLimitSql = orderByLimitSql.get(orderBy);
        return dialectLimitSql == null ? null : dialectLimitSql.get(dialect);
    }

    public void setLimitSql(String dialect, String limitSql) {
        dialectLimitSql.put(dialect, limitSql);
    }

    public void setOrderBySql(OrderBy orderBy, String sql) {
        orderBySql.put(orderBy, sql);
    }

    public void setOrderByLimitSql(OrderBy orderBy, String dialect, String sql) {
        Map<String, String> dialectLimitSql = orderByLimitSql.get(orderBy);
        if (dialectLimitSql == null) {
            dialectLimitSql = new HashMap<String, String>();
            orderByLimitSql.put(orderBy, dialectLimitSql);
        }

        dialectLimitSql.put(dialect, sql);
    }

}
