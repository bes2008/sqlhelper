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

import com.jn.sqlhelper.dialect.SQLInstrumentException;
import com.jn.sqlhelper.dialect.jsqlparser.Selects;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;

import java.util.ArrayList;
import java.util.List;

public class OrderByInstrumentor {

    public static String instrument(String sql, OrderBy orderBy) throws Throwable {
        if (orderBy == null || !orderBy.isValid()) {
            throw new IllegalArgumentException("Illegal order by");
        }
        Select select = (Select) CCJSqlParserUtil.parse(sql);

        SelectBody selectBody = select.getSelectBody();

        PlainSelect plainSelect = Selects.extractPlainSelect(selectBody);
        if (plainSelect == null) {
            return null;
        }
        List<OrderByElement> orderByElements = plainSelect.getOrderByElements();

        if (orderByElements != null && !orderByElements.isEmpty()) {
            String orderByStringInSql = PlainSelect.orderByToString(orderByElements);
            if (orderByStringInSql.contains("?")) {
                throw new SQLInstrumentException("Can't instrument order by because the original sql [" + sql + "] has ? in order by clause");
            }
        }

        if (orderBy.isValid()) {
            if (orderByElements == null) {
                orderByElements = new ArrayList<OrderByElement>();
            }

            for (OrderByItem item : orderBy) {
                Expression exprForAppend = CCJSqlParserUtil.parseExpression(item.getExpression());
                boolean needAppend = true;
                for (OrderByElement orderByElement : orderByElements) {
                    Expression exprInSql = orderByElement.getExpression();
                    if (exprForAppend.getClass() == exprInSql.getClass()) {
                        if (Selects.expressionEquals(exprForAppend, exprInSql)) {
                            needAppend = false;
                            // do asc, desc change
                            if (item.getType() == null) {
                                orderByElement.setAscDescPresent(false);
                            } else {
                                orderByElement.setAsc(item.getType() == OrderByType.ASC);
                            }
                        }
                    }

                }

                if (needAppend) {
                    OrderByElement orderByElement = new OrderByElement();
                    if (item.getType() == null) {
                        orderByElement.setAscDescPresent(false);
                    } else {
                        orderByElement.setAsc(item.getType() == OrderByType.ASC);
                    }
                    orderByElement.setExpression(exprForAppend);

                    orderByElements.add(orderByElement);
                }
            }

            if (!orderByElements.isEmpty()) {
                plainSelect.setOrderByElements(orderByElements);
            }
        }

        return select.toString();

    }


}
