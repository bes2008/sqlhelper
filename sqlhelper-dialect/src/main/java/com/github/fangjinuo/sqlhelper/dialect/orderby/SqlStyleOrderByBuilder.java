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

package com.github.fangjinuo.sqlhelper.dialect.orderby;

import com.github.fangjinuo.sqlhelper.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * expression1, expression2 desc, expression3 asc;
 */
public class SqlStyleOrderByBuilder implements OrderByBuilder<String> {

    private final List<String> keywordsAfterOrderBy = new ArrayList<String>(Arrays.asList(new String[]{
            "limit", "offset", "PROCEDURE"
    }));

    public SqlStyleOrderByBuilder addKeywords(List<String> keywords) {
        if (keywords != null) {
            for (String keyword : keywords) {
                addKeyword(keyword);
            }
        }
        return this;
    }

    public SqlStyleOrderByBuilder addKeyword(String keyword) {
        if (!Strings.isBlank(keyword)) {
            keywordsAfterOrderBy.add(keyword.toLowerCase());
        }
        return this;
    }

    @Override
    public OrderBy build(String s) {
        if (Strings.isBlank(s)) {
            return EMPTY;
        }
        OrderBy orderBy = new OrderBy();

        String currentExpression = null;
        String currentOrderType = null;

        StringTokenizer tokenizer = new StringTokenizer(s);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String tmp = token.toLowerCase();

            boolean isSqlDelimiter = ",".equals(tmp);
            boolean isSqlSortSymbol = tmp.equals("asc") || tmp.equals("desc");

            if (!isSqlDelimiter && !isSqlSortSymbol) {
                if (keywordsAfterOrderBy.contains(tmp)) {
                    break;
                }

                if (currentExpression != null) {
                    // for update
                    if (tmp.equals("update")) {
                        if (currentExpression.toLowerCase().equals("for")) {
                            currentExpression = null;
                            currentOrderType = null;
                            break;
                        }
                    }
                    // into outfile
                    if (tmp.equals("outfile")) {
                        if (currentExpression.toLowerCase().equals("into")) {
                            currentExpression = null;
                            currentOrderType = null;
                            break;
                        }
                    }
                }
                currentExpression = token;
                continue;
            }

            if (isSqlSortSymbol) {
                currentOrderType = token;
            }

            if (currentExpression == null) {
                currentOrderType = null;
            } else {
                orderBy.add(new OrderByItem(currentExpression, OrderByType.fromString(currentOrderType)));
                currentExpression = null;
                currentOrderType = null;
            }
        }

        if (currentExpression != null) {
            orderBy.add(new OrderByItem(currentExpression, OrderByType.fromString(currentOrderType)));
            currentExpression = null;
            currentOrderType = null;
        }

        return orderBy;
    }

}
