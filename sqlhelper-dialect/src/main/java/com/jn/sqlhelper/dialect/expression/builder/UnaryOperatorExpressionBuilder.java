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

package com.jn.sqlhelper.dialect.expression.builder;

import com.jn.langx.expression.operator.UnaryOperator;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.function.Supplier0;
import com.jn.sqlhelper.dialect.expression.ColumnExpression;
import com.jn.sqlhelper.dialect.expression.SQLExpression;
import com.jn.sqlhelper.dialect.expression.StringExpression;

public class UnaryOperatorExpressionBuilder<E extends SQLExpression & UnaryOperator, T extends UnaryOperatorExpressionBuilder<E, T>> implements SQLExpressionBuilder<E> {
    protected String operateSymbol; // optional
    protected SQLExpression target; // required
    private Supplier0<E> supplier; // required

    public T supplier(Supplier0<E> supplier) {
        this.supplier = supplier;
        return (T) this;
    }

    public T operateSymbol(String symbol) {
        this.operateSymbol = symbol;
        return (T) this;
    }

    public T target(SQLExpression expression) {
        this.target = expression;
        return (T) this;
    }

    public T target(String expression) {
        return target(expression, false);
    }

    public T target(String expression, boolean isColumn) {
        return target(isColumn ? new ColumnExpression(expression) : new StringExpression(expression));
    }

    @Override
    public E build() {
        Preconditions.checkNotNull(target, "the target expression is null");
        Preconditions.checkNotNull(supplier, "the supplier is null");

        E unaryOperator = supplier.get();
        if (Strings.isNotEmpty(operateSymbol)) {
            unaryOperator.setOperateSymbol(operateSymbol);
        }
        unaryOperator.setTarget(target);
        return unaryOperator;
    }
}