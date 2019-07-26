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

import java.util.StringTokenizer;

/**
 * 1) +expression1, -expression2
 * 2) expression1+, expression2-
 */
public class SymbolStyleOrderByBuilder implements OrderByBuilder<String> {
    private String ascSymbol = "+";
    private String descSymbol = "-";

    @Override
    public OrderBy build(String s) {
        if (Strings.isBlank(s)) {
            return EMPTY;
        }

        OrderBy orderBy = new OrderBy();
        String currentSymbol = null;
        String currentExpression = null;

        StringTokenizer tokenizer = new StringTokenizer(s);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            boolean isAscSymbol = ascSymbol.equals(token);
            boolean isDescSymbol = descSymbol.equals(token);
            boolean isDelimiter = ",".equals(token);

            if (isAscSymbol || isDescSymbol) {
                currentSymbol = token;
            } else if (!isDelimiter) {
                currentExpression = token;
            } else {
                if (currentExpression != null) {
                    orderBy.add(new OrderByItem(currentExpression, !descSymbol.equals(currentSymbol)));
                }
                currentExpression = null;
                currentSymbol = null;
            }
        }
        if (currentExpression != null) {
            orderBy.add(new OrderByItem(currentExpression, !descSymbol.equals(currentSymbol)));
        }
        return orderBy;
    }

    public String ascSymbol() {
        return ascSymbol;
    }

    public SymbolStyleOrderByBuilder ascSymbol(String ascSymbol) {
        this.ascSymbol = ascSymbol;
        return this;
    }

    public String descSymbol() {
        return descSymbol;
    }

    public SymbolStyleOrderByBuilder descSymbol(String descSymbol) {
        this.descSymbol = descSymbol;
        return this;
    }

}
