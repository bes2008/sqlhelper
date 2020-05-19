/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.Delegatable;
import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum ExpressionSymbol implements CommonEnum, Delegatable<EnumDelegate> {

    ALL(1, "ALL", "all"),
    ANY(2, "ANY", "any"),

    AND(3, "AND", "and"),
    OR(4, "OR", "or"),
    NOT(5, "NOT", "not"),

    BETWEEN_AND(6, "BETWEEN AND", "between and"),

    ADD(7, "+", "+"),
    DIVIDE(8, "/", "/"),
    MODE(9, "%", "%"),
    SUBTRACT(10, "-", "-"),
    MULTIPLE(11, "*", "*"),

    EQ(12, "=", "="),
    GE(13, ">=", ">="),
    GT(14, ">", ">"),
    LE(15, "<=", "<="),
    LT(16, "<", "<"),
    NE(17, "!=", "!="),

    IN(18, "IN", "in"),
    IS_NULL(19, "IS NULL", "is null"),
    EXISTS(20, "EXISTS", "exists"),
    LIKE(21, "LIKE", "like");

    private EnumDelegate delegate;

    private ExpressionSymbol(int code, String name, String displayText) {
        this.delegate = new EnumDelegate(code, name, displayText);
    }


    @Override
    public EnumDelegate getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(EnumDelegate delegate) {
    }

    @Override
    public int getCode() {
        return delegate.getCode();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getDisplayText() {
        return delegate.getDisplayText();
    }
}
