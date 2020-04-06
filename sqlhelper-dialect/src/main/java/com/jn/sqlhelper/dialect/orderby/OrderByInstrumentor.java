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

package com.jn.sqlhelper.dialect.orderby;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.sqlhelper.dialect.sqlparser.jsqlparser.JSqlParserOrderByInstrumentor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;

public class OrderByInstrumentor {

    private static String instrumentOrderByUsingJSqlParser(String sql, OrderBy orderBy) throws JSQLParserException {
        Select select = (Select) CCJSqlParserUtil.parse(sql);
        JSqlParserOrderByInstrumentor.instrument(select, orderBy);
        return select.toString();
    }

    private static String instrumentOrderByUsingStringAppend(String sql, final OrderBy orderBy) {
        final StringBuilder builder = new StringBuilder(sql);
        builder.append(" ORDER BY ");
        Collects.forEach(Collects.asList(orderBy), new Consumer2<Integer, OrderByItem>() {
            @Override
            public void accept(Integer index, OrderByItem orderByItem) {
                if (index > 0) {
                    builder.append(",");
                }
                builder.append(" ").append(orderByItem.getExpression()).append(" ").append(orderByItem.getType().name());
            }
        });
        return builder.toString();
    }

    public static String instrument(String sql, OrderBy orderBy) throws Throwable {
        if (orderBy == null || !orderBy.isValid()) {
            throw new IllegalArgumentException("Illegal order by");
        }
        try {
            return instrumentOrderByUsingJSqlParser(sql, orderBy);
        } catch (Throwable ex) {
            return instrumentOrderByUsingStringAppend(sql, orderBy);
        }

    }


}
