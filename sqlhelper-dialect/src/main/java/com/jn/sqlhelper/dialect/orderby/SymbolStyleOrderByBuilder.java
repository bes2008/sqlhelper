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

import com.jn.langx.util.Strings;
import com.jn.sqlhelper.dialect.SqlSymbolMapper;
import com.jn.sqlhelper.dialect.symbolmapper.NoopSymbolMapper;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 1) +expression1, -expression2
 * 2) expression1+, expression2-
 */
public class SymbolStyleOrderByBuilder implements OrderByBuilder<String> {
    private String ascSymbol;
    private String descSymbol;

    private Map<String, String> symbolMap = new HashMap<String, String>();
    private SqlSymbolMapper sqlSymbolMapper = NoopSymbolMapper.DEFAULT;

    public static final SymbolStyleOrderByBuilder MATH_SYMBOL_ORDER_BY_BUILDER = new SymbolStyleOrderByBuilder("+", "-");

    public SymbolStyleOrderByBuilder() {
    }

    public SymbolStyleOrderByBuilder(String ascSymbol, String descSymbol) {
        ascSymbol(ascSymbol);
        descSymbol(descSymbol);
    }

    public SymbolStyleOrderByBuilder sqlSymbolMapper(SqlSymbolMapper sqlSymbolMapper) {
        if (sqlSymbolMapper != null) {
            this.sqlSymbolMapper = sqlSymbolMapper;
        }
        return this;
    }

    @Override
    public OrderBy build(String s) {
        if (Strings.isBlank(ascSymbol) || Strings.isBlank(descSymbol)) {
            throw new OrderBySymolException("OrderByBuilder symbol is illegal, ascSymbol:" + ascSymbol + ",descSymbol:" + descSymbol);
        }

        if (Strings.isBlank(s)) {
            return OrderBy.EMPTY;
        }

        OrderBy orderBy = new OrderBy();
        String currentSymbol = null;
        String currentExpression = null;

        StringTokenizer tokenizer = new StringTokenizer(s, " \t\n\r\f,", true);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (Strings.isBlank(token)) {
                continue;
            }

            boolean isAscSymbol = ascSymbol.equals(token);
            boolean isDescSymbol = descSymbol.equals(token);
            boolean isDelimiter = ",".equals(token);

            if (isAscSymbol || isDescSymbol) {
                currentSymbol = token;
            } else if (!isDelimiter) {
                String lastOrderBySymbolForCurrentExpression = null;
                while (token.startsWith(ascSymbol) || token.startsWith(descSymbol)) {
                    if (token.startsWith(ascSymbol)) {
                        token = token.substring(ascSymbol.length());
                        lastOrderBySymbolForCurrentExpression = ascSymbol;
                    } else {
                        token = token.substring(descSymbol.length());
                        lastOrderBySymbolForCurrentExpression = descSymbol;
                    }
                }
                while (token.endsWith(ascSymbol) || token.endsWith(descSymbol)) {
                    if (token.endsWith(ascSymbol)) {
                        token = token.substring(0, token.lastIndexOf(ascSymbol));
                        lastOrderBySymbolForCurrentExpression = ascSymbol;
                    } else {
                        token = token.substring(0, token.lastIndexOf(descSymbol));
                        lastOrderBySymbolForCurrentExpression = descSymbol;
                    }
                }
                if (lastOrderBySymbolForCurrentExpression != null) {
                    currentSymbol = lastOrderBySymbolForCurrentExpression;
                }
                currentExpression = token;
            } else {
                if (currentExpression != null) {
                    orderBy.add(new OrderByItem(sqlSymbolMapper.apply(currentExpression), OrderByType.fromString(symbolMap.get(currentSymbol))));
                }
                currentExpression = null;
                currentSymbol = null;
            }
        }
        if (currentExpression != null) {
            orderBy.add(new OrderByItem(sqlSymbolMapper.apply(currentExpression), OrderByType.fromString(symbolMap.get(currentSymbol))));
        }
        return orderBy;
    }

    public String ascSymbol() {
        return ascSymbol;
    }

    public SymbolStyleOrderByBuilder ascSymbol(String ascSymbol) {
        if (isValidSymbol(ascSymbol)) {
            this.ascSymbol = ascSymbol;
            removeSymbolMapForValue("asc");
            symbolMap.put(ascSymbol, "asc");
        }
        return this;
    }


    public SymbolStyleOrderByBuilder descSymbol(String descSymbol) {
        if (isValidSymbol(descSymbol)) {
            this.descSymbol = descSymbol;
            removeSymbolMapForValue("desc");
            symbolMap.put(descSymbol, "desc");
        }
        return this;
    }


    private void removeSymbolMapForValue(@Nonnull String value) {
        Iterator<Map.Entry<String, String>> iter = symbolMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            if (value.equals(entry.getValue())) ;
        }
    }

    private boolean isValidSymbol(String symbol) {
        if (Strings.isBlank(symbol)) {
            return false;
        }
        symbol = symbol.trim();
        if (symbol.equals("?")) {
            return false;
        }
        return true;
    }

}
